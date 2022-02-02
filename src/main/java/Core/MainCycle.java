package Core;

import UI.MainWindow;

import static org.lwjgl.glfw.GLFW.*;

public final class MainCycle
{
    private static MainCycle mainCycle;

    private static Scene currentScene;

    private boolean runtimePlaying = false;

    // private EditorInfo editorInfo = null;

    private MainCycle() {

    }

/*    // класс, который хранит json настройки редактора
    private class EditorInfo
    {
        String lastScene;
        public EditorInfo(String lastScene)
        {
            this.lastScene = lastScene;
        }

        public void setLastScene(String lastScene)
        {
            this.lastScene = lastScene;
        }
    }*/

    /**
     * @return возвращает ссылку на синглетон данного класса
     */
    public static MainCycle get() {
        if (mainCycle == null) {
            mainCycle = new MainCycle();
        }
        return mainCycle;
    }

    /**
     * Включает UI часть движка
     *
     * @return указатель на окно
     */
    public static MainWindow enableUI() {
        MainWindow mw = MainWindow.get();
        mw.init();
        return mw;
    }

    /**
     * Основное тело программы
     */
    public void run(boolean isUIEnabled) {
        if(isUIEnabled)
        {
            MainWindow wnd = MainCycle.enableUI();
        }

        // TODO: загрузка сцены
        changeScene(new LevelEditorSceneInitializer()); // загрузка пустой сцены

        loop(isUIEnabled); // основной цикл программы

        // TODO: сделать сохранение конфигов
        // SaveCfg();
        if (isUIEnabled) {
            MainWindow.get().closeWnd();
        }

    }

    /**
     * Main loop of the application
     */
    private void loop(boolean isUIEnabled) {
        Time.timeAsDouble = glfwGetTime();
        Time.time = (float) Time.timeAsDouble;

        if (isUIEnabled) {
            // TODO: несоответсвие с оригиналом

            MainWindow.loadShaders();

            while (!glfwWindowShouldClose(MainWindow.get().get_windowId())) { // пока не закрыли окно опер. системы
                MainWindow.get().frameStep1();

                businessLogic();

                MainWindow.get().frameStep2();

                double endTime = glfwGetTime(); //the time when frame was ended

                Time.deltaTime = (float) (endTime - Time.timeAsDouble);
                Time.timeAsDouble = endTime;
            }
        } else {
            while (true) {
                // TODO: сейчас в бизнес логике не только бизнес логика, но и рендеринг :(
                businessLogic();
            }
        }
    }

    private void setScene(Scene newScene)
    {
        currentScene = newScene;
    }

    public static Scene getScene()
    {
        return currentScene;
    }

    private void businessLogic()
    {
        if (runtimePlaying) {
            currentScene.update(Time.deltaTime);
        } else {
            currentScene.editorUpdate(Time.deltaTime);
        }
    }

    private static void changeScene(SceneInitializer sceneInitializer)
    {
        changeScene(sceneInitializer, "");
    }

    //method for switching scenes
    private static void changeScene(SceneInitializer sceneInitializer, String sceneName) {
        if (MainCycle.getScene() != null) {
            MainCycle.getScene().destroy();
        }
        MainWindow.getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        MainCycle.get().setScene(new Scene(sceneInitializer));
        MainCycle.getScene().load(sceneName);
        MainCycle.getScene().init();
        MainCycle.getScene().start();
        // при запуске сцены нет активного объекта
        MainWindow.getImguiLayer().getPropertiesWindow().clearSelected();
    }
}

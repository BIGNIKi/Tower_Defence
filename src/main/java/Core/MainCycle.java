package Core;

import UI.MainWindow;

import static org.lwjgl.glfw.GLFW.*;

public final class MainCycle {
    private static MainCycle mainCycle;

    private boolean runtimePlaying = false;

    private MainCycle() {

    }

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
        loop(isUIEnabled);
        // TODO: несоответсвие с оригиналом
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
            /* Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
            Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");*/
            while (!glfwWindowShouldClose(MainWindow.get().get_windowId())) { // пока не закрыли окно опер. системы
                MainWindow.get().frameStep1();

                businessLogic();

                //DebugDraw.drawGrid(getScene().camera()); // рисует линию сетки
                //currentScene.render();
                //DebugDraw.drawAnother(getScene().camera()); // рисует остальные дебажные линии

                MainWindow.get().frameStep2();
                MainWindow.get().frameStep3();

                double endTime = glfwGetTime(); //the time when frame was ended

                Time.deltaTime = (float) (endTime - Time.timeAsDouble);
                Time.timeAsDouble = endTime;
            }
        } else {
            while (true) {
                businessLogic();
            }
        }
    }

    private void businessLogic()
    {
        if (runtimePlaying) {
            //currentScene.update(Time.deltaTime);
        } else {
            //currentScene.editorUpdate(Time.deltaTime);
        }
    }
}

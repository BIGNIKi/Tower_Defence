package job;

import ReplayStuff.*;
import SyncStuff.MonsterClass;
import SyncStuff.SyncClasses;
import SyncStuff.TowerClass;
import Util.AssetPool;
import Util.SyncClassesList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import controllers.Waves;
import entities.monsters.Monster;
import entities.towers.PlaceForTower;
import entities.towers.Tower;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.LevelSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class MainWindow implements Observer
{
    private int width;
    private int heigth;

    private final String title;

    private long _windowId;
    private IMGuiLayer imguiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;

    private boolean runtimePlaying = false;

    public boolean is_isReplayOn()
    {
        return _isReplayOn;
    }

    private boolean _isReplayOn = false;
    private EditorInfo editorInfo = null;

    private final String ReplaySceneName = "replay.json";

    private static MainWindow wnd = null; //we have only one instance of this class

    private static Scene currentScene;

    //it is prohibited to create instance of class outside this class (Singleton)
    private MainWindow()
    {
        this.width = 1920;
        this.heigth = 1080;
        this.title = "Tower defense";
        EventSystem.addObserver(this);
    }

    public boolean isRuntimePlaying()
    {
        return runtimePlaying;
    }

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
    }

    //method for switching scenes
    public static void changeScene(SceneInitializer sceneInitializer, String sceneName) {
        if (currentScene != null) {
            currentScene.destroy();
        }
        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load(sceneName);
        currentScene.init();
        currentScene.start();

        // при запуске сцены нет активного объекта
        getImguiLayer().getPropertiesWindow().clearSelected();
    }

    //it is used for interaction with the class instance
    public static MainWindow get()
    {
        if(wnd == null)
        {
            wnd = new MainWindow();
        }
        return wnd;
    }

    public static Scene getScene()
    {
        return currentScene;
    }

    private void SaveCfg()
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try
        {
            FileWriter writer = new FileWriter("editorCfg.json");
            writer.write(gson.toJson(editorInfo));
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void Run()
    {
        System.out.println("Hello LWGJL " + Version.getVersion() + "!");

        Init();
        Loop();
        SaveCfg();

        //вообще, всё что ниже (в этом методе) можно не делать (это сделает за нас операционная система, но мы же не ленивые, да? =)

        //free the memory (we need it because we have cpp based library under hood)
        Callbacks.glfwFreeCallbacks(_windowId); //resets all callbacks for the specified GLFW window to NULL and frees all previously set callbacks.
        GLFW.glfwDestroyWindow(_windowId); //destroys the specified window and its context
        _windowId = NULL;

        GLFW.glfwTerminate(); //destroys all remaining windows and cursors, restores any modified gamma ramps and frees any other allocated resources

        GLFWErrorCallback x = glfwSetErrorCallback(null);
        if(x != null)
        {
            x.free(); //frees any native resources held by this object.
        }

    }

    private void Init()
    {
        //Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initializaed GLFW
        if(!GLFW.glfwInit())
        {
            throw new IllegalStateException("Unable to initialized GLFW.");
        }

        //Configure GLFW
        GLFW.glfwDefaultWindowHints(); //sets default parameters for Window
        //All defaults states is described here https://javadoc.lwjgl.org/index.html?org/lwjgl/glfw/GLFWErrorCallback.html

        GLFW.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); //window mode at start = false (invisible window)
        GLFW.glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); //window will be maximized when created

        //Create the OpenGL window
        this._windowId = GLFW.glfwCreateWindow(this.width, this.heigth, this.title, NULL, NULL); // it returns the handle of the created window, or NULL if an error occurred
        if(this._windowId == NULL)
        {
            throw new IllegalStateException("Failed to create GLFW window.");
        }

        this.ConnectMouseAndKbd();

        //Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(this._windowId); //we make the context current for this thread
        //some info about context in OpenGL https://dvsav.ru/opengl-3/

        //enable vsync
        GLFW.glfwSwapInterval(1); //info https://gamedev.ru/code/terms/VSync

        //Make the window visible
        GLFW.glfwShowWindow(_windowId); //Makes the specified window visible if it was previously hidden

        GL.createCapabilities(); //we do this so that the library functions use the context of the current (our window) to draw something

        glEnable(GL_BLEND);
        //glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        this.imguiLayer = new IMGuiLayer(_windowId, pickingTexture);
        this.imguiLayer.initImGui();

        //just some interesting bullshit http://jmonkeyengine.ru/page/2/?author=0

        loadLastScene();

        MainWindow.changeScene(new LevelEditorSceneInitializer(), editorInfo.lastScene);
        //MainWindow.changeScene(new LevelEditorSceneInitializer(), "");
    }

    private void loadLastScene()
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String inFile = "";
        try
        {
            inFile = new String(Files.readAllBytes(Paths.get("editorCfg.json")));
        } catch(IOException e)
        {
            e.printStackTrace();
        }

        if(!inFile.equals(""))
        {
            editorInfo = gson.fromJson(inFile, EditorInfo.class);
        }
        else
        {
            // TODO: загрузка сцены, если нет файла конфигурации
            editorInfo = new EditorInfo("level.json");
        }
    }

    //connects tracker of mouse events
    private void ConnectMouseAndKbd()
    {
        //registers position of the cursor
        GLFW.glfwSetCursorPosCallback(_windowId, Mouse::mousePosCallback); //https://www.glfw.org/docs/3.3/input_guide.html#cursor_pos
        //registers key listener for mouse
        GLFW.glfwSetMouseButtonCallback(_windowId, Mouse::mouseButtonCallback); //https://www.glfw.org/docs/3.3/input_guide.html#input_mouse_button
        //registers scroll events of mouse
        GLFW.glfwSetScrollCallback(_windowId, Mouse::mouseScrollCallback); //https://www.glfw.org/docs/3.3/input_guide.html#scrolling
        //registers keyboard events
        GLFW.glfwSetKeyCallback(_windowId, Keyboard::keyCallback); //https://www.glfw.org/docs/3.3/input_guide.html#input_key
        glfwSetWindowSizeCallback(_windowId, (w, newWidth, newHeight) -> {
            MainWindow.setWidth(newWidth);
            MainWindow.setHeight(newHeight);
        });
    }

    //main loop of the application
    private void Loop()
    {
        double beginTime = glfwGetTime(); //the time when current frame was started
        double dt = -1.0; //the time between a start and an end of a frame

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        int frameCount = 0;
        double previousTime = beginTime;

        while(!GLFW.glfwWindowShouldClose(_windowId)) //while window shouldn't be closed
        {
            //businessLogic((float)dt);

            //Poll events
            GLFW.glfwPollEvents(); //Processes all pending events.

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            // Set the clear color
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            //Render pass 2. Render actual game
            DebugDraw.beginFrame(); // удаляет старые линии

            this.framebuffer.bind();
            //Sets the clear value for fixed-point and floating-point color buffers in RGBA mode
            Vector4f clearColor = currentScene.camera().clearColor;
            GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);

            //Sets portions of every pixel in a particular buffer to the same value
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            //if(dt >= 0)
           // {
                //DebugDraw.draw(); // рисует линию сетки
                Renderer.bindShader(defaultShader);

                businessLogic((float)dt);

/*                if (runtimePlaying) {
                    currentScene.update((float)dt);
                } else {
                    currentScene.editorUpdate((float) dt);
                }*/

                DebugDraw.drawGrid(getScene().camera()); // рисует линию сетки
                currentScene.render();
                DebugDraw.drawAnother(getScene().camera()); // рисует остальные дебажные линии
            //}
            this.framebuffer.unbind();

            this.imguiLayer.update((float)dt, currentScene);

            Keyboard.endFrame();

            //Swaps the front and back buffers of the specified window when rendering with OpenGL
            GLFW.glfwSwapBuffers(_windowId);
            Mouse.endFrame(); // это нужно делать в конце кадра, чтобы мышь забывала о скроле

            double endTime = glfwGetTime(); //the time when frame was ended

            frameCount++;

            if ( endTime - previousTime >= 1.0 )
            {
                //if we turned on v-sync, frame rate can't be more than Hz of your monitor
                System.out.println("FPS = " + frameCount);

                frameCount = 0;
                previousTime = endTime;
            }

            dt = endTime - beginTime;
            //System.out.println(dt);
            beginTime = endTime;
        }
    }

    private void businessLogic(float dt)
    {
        if (runtimePlaying && !_isReplayOn) {
            currentScene.update(dt);
            StoreReplayInfo(dt);
        } else if (!runtimePlaying) {
            currentScene.editorUpdate(dt);
        }
        else if(_isReplayOn)
        {
            currentScene.update(dt);
            DoReplayLogic(dt);
        }
    }

    private SyncClassesReplayList _allEventsData = new SyncClassesReplayList();
    private final float TimeOnOneEvent = 0.05f;
    private float _timeReplay = 0f;
    private final String ReplayDataName = "replayData.json";
    private void StoreReplayInfo(float dt)
    {
        _timeReplay += dt;
        if(_timeReplay >= TimeOnOneEvent)
        {
            _timeReplay = 0;
            SyncReplayClasses syncCl = CollectCurrentSyncData();
            _allEventsData.add(syncCl);
        }
    }

    private void SaveReplay()
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        //System.out.println(gson.toJson(syncCl));
        try
        {
            FileWriter writer = new FileWriter(ReplayDataName);
            writer.write(gson.toJson(_allEventsData));

            // сбрасываем данные
            _allEventsData = new SyncClassesReplayList();
            _timeReplay = 0;

            writer.close();
        } catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public static SyncReplayClasses CollectCurrentSyncData()
    {
        SyncReplayClasses syncCl = new SyncReplayClasses();
        List<GameObject> nL = GameObject.FindAllByComp(Tower.class);
        for(GameObject go : nL)
        {
            TowerReplayClass newTower = new TowerReplayClass();
            newTower.posX = go.stateInWorld.getPosition().x;
            newTower.posY = go.stateInWorld.getPosition().y;
            newTower.angle = go.stateInWorld.getRotation();
            syncCl.towerClasses.add(newTower);
        }

        List<GameObject> enemies = GameObject.FindAllByName("Enemy");
        for(GameObject go : enemies)
        {
            MonsterReplayClass newMonstr = new MonsterReplayClass();
            newMonstr.posX = go.stateInWorld.getPosition().x;
            newMonstr.posY = go.stateInWorld.getPosition().y;
            Monster m = go.getComponent(Monster.class);
            newMonstr.health = m.getHealthNow();
            newMonstr.numOfPointToMove = m.getNumOfPointsNow()-1;
            syncCl.monsterClasses.add(newMonstr);
        }

        List<GameObject> bullets = GameObject.FindAllByName("Bullet");
        for(GameObject go : bullets)
        {
            BulletReplayClass newBullet = new BulletReplayClass();
            newBullet.posX = go.stateInWorld.getPosition().x;
            newBullet.posY = go.stateInWorld.getPosition().y;
            syncCl.bulletClasses.add(newBullet);
        }

        return syncCl;
    }

    private SyncClassesReplayList _loadedInfoReplay = new SyncClassesReplayList();
    private int _idLastEvent = 0;
    // предзагружает данные, когда влючаем реплей
    private void LoadReplayData()
    {
        _idLastEvent = 0; // сбрасываем
        Gson gson = new GsonBuilder().create();
        try
        {
            String inFile = new String(Files.readAllBytes(Paths.get(ReplayDataName)));
            _loadedInfoReplay = gson.fromJson(inFile, SyncClassesReplayList.class);
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    // проигрывание реплея
    private void DoReplayLogic(float dt)
    {
        _timeReplay += dt;
        if(_timeReplay >= TimeOnOneEvent)
        {
            _timeReplay = 0;
            if(_idLastEvent > _loadedInfoReplay.size()-1)
            {
                _isReplayOn = true;
                EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
                MainWindow.getImguiLayer().getGameViewWindow().changePlayMode();
                return;
            }
            SyncReplayClasses sC = _loadedInfoReplay.get(_idLastEvent);
            _idLastEvent++;
            MakeAReplayStep(sC);
        }
    }
    // отображает событие в определенное время (replay stuff)
    private void MakeAReplayStep(SyncReplayClasses syncCl)
    {
        List<GameObject> allEnemy = GameObject.FindAllByName("Enemy");
        for(GameObject go : allEnemy)
        {
            go.destroy();
        }

        // sync для монстров
        Waves obToCreateMonster = GameObject.FindWithComp(Waves.class).getComponent(Waves.class);
        for(int i = 0; i<syncCl.monsterClasses.size(); i++)
        {
            MonsterReplayClass mC = syncCl.monsterClasses.get(i);
            obToCreateMonster.CreateMonsterSync(mC);
        }
        obToCreateMonster.setAlreadyMonsters(syncCl.monsterClasses.size());

        // sync для башен
        List<GameObject> towers = GameObject.FindAllByComp(Tower.class); // все башни
        for(GameObject go : towers)
        {
            go.destroy();
        }

        for(int i = 0; i<syncCl.towerClasses.size(); i++)
        {

            TowerReplayClass tC = syncCl.towerClasses.get(i);
            List<GameObject> allPlaces = GameObject.FindAllByComp(PlaceForTower.class);
            for(GameObject go : allPlaces)
            {
                if(go.stateInWorld.getPosition().equals(tC.posX, tC.posY))
                {
                    PlaceForTower pFT = go.getComponent(PlaceForTower.class);

                    pFT.AddTowerDefaultReplay(tC.angle);

                    break;
                }
            }
        }

        List<GameObject> bullets = GameObject.FindAllByName("Bullet");
        for(GameObject go : bullets)
        {
            go.destroy();
        }

        for(int i = 0; i<syncCl.bulletClasses.size(); i++)
        {
            BulletReplayClass bRC = syncCl.bulletClasses.get(i);
            Prefabs.addBulletWithoutGoalAndDamage(new Vector2f(bRC.posX, bRC.posY));
        }
    }

    public static int getWidth()
    {
        //return get().width;
        return 1920;
    }

    public static int getHeight()
    {
        //return get().heigth;
        return 1080;
    }

    public static void setWidth(int newWidth)
    {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight)
    {
        get().heigth = newHeight;
    }

    public static Framebuffer getFramebuffer()
    {
        return get().framebuffer;
    }

    public static float getTargetAspectRatio()
    {
        return 16.0f / 9.0f;
    }

    public static IMGuiLayer getImguiLayer()
    {
        return get().imguiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        switch(event.type)
        {
            case GameEngineStartPlay -> {
                MainWindow.getImguiLayer().getPropertiesWindow().clearSelected(); // это нужно, чтобы не сохранялось желтое выделение
                this.runtimePlaying = true;
                // TODO: сохранять в temp файлы
                currentScene.save(editorInfo.lastScene);
                currentScene.save(ReplaySceneName); // сохраняем как реплейную сцену
                // TODO: load current scene
                MainWindow.changeScene(new LevelSceneInitializer(), editorInfo.lastScene);
                currentScene.OnStartScene();
            }
            case GameEngineStopPlay -> {
                if(!_isReplayOn)
                    SaveReplay();
                _isReplayOn = false;
                this.runtimePlaying = false;
                // TODO: load current scene
                MainWindow.changeScene(new LevelEditorSceneInitializer(), editorInfo.lastScene);
            }
            case GameEngineReplay -> {
                _isReplayOn = true; // реплей проигрывается
                this.runtimePlaying = true;
                editorInfo.setLastScene(ReplaySceneName);
                MainWindow.changeScene(new LevelSceneInitializer(), editorInfo.lastScene);
                LoadReplayData();
            }
            case ResearchTree -> {
                editorInfo.setLastScene("researchTree.json");
                MainWindow.changeScene(new LevelEditorSceneInitializer(), "researchTree.json");
            }

            case LoadLevel1 -> {
                editorInfo.setLastScene("level.json");
                MainWindow.changeScene(new LevelEditorSceneInitializer(), "level.json");
            }
            case LoadLevel2 -> {
                editorInfo.setLastScene("level2.json");
                MainWindow.changeScene(new LevelEditorSceneInitializer(), "level2.json");
            }
            case LoadLevel3 -> {
                editorInfo.setLastScene("level3.json");
                MainWindow.changeScene(new LevelEditorSceneInitializer(), "level3.json");
            }
            case Multiplayer -> {
                editorInfo.setLastScene("Multiplayer.json");
                MainWindow.changeScene(new LevelEditorSceneInitializer(), "Multiplayer.json");
            }
            case SaveLevel -> {
                MainWindow.getImguiLayer().getPropertiesWindow().clearSelected(); // это нужно, чтобы не сохранялось желтое выделение
                currentScene.save(editorInfo.lastScene);
            }
        }
    }
}
package job;

import Util.AssetPool;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import renderer.*;
import scenes.LevelEditorSceneInitializer;
import scenes.Scene;
import scenes.SceneInitializer;

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

    private static MainWindow wnd = null; //we have only one instance of this class

    private static Scene currentScene;
    private float r,g,b,a;

    //it is prohibited to create instance of class outside this class (Singleton)
    private MainWindow()
    {
        this.width = 1920;
        this.heigth = 1080;
        this.title = "Tower defense";
        r = 23f/255f;
        g = 23f/255f;
        b = 23f/255f;
        //r = 1;
        //g = 1;
        //b = 1;
        a = 1;
        EventSystem.addObserver(this);
    }

    //method for switching scenes
    public static void changeScene(SceneInitializer sceneInitializer) {
        if (currentScene != null) {
            currentScene.destroy();
        }
        getImguiLayer().getPropertiesWindow().setActiveGameObject(null);
        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
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
        return get().currentScene;
    }

    public void Run()
    {
        System.out.println("Hello LWGJL " + Version.getVersion() + "!");

        Init();
        Loop();

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
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        this.imguiLayer = new IMGuiLayer(_windowId, pickingTexture);
        this.imguiLayer.initImGui();

        //just some interesting bullshit http://jmonkeyengine.ru/page/2/?author=0

        MainWindow.changeScene(new LevelEditorSceneInitializer());
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
            //Poll events
            GLFW.glfwPollEvents(); //Processes all pending events.

            // Render pass 1. Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            //Render pass 2. Render actual game
            DebugDraw.beginFrame();

            this.framebuffer.bind();
            //Sets the clear value for fixed-point and floating-point color buffers in RGBA mode
            GL11.glClearColor(r, g, b, a);

            //Sets portions of every pixel in a particular buffer to the same value
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            if(dt >= 0)
            {
                Renderer.bindShader(defaultShader);
                if (runtimePlaying) {
                    currentScene.update((float)dt);
                } else {
                    currentScene.editorUpdate((float) dt);
                }
                currentScene.render();
                DebugDraw.draw();
            }
            this.framebuffer.unbind();

            this.imguiLayer.update((float)dt, currentScene);
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
        switch (event.type) {
            case GameEngineStartPlay:
                this.runtimePlaying = true;
                currentScene.save();
                MainWindow.changeScene(new LevelEditorSceneInitializer());
                break;
            case GameEngineStopPlay:
                this.runtimePlaying = false;
                MainWindow.changeScene(new LevelEditorSceneInitializer());
                break;
            case LoadLevel:
                MainWindow.changeScene(new LevelEditorSceneInitializer());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }
}
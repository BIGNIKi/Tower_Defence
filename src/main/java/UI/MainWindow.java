package UI;

import Controls.Keyboard;
import Controls.Mouse;
import Core.*;
import UI.InGameGraphic.*;
import Util.AssetPool;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class MainWindow implements Observer {
    // private int width;
    // private int heigth;

    private final String title;

    private static MainWindow wnd = null; // we have only one instance of this class

    private PickingTexture pickingTexture;

    private long _windowId; // указатель на виндовое окно

    private IMGuiLayer imguiLayer;

    private Framebuffer framebuffer;

    public Shader defaultShader = null;
    public Shader pickingShader = null;

    private MainWindow()
    {
        this.title = "HuUnity 2022.1.1f1";
        EventSystem.addObserver(this);
    }

    /**
     * @return возвращает ссылку на синглетон данного класса
     */
    public static MainWindow get()
    {
        if(wnd == null)
        {
            wnd = new MainWindow();
        }
        return wnd;
    }

    public void init()
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        // All defaults states are described here https://javadoc.lwjgl.org/index.html?org/lwjgl/glfw/GLFWErrorCallback.html
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        // glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); //window will be maximized when created

        // Create the window
        _windowId = glfwCreateWindow(WindowSize.getWidth(), WindowSize.getHeight(), title, NULL, NULL);
        if ( _windowId == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        connectCallbacks();

        //Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(this._windowId); //we make the context current for this thread
        //some info about context in OpenGL https://dvsav.ru/opengl-3/

        //enable vsync
        GLFW.glfwSwapInterval(1); //info https://gamedev.ru/code/terms/VSync

        //Make the window visible
        GLFW.glfwShowWindow(_windowId); //Makes the specified window visible if it was previously hidden

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities(); //we do this so that the library functions use the context of the current (our window) to draw something

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // TODO: несоответсвие с оригиналом
        this.framebuffer = new Framebuffer(WindowSize.getWidth(), WindowSize.getHeight());
        this.pickingTexture = new PickingTexture(WindowSize.getWidth(), WindowSize.getHeight());
        // TODO: нужно делать изменяемое разрешение
        glViewport(0, 0, WindowSize.getWidth(), WindowSize.getHeight());

        this.imguiLayer = new IMGuiLayer(_windowId, pickingTexture);
        this.imguiLayer.initImGui();

        //just some interesting bullshit http://jmonkeyengine.ru/page/2/?author=0

        //loadLastScene();

        //MainWindow.changeScene(new LevelEditorSceneInitializer(), editorInfo.lastScene);
    }

    /**
     * включает прерывания на изменения в системе
     * Прерывания:
     * 1) движение ммыши
     * 2) нажатия клавиш мыши
     * 3) scroll events для мышки
     * 4) нажатие клавиш клавиатуры
     * 5) изменение размера виндового окна
     */
    private void connectCallbacks()
    {
        //registers position of the cursor
        GLFW.glfwSetCursorPosCallback(_windowId, Mouse::mousePosCallback); //https://www.glfw.org/docs/3.3/input_guide.html#cursor_pos
        //registers key listener for mouse
        GLFW.glfwSetMouseButtonCallback(_windowId, Mouse::mouseButtonCallback); //https://www.glfw.org/docs/3.3/input_guide.html#input_mouse_button
        //registers scroll events of mouse
        GLFW.glfwSetScrollCallback(_windowId, Mouse::mouseScrollCallback); //https://www.glfw.org/docs/3.3/input_guide.html#scrolling
        //registers keyboard events
        GLFW.glfwSetKeyCallback(_windowId, Keyboard::keyCallback); //https://www.glfw.org/docs/3.3/input_guide.html#input_key
/*        glfwSetWindowSizeCallback(_windowId, (w, newWidth, newHeight) -> {
            MainWindow.setWidth(newWidth);
            MainWindow.setHeight(newHeight);
        });*/

        glfwSetWindowSizeCallback(_windowId, WindowSize::windowSizeCallback);
    }

/*    public static void setWidth(int newWidth)
    {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight)
    {
        get().heigth = newHeight;
    }*/

    /**
     * Возвращаем ресурсы операционной системе
     */
    public void closeWnd()
    {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(_windowId);
        glfwDestroyWindow(_windowId);

        _windowId = NULL;

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public long get_windowId() {
        return _windowId;
    }

    /**
     * Отображение кадра на экран
     */
    public void frameStep1()
    {
        // Poll for window events. The key callbacks will only be
        // invoked during this call.
        glfwPollEvents();

        // TODO: несоответсвие с оригиналом
        // Render pass 1. Render to picking texture
        glDisable(GL_BLEND);
        pickingTexture.enableWriting();

        glViewport(0, 0, WindowSize.getWidth(), WindowSize.getHeight());
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Run the rendering loop until the user has attempted to close the window.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        Renderer.bindShader(pickingShader);
        MainCycle.getScene().render();

        pickingTexture.disableWriting();
        glEnable(GL_BLEND);

        //Render pass 2. Render actual game
        DebugDraw.beginFrame(); // удаляет старые линии

        this.framebuffer.bind();
        //Sets the clear value for fixed-point and floating-point color buffers in RGBA mode
        Vector4f clearColor = MainCycle.getScene().camera().clearColor;
        GL11.glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);

        //Sets portions of every pixel in a particular buffer to the same value
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        Renderer.bindShader(defaultShader);
    }

    public void frameStep2()
    {
        this.framebuffer.unbind();

        this.imguiLayer.updateFrame();

        Keyboard.endFrame();
        Mouse.endFrame();
    }

    public void frameStep3()
    {
        GLFW.glfwSwapBuffers(_windowId);
    }

    public static IMGuiLayer getImguiLayer()
    {
        return get().imguiLayer;
    }

    @Override
    public void onNotify(GameObject object, Event event) {
        //TODO доделать Notify
/*        switch(event.type)
        {
            case GameEngineStartPlay -> {
                getImguiLayer().getPropertiesWindow().clearSelected(); // это нужно, чтобы не сохранялось желтое выделение
                this.runtimePlaying = true;
                // TODO: сохранять в temp файлы
                currentScene.save(editorInfo.lastScene);
                // TODO: load current scene
                changeScene(new LevelSceneInitializer(), editorInfo.lastScene);
            }
            case GameEngineStopPlay -> {
                this.runtimePlaying = false;
                // TODO: load current scene
                changeScene(new LevelEditorSceneInitializer(), editorInfo.lastScene);
            }
            case ResearchTree -> {
                editorInfo.setLastScene("researchTree.json");
                changeScene(new LevelEditorSceneInitializer(), "researchTree.json");
            }

            case LoadLevel1 -> {
                editorInfo.setLastScene("level.json");
                changeScene(new LevelEditorSceneInitializer(), "level.json");
            }
            case LoadLevel2 -> {
                editorInfo.setLastScene("level2.json");
                changeScene(new LevelEditorSceneInitializer(), "level2.json");
            }
            case LoadLevel3 -> {
                editorInfo.setLastScene("level3.json");
                changeScene(new LevelEditorSceneInitializer(), "level3.json");
            }
            case SaveLevel -> {
                getImguiLayer().getPropertiesWindow().clearSelected(); // это нужно, чтобы не сохранялось желтое выделение
                currentScene.save(editorInfo.lastScene);
            }
        }*/
    }

    public static Framebuffer getFramebuffer()
    {
        return get().framebuffer;
    }
}

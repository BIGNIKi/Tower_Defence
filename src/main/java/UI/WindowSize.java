package UI;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class WindowSize {
    // TODO: можно сделать запоминание размера экрана при выходе и сделать загрузку при входе
    private static int width = 3000;
    private static int heigth = 3000;

    public static void init(long _windowId)
    {
        int[] winWidth = new int[1], winHeight = new int[1];
        glfwGetWindowSize(_windowId, winWidth, winHeight);
        width = winWidth[0];
        heigth = winHeight[0];
    }

    // TODO: лаги во время изменения размера экрана
    public static void windowSizeCallback(long w, int newWidth, int newHeight)
    {
        width = newWidth;
        heigth = newHeight;

        MainWindow.getFramebuffer().resizeBuffer(width, heigth);
        MainWindow.getPickingtexture().reinit(width, heigth);
        //glViewport(0, 0, width, heigth); // вроде не нужна
    }

    public static int getHeight() {
        return heigth;
    }

    public static int getWidth() {
        return width;
    }

    public static float getTargetAspectRatio()
    {
        // подогнанные значения для окна игры
        return 16.0f / 9.0f;
    }
}

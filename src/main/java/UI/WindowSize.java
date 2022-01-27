package UI;

public class WindowSize {
    // TODO: сделать запоминание размера экрана
    private static int width = 1920;
    private static int heigth = 1080;

    public static void windowSizeCallback(long w, int newWidth, int newHeight)
    {
        width = newWidth;
        heigth = newHeight;
    }

    public static int getHeight() {
        return heigth;
    }

    public static int getWidth() {
        return width;
    }

    public static float getTargetAspectRatio()
    {
        // TODO: покумекать с отношениями сторон
        return 16.0f / 9.0f;
    }
}

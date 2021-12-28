package engine.controls;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

//all bullshit connected with I operation here https://www.glfw.org/docs/3.3/input_guide.html

public final class Keyboard
{
    private static Keyboard kbd;
    private final boolean[] keyPressed = new boolean[350];
    private boolean keyBeginPress[] = new boolean[350];

    private Keyboard()
    {

    }

    public static void endFrame() {
        Arrays.fill(get().keyBeginPress, false);
    }

    public static Keyboard get()
    {
        if(Keyboard.kbd == null)
        {
            Keyboard.kbd = new Keyboard();
        }
        return Keyboard.kbd;
    }

    public static void keyCallback(long wnd, int key, int scancode, int action, int mods)
    {
        if(action == GLFW.GLFW_PRESS) //the key or button was pressed
        {
            get().keyPressed[key] = true;
            get().keyBeginPress[key] = true;
        }
        else if(action == GLFW.GLFW_RELEASE)
        {
            get().keyPressed[key] = false;
            get().keyBeginPress[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return get().keyPressed[keyCode];
    }

    // эта штука возвращает true только в первый кадр, когда кнопка была нажата
    // если продолжать удерживать кнопку, то во все последующие кадры будет возвращать false
    public static boolean keyBeginPress(int keyCode) {
        return get().keyBeginPress[keyCode];
    }
}

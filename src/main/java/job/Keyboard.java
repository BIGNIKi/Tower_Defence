package job;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

//all bullshit connected with IO operation here https://www.glfw.org/docs/3.3/input_guide.html

import org.lwjgl.glfw.GLFW;

public final class Keyboard
{
    private static Keyboard kbd;
    private final boolean[] keyPressed = new boolean[350];

    private Keyboard()
    {

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
        if(action == GLFW_PRESS) //the key or button was pressed
        {
            get().keyPressed[key] = true;
        }
        else if(action == GLFW_RELEASE)
        {
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return get().keyPressed[keyCode];
    }
}

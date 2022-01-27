package Controls;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

//all bullshit connected with IO operations are here https://www.glfw.org/docs/3.3/input_guide.html

public final class Keyboard {
    private static Keyboard kbd; //we have only one instance of this class

    private final boolean[] keyPressed = new boolean[350]; // клавиша будет true всегда, пока она нажата
    private final boolean[] keyBeginPress = new boolean[350]; // клавиша будет true только в первый кадр нажатия на неё

    private Keyboard()
    {

    }

    public boolean[] getKeyPressed() {
        return keyPressed;
    }

    public boolean[] getKeyBeginPress() {
        return keyBeginPress;
    }

    /**
     * Вызывается как прерывание от винды при нажатии клавиш на клавиатуре
     * @param wnd указатель на окно
     * @param key id кнопки
     * @param scancode уникальные сканкоды клавиш
     * @param action PRESS or REPEAT or RELEASE
     * @param mods модификатор
     */
    public static void keyCallback(long wnd, int key, int scancode, int action, int mods)
    {
        if(action == GLFW_PRESS) // нажали кнопку (именно нажали, а не держим)
        {
            get().keyPressed[key] = true;
            get().keyBeginPress[key] = true;
        }
        else if(action == GLFW_RELEASE) // отпустили кнопку
        {
            get().keyPressed[key] = false;
            get().keyBeginPress[key] = false;
        }
    }

    /**
     * @return возвращает ссылку на синглетон данного класса
     */
    public static Keyboard get()
    {
        if(Keyboard.kbd == null)
        {
            Keyboard.kbd = new Keyboard();
        }
        return Keyboard.kbd;
    }

    /**
     * Call it when frame ended
     */
    public static void endFrame() {
        Arrays.fill(get().keyBeginPress, false);
    }
}

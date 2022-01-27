package Controls;

import org.joml.Vector2f;

public class Input {
    /**
     * @return Нажата ли какая-либо клавиша в текущий момент?
     */
    public static boolean anyKey()
    {
        final boolean[] keyPressed = Keyboard.get().getKeyPressed();
        for(boolean isKey : keyPressed)
        {
            if(isKey)
            {
                return true;
            }
        }

        final boolean[] keyMousePressed = Mouse.get().getMouseButtonPressed();
        for(boolean isKey : keyMousePressed)
        {
            if(isKey)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return Возвращает true, когда любая клавиша (клавиатуры или мыши) нажата в этот конкретный кадр
     */
    public static boolean anyKeyDown()
    {
        final boolean[] keyPressed = Keyboard.get().getKeyBeginPress();
        for(boolean isKey : keyPressed)
        {
            if(isKey)
            {
                return true;
            }
        }

        final boolean[] keyMousePressed = Mouse.get().getMouseButtonBeginPress();
        for(boolean isKey : keyMousePressed)
        {
            if(isKey)
            {
                return true;
            }
        }

        return false;
    }


    // TODO: проверить роботоспособность, будет работать, когда будем звать setGameViewportPos
    /**
     * @return текущее положение мыши в пикселях (внутри игрового окна (не окна операционной системы, а прям игрового))
     */
    public static Vector2f getMousePosition()
    {
        return Mouse.getScreen();
    }
}

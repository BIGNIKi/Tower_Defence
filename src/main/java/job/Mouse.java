package job;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

//all bullshit connected with I operation here https://www.glfw.org/docs/3.3/input_guide.html

public final class Mouse
{
    private static Mouse mouse; //we have only one instance of this class
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY, worldX, worldY, lastWorldX, lastWorldY;
    private final boolean[] mouseButtonPressed = new boolean[9];
    private boolean isDragging;

    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    //it is prohibited to create instance of class outside this class (Singleton)
    private Mouse()
    {
        scrollX = 0;
        scrollY = 0;
        xPos = 0;
        yPos = 0;
        lastX = 0;
        lastY = 0;
    }

    public static Mouse get()
    {
        if(Mouse.mouse == null)
        {
            Mouse.mouse = new Mouse();
        }

        return Mouse.mouse;
    }

    //tracks position of mouse
    public static void mousePosCallback(long wnd, double xPos, double yPos)
    {
        if(get().mouseButtonDown > 0)
        {
            get().isDragging = true;
        }

        //remember old position values
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        //set actual coordinates
        get().xPos = xPos;
        get().yPos = yPos;
        calcOrthoX();
        calcOrthoY();
    }

    public static void mouseButtonCallback(long wnd, int button, int action, int mods)
    {
        if(action == GLFW_PRESS) //the key or button was pressed
        {
            get().mouseButtonDown++;

            if(button < get().mouseButtonPressed.length)
            {
                get().mouseButtonPressed[button] = true;
            }
        }
        else if(action == GLFW_RELEASE) //the key or button was released
        {
            get().mouseButtonDown--;

            if(button < get().mouseButtonPressed.length)
            {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long wnd, double xOffset, double yOffset)
    {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame()
    {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
    }

    public static float getX()
    {
        return (float)get().xPos;
    }

    public static float getY()
    {
        return (float)get().yPos;
    }

    public static float getDx()
    {
        return (float)(get().xPos-get().lastX);
    }

    public static float getWorldDx()
    {
        return (float)(get().lastWorldX-get().worldX);
    }

    public static float getDy()
    {
        return (float)(get().yPos-get().lastY);
    }

    public static float getWorldDy()
    {
        return (float)(get().lastWorldY-get().worldY);
    }

    public static float getScrollX()
    {
        return (float)(get().scrollX);
    }

    public static float getScrollY()
    {
        return (float)(get().scrollY);
    }

    public static boolean isDragging()
    {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button)
    {
        if(button < get().mouseButtonPressed.length)
        {
            return get().mouseButtonPressed[button];
        }
        else
        {
            return false;
        }
    }

    public static float getScreenX()
    {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 1920.0f;

        return currentX;
    }

    public static float getScreenY()
    {
        float currentY = getY() - get().gameViewportPos.y;
        currentY = 1080.0f - ((currentY / get().gameViewportSize.y) * 1080.0f);

        return currentY;
    }

    public static float getOrthoX()
    {
        return (float)get().worldX;
    }

    private static void calcOrthoX()
    {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Camera camera = MainWindow.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseVeiw().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);

        get().worldX = tmp.x;
    }

    private static void calcOrthoY()
    {
        float currentY = getY() - get().gameViewportPos.y;
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);

        Camera camera = MainWindow.getScene().camera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseVeiw().mul(camera.getInverseProjection(), viewProjection);
        tmp.mul(viewProjection);

        get().worldY = tmp.y;
    }

    public static float getOrthoY()
    {
        return (float)get().worldY;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos)
    {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize)
    {
        get().gameViewportSize.set(gameViewportSize);
    }
}

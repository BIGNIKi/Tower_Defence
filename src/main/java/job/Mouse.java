package job;

import com.sun.tools.javac.Main;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

//all bullshit connected with I operation here https://www.glfw.org/docs/3.3/input_guide.html

public final class Mouse
{
    private static Mouse mouse; //we have only one instance of this class
    private double scrollX, scrollY;
    private double xPos, yPos, worldX, lastY, lastX, worldY, lastWorldX, lastWorldY;
    private final boolean[] mouseButtonPressed = new boolean[9];
    private boolean isDragging;

    private int mouseButtonDown = 0;

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    //it is prohibited to create instance of class outside this class (Singleton)
    private Mouse()
    {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static Mouse get()
    {
        if(Mouse.mouse == null)
        {
            Mouse.mouse = new Mouse();
        }

        return Mouse.mouse;
    }

    public static void clear()
    {
        get().scrollX = 0;
        get().scrollY = 0;
        get().xPos = 0;
        get().yPos = 0;
        get().lastX = 0.0;
        get().lastY = 0.0;
        get().mouseButtonDown = 0;
        get().isDragging = false;
        Arrays.fill(get().mouseButtonPressed, false);
    }

    //tracks position of mouse
    public static void mousePosCallback(long wnd, double xPos, double yPos)
    {
        if(!MainWindow.getImguiLayer().getGameViewWindow().getWantCaptureMouse())
        {
            clear();
        }

        if(get().mouseButtonDown > 0)
        {
            get().isDragging = true;
        }

        //set actual coordinates
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().xPos = xPos;
        get().yPos = yPos;
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
    }

    public static float getX()
    {
        return (float)get().xPos;
    }

    public static float getY()
    {
        return (float)get().yPos;
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

    public static Vector2f screenToWorld(Vector2f screenCords)
    {
        Vector2f normalizesScreenCords = new Vector2f(
                screenCords.x / MainWindow.getWidth(),
                screenCords.y / MainWindow.getHeight()
        );
        normalizesScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        // сейчас имеем кординаты в разбросе [-1, 1] именно так к ним можно обратиться через видеокарту
        Camera camera = MainWindow.getScene().camera();
        Vector4f tmp = new Vector4f(normalizesScreenCords.x, normalizesScreenCords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseVeiw());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f worldToScreen(Vector2f worldCoords)
    {
        Camera camera = MainWindow.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(MainWindow.getWidth(), MainWindow.getHeight()));

        return windowSpace;
    }

    public static float getScreenX()
    {
        return getScreen().x;
    }

    public static float getScreenY()
    {
        return getScreen().y;
    }

    public static Vector2f getScreen()
    {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 1920.0f;
        float currentY = getY() - get().gameViewportPos.y;
        currentY = 1080.0f - ((currentY / get().gameViewportSize.y) * 1080.0f);

        return new Vector2f(currentX, currentY);
    }

    public static float getWorldX()
    {
        return getWorld().x;
    }

    // возвращает на какаую координату (в игровом мире) была наведена мышь
    public static Vector2f getWorld()
    {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        float currentY = getY() - get().gameViewportPos.y;
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = MainWindow.getScene().camera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseVeiw());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));

        return new Vector2f(tmp.x, tmp.y);
    }

    public static float getWorldY()
    {
        return getWorld().y;
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
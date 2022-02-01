package Controls;

import Core.Camera;
import Core.MainCycle;
import UI.MainWindow;
import UI.WindowSize;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public final class Mouse {
    private static Mouse mouse; // we have only one instance of this class

    private double scrollX, scrollY;
    private double xPos, yPos;
    private double deltaWorldX, deltaWorldY;
    private double lastWorldX, lastWorldY;
    // private double deltaWorldX, deltaWorldY;

    private int mouseButtonDown = 0; // сколько клавиш мыши в текущий момент нажато

    private final boolean[] mouseButtonPressed = new boolean[9]; // состояние кнопок мыши (нажата, отжата)
    private final boolean[] mouseButtonBeginPress = new boolean[9]; // клавиша будет true только в первый кадр нажатия на неё

    private boolean isDragging; // зажата ли последняя нажатая клавиша (типо тянем кнопкой мыши что-то)

    private Vector2f gameViewportPos = new Vector2f();
    private Vector2f gameViewportSize = new Vector2f();

    public boolean[] getMouseButtonPressed() {
        return mouseButtonPressed;
    }

    public boolean[] getMouseButtonBeginPress() {
        return mouseButtonBeginPress;
    }

    /**
     * Вызывается как прерывание от винды при движении мышкой
     * @param wnd указатель на окно
     * @param xPos позиция x
     * @param yPos позиция y
     */
    public static void mousePosCallback(long wnd, double xPos, double yPos)
    {
        // TODO: несоответсвие с оригиналом
/*        if(!MainWindow.getImguiLayer().getGameViewWindow().getWantCaptureMouse())
        {
            clear();
        }*/

/*        if(get().mouseButtonDown > 0)
        {
            get().isDragging = true;
        }*/

        //set actual coordinates
        //get().lastX = get().xPos;
        //get().lastY = get().yPos;
        get().deltaWorldX = getWorldX() - get().lastWorldX;
        get().deltaWorldY = getWorldY() - get().lastWorldY;
        get().lastWorldX = getWorldX();
        get().lastWorldY = getWorldY();
        get().xPos = xPos;
        get().yPos = yPos;
    }

    /**
     * Вызывается как прерывание от винды при нажатии на клавиши мыши
     * @param wnd указатель на окно
     * @param button id кнопки
     * @param action событие - нажата / отпущена
     * @param mods модификация, используется для отслеживания сочетаний клавиш
     */
    public static void mouseButtonCallback(long wnd, int button, int action, int mods)
    {
        if(action == GLFW_PRESS) //the key or button was pressed
        {
            get().mouseButtonDown++;

            if(button < get().mouseButtonPressed.length)
            {
                get().mouseButtonPressed[button] = true;
                get().mouseButtonBeginPress[button] = true;
            }
        }
        else if(action == GLFW_RELEASE) //the key or button was released
        {
            get().mouseButtonDown--;

            if(button < get().mouseButtonPressed.length)
            {
                get().mouseButtonPressed[button] = false;
                get().mouseButtonBeginPress[button] = false;
                get().isDragging = false;
            }
        }
    }

    /**
     * Вызывается как прерывание от винды при листании колесиком мыши или на тачпаде
     * @param wnd указатель на окно
     * @param xOffset смещение по x
     * @param yOffset смещение по y
     */
    public static void mouseScrollCallback(long wnd, double xOffset, double yOffset)
    {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static Mouse get()
    {
        if(Mouse.mouse == null)
        {
            Mouse.mouse = new Mouse();
        }

        return Mouse.mouse;
    }

    public static void endFrame()
    {
        // TODO: несоответсвие с оригиналом

        get().scrollX = 0;
        get().scrollY = 0;
        get().deltaWorldX = 0;
        get().deltaWorldY = 0;
        Arrays.fill(get().mouseButtonBeginPress, false);
    }

    /**
     * @return возвращает координаты мыши внутри окна
     */
    public static Vector2f getMouseCoords()
    {
        return new Vector2f((float) get().xPos, (float) get().yPos);
    }

    public static Vector2f getScreen()
    {
        // TODO: протестить, точно ли работает
        float currentX = getMouseCoords().x - get().gameViewportPos.x;
        // currentX = (currentX / get().gameViewportSize.x) * 1920.0f;
        currentX = (currentX / get().gameViewportSize.x) * WindowSize.getWidth();
        float currentY = getMouseCoords().y - get().gameViewportPos.y;
        // currentY = 1080.0f - ((currentY / get().gameViewportSize.y) * 1080.0f);
        currentY = WindowSize.getHeight() - ((currentY / get().gameViewportSize.y) * WindowSize.getHeight());

        return new Vector2f(currentX, currentY);
    }

    public static void setGameViewportPos(Vector2f gameViewportPos)
    {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize)
    {
        get().gameViewportSize.set(gameViewportSize);
    }

    public static void clear()
    {
        get().scrollX = 0;
        get().scrollY = 0;
        get().xPos = 0;
        get().yPos = 0;
        //get().lastX = 0.0;
        //get().lastY = 0.0;
        get().deltaWorldX = 0.0;
        get().deltaWorldY = 0.0;
        get().mouseButtonDown = 0;
        get().isDragging = false;
        Arrays.fill(get().mouseButtonPressed, false);
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
        return getScreen().x;
    }

    public static float getScreenY()
    {
        return getScreen().y;
    }

    public static float getWorldX()
    {
        return getWorld().x;
    }

    public static float getWorldY()
    {
        return getWorld().y;
    }

    // возвращает на какаую координату (в игровом мире) была наведена мышь
    public static Vector2f getWorld()
    {
        float currentX = getX() - get().gameViewportPos.x;
        currentX = (currentX / get().gameViewportSize.x) * 2.0f - 1.0f;
        float currentY = getY() - get().gameViewportPos.y;
        currentY = -((currentY / get().gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = MainCycle.getScene().camera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseVeiw());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));

        return new Vector2f(tmp.x, tmp.y);
    }

    public static float getX()
    {
        return (float)get().xPos;
    }

    public static float getY()
    {
        return (float)get().yPos;
    }

    public static boolean isDragging()
    {
        return get().isDragging;
    }

    public static float getDeltaWorldX()
    {
        return (float)get().deltaWorldX;
    }

    public static float getDeltaWorldY()
    {
        return (float)get().deltaWorldY;
    }

    public static Vector2f screenToWorld(Vector2f screenCords)
    {
        Vector2f normalizesScreenCords = new Vector2f(
                screenCords.x / MainWindow.getWidth(),
                screenCords.y / MainWindow.getHeight()
        );
        normalizesScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        // сейчас имеем кординаты в разбросе [-1, 1] именно так к ним можно обратиться через видеокарту
        Camera camera = MainCycle.getScene().camera();
        Vector4f tmp = new Vector4f(normalizesScreenCords.x, normalizesScreenCords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseVeiw());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f worldToScreen(Vector2f worldCoords)
    {
        Camera camera = MainCycle.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(MainWindow.getWidth(), MainWindow.getHeight()));

        return windowSpace;
    }

    public static float getScrollX()
    {
        return (float)(get().scrollX);
    }

    public static float getScrollY()
    {
        return (float)(get().scrollY);
    }
}

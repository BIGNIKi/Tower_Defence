package entities.components;

import Util.Settings;
import editor.PropertiesWindow;
import entities.job.GameObject;
import entities.job.Keyboard;
import entities.job.MainWindow;
import entities.job.Mouse;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import entities.scenes.Scene;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

// отвечает за захват, размещение игрового объекта
public class MouseControls extends Component
{
    GameObject holdingObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();

    // захват объекта
    public void pickupObject(GameObject go)
    {
        // это нужно, чтобы когда мы уже выбрали объект, который хотим поставить, но
        // внезапно передумали и выбрали другой - старый удалился
        if(this.holdingObject != null)
        {
            this.holdingObject.destroy();
        }
        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        MainWindow.getScene().addGameObjectToScene(go);
    }

    public void place()
    {
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,1));
        this.holdingObject = null;
    }

    // если есть захваченный объект, то он тянется за мышкой (также можно разместить его на экране)
    @Override
    public void editorUpdate(float dt)
    {
        debounce -= dt;
        PickingTexture pickingTexture = MainWindow.getImguiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = MainWindow.getScene();

        if(holdingObject != null)
        {
            holdingObject.stateInWorld.setPosition(new Vector2f(Mouse.getWorldX(), Mouse.getWorldY()));
            float xx = ((int)Math.floor(holdingObject.stateInWorld.getPosition().x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            float yy = ((int)Math.floor(holdingObject.stateInWorld.getPosition().y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;
            holdingObject.stateInWorld.setPosition(new Vector2f(xx, yy));

            if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                float halfWidth = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                if (Mouse.isDragging() &&
                        !blockInSquare(holdingObject.stateInWorld.getPosition().x - halfWidth,
                                holdingObject.stateInWorld.getPosition().y - halfHeight)) {
                    place();
                } else if (!Mouse.isDragging() && debounce < 0) {
                    place();
                    debounce = debounceTime;
                }
            }

            // отмена создания нового игрового объекта
            if(Keyboard.isKeyPressed(GLFW_KEY_ESCAPE))
            {
                holdingObject.destroy();
                holdingObject = null;
            }
        }
        else if(!Mouse.isDragging() && Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0)
        {
            int x = (int)Mouse.getScreenX();
            int y = (int)Mouse.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if(pickedObj != null && pickedObj.getComponent(NonPickable.class) == null)
            {
                MainWindow.getImguiLayer().getPropertiesWindow().setActiveGameObject(pickedObj);
            }
            else if(pickedObj == null && !Mouse.isDragging())
            {
                MainWindow.getImguiLayer().getPropertiesWindow().clearSelected();
            }
            this.debounce = 0.2f;
        }
        else if(Mouse.isDragging() && Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)
                && !gameObject.getComponent(GizmoSystem.class).checkHoverity())
        {
            if(!boxSelectSet)
            {
                MainWindow.getImguiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = Mouse.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = Mouse.getScreen();
            Vector2f boxSelectStartWorld = Mouse.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = Mouse.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D(
                    (new Vector2f(boxSelectStartWorld)).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f),
                    0.0f);
/*            DebugDraw.addBox2D(
                    new Vector2f(boxSelectStartWorld),
                    new Vector2f(boxSelectEndWorld),
                    0.0f);*/
        }
        else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectIds = pickingTexture.readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectIds) {
                uniqueGameObjectIds.add((int)objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObj = MainWindow.getScene().getGameObject(gameObjectId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    MainWindow.getImguiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }

    private boolean blockInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = MainWindow.getImguiLayer().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = Mouse.worldToScreen(start);
        Vector2f endScreenf = Mouse.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
        float[] gameObjectIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < gameObjectIds.length; i++) {
            if (gameObjectIds[i] >= 0) {
                GameObject pickedObj = MainWindow.getScene().getGameObject((int)gameObjectIds[i]);
                if (pickedObj.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }

        return false;
    }
}
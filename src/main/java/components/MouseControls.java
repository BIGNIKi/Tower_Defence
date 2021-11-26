package components;

import Util.Settings;
import job.GameObject;
import job.Keyboard;
import job.MainWindow;
import job.Mouse;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import renderer.DebugDraw;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

// отвечает за захват, размещение игрового объекта
public class MouseControls extends Component
{
    GameObject holdingObject = null;
    private float debounceTime = 0.05f;
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

        if(holdingObject != null && debounce <= 0.0f)
        {
            holdingObject.transform.position.x = Mouse.getWorldX();
            holdingObject.transform.position.y = Mouse.getWorldY();
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                place();
                debounce = debounceTime;
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
            System.out.println("aboba");
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
        else if(Mouse.isDragging() && Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
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
}
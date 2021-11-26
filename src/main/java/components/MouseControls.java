package components;

import Util.Settings;
import job.GameObject;
import job.Keyboard;
import job.MainWindow;
import job.Mouse;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

// отвечает за захват, размещение игрового объекта
public class MouseControls extends Component
{
    GameObject holdingObject = null;

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
        if(holdingObject != null)
        {
            holdingObject.transform.position.x = Mouse.getWorldX();
            holdingObject.transform.position.y = Mouse.getWorldY();
            holdingObject.transform.position.x = ((int)Math.floor(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingObject.transform.position.y = ((int)Math.floor(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                place();
            }

            // отмена создания нового игрового объекта
            if(Keyboard.isKeyPressed(GLFW_KEY_ESCAPE))
            {
                holdingObject.destroy();
                holdingObject = null;
            }
        }
    }
}
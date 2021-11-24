package components;

import Util.Settings;
import job.GameObject;
import job.MainWindow;
import job.Mouse;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

// отвечает за захват, размещение игрового объекта
public class MouseControls extends Component
{
    GameObject holdingObject = null;

    // захват объекта
    public void pickupObject(GameObject go)
    {
        this.holdingObject = go;
        MainWindow.getScene().addGameObjectToScene(go);
    }

    public void place()
    {
        this.holdingObject = null;
    }

    // если есть захваченный объект, то он тянется за мышкой (также можно разместить его на экране)
    @Override
    public void editorUpdate(float dt)
    {
        if(holdingObject != null)
        {
            holdingObject.transform.position.x = Mouse.getOrthoX();
            holdingObject.transform.position.y = Mouse.getOrthoY();
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                place();
            }
        }
    }
}
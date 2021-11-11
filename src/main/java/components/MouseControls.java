package components;

import job.GameObject;
import job.MainWindow;
import job.Mouse;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component
{
    GameObject holdingObject = null;

    public void pickupObject(GameObject go)
    {
        this.holdingObject = go;
        MainWindow.getScene().addGameObjectToScene(go);
    }

    public void place()
    {
        this.holdingObject = null;
    }

    @Override
    public void update(float dt)
    {
        if(holdingObject != null)
        {
            //TODO поиграться с ценрированием
            holdingObject.transform.position.x = Mouse.getOrthoX() - (float)holdingObject.getComponent(SpriteRenderer.class).getTexture().getWidth()/4;
            holdingObject.transform.position.y = Mouse.getOrthoY() - (float)holdingObject.getComponent(SpriteRenderer.class).getTexture().getHeight()/2;

            if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
            {
                place();
            }
        }
    }
}

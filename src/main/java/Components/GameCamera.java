package Components;

import Controls.Keyboard;
import Controls.Mouse;
import Core.*;
import UI.InGameGraphic.PickingTexture;
import UI.MainWindow;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class GameCamera extends Component
{
    private transient Camera gameCamera;
    private transient PickingTexture pickTxt = null;

    private transient float debounceTime = 0.2f;
    private transient float debounce = debounceTime;
    private transient GameObject selectedGO = null;

    public GameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;
    }

    @Override
    public void start()
    {
        pickTxt = MainWindow.getImguiLayer().getPropertiesWindow().getPickingTexture();
    }

    @Override
    public void update(float dt)
    {
        debounce -= dt;
        if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0)
        {
            int x = (int)Mouse.getScreenX();
            int y = (int)Mouse.getScreenY();
            int gameObjectId = pickTxt.readPixel(x,y);
            Scene currentScene = MainCycle.getScene();
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            // если нажали на место для постановки башни
            if(pickedObj != null && pickedObj.getComponent(NonPickable.class) == null)
            {

            }
            this.debounce = 0.2f;
        }

        if(Keyboard.isKeyPressed(GLFW_KEY_ESCAPE) && debounce < 0)
        {

            this.debounce = 0.2f;
        }

    }
}

package entities.components;

import entities.entities1.towers.PlaceForTower;
import entities.job.Camera;
import entities.job.GameObject;
import entities.textures.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class GameCamera extends Camera {

  private transient Camera gameCamera;
  private transient PickingTexture pickTxt = null;

  private transient float debounceTime = 0.2f;
  private transient float debounce = debounceTime;
  private transient GameObject selectedGO = null;

  public GameCamera(Camera gameCamera) {
    this.gameCamera = gameCamera;
  }

  @Override
  public void start() {
    pickTxt = MainWindow.getImguiLayer().getPropertiesWindow().getPickingTexture();
  }

  @Override
  public void update(float dt) {
    debounce -= dt;
    if (Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
      int x = (int) Mouse.getScreenX();
      int y = (int) Mouse.getScreenY();
      int gameObjectId = pickTxt.readPixel(x, y);
      Scene currentScene = MainWindow.getScene();
      GameObject pickedObj = currentScene.getGameObject(gameObjectId);
      // если нажали на место для постановки башни
      if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null
          && pickedObj.getComponent(PlaceForTower.class) != null) {
        if (selectedGO != null) {
          selectedGO.getComponent(PlaceForTower.class).resetSelected();
        }
        selectedGO = pickedObj;
        selectedGO.getComponent(PlaceForTower.class).setSelected();
      }
      this.debounce = 0.2f;
    }

    if (Keyboard.isKeyPressed(GLFW_KEY_ESCAPE) && debounce < 0) {
      if (selectedGO != null) {
        selectedGO.getComponent(PlaceForTower.class).resetSelected();
        selectedGO = null;
      }
      this.debounce = 0.2f;
    }

  }
}

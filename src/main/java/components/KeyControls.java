package components;

import Util.Settings;
import com.sun.tools.javac.Main;
import editor.PropertiesWindow;
import job.GameObject;
import job.Keyboard;
import job.MainWindow;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component
{
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;

    @Override
    public void editorUpdate(float dt)
    {
        debounce -= dt;

        PropertiesWindow propertiesWindow = MainWindow.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
        // можно нажать на шифт, чтобы двигать объекты с большей точностью
        float multiplier = Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                Keyboard.keyBeginPress(GLFW_KEY_D) && activeGameObject != null)
        {
            GameObject newObj = activeGameObject.copy();
            MainWindow.getScene().addGameObjectToScene(newObj);
            // newObj.transform.position.add(0.1f, 0.1f);
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
        }
        // множественное копирование объектов
        else if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
            Keyboard.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1)
        {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for(GameObject go : gameObjects)
            {
                GameObject copy = go.copy();
                MainWindow.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
            }
        }
        else if (Keyboard.keyBeginPress(GLFW_KEY_DELETE))
        {
            for(GameObject go : activeGameObjects)
            {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
        else if (Keyboard.isKeyPressed(GLFW_KEY_UP) && debounce < 0)
        {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects)
            {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        }
        else if (Keyboard.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0)
        {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects)
            {
                go.transform.position.x -= Settings.GRID_HEIGHT * multiplier;
            }
        }
        else if (Keyboard.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {

            debounce = debounceTime;
            for (GameObject go : activeGameObjects)
            {
                go.transform.position.x += Settings.GRID_HEIGHT * multiplier;
            }
        }
        else if (Keyboard.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0)
        {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects)
            {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        }
    }
}

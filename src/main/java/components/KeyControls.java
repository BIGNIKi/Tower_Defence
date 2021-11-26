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
    @Override
    public void editorUpdate(float dt)
    {
        PropertiesWindow propertiesWindow = MainWindow.getImguiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();

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
    }
}

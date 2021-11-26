package editor;

import components.NonPickable;
import components.TestComponent;
import components.TowerRotate;
import imgui.ImGui;
import job.GameObject;
import job.Mouse;
import renderer.PickingTexture;
import scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow
{
    private List<GameObject> activeGameObjects;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture)
    {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
    }

    public void imgui()
    {
        // когда объект выбран (один объект, не больше), выводим его компоненты
        if(activeGameObjects.size() == 1 && activeGameObjects.get(0) != null)
        {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add test component"))
                {
                    if(activeGameObject.getComponent(TestComponent.class) == null)
                    {
                        activeGameObject.addComponent(new TestComponent());
                    }
                }
                if (ImGui.menuItem("Add tower rotate"))
                {
                    if(activeGameObject.getComponent(TowerRotate.class) == null)
                    {
                        activeGameObject.addComponent(new TowerRotate());
                    }
                }
                ImGui.endPopup();
            }
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject()
    {
        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    public List<GameObject> getActiveGameObjects()
    {
        return this.activeGameObjects;
    }

    public void clearSelected()
    {
        this.activeGameObjects.clear();
    }

    public void setActiveGameObject(GameObject go) {
        if(go != null)
        {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    public void addActiveGameObject(GameObject go)
    {
        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture()
    {
        return this.pickingTexture;
    }
}
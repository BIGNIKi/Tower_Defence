package editor;

import components.EnemyAI;
import components.SpriteRenderer;
import components.TowerRotate;
import imgui.ImGui;
import job.GameObject;
import org.joml.Vector4f;
import renderer.PickingTexture;

import java.util.ArrayList;
import java.util.List;

public class PropertiesWindow
{
    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeGameObjectsOgColor;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture)
    {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectsOgColor = new ArrayList<>();
    }

    public void imgui()
    {
        // когда объект выбран (один объект, не больше), выводим его компоненты
        if(activeGameObjects.size() == 1 && activeGameObjects.get(0) != null)
        {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add EnemyAI"))
                {
                    if(activeGameObject.getComponent(EnemyAI.class) == null)
                    {
                        activeGameObject.addComponent(new EnemyAI());
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
        else
        {
            ImGui.begin("Properties");
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

    // сброс выделения объектов на сцене
    public void clearSelected()
    {
        if (activeGameObjectsOgColor.size() > 0) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {
                    spr.setColor(activeGameObjectsOgColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjectsOgColor.clear();
    }

    public void setActiveGameObject(GameObject go) {
        if(go != null)
        {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    // подсвечивает выделенный gameObject и помечает как "активный"
    public void addActiveGameObject(GameObject go)
    {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null ) {
            this.activeGameObjectsOgColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjectsOgColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture()
    {
        return this.pickingTexture;
    }
}
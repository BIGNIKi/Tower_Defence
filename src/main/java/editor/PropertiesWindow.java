package editor;

import components.Component;
import components.SpriteRenderer;
import entities.monsters.Monster;
import entities.towers.Tower;
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

    // все классы, которые можно добавить объекту
    private List<Class<? extends Component>> possibleClasses = new ArrayList<>();

    public PropertiesWindow(PickingTexture pickingTexture)
    {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectsOgColor = new ArrayList<>();

        // СЮДА нужно писать компоненты, которые хочешь добавить к объекту на сцене
        possibleClasses.add(Monster.class);
        possibleClasses.add(Tower.class);
    }

    public void imgui()
    {
        // когда объект выбран (один объект, не больше), выводим его компоненты
        if(activeGameObjects.size() == 1 && activeGameObjects.get(0) != null)
        {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                // добавляет всевозможные кнопки для добавления компонентов
                for(Class<? extends Component> c : possibleClasses)
                {
                    if(activeGameObject.getComponent(c) == null)
                    {
                        if(ImGui.menuItem("Add " + c.getSimpleName()))
                        {
                            try
                            {
                                Object ob = c.newInstance();
                                Component co = Component.class.cast(ob).getClass().newInstance();
                                activeGameObject.addComponent(co);
                            } catch(InstantiationException | IllegalAccessException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for(Component comp : activeGameObject.getAllComponents())
                {
                    if(comp.getClass().getSimpleName().equals("Transform"))
                    {
                        continue;
                    }
                    if(ImGui.menuItem("Remove " + comp.getClass().getSimpleName()))
                    {
                        // TODO: при удалении sprite render объект всё-равно рисуется
                        activeGameObject.removeComponent(comp.getClass());
                        break;
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
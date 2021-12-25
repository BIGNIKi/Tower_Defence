package entities.job;

import Util.AssetPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.components.Component;
import entities.components.ComponentDeserializer;
import entities.components.SpriteRenderer;
import imgui.ImGui;
import core.scenes.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameObject
{
    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    private List<Component> components;
    public transient StateInWorld stateInWorld;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public transient Scene currentScene;

    public GameObject(String name, Scene currentScene)
    {
        this.name = name;
        this.components = new ArrayList<>();

        this.uid = ID_COUNTER++;
        this.currentScene = currentScene;
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for(Component c : components)
        {
            if(componentClass.isAssignableFrom(c.getClass()))
            {
                try
                {
                    return componentClass.cast(c);
                }
                catch(ClassCastException e)
                {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass)
    {
        for(int i = 0; i<components.size(); i++)
        {
            Component c = components.get(i);
            if(componentClass.isAssignableFrom(c.getClass()))
            {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c)
    {
        c.generateId();
        this.components.add(c);
        c.gameObject = this; //all components have to have reference to a relevant gameObject
    }

    public void update(float dt)
    {
        for(int i = 0; i<components.size(); i++)
        {
            components.get(i).update(dt);
        }
    }

    public void editorUpdate(float dt) {
        for (int i=0; i < components.size(); i++) {
            components.get(i).editorUpdate(dt);
        }
    }

    public void start()
    {
        for(int i = 0; i<components.size(); i++)
        {
            components.get(i).start();
        }
    }

    public void destroy() {
        this.isDead = true;
        for (int i=0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    public GameObject copy() {
        // TODO: come up with cleaner solution
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer(currentScene))
                .create();
        String objAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);

        obj.generateUid();
        for (Component c : obj.getAllComponents()) {
            c.generateId();
        }

        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilePath()));
        }

        return obj;
    }

    public void imgui()
    {
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }

    public static void init(int maxId)
    {
        ID_COUNTER = maxId;
    }

    public int getUid()
    {
        return this.uid;
    }

    public List<Component> getAllComponents()
    {
        return this.components;
    }

    public void setNoSerialize()
    {
        this.doSerialization = false;
    }

    public void generateUid() {
        this.uid = ID_COUNTER++;
    }

    public boolean doSerialization()
    {
        return this.doSerialization;
    }

    public boolean isDead()
    {
        return this.isDead;
    }

    // найти объект по имени
    public static GameObject Find(String name)
    {
        Optional<GameObject> result = MainWindow.getScene().getGameObjects().stream()
                .filter(gameObject -> gameObject.name.equals(name))
                .findFirst();
        return result.orElse(null);
    }

    // получить список объектов со схожим именем
    public static List<GameObject> FindAllByName(String name)
    {
        return MainWindow.getScene().getGameObjects().stream()
                .filter(gameObject -> gameObject.name.equals(name))
                .collect(Collectors.toList());
    }

    public static <T extends Component> GameObject FindWithComp(Class<T> componentClass)
    {
        Optional<GameObject> result = MainWindow.getScene().getGameObjects().stream()
                .filter(gameObject -> gameObject.getComponent(componentClass) != null)
                .findFirst();
        return result.orElse(null);
    }

    public static <T extends Component> List<GameObject> FindAllByComp(Class<T> componentClass)
    {
        return MainWindow.getScene().getGameObjects().stream()
                .filter(gameObject -> gameObject.getComponent(componentClass) != null)
                .collect(Collectors.toList());
    }
}

package engine.builders;

import engine.components.Component;
import engine.components.GameObject;
import engine.components.StateInWorld;

import java.util.ArrayList;

public class GameObjectBuilder implements Builder {
    private String name;
    private Scene scene;
    private ArrayList<Component> components;

    public GameObjectBuilder() {
        name = "";
        components = new ArrayList<>();
        components.add(new StateInWorld());
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addComponent(Component component) {
        this.components.add(component);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public GameObject build(){
        return new GameObject(name, scene, components);
    }
}
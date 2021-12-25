package entities.job;

import com.google.gson.*;
import entities.components.Component;
import core.scenes.Scene;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject>
{
    private Scene currentScene;

    public GameObjectDeserializer(Scene currentScene)
    {
        this.currentScene = currentScene;
    }

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("entities/components");

        GameObject go = new GameObject(name, currentScene);
        for(JsonElement e : components)
        {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.stateInWorld = go.getComponent(StateInWorld.class);
        return go;
    }
}

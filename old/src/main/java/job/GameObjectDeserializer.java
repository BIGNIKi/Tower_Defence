package entities.job;

import com.google.gson.*;
import components.Component;
import scenes.Scene;

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
        var jsonObject = json.getAsJsonObject();
        var name = jsonObject.get("name").getAsString();
        var components = jsonObject.getAsJsonArray("components");

        var gameObject = new GameObject(name, currentScene);

        var builder = new GameObjectBuilder();
        for(var jsonedComponent : components)
        {
            var component = context.deserialize(jsonedComponent, Component.class);
            gameObject.addComponent(c);
        }
        return go;
    }
}

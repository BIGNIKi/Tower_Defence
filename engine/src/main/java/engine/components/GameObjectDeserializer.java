package engine.components;

import com.google.gson.*;
import engine.builders.GameObjectBuilder;

import java.lang.reflect.Type;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    private Scene currentScene;

    public GameObjectDeserializer(Scene currentScene) {
        this.currentScene = currentScene;
    }

    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var jsonObject = json.getAsJsonObject();
        var name = jsonObject.get("name").getAsString();
        var jsonComponents = jsonObject.getAsJsonArray("entities/components");

        var builder = new GameObjectBuilder();
        builder.setName(name);
        builder.setScene(currentScene);
        for (var jsonComponent : jsonComponents) {
            var component = (Component) context.deserialize(jsonComponent, Component.class);
            builder.addComponent(component);
        }

        return builder.build();
    }
}

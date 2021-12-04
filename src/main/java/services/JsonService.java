package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import job.GameObject;
import job.GameObjectDeserializer;
import scenes.Scene;

public class JsonService {

  private final Gson gson;

  public JsonService(Scene currentScene) {
    this.gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Component.class, new ComponentDeserializer())
        .registerTypeAdapter(GameObject.class, new GameObjectDeserializer(currentScene))
        .create();
  }

  public GameObject[] LoadFrom(Path path) {
    try {
      var source = new String(Files.readAllBytes(path));

      if (source.isBlank()) {
        return null;
      }

      var gameObjects = gson.fromJson(source, GameObject[].class);
      return gameObjects;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void SaveTo(String filename, Collection<GameObject> objects) {
    try {
      var writer = new FileWriter(filename);
      var onSerialization = objects.stream()
          .filter(o -> o.isSerializable())
          .collect(Collectors.toCollection(ArrayList::new));

      writer.write(gson.toJson(onSerialization));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

package scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import java.nio.file.Path;
import java.util.Comparator;
import job.Camera;
import job.GameObject;
import job.GameObjectDeserializer;
import job.StateInWorld;
import org.joml.Vector2f;
import renderer.Renderer;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import services.JsonService;

public class Scene {

  private Renderer renderer;
  private Camera camera;
  private boolean isRunning;
  private List<GameObject> gameObjects;

  private SceneInitializer sceneInitializer;
  private final JsonService jsonService;

  public Scene(SceneInitializer sceneInitializer) {
    this.sceneInitializer = sceneInitializer;
    this.renderer = new Renderer();
    this.gameObjects = new ArrayList<>();
    this.isRunning = false;
    this.jsonService = new JsonService(this);
  }

  public Renderer getRenderer() {
    return renderer;
  }

  public void init() {
    this.camera = new Camera(new Vector2f(0, 0));
    this.sceneInitializer.loadResources(this);
    this.sceneInitializer.init(this);
  }

  public void start() {
    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      go.start();
      this.renderer.add(go);
    }
    isRunning = true;
  }

  public void destroy() {
    for (GameObject go : gameObjects) {
      go.destroy();
    }
  }

  // получает первый объект с данный компонентом или null
  public <T extends Component> GameObject getGameObjectWith(Class<T> clazz) {
    for (GameObject go : gameObjects) {
      if (go.getComponent(clazz) != null) {
        return go;
      }
    }

    return null;
  }

  public void addGameObjectToScene(GameObject go) {
    if (!isRunning) {
      gameObjects.add(go);
    } else {
      gameObjects.add(go);
      go.start();
      this.renderer.add(go);
    }
  }

  public GameObject getGameObject(int gameObjectId) {
    Optional<GameObject> result = this.gameObjects.stream()
        .filter(gameObject -> gameObject.getUid() == gameObjectId)
        .findFirst();
    // ???? ???? ?????????, ?????? GameObject, ???? ???, ?? null
    return result.orElse(null);
  }

  public GameObject getGameObjectByName(String name) {
    Optional<GameObject> result = this.gameObjects.stream()
        .filter(gameObject -> gameObject.name.equals(name))
        .findFirst();
    return result.orElse(null);
  }

  public void editorUpdate(float dt) {
    this.camera.adjuctProjection();

    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      go.editorUpdate(dt);

      if (go.isDead()) {
        gameObjects.remove(i);
        this.renderer.destroyGameObject(go);
        i--;
      }
    }
  }

  //each scene has to have such method (it is all job which executes each frame)
  public void update(double dt) {
    this.camera.adjuctProjection(); // ?????, ????? ??? ???????

    //DebugDraw.addBox2D(new Vector2f(200, 200), new Vector2f(64, 32), 45);
    //DebugDraw.addCircle(new Vector2f(300, 300), 50);

/*        float x = ((float)Math.sin(t) * 200.0f) + 600;
        float y = ((float)Math.cos(t) * 200.0f) + 400;
        t += 0.05f;
        DebugDraw.addLine2D(new Vector2f(600, 400), new Vector2f(x, y), new Vector3f(0, 0, 1), 100);*/

/*        spriteFlipTimeLeft -= dt;
        if(spriteFlipTimeLeft <= 0)
        {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex >= 2)
            {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }*/

    for (int i = 0; i < gameObjects.size(); i++) {
      GameObject go = gameObjects.get(i);
      go.update((float) dt);

      if (go.isDead()) {
        gameObjects.remove(i);
        this.renderer.destroyGameObject(go);
        i--;
      }
    }
  }

  public void render() {
    this.renderer.render();
  }

  public Camera camera() {
    return this.camera;
  }

  public void imgui() {
    this.sceneInitializer.imgui();
  }

  public GameObject createGameObject(String name) {
    GameObject go = new GameObject(name, this);
    go.addComponent(new StateInWorld());
    go.stateInWorld = go.getComponent(StateInWorld.class);
    return go;
  }

  public void save() {
    this.jsonService.SaveTo("level.json", this.gameObjects);
  }

  public void load() {
    var path = Path.of("level.json");
    var gameObjects = this.jsonService.LoadFrom(path);

    int maxGoId = -1;
    int maxCompId = -1;
    for (var gameObject : gameObjects) {
      addGameObjectToScene(gameObject);
      var maxGameObjectComponentUid = gameObject.GetMaxComponentsUid();
      if (maxGameObjectComponentUid > maxCompId) {
        maxCompId = maxGameObjectComponentUid;
      }

      var uid = gameObject.getUid();
      if (uid > maxGoId) {
        maxGoId = uid;
      }
    }

    maxGoId++;
    maxCompId++;
    GameObject.init(maxGoId);
    Component.init(maxCompId);

  }

  public List<GameObject> getGameObjects() {
    return this.gameObjects;
  }
}
package engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.renderers.Renderer;
import engine.builders.GameObjectBuilder;
import engine.components.Component;
import entities.components.ComponentDeserializer;
import entities.job.Camera;
import engine.components.GameObject;
import entities.job.GameObjectDeserializer;
import engine.components.StateInWorld;

import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Scene {
    private Renderer renderer;
    private Camera camera;
    private boolean isRunning;
    private List<GameObject> gameObjects;

    private SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.isRunning = false;
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
        gameObjects.forEach(o -> {
            o.start();
            renderer.add(o);
        });
        isRunning = true;
    }

    public void destroy() {
        gameObjects.forEach(o -> o.destroy());
    }

    public <T extends Component> GameObject getGameObjectWith(Class<T> component) {
        return gameObjects.stream()
                .filter(o -> o.getComponent(component) != null)
                .findFirst()
                .orElse(null);
    }

    public void addGameObjectToScene(GameObject gameObject) {
        gameObjects.add(gameObject);
        if (!isRunning) {
            return;
        }

        gameObjects.add(gameObject);
        gameObject.start();
        this.renderer.add(gameObject);
    }

    public GameObject getGameObject(int gameObjectId) {
        return this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUid() == gameObjectId)
                .findFirst()
                .orElse(null);
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
        var builder = new GameObjectBuilder();
        builder.setName(name);
        return builder.build();
    }

    public void save() {
        var gson = getGson();

        try {
            FileWriter writer = new FileWriter("level.json");
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : this.gameObjects) {
                if (obj.doSerialization()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        var gson = getGson();

        var inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inFile.equals("")) {
            return;
        }

        int maxGoId = -1;
        int maxCompId = -1;
        var gameObjects = gson.fromJson(inFile, GameObject[].class);
        for (var gameObject : gameObjects) {
            addGameObjectToScene(gameObject);

            var components = gameObject.getAllComponents();
            for (var component : components) {
                if (component.getUid() > maxCompId) {
                    maxCompId = component.getUid();
                }
            }

            if (gameObject.getUid() > maxGoId) {
                maxGoId = gameObject.getUid();
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

    private Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer(this))
                .create();
    }
}
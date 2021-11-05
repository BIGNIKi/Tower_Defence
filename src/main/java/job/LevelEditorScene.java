package job;

import Util.AssetPool;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

public class LevelEditorScene extends Scene
{

    @Override
    public void init()
    {
        this.camera = new Camera(new Vector2f());

        GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(131, 132)));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/Stone0.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(132, 132)));
        obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/SpaceForTower.png")));
        this.addGameObjectToScene(obj2);

        loadResources();
    }

    private void loadResources()
    {
        AssetPool.getShader("assets/shaders/default.glsl");
    }

    public LevelEditorScene()
    {

    }

    //all bullshits bellow are for test new features

    @Override
    public void update(double dt)
    {
        for(GameObject go : this.gameObjects)
        {
            go.update((float)dt);
        }
        this.renderer.render();
    }
}

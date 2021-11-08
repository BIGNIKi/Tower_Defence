package job;

import Util.AssetPool;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

public class LevelEditorScene extends Scene
{
    private GameObject obj1;
    private SpriteSheet sprites;

    @Override
    public void init()
    {
        loadResources();

        this.camera = new Camera(new Vector2f());

        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(121, 120)), 0);
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        //GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(600, 100), new Vector2f(121, 120)));
        //obj2.addComponent(new SpriteRenderer(sprites.getSprite(1)));
        //this.addGameObjectToScene(obj2);


        // Below is the way to load and draw particular image
        /*GameObject obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(131, 132)));
        obj1.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/Stone0.png"))));
        this.addGameObjectToScene(obj1);
        */

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(100, 100)), 1);
        obj2.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/Red.png"))));
        this.addGameObjectToScene(obj2);
        GameObject obj3 = new GameObject("Object 3", new Transform(new Vector2f(450, 100), new Vector2f(100, 100)), 0);
        obj3.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/images/Yellow.png"))));
        this.addGameObjectToScene(obj3);
    }

    private void loadResources()
    {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        121, 120, 2,0));
    }

    public LevelEditorScene()
    {

    }

    //all bullshits bellow are for test new features

    private int spriteIndex = 0;
    private float spriteFlipTime = 0.3f;
    private float spriteFlipTimeLeft = 0.0f;
    @Override
    public void update(double dt)
    {
        spriteFlipTimeLeft -= dt;
        if(spriteFlipTimeLeft <= 0)
        {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex >= 2)
            {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }

        for(GameObject go : this.gameObjects)
        {
            go.update((float)dt);
        }
        this.renderer.render();
    }
}

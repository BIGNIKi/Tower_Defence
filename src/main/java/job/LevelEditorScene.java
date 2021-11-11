package job;

import Util.AssetPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Regidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImGui;
import imgui.ImVec2;
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
        if(levelLoaded)
        {
            this.activeGameObject = gameObjects.get(0);
            return;
        }

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(121, 120)), 0);
        SpriteRenderer obj1SpriteRenderer = new SpriteRenderer();
        obj1SpriteRenderer.setSprite(sprites.getSprite(0));
        obj1.addComponent(obj1SpriteRenderer);
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(100, 100)), 1);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/images/White.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        obj2.addComponent(new Regidbody());
        this.addGameObjectToScene(obj2);

        GameObject obj3 = new GameObject("Object 3", new Transform(new Vector2f(450, 100), new Vector2f(100, 100)), 0);
        SpriteRenderer obj3SpriteRenderer = new SpriteRenderer();
        Sprite obj3Sprite = new Sprite();
        obj3Sprite.setTexture(AssetPool.getTexture("assets/images/White.png"));
        obj3SpriteRenderer.setSprite(obj3Sprite);
        obj3.addComponent(obj3SpriteRenderer);
        this.addGameObjectToScene(obj3);

/*        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(100, 100)), 1);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        obj2SpriteRenderer.setColor(new Vector4f(1, 0, 0, 1));
        //Sprite obj2Sprite = new Sprite();
        //obj2Sprite.setTexture(AssetPool.getTexture("assets/images/White.png"));
        //obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        obj2.addComponent(new Regidbody());
        this.addGameObjectToScene(obj2);*/

        // the way to make colored rectangle without texture
        //obj1.addComponent(new SpriteRenderer(new Vector4f(1,0,0,1)));
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

        for(GameObject go : this.gameObjects)
        {
            go.update((float)dt);
        }
        this.renderer.render();
    }

    @Override
    public void imgui()
    {
        ImGui.begin("������������ �������: ");
/*        ImGui.text("����: 5000");
        ImGui.text("HP: 1");*/

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite sprite = sprites.getSprite(i);
            //float spriteWidth = sprite.getWidth() * 4;
            //float spriteHeight = sprite.getHeight() * 4;
            float spriteWidth = 50;
            float spriteHeight = 50;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y))
            {
                System.out.println("Button " + i + " clicked");
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if(i + 1 < sprites.size() && nextButtonX2 < windowX2)
            {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}

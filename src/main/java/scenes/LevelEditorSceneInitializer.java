package scenes;

import Util.AssetPool;
import Util.Settings;
import com.sun.tools.javac.Main;
import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import job.*;
import org.joml.Vector2f;
import renderer.DebugDraw;

public class LevelEditorSceneInitializer extends SceneInitializer
{
    private SpriteSheet sprites;
    private GameObject levelEditorStuff;

    @Override
    public void init(Scene scene)
    {
        sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");
        SpriteSheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.camera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);

        //DebugDraw.addLine2D(new Vector2f(0,0), new Vector2f(800, 800), new Vector3f(1, 1, 1), 120);

/*        obj1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(121, 120)), 0);
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
        obj2.addComponent(new Rigidbody());
        this.addGameObjectToScene(obj2);
        GameObject obj3 = new GameObject("Object 3", new Transform(new Vector2f(450, 100), new Vector2f(100, 100)), 0);
        SpriteRenderer obj3SpriteRenderer = new SpriteRenderer();
        Sprite obj3Sprite = new Sprite();
        obj3Sprite.setTexture(AssetPool.getTexture("assets/images/White.png"));
        obj3SpriteRenderer.setSprite(obj3Sprite);
        obj3.addComponent(obj3SpriteRenderer);
        this.addGameObjectToScene(obj3);*/

        // the way to make colored rectangle without texture
        //obj1.addComponent(new SpriteRenderer(new Vector4f(1,0,0,1)));
    }

    @Override
    public void loadResources(Scene scene)
    {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        121, 120, 2,0));
        AssetPool.addSpritesheet("assets/images/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));
        //AssetPool.getTexture("assets/images/Stone0.png");

        for(GameObject g : scene.getGameObjects())
        {
            if(g.getComponent(SpriteRenderer.class) != null)
            {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if(spr.getTexture() != null)
                {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilePath()));
                }
            }
        }
    }

    public LevelEditorSceneInitializer()
    {

    }

    //all bullshits bellow are for test new features

    private int spriteIndex = 0;
    private float spriteFlipTime = 0.3f;
    private float spriteFlipTimeLeft = 0.0f;
    //float t = 0.0f;
/*    @Override
    public void update(double dt)
    {
        levelEditorStuff.update((float)dt);
        this.camera.adjuctProjection(); // íóæíî, ÷òîáû çóì ðàáîòàë
        //DebugDraw.addBox2D(new Vector2f(200, 200), new Vector2f(64, 32), 45);
        //DebugDraw.addCircle(new Vector2f(300, 300), 50);
*//*        float x = ((float)Math.sin(t) * 200.0f) + 600;
        float y = ((float)Math.cos(t) * 200.0f) + 400;
        t += 0.05f;
        DebugDraw.addLine2D(new Vector2f(600, 400), new Vector2f(x, y), new Vector3f(0, 0, 1), 100);*//*
 *//*        spriteFlipTimeLeft -= dt;
        if(spriteFlipTimeLeft <= 0)
        {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if(spriteIndex >= 2)
            {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }*//*
        for(GameObject go : this.gameObjects)
        {
            go.update((float)dt);
        }
    }*/

    @Override
    public void imgui()
    {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Sprites:");
/*        ImGui.text("Óðîí: 5000");
        ImGui.text("HP: 1");*/

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        // TODO: óäàëèòü ïåðåìåííóþ
        int tempI = 0;

        float windowX2 = windowPos.x + windowSize.x;
        // ñîçäàíèå êíîïîê (òåêñòóð) â öèêëå
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
            // óñëîâèÿ äëÿ êíîïîê
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
            {
                //GameObject object = Prefabs.generateSpriteObject(sprite, sprite.getWidth(), sprite.getHeight());
                GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                // Attach this to the mouse cursor
                // mouseControls.pickupObject(object);
                // ÇÀÕÂÀÒ ÎÁÚÅÊÒÀ
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
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
            tempI = i;
        }

        // ВРЕМЕННО

        Sprite sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture("assets/images/greenEnemy1.png"));
        float spriteWidth = 50;
        float spriteHeight = 50;
        int id = sprite.getTexId();
        Vector2f[] texCoords = sprite.getTexCoords();
        ImGui.pushID(tempI);
        tempI++;
        // óñëîâèÿ äëÿ êíîïîê
        if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
        {
            //GameObject object = Prefabs.generateSpriteObject(sprite, sprite.getWidth(), sprite.getHeight());
            GameObject object = Prefabs.generateSpriteObject(sprite, 0.125f, 0.125f);
            // Attach this to the mouse cursor
            // mouseControls.pickupObject(object);
            // ÇÀÕÂÀÒ ÎÁÚÅÊÒÀ
            levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
        }
        ImGui.popID();
        sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture("assets/images/base.png"));
        spriteWidth = 50;
        spriteHeight = 50;
        id = sprite.getTexId();
        texCoords = sprite.getTexCoords();
        ImGui.pushID(tempI);
        tempI++;
        // óñëîâèÿ äëÿ êíîïîê
        if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
        {
            //GameObject object = Prefabs.generateSpriteObject(sprite, sprite.getWidth(), sprite.getHeight());
            GameObject object = Prefabs.generateSpriteObject(sprite, 0.125f, 0.125f);
            // Attach this to the mouse cursor
            // mouseControls.pickupObject(object);
            // ÇÀÕÂÀÒ ÎÁÚÅÊÒÀ
            levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
        }
        ImGui.popID();

        // ВРЕМЕННО

        ImGui.end();
    }

    // TODO: ñäåëàòü íîðìàëüíî äîáàâëåíèå òåêñòóð
}
package entities.scenes;

import Util.AssetPool;
import components.*;
import entities.components.EditorCamera;
import entities.components.GizmoSystem;
import entities.components.GridLines;
import entities.components.KeyControls;
import entities.components.MouseControls;
import entities.components.Sprite;
import entities.components.SpriteRenderer;
import entities.components.SpriteSheet;
import entities.job.GameObject;
import entities.job.Prefabs;
import imgui.ImGui;
import imgui.ImVec2;
import job.*;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class LevelEditorSceneInitializer extends SceneInitializer
{
    //private SpriteSheet sprites;
    private GameObject levelEditorStuff;

    private final List<Sprite> btnTexture = new ArrayList<>(); // все спрайты, которые нужно будет отобразить как кнопки

    // этот метод запускается ПОСЛЕ loadResources
    @Override
    public void init(Scene scene)
    {
        System.out.println("LevelEditor");
        SpriteSheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");

        levelEditorStuff = scene.createGameObject("LevelEditor"); // объект, который всегда висит на сцене
        // и задействует следующие функции:
        levelEditorStuff.setNoSerialize(); // этот объект не сохраняется
        levelEditorStuff.addComponent(new MouseControls()); // отвечает за захват, размещение игрового объекта
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines()); // рисует сетку
        levelEditorStuff.addComponent(new EditorCamera(scene.camera())); // все взаимодействия с камерой для редактора
        levelEditorStuff.addComponent(new GizmoSystem(gizmos)); // отрисовка стрелок для смены позиции/размера
        scene.addGameObjectToScene(levelEditorStuff);

        //DebugDraw.addLine2D(new Vector2f(0,0), new Vector2f(800, 800), new Vector3f(1, 1, 1), 120);

        // the way to make colored rectangle without texture
        //obj1.addComponent(new SpriteRenderer(new Vector4f(1,0,0,1)));
    }

    // перегоняет все спрайты из спрайтшита в List всех спрайтов btnTexture
    private void spriteSheetToSprites(String path, int spriteWidth, int spriteHeight, int numSprites, int spacing)
    {
        // положили спрайты на видеокарту (теперь она знает о них)
        AssetPool.addSpritesheet(path,
                new SpriteSheet(AssetPool.getTexture(path),
                        spriteWidth, spriteHeight, numSprites,spacing));
        SpriteSheet sprites = AssetPool.getSpritesheet(path); // получили сам созданный спрайтshit
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite sprite = sprites.getSprite(i);
            btnTexture.add(sprite);
        }

    }

    // загрузит текстуру в List всех спрайтов btnTexture
    private void textureToSprite(String path)
    {
        Sprite sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture(path));
        btnTexture.add(sprite);
    }

    // эта функция запускается РАНЬШЕ чем init
    @Override
    public void loadResources(Scene scene)
    {
        AssetPool.getShader("assets/shaders/default.glsl");

        // START сюда пишутся текстуры, которые нужно видеть в редакторе выбора текстур

        spriteSheetToSprites("assets/images/spritesheet.png", 121, 120, 2,0);
        textureToSprite("assets/images/greenEnemy1.png");
        textureToSprite("assets/images/base.png");
        textureToSprite("assets/images/enemySpawn.png");
        textureToSprite("assets/images/bash.png");
        textureToSprite("assets/images/bush1.png");
        textureToSprite("assets/images/bush2.png");
        textureToSprite("assets/images/bush3.png");
        textureToSprite("assets/images/bush4.png");
        //textureToSprite("assets/images/angryBird.png");

        // STOP

        AssetPool.addSpritesheet("assets/images/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        24, 48, 3, 0));


        // проходит все объекты сцены, натягивая на них текстуры
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

/*    private int spriteIndex = 0;
    private float spriteFlipTime = 0.3f;
    private float spriteFlipTimeLeft = 0.0f;*/
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

    // метод вызывается каждый кадр (отвечает за вывод окошка с текстурами)
    @Override
    public void imgui()
    {
/*        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imgui();
        ImGui.end();*/

        ImGui.begin("Sprites:");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        // создание кнопок на котороые можно нажать, чтобы выбрать текстуру
        for(int i = 0; i < btnTexture.size(); i++)
        {
            Sprite sprite = btnTexture.get(i);
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            // само создание кнопок
            // количество пикселей в ширину
            float spriteWidth = 50;
            // в длину
            float spriteHeight = 50;
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
            {
                GameObject object = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                // Attach this to the mouse cursor
                // mouseControls.pickupObject(object);
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if(nextButtonX2 < windowX2)
            {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}
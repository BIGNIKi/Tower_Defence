package job;

import Util.AssetPool;
import Util.StringList;
import components.Sprite;
import components.SpriteRenderer;
import entities.monsters.Monster;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Prefabs
{
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
    {
        GameObject block = MainWindow.getScene().createGameObject("Sprite_Object_Gen");
        block.stateInWorld.setScale(new Vector2f(sizeX, sizeY));
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

    // добавление префаба монстра с заданными параметрами
    public static void addEnemy(float speed, StringList wayPoints, Vector2f position)
    {
        GameObject go = MainWindow.getScene().createGameObject("Enemy");
        go.stateInWorld.setPosition(new Vector2f(position.x, position.y));
        go.stateInWorld.setScale(new Vector2f(0.07f, 0.07f));

        SpriteRenderer renderer = new SpriteRenderer();
        renderer.zIndex = 1;
        Sprite sp = new Sprite();
        sp.setTexture(AssetPool.getTexture("assets/images/greenEnemy1.png"));
        renderer.setSprite(sp);
        renderer.setColor(new Vector4f(0,1,0,1)); // green
        go.addComponent(renderer);

        Monster m = new Monster();
        m.settingMonster(speed, wayPoints);
        go.addComponent(m);

        MainWindow.getScene().addGameObjectToScene(go);
    }
}

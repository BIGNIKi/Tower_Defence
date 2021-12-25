package entities.job;

import utils.StringList;
import entities.components.Sprite;
import entities.components.SpriteRenderer;
import entities.entities1.Bullet;
import entities.entities1.monsters.Monster;
import entities.entities1.towers.Tower;
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
    public static void addEnemy(float speed, StringList wayPoints, Vector2f position, float health, int moneyForKill)
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
        m.settingMonster(speed, wayPoints, health, moneyForKill);
        go.addComponent(m);

        MainWindow.getScene().addGameObjectToScene(go);
    }

    public static void addBullet(GameObject goal, Vector2f startPosition, float damage)
    {
        GameObject go = MainWindow.getScene().createGameObject("Bullet");
        go.stateInWorld.setPosition(new Vector2f(startPosition.x, startPosition.y));
        go.stateInWorld.setScale(new Vector2f(0.035f, 0.035f));

        SpriteRenderer renderer = new SpriteRenderer();
        renderer.zIndex = 2;
        Sprite sp = new Sprite();
        sp.setTexture(AssetPool.getTexture("assets/images/greenEnemy1.png"));
        renderer.setSprite(sp);
        renderer.setColor(new Vector4f(1,1,1,0.5f)); // green
        go.addComponent(renderer);

        Bullet b = new Bullet();
        b.settingBullet(goal, startPosition, damage);
        go.addComponent(b);

        MainWindow.getScene().addGameObjectToScene(go);
    }

    public static void addTower(Vector2f position, String pathSpr0, Vector2f sizeTower,
                                String pathSpr1, float initialRotation, float rotateSpeed,
                                float observeRadius, float timeToAttack, float damage)
    {
        GameObject go = MainWindow.getScene().createGameObject("TowerSt");
        go.stateInWorld.setPosition(new Vector2f(position.x, position.y));
        go.stateInWorld.setScale(new Vector2f(0.166f, 0.166f));

        SpriteRenderer renderer = new SpriteRenderer();
        renderer.zIndex = 1;
        Sprite sp = new Sprite();
        sp.setTexture(AssetPool.getTexture(pathSpr0));
        renderer.setSprite(sp);
        renderer.setColor(new Vector4f(1,1,1,1));
        go.addComponent(renderer);

        MainWindow.getScene().addGameObjectToScene(go);
//-----------------------------
        GameObject go1 = MainWindow.getScene().createGameObject("Tower");
        go1.stateInWorld.setPosition(new Vector2f(position.x, position.y));
        go1.stateInWorld.setScale(sizeTower);
        go1.stateInWorld.setRotation(initialRotation);

        SpriteRenderer renderer1 = new SpriteRenderer();
        renderer1.zIndex = 2;
        Sprite sp1 = new Sprite();
        sp1.setTexture(AssetPool.getTexture(pathSpr1));
        renderer1.setSprite(sp1);
        renderer1.setColor(new Vector4f(1,1,1,1));
        go1.addComponent(renderer1);

        Tower t = new Tower();
        t.settingTower(rotateSpeed, observeRadius, timeToAttack, damage);
        go1.addComponent(t);

        MainWindow.getScene().addGameObjectToScene(go1);
    }
}

package job;

import components.Sprite;
import components.SpriteRenderer;
import entities.towers.Tower;
import org.joml.Vector2f;

public class Prefabs
{
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
    {
        GameObject block = MainWindow.getScene().createGameObject("Sprite_Object_Gen");
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }

/*    public void addEnemy()
    {
        GameObject blabla = MainWindow.getScene().createGameObject("TOWER");
        blabla.addComponent(new Transform());
        blabla.addComponent(new SpriteRenderer());
        blabla.addComponent(new Tower());
        MainWindow.getScene().addGameObjectToScene(blabla);
    }*/
}

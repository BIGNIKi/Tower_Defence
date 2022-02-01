package Core;

import Components.SpriteRenderer;
import UI.InGameGraphic.Sprite;
import org.joml.Vector2f;

public class Prefabs
{
    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY)
    {
        GameObject block = MainCycle.getScene().createGameObject("Sprite_Object_Gen");
        block.stateInWorld.setScale(new Vector2f(sizeX, sizeY));
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}

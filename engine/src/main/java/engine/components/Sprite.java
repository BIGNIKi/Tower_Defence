package engine.components;

import engine.textures.Texture;
import org.joml.Vector2f;

public class Sprite
{
    private float width, height;

    private Texture texture = null;

    private Vector2f[] texCoords = {
            new Vector2f(1,1),
            new Vector2f(1, 0),
            new Vector2f(0,0),
            new Vector2f(0,1)
    };

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public Texture getTexture()
    {
        return this.texture;
    }

    public void setTexture(Texture tex)
    {
        this.texture = tex;
    }

    public Vector2f[] getTexCoords()
    {
        return texCoords;
    }

    public void setTexCoords(Vector2f[] texCoords)
    {
        this.texCoords = texCoords;
    }
}

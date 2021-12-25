package utils;

import components.SpriteSheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool
{
    //хеш-мапа ключ:абсолютный путь к файлу, значение: объект Shader
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourceName)
    {
        File file = new File(resourceName);
        if(AssetPool.shaders.containsKey(file.getAbsolutePath()))
        {
            return AssetPool.shaders.get(file.getAbsolutePath());
        }
        else
        {
            Shader shader = new Shader(resourceName);
            shader.compileAndLink();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName)
    {
        File file = new File(resourceName);
        if(AssetPool.textures.containsKey(file.getAbsolutePath()))
        {
            return AssetPool.textures.get(file.getAbsolutePath());
        }
        else
        {
            Texture texture = new Texture();
            texture.init(resourceName);
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpritesheet(String resourceName, SpriteSheet spriteSheet)
    {
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath()))
        {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
        }
    }

    public static SpriteSheet getSpritesheet(String resourceName)
    {
        File file = new File(resourceName);
        if(!AssetPool.spriteSheets.containsKey(file.getAbsolutePath()))
        {
            assert false : "Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.";
        }
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
}

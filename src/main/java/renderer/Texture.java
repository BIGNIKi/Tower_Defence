package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture
{
    private String filePath;
    private int texId;
    private int width, height;

    public Texture (String filePath)
    {
        this.filePath = filePath;

        //Generate texture on GPU
        texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);

        //Set texture parameters
        //Repeat image in both coordinates
        //i на конце means info parameter
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); //говорит о том, что нужно "растягивать" текстуру по x(S)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); //говорит о том, что нужно "растягивать" текстуру по y(T)

        //When stretching the image, pixelate it
        //GL_TEXTURE_MIN_FILTER - это значит, что текстура растягивается
        //GL_NEAREST - значит, что зальёт текстуру последним использованным цветом (пикселизация)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        //When shrinking, also pixelate
        //GL_TEXTURE_MAG_FILTER - это для сужения (сжатия) текстуры
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1); //какой формат - RGB или RGBA
        stbi_set_flip_vertically_on_load(true);
        //stbi - image loading library
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

        if(image != null)
        {
            this.width = width.get(0);
            this.height = height.get(0);

            if(channels.get(0) == 3) //RGB picture
            {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }
            else if(channels.get(0) == 4) //RGBA picture
            {
                //создаёт буфер размером width.get(0) на height.get(0)
                //кладёт туда все наши байты картинки
                //формат - GL_RGBA
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }
            else
            {
                assert false : "Error: (Texture) unknown number of channel '" + channels.get(0) + "'";
            }

        }
        else
        {
            assert false : "Error: (Texture) couldn't load image '" + filePath + "'";
        }

        //если это не сделать, будет memory leak =)
        stbi_image_free(image); //делает free для памяти, которую выделила библиотека stbi
    }

    public void bind()
    {
        glBindTexture(GL_TEXTURE_2D, texId);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

}

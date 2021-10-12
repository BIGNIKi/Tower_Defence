package job;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexId, fragmentId, shaderProgram;
    //shaderProgram - the combination of the vertex and the fragment source

    private float[] vertexArray = {
            //postion               //color
            0.5f, -0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f, //Bottom right - red
           -0.5f,  0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f, //Top left - green
            0.5f,  0.5f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f, //Top right - blue
           -0.5f, -0.5f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f //Bottom left - yellow
    };

    //IMPORTANT: must be in counter-clockwise order
    private int[] elementArray = {
            /*
               x       x


               x       x
            */
            2,1,0, //Top right triangle
            0,1,3  //bottom left triangle
    };

    //vertex array object, vertex buffer object, element buffer object
    private int vaoId, vboId, eboId;

    @Override
    public void init()
    {
        //at start we need to compile shaders

        vertexId = glCreateShader(GL_VERTEX_SHADER); //Creates a vertex shader object
        //pass the shader source to the GPU
        glShaderSource(vertexId, vertexShaderSrc);
        glCompileShader(vertexId); //it compiles shader

        //check for errors in compilation process
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS); //gets return states of compilation (0 is unsuccessful)
        if(success == GL_FALSE)
        {
            //так как мы работаем с оберткой над си функциями
            //нам нужно знать длину строки, чтобы её корректно вывести
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        //the same for fragments shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentShaderSrc);
        glCompileShader(fragmentId);

        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE)
        {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false : "";
        }

        //now we need to link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram); //has linked

        //check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE)
        {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultShader.glsl'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

        //Generate VAO, VBO, and EBO buffer objects, and send to GPU
        vaoId = glGenVertexArrays();
        //it is says: everything we are about to do make sure we are doing it to this array specifically
        //so make sure that everything that comes after this line is happening to this vertexArray
        glBindVertexArray(vaoId);

        //create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length); //openGL waits FloatBuffer from us
        vertexBuffer.put(vertexArray).flip();

        //create VBO upload the vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        //мы работает с Array буфером - vertexBuffer, который мы отправим сюда vboId
        //также мы будем только отрисовывать это статически (внутри буфера ничего меняться не будет)
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //add the vertex attribute pointers
        //это нужно, чтобы GPU понимала длину Vertex'а (например 3 float'а на координаты и 4 на цвет)
        int positionSize = 3; //how many positions (XYZ)
        int colorSize = 4; //how many colors (RGBA)
        int floatSizeBytes = 4; //4 bytes for one float    (если бы мы писали на си, написали бы "sizeof(float)")
        int vertexSizeBytes = (positionSize +  colorSize) * floatSizeBytes; //сколько байт на одну вершину
        //этой строчкой мы стучимся к шейдеру, передавая ему layout(location = 0) - aPos
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        //этой строчкой мы стучимся к шейдеру, передавая ему layout(location = 1) - aColor
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    public LevelEditorScene()
    {

    }

    //all bullshits bellow are for test new features

    @Override
    public void update(double dt)
    {
        //bind shader program
        glUseProgram(shaderProgram);
        //bind the VAO that we're using
        glBindVertexArray(vaoId);

        //Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); // bind to nothing
        glUseProgram(0); // GPU, use nothing
    }
}

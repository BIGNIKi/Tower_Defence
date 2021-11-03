package job;

import Util.Time;
import components.FontRenderer;
import components.SpriteRenderer;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import renderer.Shader;
import renderer.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
    GameObject testObj;
    private boolean firstTime = false;

    private int vertexId, fragmentId, shaderProgram;
    //shaderProgram - the combination of the vertex and the fragment source

    private final float offsetX = 0f;
    private final float offsetY = 0f;
    private final float offsetX1 = offsetX + 300f;
    private final float offsetY1 = offsetY + 300f;

    private float[] vertexArray = {
            //postion               //color                     //UV coordinates
            offsetX1,  offsetY, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,      1, 1,   //Bottom right
            offsetX, offsetY1, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,      0, 0,   //Top left
            offsetX1, offsetY1, 0.0f,      1.0f, 0.0f, 1.0f, 1.0f,      1, 0,   //Top right
            offsetX,   offsetY, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f,      0, 1   //Bottom left
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

    private Shader defaultShader;

    private Texture testTexture;

    @Override
    public void init()
    {
        System.out.println("Creating 'test object'");
        this.testObj = new GameObject("test object");
        this.testObj.addComponent(new SpriteRenderer());
        this.testObj.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObj);

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();
        testTexture = new Texture("assets/images/testImage.png");

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
        int uvSize = 2;
        int vertexSizeBytes = (positionSize +  colorSize + uvSize) * Float.BYTES; //сколько байт на одну вершину
        //этой строчкой мы стучимся к шейдеру, передавая ему layout(location = 0) - aPos
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        //этой строчкой мы стучимся к шейдеру, передавая ему layout(location = 1) - aColor
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }



    public LevelEditorScene()
    {

    }

    //all bullshits bellow are for test new features

    @Override
    public void update(double dt)
    {
        defaultShader.use();

        //Upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", (float)Time.getTime());

        //bind the VAO that we're using
        glBindVertexArray(vaoId);

        //Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT))
        {
            camera.position.x+=2;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_RIGHT))
        {
            camera.position.x-=2;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_UP))
        {
            camera.position.y-=2;
        }
        if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_DOWN))
        {
            camera.position.y+=2;
        }

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); // bind to nothing

        defaultShader.detach(); // GPU, use nothing

        if(!firstTime)
        {
            System.out.println("Creating gameObject!");
            GameObject go = new GameObject("Game Test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }


        for(GameObject go : this.gameObjects)
        {
            go.update((float)dt);
        }
    }
}

package core.renderers;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader
{
    private int shaderProgramID;

    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filePath)
    {
        this.filePath = filePath;
        try
        {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //Find the first one pattern "vertex"
            int index = source.indexOf(("#type")) + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            //Find the second one pattern "fragment"
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex"))
            {
                vertexSource = splitString[1];
            }
            else if(firstPattern.equals("fragment"))
            {
                fragmentSource = splitString[1];
            }
            else
            {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if(secondPattern.equals("vertex"))
            {
                vertexSource = splitString[2];
            }
            else if(secondPattern.equals("fragment"))
            {
                fragmentSource = splitString[2];
            }
            else
            {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            assert false : "Error: couldn't open file for shader: '" + filePath + "'";
        }
    }

    public void compileAndLink()
    {
        int vertexId, fragmentId;

        //at start we need to compile shaders

        vertexId = glCreateShader(GL_VERTEX_SHADER); //Creates a vertex shader object
        //pass the shader source to the GPU
        glShaderSource(vertexId, vertexSource);
        glCompileShader(vertexId); //it compiles shader

        //check for errors in compilation process
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS); //gets return states of compilation (0 is unsuccessful)
        if(success == GL_FALSE)
        {
            //так как мы работаем с оберткой над си функциями
            //нам нужно знать длину строки, чтобы её корректно вывести
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filePath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        //the same for fragments shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, fragmentSource);
        glCompileShader(fragmentId);

        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE)
        {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filePath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false : "";
        }

        //now we need to link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexId);
        glAttachShader(shaderProgramID, fragmentId);
        glLinkProgram(shaderProgramID); //has linked

        //check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if(success == GL_FALSE)
        {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + filePath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use()
    {
        if(!beingUsed)
        {
            //bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach()
    {
        glUseProgram(0); // GPU, use nothing
        beingUsed = false;
    }

    //загрузка uniform-переменных в шейдер
    public void uploadMat4f(String varName, Matrix4f mat4)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(4*4);
        mat4.get(matBuffer); //кладет mat4 в matBuffer
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(3*3);
        mat3.get(matBuffer); //кладет mat4 в matBuffer
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadTexture(String varName, int slot)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    // Uploading multiple textures
    public void uploadIntArray(String varName, int[] array)
    {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}

package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader
{
    private int shaderProgramID;

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

    public void compile()
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
        //bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach()
    {
        glUseProgram(0); // GPU, use nothing
    }
}

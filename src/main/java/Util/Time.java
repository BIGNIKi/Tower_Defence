package Util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class Time
{
    //public static float timeStarted = System.nanoTime(); //time of started the application
    public static double timeStarted = glfwGetTime(); //time of started the application

    //returns the time since start moment of the application
    //public static float getTime()
    //{
    //    return (float)((System.nanoTime() - timeStarted) * 1E-9);
    //}

    //returns the time since start moment of the application (in seconds)
    public static double getTime()
    {
        return glfwGetTime() - timeStarted;
    }
}

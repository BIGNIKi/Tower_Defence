package job;

import java.awt.event.KeyEvent;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class LevelEditorScene extends Scene
{
    private boolean changingScene = false;

    private float timeToChangeScene = 2.0f;

    public LevelEditorScene()
    {
        System.out.println("Inside level editor scene");
    }

    //all bullshits bellow are for test new features

    int frameCount = 0;
    double previousTime = glfwGetTime();
    @Override
    public void update(double dt)
    {
        double currentTime = glfwGetTime();
        frameCount++;

        if ( currentTime - previousTime >= 1.0 )
        {
            //if we turned on v-sync, frame rate can't be more than Hz of your monitor
            System.out.println("FPS = " + frameCount);

            frameCount = 0;
            previousTime = currentTime;
        }

        if(!changingScene && Keyboard.isKeyPressed(KeyEvent.VK_SPACE))
        {
            changingScene = true;
        }

        if(changingScene && timeToChangeScene > 0)
        {
            timeToChangeScene -= dt;
            MainWindow.get().r -= dt * 5.0f;
            MainWindow.get().g -= dt * 5.0f;
            MainWindow.get().b -= dt * 5.0f;
        }
        else if(changingScene)
        {
            MainWindow.changeScene(1);
        }
    }
}

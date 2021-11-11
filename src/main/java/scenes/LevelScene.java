package scenes;

import job.MainWindow;
import scenes.Scene;

public class LevelScene extends Scene
{
    public LevelScene()
    {
        System.out.println("Inside level scene");
        MainWindow.get().r = 1;
        MainWindow.get().g = 1;
        MainWindow.get().b = 1;
    }

    @Override
    public void update(double dt)
    {

    }
}

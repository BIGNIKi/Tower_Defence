package job;

public abstract class Scene
{
    public Scene()
    {

    }

    public void init()
    {

    }

    //each scene has to have such method (it is all job which executes each frame)
    public abstract void update(double dt);
}

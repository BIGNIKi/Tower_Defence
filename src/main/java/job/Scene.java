package job;

import imgui.ImGui;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;

    public Scene()
    {

    }

    public void init()
    {

    }

    public void start()
    {
        for(GameObject go : gameObjects)
        {
            go.start();
            this.renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go)
    {
        if(!isRunning)
        {
            gameObjects.add(go);
        }
        else
        {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
        }
    }

    //each scene has to have such method (it is all job which executes each frame)
    public abstract void update(double dt);

    public Camera camera()
    {
        return this.camera;
    }

    public void sceneImgui()
    {
        if(activeGameObject != null)
        {
            ImGui.begin("Настройка цвета: ");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui()
    {

    }
}

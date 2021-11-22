package components;

import job.Keyboard;
import job.MainWindow;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class GizmoSystem extends Component
{
    private SpriteSheet gizmos;
    private int usingGizmo = 0;

    public GizmoSystem(SpriteSheet gizmoSprites)
    {
        gizmos = gizmoSprites;
    }

    @Override
    public void start()
    {
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                MainWindow.getImguiLayer().getPropertiesWindow()));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2),
                MainWindow.getImguiLayer().getPropertiesWindow()));
    }

    @Override
    public void update(float dt)
    {
        if(usingGizmo == 0)
        {
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        }
        else if(usingGizmo == 1)
        {
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        if(Keyboard.isKeyPressed(GLFW_KEY_E))
        {
            usingGizmo = 0;
        }
        else if(Keyboard.isKeyPressed(GLFW_KEY_S))
        {
            usingGizmo = 1;
        }
    }
}

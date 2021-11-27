package components;

import job.Keyboard;
import job.MainWindow;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;

public class GizmoSystem extends Component
{
    private SpriteSheet gizmos;
    private int usingGizmo = 0;

    private ScaleGizmo scaleGizmo;
    private TranslateGizmo translateGizmo;

    public GizmoSystem(SpriteSheet gizmoSprites)
    {
        gizmos = gizmoSprites;
    }

    @Override
    public void start()
    {
        translateGizmo = new TranslateGizmo(gizmos.getSprite(1), MainWindow.getImguiLayer().getPropertiesWindow());
        gameObject.addComponent(translateGizmo);
        //gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1),
        //        MainWindow.getImguiLayer().getPropertiesWindow()));
        scaleGizmo = new ScaleGizmo(gizmos.getSprite(2),
                MainWindow.getImguiLayer().getPropertiesWindow());
        gameObject.addComponent(scaleGizmo);
        //gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2),
        //        MainWindow.getImguiLayer().getPropertiesWindow()));

        gameObject.getComponent(TranslateGizmo.class).setUsing();
        gameObject.getComponent(ScaleGizmo.class).setNotUsing();
    }

    @Override
    public void editorUpdate(float dt)
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
            //gameObject.getComponent(TranslateGizmo.class).setUsing();
            //gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        }
        else if(Keyboard.isKeyPressed(GLFW_KEY_S))
        {
            usingGizmo = 1;
            //gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            //gameObject.getComponent(ScaleGizmo.class).setUsing();
        }
    }

    public boolean checkHoverity()
    {
        boolean boo = translateGizmo.checkXHoverState() || translateGizmo.checkYHoverState()
            || translateGizmo.xAxisActive || translateGizmo.yAxisActive
            || scaleGizmo.checkXHoverState() || scaleGizmo.checkYHoverState()
            || scaleGizmo.xAxisActive || scaleGizmo.yAxisActive;
        if(translateGizmo.checkYHoverState()) System.out.println("true4");
        return boo;
    }
}

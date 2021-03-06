package components;

import editor.PropertiesWindow;
import job.*;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component
{
    private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
    private Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
    private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f,1);
    private Vector4f yAxisColorHover = new Vector4f(0,1,0,1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    protected GameObject activeGameObject = null;

/*    private Vector2f xAxisOffset = new Vector2f(24f/80f, -6f/80f);
    private Vector2f yAxisOffset = new Vector2f(-7f/80f, 21f/80f);    */
    private Vector2f xAxisOffset = new Vector2f(12f/80f, 0f/80f);
    private Vector2f yAxisOffset = new Vector2f(0f/80f, 12f/80f);

/*    private float gizmoWidth = 16 / 80f;
    private float gizmoHeight = 48 / 80f;*/
    private float gizmoWidth = 0.1f;
    private float gizmoHeight = 0.3f;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false; // эта штука висит локально на translate и на scale, чтобы знать, кто из них используется
    private static boolean isUseGizmo = false; // это общая штука, чтобы понять, пытаемся ли что-то двигать или менять размеры

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
    {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        MainWindow.getScene().addGameObjectToScene(this.xAxisObject);
        MainWindow.getScene().addGameObjectToScene(this.yAxisObject);
    }

    @Override
    public void start()
    {
        this.xAxisObject.stateInWorld.setRotation(90);
        //this.xAxisObject.transform.rotation = 90;
        //this.yAxisObject.transform.rotation = 180;

        this.yAxisObject.stateInWorld.setRotation(-180);
        this.xAxisObject.getComponent(SpriteRenderer.class).zIndex = 100;
        this.yAxisObject.getComponent(SpriteRenderer.class).zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (using) {
            this.setInactive();
        }
        xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
        yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
    }

    @Override
    public void editorUpdate(float dt)
    {
        //System.out.println(using);
        if(!using) return;


        this.activeGameObject = this.propertiesWindow.getActiveGameObject();
        if(this.activeGameObject != null)
        {
            this.setActive();
        }
        else
        {
            this.setInactive();
            return;
        }

        // тянем ли ползунок по x или y
        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if((xAxisHot || xAxisActive) && Mouse.isDragging() && Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
        {
            xAxisActive = true;
            yAxisActive = false;
        }
        else if((yAxisHot || yAxisActive) && Mouse.isDragging() && Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
        {
            yAxisActive = true;
            xAxisActive = false;
        }
        else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if(this.activeGameObject != null)
        {
            this.xAxisObject.stateInWorld.set(this.activeGameObject.stateInWorld.getPosition());
            this.yAxisObject.stateInWorld.set(this.activeGameObject.stateInWorld.getPosition());
            this.xAxisObject.stateInWorld.addToPosition(this.xAxisOffset);
            this.yAxisObject.stateInWorld.addToPosition(this.yAxisOffset);
        }
    }

    private void setActive()
    {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive()
    {
        this.activeGameObject = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
        // выглядит как костыльная фигня, но эти две строчки фиксят баг с невозможностью выделения объектов =)
        xAxisObject.stateInWorld.setPosition(new Vector2f(10000000, 10000000));
        yAxisObject.stateInWorld.setPosition(new Vector2f(10000000, 10000000));
    }

    public boolean checkXHoverState()
    {
        Vector2f mousePos = Mouse.getWorld();
        // проверка на вхождение мышки в прямоугольник
        if(mousePos.x <= xAxisObject.stateInWorld.getPosition().x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.stateInWorld.getPosition().x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.stateInWorld.getPosition().y - (gizmoHeight / 2.0f) &&
                mousePos.y <= xAxisObject.stateInWorld.getPosition().y + (gizmoWidth / 2.0f))
        {
            isUseGizmo = true;
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }

        xAxisSprite.setColor(xAxisColor);
        return false;
    }

    public boolean checkYHoverState()
    {
        Vector2f mousePos = Mouse.getWorld();
        if (mousePos.x <= yAxisObject.stateInWorld.getPosition().x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.stateInWorld.getPosition().x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.stateInWorld.getPosition().y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.stateInWorld.getPosition().y - (gizmoHeight / 2.0f)) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }

        yAxisSprite.setColor(yAxisColor);
        return false;
    }

    public void setUsing()
    {
        this.using = true;
    }

    public void setNotUsing()
    {
        this.using = false;
        this.setInactive();
    }
}
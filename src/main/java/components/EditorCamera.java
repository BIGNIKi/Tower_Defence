package components;

import job.Camera;
import job.Keyboard;
import job.Mouse;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component
{
    //TODO: костыльная фигня, так как это привязка к кадрам (надо бы привязаться ко времени)
    private float dragDebounce = 0.032f; //это 2 кадра при стабильных 60FPS

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;

    private float lerpTimePos = 0.0f;
    private float lerpTimeSize = 0.0f;
    private float dragSensitivity = 0.3f;
    private float scrollSensitivity = 0.1f;

    public EditorCamera(Camera levelEditorCamera)
    {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void editorUpdate(float dt)
    {
        if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0)
        {
            this.clickOrigin = Mouse.getWorld();
            dragDebounce -= dt;
            return;
        }
        else if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
        {
            Vector2f mousePos = Mouse.getWorld();
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
            // если мы пытаемся двигать камеру, отключаем режим сброса положения камеры
            setResetToFalse();
        }

        if(dragDebounce <= 0.0f && !Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE))
        {
            dragDebounce = 0.1f;
        }

        // приближение, отдаление камеры
        if(Mouse.getScrollY() != 0.0f)
        {
            float addValue = (float)Math.pow(Math.abs(Mouse.getScrollY() * scrollSensitivity),
                    1/levelEditorCamera.getZoom());
            addValue *= -Math.signum(Mouse.getScrollY());
            levelEditorCamera.addZoom(addValue);
            setResetToFalse();
        }

        if(Keyboard.isKeyPressed(GLFW_KEY_R))
        {
            reset = true;
        }

        if(reset)
        {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTimePos);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTimeSize));
            this.lerpTimePos += 0.01f * dt;
            this.lerpTimeSize += 0.025f * dt;
            if(Math.abs(levelEditorCamera.position.x) <= 0.001f &&
                    Math.abs(levelEditorCamera.position.y) <= 0.001f &&
                    levelEditorCamera.getZoom() > 0.997f && levelEditorCamera.getZoom() < 1.003f)
            {
                levelEditorCamera.position.set(0f, 0f);
                this.levelEditorCamera.setZoom(1.0f);
                setResetToFalse();
            }
        }
    }

    // отключение режима сброса положения камеры к изначальному
    private void setResetToFalse()
    {
        this.lerpTimePos = 0.0f;
        this.lerpTimeSize = 0.0f;
        reset = false;
    }
}
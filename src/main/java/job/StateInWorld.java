package job;

import components.Component;
import editor.JImGui;
import org.joml.Vector2f;
import org.joml.Vector2fc;

public class StateInWorld extends Component
{
    private Vector2f position;
    private Vector2f size;
    private float rotation = 0.0f;

    public Vector2f getPosition()
    {
        return position;
    }

    public void setPosition(Vector2f position)
    {
        this.position = position;
    }

    public void addToPosition(Vector2f position)
    {
        this.position.x += position.x;
        this.position.y += position.y;
    }


    public Vector2f getScale()
    {
        return size;
    }

    public void setScale(Vector2f scale)
    {
        this.size = scale;
    }

    public float getRotation()
    {
        return rotation;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }

    public StateInWorld()
    {
        init(new Vector2f(), new Vector2f());
    }

    public StateInWorld(Vector2f position)
    {
        init(position, new Vector2f());
    }

    public StateInWorld(Vector2f position, Vector2f scale)
    {
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale)
    {
        this.position = position;
        this.size = scale;
    }

    public StateInWorld copy()
    {
        return new StateInWorld(new Vector2f(this.position), new Vector2f(this.size));
    }

    public void copy(StateInWorld to)
    {
        to.position.set(this.position);
        to.size.set(this.size);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null) return false;
        if(!(o instanceof StateInWorld)) return false;

        StateInWorld t = (StateInWorld)o;
        return t.position.equals(this.position) && t.size.equals(this.size) &&
                t.rotation == this.rotation;
    }

    @Override
    public void imgui() {
        gameObject.name = JImGui.inputText("Name: ", gameObject.name);
        super.imgui();
    }

    public void set(Vector2fc v) {
        this.position.x = v.x();
        this.position.y = v.y();
    }
}

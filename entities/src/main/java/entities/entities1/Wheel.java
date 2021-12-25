package entities.entities1;

import entities.components.Component;
import org.joml.Vector2f;

public class Wheel extends Component
{
    @Override
    public void update(float dt)
    {
        this.gameObject.stateInWorld.addToPosition(new Vector2f(-dt/3,0));
        this.gameObject.stateInWorld.setRotation(this.gameObject.stateInWorld.getRotation()+dt*55);
    }
}

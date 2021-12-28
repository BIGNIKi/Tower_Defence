package entities1;

import components.Component;
import org.joml.Vector2f;

public class Bird extends Component
{
    private transient float times = 0;

    @Override
    public void update(float dt)
    {
        times += dt/5;
        this.gameObject.stateInWorld.addToPosition(new Vector2f(2*dt,-times*dt));
    }
}

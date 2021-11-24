package components;

public class TestComponent extends Component
{
    private float speed;
    private float timer = 0.0f;
    private transient int step = 0;

    private float distanceFinish = 0;
    private transient float neededDistance0 = 0.55f;
    private transient float neededDistance1 = 1.05f;
    private transient float neededDistance2 = 1.55f;

    @Override
    public void update(float dt)
    {
        timer += dt;
        distanceFinish += dt*speed;
        if(neededDistance0 > distanceFinish)
        {
            this.gameObject.transform.position.x += dt*speed;
        }
        else if(neededDistance1 > distanceFinish)
        {
            this.gameObject.transform.position.y -= dt*speed;
        }
        else if(neededDistance2 > distanceFinish)
        {
            this.gameObject.transform.position.x += dt*speed;
        }
        else
        {
            this.gameObject.destroy();
        }

    }

    public float getDistanceFinish()
    {
        return distanceFinish;
    }
}

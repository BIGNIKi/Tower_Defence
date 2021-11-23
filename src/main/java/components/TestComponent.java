package components;

public class TestComponent extends Component
{
    private float speed;
    private float timer = 0.0f;
    private transient int step = 0;

    private float distanceFinish = 0;
    private float neededDistance0;
    private float neededDistance1;
    private float neededDistance2;

    @Override
    public void update(float dt)
    {
        timer += dt;
        distanceFinish += dt*speed;
        if(neededDistance0 > distanceFinish)
        {
            this.gameObject.transform.position.x += speed;
        }
        else if(neededDistance1 > distanceFinish)
        {
            this.gameObject.transform.position.y -= speed;
        }
        else if(neededDistance2 > distanceFinish)
        {
            this.gameObject.transform.position.x += speed;
        }

    }
}

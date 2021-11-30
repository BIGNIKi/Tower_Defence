package components;

public class EnemyAI extends Component
{
    private float speed;

    private float distanceFinish = 0;

    @Override
    public void update(float dt)
    {
        distanceFinish += dt*speed;

        float neededDistance0 = 0.55f;
        float neededDistance1 = 1.05f;
        float neededDistance2 = 1.55f;

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
}

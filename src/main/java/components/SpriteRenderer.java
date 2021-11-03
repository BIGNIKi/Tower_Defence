package components;

import job.Component;

public class SpriteRenderer extends Component
{
    public boolean firstTime = false;

    @Override
    public void start()
    {
        System.out.println("I'm starting");
    }

    @Override
    public void update(float dt)
    {
        if(!firstTime)
        {
            System.out.println("I'm updating");
            firstTime = true;
        }
    }
}

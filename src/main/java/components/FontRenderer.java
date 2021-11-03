package components;

import job.Component;

public class FontRenderer extends Component
{
    @Override
    public void start()
    {
        if(gameObject.getComponent(SpriteRenderer.class) != null)
        {
            System.out.println("Found sprite Renderer");
        }
    }

    @Override
    public void update(float dt)
    {

    }
}

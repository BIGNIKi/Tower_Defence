package components;

import job.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import renderer.DebugDraw;

public class TowerRotate extends Component
{
    public transient GameObject goal;

    @Override
    public void start()
    {
        goal = this.gameObject.currentScene.getGameObjectByName("Enemy");
    }

    @Override
    public void update(float dt)
    {
        if(goal != null)
        {
            Vector2f from = this.gameObject.transform.position;
            Vector2f to = goal.transform.position;
            var h = to.x - from.x;
            var w = to.y - from.y;

            var atan = Math.atan(h/w) / Math.PI * 180;
            if (w < 0 || h < 0)
                atan += 180;
            if (w > 0 && h < 0)
                atan -= 180;
            if (atan < 0)
                atan += 360;

            this.gameObject.transform.rotation =  (float)(atan % 360);
        }

        //DebugDraw.addCircle(this.gameObject.transform.position, 1, new Vector3f(0,1,0));
    }
}

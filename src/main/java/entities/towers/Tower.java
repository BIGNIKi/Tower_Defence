package entities.towers;

import components.*;
import entities.effects.Effect;
import entities.monsters.Monster;
import entities.upgrades.Upgrade;
import job.GameObject;
import job.MainWindow;
import job.Transform;
import org.joml.Vector2f;

public class Tower extends Component
{
  public transient int constructionCost;

  private transient int damage;
  private transient int range;
  private transient int firingRate;
  private transient Effect effect;
  private transient TowerType type;
  private transient int level;
  private transient int x;
  private transient int y;

  //public abstract void attack(Monster target);

  //public abstract void use(Upgrade upgrade);

  public TowerType GetType() {
    return this.type;
  }

  public int GetConstructionCost() {
    return this.constructionCost;
  }

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

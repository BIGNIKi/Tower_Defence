package entities.towers;

import Util.SmartCalc;
import components.*;
import entities.effects.Effect;
import job.GameObject;
import job.MainWindow;
import org.joml.Vector2f;
import renderer.DebugDraw;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

  public float rotateSpeed;

  @Override
  public void start()
  {
  }

  @Override
  public void update(float dt)
  {
    List<GameObject> result = GameObject.FindAllByName("Enemy");
    GameObject nearest = null;
    for(GameObject go : result)
    {
      if(nearest == null)
      {
        nearest = go;
        continue;
      }
      if(this.gameObject.stateInWorld.getPosition().distance(go.stateInWorld.getPosition()) <
              this.gameObject.stateInWorld.getPosition().distance(nearest.stateInWorld.getPosition()))
      {
        nearest = go;
      }
    }
    if(nearest != null)
    {
      Vector2f from = this.gameObject.stateInWorld.getPosition();
      Vector2f to = nearest.stateInWorld.getPosition();
      var h = to.x - from.x;
      var w = to.y - from.y;

      var atan = Math.atan(h/w) / Math.PI * 180;
      if (w < 0 || h < 0)
        atan += 180;
      if (w > 0 && h < 0)
        atan -= 180;
      if (atan < 0)
        atan += 360;

      float degree = -(float)(atan % 360);
      this.gameObject.stateInWorld.setRotation(SmartCalc.rotateAtoBwithStepT(this.gameObject.stateInWorld.getRotation(), degree, rotateSpeed*dt));
    }
  }
}

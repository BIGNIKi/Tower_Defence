package entities.towers;

import Util.SmartCalc;
import components.*;
import entities.effects.Effect;
import entities.monsters.Monster;
import job.GameObject;
import job.MainWindow;
import org.joml.Vector2f;
import renderer.DebugDraw;

import java.util.Comparator;
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

  public transient GameObject goal = null;

  public float rotateSpeed; // скорость поворота
  public float observeRadius; // радиус, в котором может стрелять

  @Override
  public void start()
  {
  }

  @Override
  public void update(float dt)
  {
    DebugDraw.addCircle(this.gameObject.stateInWorld.getPosition(), observeRadius);
    if(goal != null)
    {
      if(goal.stateInWorld.getPosition().distance(this.gameObject.stateInWorld.getPosition()) > observeRadius
      || goal.isDead())
      {
        goal = null;
      }
    }
    if(goal == null)
    {
      goal = findGoal();
    }

    if(goal != null)
    {
      Vector2f from = this.gameObject.stateInWorld.getPosition();
      Vector2f to = goal.stateInWorld.getPosition();
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

  private GameObject findGoal()
  {
    Vector2f myPos = this.gameObject.stateInWorld.getPosition();
    List<GameObject> result = GameObject.FindAllByName("Enemy");
    // получили список противников, которые в радиусе поражения
    List<GameObject> inRadius = result.stream()
            .filter(gameObject -> gameObject.stateInWorld.getPosition().distance(myPos) <= observeRadius)
            .collect(Collectors.toList());
    GameObject nearest = null;
    float oldFinishDistance = -1;
    for(GameObject go : inRadius)
    {
      // находит врага, который максимально близко к базе
      if(oldFinishDistance < go.getComponent(Monster.class).getFinishDistance())
      {
        oldFinishDistance = go.getComponent(Monster.class).getFinishDistance();
        nearest = go;
      }
    }

    return nearest;
  }
}

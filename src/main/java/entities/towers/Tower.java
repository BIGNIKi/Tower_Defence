package entities.towers;

import Util.SmartCalc;
import components.*;
import entities.effects.Effect;
import entities.monsters.Monster;
import job.GameObject;
import job.MainWindow;
import job.Prefabs;
import org.joml.Vector2f;
import renderer.DebugDraw;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Tower extends Component
{
/*  public transient int constructionCost;*/

/*  private transient int range;
  private transient int firingRate;
  private transient Effect effect;
  private transient TowerType type;
  private transient int level;
  private transient int x;
  private transient int y;*/

  //public abstract void attack(Monster target);

  //public abstract void use(Upgrade upgrade);

/*  public TowerType GetType() {
    return this.type;
  }*/

/*  public int GetConstructionCost() {
    return this.constructionCost;
  }*/

  public transient GameObject goal = null;

  public transient float rotateSpeed; // скорость поворота
  public transient float observeRadius; // радиус, в котором может стрелять
  public transient float timeToAttack; // время между выстрелами
  private transient float reloadTime = 0; // если 0 - может стрелять
  public transient float damage; // урон

  public void settingTower(float rotateSpeed, float observeRadius, float timeToAttack, float damage)
  {
    this.rotateSpeed = rotateSpeed;
    this.observeRadius = observeRadius;
    this.timeToAttack = timeToAttack;
    this.damage = damage;
  }

  @Override
  public void start()
  {
  }

  @Override
  public void update(float dt)
  {
    // строчка ниже отрисует дистанцию поражения
    // DebugDraw.addCircle(this.gameObject.stateInWorld.getPosition(), observeRadius);
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
      if(goal == null)
      {
        GameObject goalToSee = findNearest(); // ищем ближайшего врага, чтобы смотреть в его сторону (хоть и не можем стрелять, зато можем на него смотреть)
        if(goalToSee != null)
        {
          seeTowardsObj(dt, goalToSee);
        }
      }
    }

    if(goal != null)
    {
      seeTowardsObj(dt, goal);
      attack(dt);
    }
  }

  private void seeTowardsObj(float dt, GameObject obj)
  {
    float degree = SmartCalc.getAngleToVec(this.gameObject.stateInWorld.getPosition(), obj.stateInWorld.getPosition());
    this.gameObject.stateInWorld.setRotation(SmartCalc.rotateAtoBwithStepT(this.gameObject.stateInWorld.getRotation(), degree, rotateSpeed*dt));
  }

  private GameObject findGoal()
  {
    Vector2f myPos = this.gameObject.stateInWorld.getPosition();
    List<GameObject> result = GameObject.FindAllByName("Enemy");
    // получили список противников, которые в радиусе поражения
    List<GameObject> inRadius = result.stream()
            .filter(gameObject -> gameObject.stateInWorld.getPosition().distance(myPos) <= observeRadius
                    && gameObject.getComponent(Monster.class) != null)
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

  private GameObject findNearest()
  {
    Vector2f myPos = this.gameObject.stateInWorld.getPosition();
    List<GameObject> result = GameObject.FindAllByName("Enemy");
    // получили список противников, которые в радиусе поражения
    List<GameObject> inRadius = result.stream()
            .filter(gameObject -> gameObject.getComponent(Monster.class) != null)
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

  private void attack(float dt)
  {
    reloadTime -= dt;
    if(reloadTime <= 0)
    {
      reloadTime = 0;
    }
    // если башня смотрит (ПОЧTI) на цель (только тогда можно стрелять)
    float angleToGoal = Math.abs(
            SmartCalc.getNorm(SmartCalc.getAngleToVec(this.gameObject.stateInWorld.getPosition(), goal.stateInWorld.getPosition()))
            - SmartCalc.getNorm(this.gameObject.stateInWorld.getRotation())
    );
    if(angleToGoal < 15)
    {
      if(reloadTime <= 0)
      {
        Prefabs.addBullet(goal, this.gameObject.stateInWorld.getPosition(), damage); // создает пулю
        if(goal.getComponent(Monster.class).amIDie(damage)) goal = null; // если этот чечен будет убит, убираем как цель

        reloadTime = timeToAttack;
      }
    }

  }
}

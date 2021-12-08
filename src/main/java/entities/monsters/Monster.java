package entities.monsters;

import Util.SmartCalc;
import Util.StringList;
import components.Component;
import job.GameObject;
import org.joml.Vector2f;

public class Monster extends Component
{

  /*
  private int coinsForKilling;
  private int experienceForKilling;

  private int health;
  private int damage;
  private int range;
  private int firingRate;
  private int travelSpeed;
  private Effect effect;
  private double shareOfDamageTaken;
  private TowerEfficiency towerEfficiency;
  private int x;
  private int y;
*/

    private transient float speed;

    private transient StringList wayPoints; // список имен точек, по которым нужно ходить
    private transient int numOfPointsNow = 0; // точка, в которой мы сейчас
    private transient float currentTime = 0;
    private transient float timeOfTravel;
    private transient Vector2f startPosition; // позиция врага перед тем, как идти к точке
    private transient boolean isInProgress = false; // идет ли уже к точке
    private transient GameObject goal; // точка, к которой идет противник
    private transient float finishDistance = 0; // общая пройденная дистанция

    public void settingMonster(float speed, StringList wayPoints)
    {
        this.speed = speed;
        this.wayPoints = (StringList) wayPoints.clone();
    }

    @Override
    public void start()
    {

    }

    @Override
    public void update(float dt)
    {
        if(!isInProgress)
        {
            if(numOfPointsNow >= wayPoints.size())
            {
                this.gameObject.destroy();
                return;
            }
            else
            {
                goal = GameObject.Find(wayPoints.get(numOfPointsNow));
            }
            numOfPointsNow++;
            if(goal != null)
            {
                float distance = Vector2f.distance(this.gameObject.stateInWorld.getPosition().x, this.gameObject.stateInWorld.getPosition().y,
                        goal.stateInWorld.getPosition().x, goal.stateInWorld.getPosition().y);
                timeOfTravel = distance/speed;
                startPosition = this.gameObject.stateInWorld.getPosition();
                isInProgress = true;
            }
        }
        if(goal != null)
        {
            currentTime += dt;
            finishDistance += dt*speed;
            System.out.println(finishDistance);
            if(currentTime < timeOfTravel) {
                float normalizedValue = currentTime / timeOfTravel;
                this.gameObject.stateInWorld.setPosition(SmartCalc.Lerp(startPosition, goal.stateInWorld.getPosition(), normalizedValue));
            }
            else
            {
                goal = null;
                isInProgress = false;
                currentTime = 0;
            }
        }
    }

    public float getFinishDistance()
    {
        return finishDistance;
    }
}

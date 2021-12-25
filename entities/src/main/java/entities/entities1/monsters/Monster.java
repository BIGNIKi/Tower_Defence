package entities.entities1.monsters;

import utils.SmartCalc;
import utils.StringList;
import entities.components.Component;
import entities.components.SpriteRenderer;
import entities.job.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;

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
    private transient float health; // число хп максимальное
    private transient float healthNow; // число хп сейчас
    private transient int moneyForKill; // число coin'ов за убийство

    public void settingMonster(float speed, StringList wayPoints, float health, int moneyForKill)
    {
        this.speed = speed;
        this.wayPoints = (StringList) wayPoints.clone();
        this.health = health;
        this.healthNow = health;
        this.moneyForKill = moneyForKill;
    }

    @Override
    public void start()
    {

    }

    @Override
    public void update(float dt)
    {
        changeColor();
        if(!isInProgress)
        {
            if(numOfPointsNow >= wayPoints.size())
            {
                GameObject lvlCntrl = GameObject.FindWithComp(LevelCntrl.class);
                if(lvlCntrl != null)
                {
                    lvlCntrl.getComponent(LevelCntrl.class).getDamage();
                }
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

    public boolean amIDie(float damage)
    {
        float helthWillHave = healthNow - damage;
        if(helthWillHave <= 0)
        {
            return true;
        }
        return false;
    }

    // получение урона
    // return true - если умер
    public void getDamage(float damage)
    {
        healthNow -= damage;
        if(healthNow <= 0)
        {
            GameObject lvlCntrl = GameObject.FindWithComp(LevelCntrl.class);
            if(lvlCntrl != null)
            {
                lvlCntrl.getComponent(LevelCntrl.class).addCoin(moneyForKill);
            }
            this.gameObject.destroy();
        }
    }

    private void changeColor()
    {
        SpriteRenderer sR = this.gameObject.getComponent(SpriteRenderer.class);
        float percent = healthNow * 100 / health;
        if(percent >= 50)
        {
            float temp = 100 - percent;
            sR.setColor(new Vector4f(temp/50,1,0,1));
        }
        else if(percent<50 && percent >= 0)
        {
            sR.setColor(new Vector4f(1, percent/50, 0, 1));
        }
    }
}

package entities.monsters;

import components.Component;
import entities.Castle;
import entities.common.Direction;
import entities.common.TowerEfficiency;
import entities.effects.Effect;
import entities.towers.TowerType;

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

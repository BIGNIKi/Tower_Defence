package entities.entities1;

import Util.SmartCalc;
import entities.components.Component;
import entities.entities1.monsters.Monster;
import entities.job.GameObject;
import org.joml.Vector2f;

public class Bullet extends Component
{
    private transient float currentTime = 0;
    private final transient float timeOfTravel = 0.5f;
    private transient GameObject goal;
    private transient Vector2f startPosition;
    private transient float damage;

    public void settingBullet(GameObject goal, Vector2f startPosition, float damage)
    {
        this.goal = goal;
        this.startPosition = startPosition;
        this.damage = damage;
    }

    @Override
    public void update(float dt)
    {
        this.gameObject.stateInWorld.setPosition(goal.stateInWorld.getPosition());
        currentTime += dt;
        if(currentTime < timeOfTravel) {
            float normalizedValue = currentTime / timeOfTravel;
            this.gameObject.stateInWorld.setPosition(SmartCalc.Lerp(startPosition, goal.stateInWorld.getPosition(), normalizedValue));
        }
        else
        {
            goal.getComponent(Monster.class).getDamage(damage);
            this.gameObject.destroy();
        }
    }
}

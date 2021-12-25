package core.controllers;

import utils.StringList;
import entities.components.Component;
import entities.job.Prefabs;

public class Waves extends Component
{
    private float speed; // скорость врагов

    public transient int alreadyMonsters = 0;
    public int numOfMonsters; // число монстров
    public float healthOfMonsters; // здоровье монстров

    public transient float alreadyTime = 0;
    public float timeBetweenMonsters; // время между спауном монстров
    public float timeToStart; // время до начала атаки
    public int moneyForKill; // число монет за убийство моба

    private StringList wayPoints = new StringList(); // список имен точек, по которым нужно ходить

    @Override
    public void update(float dt)
    {
        alreadyTime += dt;
        if(timeToStart > 0.0f)
        {
            timeToStart -= dt;
            return;
        }
        if(alreadyMonsters < numOfMonsters && alreadyTime >= timeBetweenMonsters)
        {
            alreadyTime = 0;

            Prefabs.addEnemy(speed, wayPoints, this.gameObject.stateInWorld.getPosition(), healthOfMonsters, moneyForKill);

            alreadyMonsters++;
        }
    }
}

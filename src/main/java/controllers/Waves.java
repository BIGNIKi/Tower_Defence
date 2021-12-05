package controllers;

import Util.StringList;
import components.Component;
import job.Prefabs;

public class Waves extends Component
{
    private float speed; // скорость врагов

    public transient int alreadyMonsters = 0;
    public int numOfMonsters; // число монстров

    public transient float alreadyTime = 0;
    public float timeBetweenMonsters; // время между спауном монстров

    private StringList wayPoints = new StringList(); // список имен точек, по которым нужно ходить

    @Override
    public void update(float dt)
    {
        alreadyTime += dt;
        if(alreadyMonsters < numOfMonsters && alreadyTime >= timeBetweenMonsters)
        {
            alreadyTime = 0;

            Prefabs.addEnemy(speed, wayPoints, this.gameObject.stateInWorld.getPosition());

            alreadyMonsters++;
        }
    }
}

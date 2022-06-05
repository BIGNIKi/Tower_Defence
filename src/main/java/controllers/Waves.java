package controllers;

import SyncStuff.MonsterClass;
import Util.StringList;
import components.Component;
import job.Prefabs;
import org.joml.Vector2f;

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

    private transient boolean _isWaitingForEnemy = false; // настройка для ожидания противника онлайн

    @Override
    public void update(float dt)
    {
        if(_isWaitingForEnemy)
            return;

        alreadyTime += dt;
        if(timeToStart > 0.0f)
        {
            timeToStart -= dt;
            return;
        }
        if(alreadyMonsters < numOfMonsters && alreadyTime >= timeBetweenMonsters)
        {
            alreadyTime = 0;

            Prefabs.addEnemy(speed, wayPoints, this.gameObject.stateInWorld.getPosition(), healthOfMonsters, moneyForKill, healthOfMonsters);

            alreadyMonsters++;
        }
    }

    public void CreateMonsterSync(MonsterClass mC)
    {
        Vector2f actualPos = new Vector2f(mC.posX, mC.posY);
        Prefabs.addEnemy(speed, wayPoints, actualPos, healthOfMonsters, moneyForKill, mC.health);
    }

    public void setAlreadyMonsters(int alreadyMonsters)
    {
        this.alreadyMonsters = alreadyMonsters;
    }

    public void SetisWaitingForEnemy(boolean val)
    {
        _isWaitingForEnemy = val;
    }

    public boolean GetisWaitingForEnemy()
    {
        return _isWaitingForEnemy;
    }
}

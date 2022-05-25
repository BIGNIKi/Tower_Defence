package controllers;

import SyncStuff.MonsterClass;
import SyncStuff.SyncClasses;
import SyncStuff.TowerClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import entities.monsters.Monster;
import entities.towers.PlaceForTower;
import job.GameObject;
import job.Prefabs;
import onlineStuff.OurWebRequest;
import onlineStuff.WWWForm;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;


public class OnlineObserver extends Component
{
    private transient OurWebRequest www = null;

    private transient String _sessionId = null;
    private transient int _numPlayer = -1;
    private transient boolean _isGameStarted = false;

    private transient final float TimeToCheck = 0.5f;
    private transient float _actualTime = 0f;

    private transient final float TimeToSync = 5f;
    private transient float _actualTimeSync = 0f; // текущее время для синхронизации
    private transient int _syncId = 0; //номер последней синхронизации

    public String get_sessionId()
    {
        return _sessionId;
    }

    public int get_numPlayer()
    {
        return _numPlayer;
    }

    private int getEnemyId()
    {
        if(_numPlayer == 1)
            return 2;
        else
            return 1;
    }

    @Override
    public void OnStartScene()
    {
        WWWForm form = new WWWForm();
        //form.AddField("name", "John Doe");

        www = OurWebRequest.Post("http://abobnik228.ru/main/findSession.php", form);
        www.SendWebRequest();

        GameObject.FindWithComp(Waves.class).getComponent(Waves.class).SetisWaitingForEnemy(true);
    }

    @Override
    public void update(float dt)
    {
        if(_sessionId == null && www != null && www.CheckError() == OurWebRequest.Status.Success)
        {
            System.out.println(www.GetResponseBody());
            if(www.GetResponseBody().equals("Nope"))
            {
                byte[] array = new byte[7]; // length is bounded by 7
                new Random().nextBytes(array);
                _sessionId = new String(array, StandardCharsets.UTF_8);
                _numPlayer = 1;
                WWWForm form = new WWWForm();
                form.AddField("id", _sessionId);
                www = OurWebRequest.Post("http://abobnik228.ru/main/createSession.php", form);
                www.SendWebRequest();
            }
            else
            {
                _numPlayer = 2;
                _sessionId = www.GetResponseBody();
                //System.out.println(_sessionId);
                GameObject.FindWithComp(Waves.class).getComponent(Waves.class).SetisWaitingForEnemy(false);
                _isGameStarted = true;
            }

            www = null;
        }

        // проверка на то, что нашёлся противник
        if(_sessionId != null && !_isGameStarted)
        {
            if(www == null)
            {
                WWWForm form = new WWWForm();
                form.AddField("sessionId", _sessionId);
                www = OurWebRequest.Post("http://abobnik228.ru/main/checkSession.php", form);
                www.SendWebRequest();
            }
            else if(www.CheckError() == OurWebRequest.Status.Success)
            {
                System.out.println(www.GetResponseBody());
                if(www.GetResponseBody().equals("GO"))
                {
                    GameObject.FindWithComp(Waves.class).getComponent(Waves.class).SetisWaitingForEnemy(false);
                    _isGameStarted = true;
                }

                www = null;
            }
        }

        _actualTime += dt;
        if(_sessionId != null && _isGameStarted)
        {
            if(www == null && _actualTime >= TimeToCheck)
            {
                _actualTime = 0;
                WWWForm form = new WWWForm();
                form.AddField("sessionId", get_sessionId());
                form.AddField("idPlayer", get_numPlayer());
                www = OurWebRequest.Post("http://abobnik228.ru/main/checkNewTower.php", form);
                www.SendWebRequest();
            }
            else if(www != null && www.CheckError() == OurWebRequest.Status.Success)
            {


                if(!www.GetResponseBody().equals("Nope"))
                {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();
                    PlaceForTower.TowerPlaceData tPD = gson.fromJson(www.GetResponseBody(), PlaceForTower.TowerPlaceData.class);
                    Prefabs.addTower(tPD.position, tPD.pathSpr0, tPD.sizeTower, tPD.pathSpr1, tPD.initialRotation,
                            tPD.rotateSpeed, tPD.observeRadius, tPD.timeToAttack, tPD.damage);
                    GameObject.Find(tPD.placeName).getComponent(PlaceForTower.class).SetCantBeSelected();
                }

                www = null;
            }

            if(_numPlayer == 1) // если мы - хост
            {
                _actualTimeSync += dt;
                if(_actualTimeSync >= TimeToSync)
                {
                    _actualTimeSync = 0;
                    SyncClasses syncCl = new SyncClasses();

                    List<GameObject> nL = GameObject.FindAllByName("TowerSt");
                    for(GameObject go : nL)
                    {
                        TowerClass newTower = new TowerClass();
                        newTower.posX = go.stateInWorld.getPosition().x;
                        newTower.posY = go.stateInWorld.getPosition().y;
                        newTower.angle = go.stateInWorld.getRotation();
                        syncCl.towerClasses.add(newTower);
                    }

                    List<GameObject> enemies = GameObject.FindAllByName("Enemy");
                    for(GameObject go : enemies)
                    {
                        MonsterClass newMonstr = new MonsterClass();
                        newMonstr.posX = go.stateInWorld.getPosition().x;
                        newMonstr.posY = go.stateInWorld.getPosition().y;
                        Monster m = go.getComponent(Monster.class);
                        newMonstr.health = m.getHealthNow();
                        syncCl.monsterClasses.add(newMonstr);
                    }

                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();
                    System.out.println(gson.toJson(syncCl));
                }
            }
        }
    }
}

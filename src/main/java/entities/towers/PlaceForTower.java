package entities.towers;

import Util.AssetPool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.MouseControls;
import components.Sprite;
import controllers.LevelCntrl;
import controllers.OnlineObserver;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import job.GameObject;
import job.GameObjectDeserializer;
import job.Prefabs;
import onlineStuff.OurWebRequest;
import onlineStuff.WWWForm;
import org.joml.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaceForTower extends Component
{
    private transient boolean isIamSelected = false;
    private transient boolean cantBeSelected = false;

    //private transient OurWebRequest wwwTest = null;

    private transient final List<Sprite> btnTexture = new ArrayList<>(); // все спрайты, которые нужно будет отобразить как кнопки

    private int initialRotation;

    public PlaceForTower()
    {
        textureToSprite("assets/images/fullBlue.png");
        textureToSprite("assets/images/fullGreen.png");
        textureToSprite("assets/images/fullRed.png");
    }

    private void textureToSprite(String path)
    {
        Sprite sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture(path));
        btnTexture.add(sprite);
    }

    @Override
    public void update(float dt)
    {
/*        if(wwwTest != null)
        {
            if(wwwTest.GetResponseBody() != null)
            {
                System.out.println(wwwTest.GetResponseBody());
            }
        }*/
    }

    public void selectWindowDraw()
    {
        if(cantBeSelected)
        {
            return;
        }
        if(isIamSelected)
        {
            ImGui.setNextWindowSize(140, 270);
            ImGui.begin("Tower selection:", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking);

            GameObject lvlCntrl = GameObject.FindWithComp(LevelCntrl.class);

            int costBlue = 0, costGreen = 0, costRed = 0;
            if(lvlCntrl != null)
            {
                LevelCntrl lc = lvlCntrl.getComponent(LevelCntrl.class);
                costGreen = lc.costGreen;
                costBlue = lc.costBlue;
                costRed = lc.costRed;
            }

            ImGui.text("Price = " + costBlue);
            genTowerSel(0);
            ImGui.text("Price = " + costGreen);
            genTowerSel(1);
            ImGui.text("Price = " + costRed);
            genTowerSel(2);


            ImGui.end();
        }

    }

    // создает кнопку выбора башни
    private void genTowerSel(int ide)
    {
        Sprite sprite = btnTexture.get(ide);
        int id = sprite.getTexId();
        Vector2f[] texCoords = sprite.getTexCoords();

        ImGui.pushID(ide);
        // количество пикселей в ширину
        float spriteWidth = 50;
        // в длину
        float spriteHeight = 50;

        GameObject onlineTool = GameObject.FindWithComp(OnlineObserver.class);
        if(onlineTool != null && onlineTool.getComponent(OnlineObserver.class).get_sessionId() != null)
        {
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
            {
                OnTowerLogic(ide);
            }
        }
        else
        {
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
            {
                OnTowerLogic(ide);
            }
        }
        ImGui.popID();
    }

    private void OnTowerLogic(int ide)
    {
        GameObject lvlCntrl = GameObject.FindWithComp(LevelCntrl.class);
        if(lvlCntrl != null)
        {
            LevelCntrl lC = lvlCntrl.getComponent(LevelCntrl.class);
            int cost;
            String path;
            Vector2f size = null;
            String path1;
            float rotateSpeed, observeRadius, timeToAttack, damage;
            //TODO: переделать на towertype
            switch(ide)
            {
                case 0 -> { // blue
                    cost = lC.costBlue;
                    path = "assets/images/standTower.png";
                    size = new Vector2f(0.086f, 0.154f);
                    path1 = "assets/images/standTower1.png";
                    rotateSpeed = 60;
                    observeRadius = 0.5f;
                    timeToAttack = 0.25f;
                    damage = 8;
                }
                case 1 -> {
                    cost = lC.costGreen;
                    path = "assets/images/standTowerGreen.png";
                    size = new Vector2f(0.064f, 0.065f);
                    path1 = "assets/images/standTower1Green.png";
                    rotateSpeed = 60;
                    observeRadius = 0.5f;
                    timeToAttack = 1f;
                    damage = 32;
                }
                case 2 -> {
                    cost = lC.costRed;
                    path = "assets/images/standTowerRed.png";
                    size = new Vector2f(0.097f, 0.099f);
                    path1 = "assets/images/standTower1Red.png";
                    rotateSpeed = 60;
                    observeRadius = 0.5f;
                    timeToAttack = 2f;
                    damage = 100;
                }
                default -> {
                    cost = 0;
                    path = "";
                    size = new Vector2f(0, 0);
                    path1 = "";
                    rotateSpeed = 0;
                    observeRadius = 0;
                    timeToAttack = 0;
                    damage = 0;
                }
            }
            if(lC.getCoin() >= cost)
            {
                Prefabs.addTower(this.gameObject.stateInWorld.getPosition(), path,
                        size, path1, initialRotation, rotateSpeed, observeRadius, timeToAttack, damage);
                lC.addCoin(-cost);
                resetSelected();

                GameObject onlineTool = GameObject.FindWithComp(OnlineObserver.class);
                if(onlineTool != null)
                {
                    SendOnServerThatPlaced(this.gameObject.stateInWorld.getPosition(), path,
                            size, path1, initialRotation, rotateSpeed, observeRadius, timeToAttack, damage);
                }

                cantBeSelected = true;
            }
        }
    }

    private void SendOnServerThatPlaced(Vector2f position, String pathSpr0, Vector2f sizeTower,
                                        String pathSpr1, float initialRotation, float rotateSpeed,
                                        float observeRadius, float timeToAttack, float damage)
    {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        TowerPlaceData tPD = new TowerPlaceData(position, pathSpr0, sizeTower, pathSpr1, initialRotation,
            rotateSpeed, observeRadius, timeToAttack, damage);

        String jsonText = gson.toJson(tPD);

        WWWForm form = new WWWForm();
        form.AddField("placeData", jsonText);
        GameObject onlineTool = GameObject.FindWithComp(OnlineObserver.class);
        form.AddField("sessionId", onlineTool.getComponent(OnlineObserver.class).get_sessionId());
        form.AddField("idPlayer", onlineTool.getComponent(OnlineObserver.class).get_numPlayer());
        OurWebRequest www = OurWebRequest.Post("http://abobnik228.ru/main/placeTower.php", form);
        www.SendWebRequest();
            //wwwTest = www;
    }

    public class TowerPlaceData
    {
        public Vector2f position;
        public String pathSpr0;
        public Vector2f sizeTower;
        public String pathSpr1;
        public float initialRotation;
        public float rotateSpeed;
        public float observeRadius;
        public float timeToAttack;
        public float damage;

        public TowerPlaceData(Vector2f position, String pathSpr0, Vector2f sizeTower,
                              String pathSpr1, float initialRotation, float rotateSpeed,
                              float observeRadius, float timeToAttack, float damage)
        {
            this.position = position;
            this.pathSpr0 = pathSpr0;
            this.sizeTower = sizeTower;
            this.pathSpr1 = pathSpr1;
            this.initialRotation = initialRotation;
            this.rotateSpeed = rotateSpeed;
            this.observeRadius = observeRadius;
            this.timeToAttack = timeToAttack;
            this.damage = damage;
        }
    }

    public void setSelected()
    {
        isIamSelected = true;
    }

    public void resetSelected()
    {
        isIamSelected = false;
    }
}

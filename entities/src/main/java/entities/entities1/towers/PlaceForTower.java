package entities.entities1.towers;

import Util.AssetPool;
import entities.components.Component;
import entities.components.Sprite;
import controllers.LevelCntrl;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import entities.job.GameObject;
import entities.job.Prefabs;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class PlaceForTower extends Component
{
    private transient boolean isIamSelected = false;
    private transient boolean cantBeSelected = false;

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
        if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
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
                    cantBeSelected = true;
                }
            }
        }
        ImGui.popID();
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

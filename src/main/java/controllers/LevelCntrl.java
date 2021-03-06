package controllers;

import components.Component;
import entities.towers.PlaceForTower;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import job.GameObject;
import job.MainWindow;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

public class LevelCntrl extends Component
{
    private transient boolean isGame = false;

    private int coin;
    private int baseHpMax;
    private transient int baseHpNow;
    public int costBlue;
    public int costGreen;
    public int costRed;

    @Override
    public void start()
    {
        baseHpNow = baseHpMax;
    }

    @Override
    public void update(float dt)
    {
        isGame = true;
        if(baseHpNow <= 0)
        {
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
            MainWindow.getImguiLayer().getGameViewWindow().changePlayMode();
        }
    }

    @Override
    public void editorUpdate(float dt)
    {
        isGame = false;
    }

    public void guiStatus()
    {
        if(isGame)
        {
            ImGui.begin("Level state", ImGuiWindowFlags.NoDocking);
            ImGui.text("Base hp = " + baseHpNow);
            ImGui.text("Coin = " + coin);
            GameObject wGO = GameObject.FindWithComp(Waves.class);
            if(wGO!=null)
            {
                Waves w = wGO.getComponent(Waves.class);
                if(w.timeToStart > 0f)
                {
                    ImGui.text("The attack will start in = " + w.timeToStart);
                }
                ImGui.text("Monsters left = " + (w.numOfMonsters - w.alreadyMonsters));
            }
            if(GameObject.FindWithComp(Waves.class).getComponent(Waves.class).GetisWaitingForEnemy())
            {
                ImGui.text("Waiting for enemy...");
            }
            ImGui.end();
        }
    }

    public void addCoin(int coinToAdd)
    {
        this.coin += coinToAdd;
    }

    public void getDamage()
    {
        baseHpNow--;
    }

    public int getCoin()
    {
        return coin;
    }
}

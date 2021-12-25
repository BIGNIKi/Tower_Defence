package entities1;

import entities1.towers.Tower;
import java.util.ArrayList;
import java.util.LinkedList;

public class Level {
  private ArrayList<Wave> waves;
  private int currentWave = 0;
  private LinkedList<Tower> towers;
  private Player player;
  private Castle castle;
  private long experienceForCompletion;
  private long currentCoins = 0;

  public Level(ArrayList<Wave> waves, long experienceForCompletion) {
    waves = waves;
    experienceForCompletion = experienceForCompletion;
  }

  public void SetPlayer(Player player) {
    towers = new LinkedList<Tower>();
    castle = player.getCastle();
  }

  public void SetWaves(ArrayList<Wave> waves) {
    waves = waves;
  }

  public ArrayList<Wave> GetWaves() {
    return waves;
  }

  public Wave GetCurrentWave(){
    return waves.get(currentWave);
  }

  public Player GetPlayer() { return player; }

  public long GetCurrentCoins() { return currentCoins; }

  public void AddTower(Tower tower) {
    towers.add(tower);
  }

  public void DecreaseCoins(int getConstructionCost) {
    this.currentCoins -= getConstructionCost;
  }
}

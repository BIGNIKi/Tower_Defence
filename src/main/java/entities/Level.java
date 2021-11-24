package entities;

import entities.towers.Tower;
import java.util.ArrayList;

public class Level {
  private ArrayList<Wave> waves;
  private long currentWave = 0;
  private ArrayList<Tower> towers;
  private Player player;
  private Castle castle;
  private long experienceForCompletion;
  private long currentCoins = 0;

  public Level(ArrayList<Wave> waves, long experienceForCompletion) {
    waves = waves;
    experienceForCompletion = experienceForCompletion;
  }

  public void SetPlayer(Player player) {
    towers = player.getTowers();
    castle = player.getCastle();
  }

  public void SetWaves(ArrayList<Wave> waves) {
    waves = waves;
  }

  public ArrayList<Wave> GetWaves() {
    return waves;
  }
}

package entities.entities1;

import entities.entities1.researching.ResearchTree;
import entities.entities1.abitilies.Ability;
import entities.entities1.towers.TowerType;
import java.util.ArrayList;

public class Player {
  private ArrayList<TowerType> availableTowerTypes;
  private ArrayList<Ability> abilities;
  private ArrayList<Level> completedLevels;
  private Castle castle;
  private ResearchTree researchTree;
  private long experience;

  public Player() {

  }

  public boolean HasTowerOfType(TowerType towerType) {
    return availableTowerTypes.contains(towerType);
  }

  public Castle getCastle() {
    return castle;
  }
}

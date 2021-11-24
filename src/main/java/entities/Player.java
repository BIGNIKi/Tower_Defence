package entities;

import entities.abitilies.Ability;
import entities.researching.ResearchTree;
import entities.towers.Tower;
import java.util.ArrayList;

public class Player {
  private ArrayList<Tower> availableTowers;
  private ArrayList<Ability> abilities;
  private ArrayList<Level> completedLevels;
  private Castle castle;
  private ResearchTree researchTree;
  private long experience;

  public Player() {

  }

  public ArrayList<Tower> getTowers(){
    return availableTowers;
  }

  public Castle getCastle() {
    return castle;
  }
}

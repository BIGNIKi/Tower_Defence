package entities.entities1;

import entities.entities1.monsters.Monster;
import entities.entities1.monsters.MonsterType;
import java.util.ArrayList;
import java.util.HashMap;

public class Wave {
  private HashMap<MonsterType, Integer> monsterDistribution;
  private long coinsForCompletion;
  private long experienceForCompletion;
  private ArrayList<Monster> monsters;

  public Wave(HashMap<MonsterType, Integer> monsterDistribution, long coinsForCompletion, long experienceForCompletion) {
    monsterDistribution = monsterDistribution;
    coinsForCompletion = coinsForCompletion;
    experienceForCompletion = experienceForCompletion;
  }

  public ArrayList<Monster> GetMonsters() {
    return (ArrayList<Monster>) monsters.clone();
  }

  public void SetMonsters(ArrayList<Monster> monsters) {
    monsters = monsters;
  }

  public HashMap<MonsterType, Integer> GetMonsterDistribution() {
    return this.monsterDistribution;
  }
}

package entities;

import entities.monsters.Monster;
import entities.monsters.MonsterType;
import java.util.ArrayList;
import java.util.HashMap;

public class Wave {
  private HashMap<MonsterType, Long> monsterDistribution;
  private long coinsForCompletion;
  private long experienceForCompletion;
  private ArrayList<Monster> monsters;
  private GameField GameField;

  public Wave(GameField gameField, HashMap<MonsterType, Long> monsterDistribution, Long coinsForCompletion, Long experienceForCompletion) {
    gameField = gameField;
    monsterDistribution = monsterDistribution;
    coinsForCompletion = coinsForCompletion;
    experienceForCompletion = experienceForCompletion;
  }

  public ArrayList<Monster> getMonsters() {
    return (ArrayList<Monster>) monsters.clone();
  }
}

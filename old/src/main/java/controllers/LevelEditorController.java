package controllers;

import entities1.GameField;
import entities1.Level;
import entities1.Wave;
import entities1.monsters.MonsterType;
import java.util.HashMap;

public class LevelEditorController {
  private Level level;

  public Level CreateLevel() {
    return level;
  }

  public void CreateWave(HashMap<MonsterType, Integer> monsterDistribution, Long experienceForCompletion, Long coinsForCompletion) {
    var wave = new Wave(monsterDistribution, experienceForCompletion, coinsForCompletion);
  }
}

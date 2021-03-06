package controllers;

import entities.GameField;
import entities.Level;
import entities.Wave;
import entities.monsters.MonsterType;
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

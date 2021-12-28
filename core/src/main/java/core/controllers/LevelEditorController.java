package core.controllers;

import entities.entities1.Level;
import entities.entities1.Wave;
import entities.entities1.monsters.MonsterType;
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

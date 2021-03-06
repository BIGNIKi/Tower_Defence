package controllers;

import entities.Level;
import entities.Player;
import entities.Wave;
import entities.towers.TowerType;
import java.util.ArrayList;
import java.util.stream.Collectors;
import services.MonstersFactory;
import services.TowersFactory;

public class LevelController {

  private Level level;
  private MonstersFactory monstersFactory;
  private TowersFactory towersFactory;

  public LevelController(Level level) {
    this.level = level;
  }

  public void StartFor(Player player) {
    level.SetPlayer(player);
    initWaves();
  }

  public void StartWave() {
    var curWave = level.GetCurrentWave();
  }

/*  public boolean CreateTower(TowerType towerType, int x, int y) {
    var player = level.GetPlayer();
    if (!player.HasTowerOfType(towerType)) {
      return false;
    }

    var tower = towersFactory.CreateTower(towerType, x, y, level.GetCurrentCoins());
    if (tower == null) {
      return false;
    }

    level.DecreaseCoins(tower.GetConstructionCost());
    level.AddTower(tower);
    return true;
  }*/

  private void initWaves() {
    var waves = level.GetWaves().stream().map(wave -> fillWave(wave))
        .collect(Collectors.toCollection(ArrayList::new));

    level.SetWaves(waves);
  }

  private Wave fillWave(Wave wave) {
    var monsterDistribution = wave.GetMonsterDistribution();

    var monsters = monstersFactory.CreateMonsters(monsterDistribution);

    wave.SetMonsters(monsters);

    return wave;
  }
}


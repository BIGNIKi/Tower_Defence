package services;
import entities.towers.Tower;
import entities.towers.TowerType;
import java.util.ArrayList;

public class TowersFactory {

  private final ArrayList<Tower> availableTowers;

  public TowersFactory(ArrayList<Tower> availableTowers) {
    this.availableTowers = availableTowers;
  }

/*  public Tower CreateTower(TowerType towerType, int x, int y, long coins) {
    var availableTower = availableTowers.stream()
        .filter(tower -> tower.GetType() == towerType)
        .findFirst()
        .orElse(null);

    if (availableTower == null) {
      return null;
    }

    if (availableTower.GetConstructionCost() > coins) {
      return null;
    }

    return availableTower;
  }*/
}

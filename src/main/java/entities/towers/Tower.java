package entities.towers;

import entities.effects.Effect;
import entities.monsters.Monster;
import entities.upgrades.Upgrade;

public abstract class Tower {
  public int constructionCost;

  private int damage;
  private int range;
  private int firingRate;
  private Effect effect;
  private TowerType type;
  private int level;
  private int x;
  private int y;

  public abstract void attack(Monster target);

  public abstract void use(Upgrade upgrade);

  public TowerType GetType() {
    return this.type;
  }

  public int GetConstructionCost() {
    return this.constructionCost;
  }
}

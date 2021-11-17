package entities.towers;

import entities.effects.Effect;
import entities.monsters.Monster;
import entities.upgrades.Upgrade;

public abstract class Tower {
  public static int constructionCost;

  private int damage;
  private int range;
  private int firingRate;
  private Effect effect;
  private int level;
  private int x;
  private int y;

  public abstract void create(int x, int y);

  public abstract void attack(Monster target);

  public abstract void use(Upgrade upgrade);
}

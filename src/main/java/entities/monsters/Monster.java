package entities.monsters;

import entities.Castle;
import entities.common.Direction;
import entities.common.TowerEfficiency;
import entities.effects.Effect;
import entities.towers.TowerType;

public abstract class Monster {
  private int coinsForKilling;
  private int experienceForKilling;

  private int health;
  private int damage;
  private int range;
  private int firingRate;
  private int travelSpeed;
  private Effect effect;
  private double shareOfDamageTaken;
  private TowerEfficiency towerEfficiency;
  private int x;
  private int y;

  public abstract void move(Direction direction);

  public abstract void attack(Castle target);

  public abstract void getDamage(int value, TowerType attacker);

  public abstract void getDamage(int value);

  public abstract void use(Effect effect);

  public abstract void die();

  public boolean isAlive() {
    return health > 0;
  }
}

package controllers;

import entities.Level;
import entities.Player;

public class LevelController {
  private entities.Level level;

  public LevelController(Level level) {
    level = level;
  }

  public void StartFor(Player player) {
    level.SetPlayer(player);
  }
}

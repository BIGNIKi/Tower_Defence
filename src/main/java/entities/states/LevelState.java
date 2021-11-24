package entities.states;

import entities.Castle;
import entities.Level;
import entities.Player;
import entities.towers.Tower;
import java.util.ArrayList;

public class LevelState {
  public Castle Castle;
  public entities.Level Level;
  public ArrayList<Tower> Towers;

  public LevelState(Player player, Level level) {
    
  }

}

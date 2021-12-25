package core.factories;

import entities.entities1.monsters.Monster;
import entities.entities1.monsters.MonsterType;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class MonstersFactory {

  public ArrayList<Monster> CreateMonsters(Map<MonsterType, Integer> monsterDistribution) {
    return monsterDistribution.keySet()
        .stream()
        .map(type -> CreateMonsters(type, monsterDistribution.get(type)))
        .flatMap(ArrayList::stream)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<Monster> CreateMonsters(MonsterType monsterType, int count) {
    var monsters = new ArrayList<Monster>(count);
    for (var i = 0; i < count; i++) {
      //monsters.set(i, CreateMonster(monsterType));
    }

    return monsters;
  }

/*  public Monster CreateMonster(MonsterType monsterType) {
    return switch (monsterType) {
      case REGULAR -> new BasicMonster();
    };
  }*/
}

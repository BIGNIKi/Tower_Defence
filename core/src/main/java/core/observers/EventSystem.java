package core.observers;

import engine.components.GameObject;
import entities.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem
{
    private static final List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public static void notify(GameObject obj, Event event) {
        observers.forEach(o -> o.onNotify(obj, event));
    }
}
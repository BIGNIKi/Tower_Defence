package core.observers;

import engine.components.GameObject;
import entities.events.Event;

public interface Observer {
    void onNotify(GameObject object, Event event);
}
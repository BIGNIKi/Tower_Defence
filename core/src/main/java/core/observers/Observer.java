package core.observers;

import entities.job.GameObject;
import entities.events.Event;

public interface Observer {
    void onNotify(GameObject object, Event event);
}
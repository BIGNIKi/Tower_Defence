package Core;


public interface Observer {
    void onNotify(GameObject object, Event event);
}

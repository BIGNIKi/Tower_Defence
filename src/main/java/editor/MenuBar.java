package editor;

import imgui.ImGui;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

public class MenuBar {

    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl+S")) {
                EventSystem.notify(null, new Event(EventType.SaveLevel));
            }

            if (ImGui.menuItem("Load research tree")) {
                EventSystem.notify(null, new Event(EventType.ResearchTree));
            }

            if (ImGui.menuItem("Load level 1")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel1));
            }

            if (ImGui.menuItem("Load level 2")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel2));
            }

            if (ImGui.menuItem("Load level 3")) {
                EventSystem.notify(null, new Event(EventType.LoadLevel3));
            }

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
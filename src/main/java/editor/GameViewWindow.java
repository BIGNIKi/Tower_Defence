package editor;

import controllers.OnlineObserver;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import job.GameObject;
import job.MainWindow;
import job.Mouse;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import onlineStuff.OurWebRequest;
import onlineStuff.WWWForm;
import org.joml.Vector2f;

public class GameViewWindow
{
    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying;

    public void imgui()
    {
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            GameObject go = GameObject.FindWithComp(OnlineObserver.class);
            if(go != null)
            {
                WWWForm form = new WWWForm();
                form.AddField("sessionId", go.getComponent(OnlineObserver.class).get_sessionId());
                OurWebRequest www = OurWebRequest.Post("http://abobnik228.ru/main/stopSession.php", form);
                www.SendWebRequest();
            }

            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        if(ImGui.menuItem("Replay", "", isPlaying, !isPlaying))
        {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineReplay));
        }
        ImGui.endMenuBar();

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        topY = topLeft.y + windowSize.y;
        rightX = topLeft.x + windowSize.x;
        bottomY = topLeft.y;

        int textureId = MainWindow.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        Mouse.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        Mouse.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    public boolean getWantCaptureMouse()
    {
        return Mouse.getX() >= leftX && Mouse.getX() <= rightX &&
                Mouse.getY() >= bottomY && Mouse.getY() <= topY;
    }

    private ImVec2 getLargestSizeForViewport()
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / MainWindow.getTargetAspectRatio();
        if(aspectHeight > windowSize.y)
        {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * MainWindow.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }

    public void changePlayMode()
    {
        isPlaying = !isPlaying;
    }
}
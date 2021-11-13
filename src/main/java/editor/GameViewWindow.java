package editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import job.MainWindow;
import job.Mouse;
import org.joml.Vector2f;

public class GameViewWindow
{
    private static float leftX, rightX, topY, bottomY;

    public static void imgui()
    {
        ImGui.begin("Область предпросмотра игры", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

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

    public static boolean getWantCaptureMouse()
    {
        return Mouse.getX() >= leftX && Mouse.getX() <= rightX &&
                Mouse.getY() >= bottomY && Mouse.getY() <= topY;
    }

    private static ImVec2 getLargestSizeForViewport()
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

    private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
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
}

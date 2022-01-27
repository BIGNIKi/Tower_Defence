package UI;

import Controls.Mouse;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GameViewWindow {
    private float leftX, rightX, topY, bottomY; // координаты углов внутри окна, чтобы знать, куда рисовать самц сцену

    /**
     * @return true - если мышь внутри игрового окна, иначе - false
     */
    public boolean getWantCaptureMouse()
    {
        return Mouse.getMouseCoords().x >= leftX && Mouse.getMouseCoords().x <= rightX &&
                Mouse.getMouseCoords().y >= bottomY && Mouse.getMouseCoords().y <= topY;
    }

    /**
     * вывод интерфейска окна игры + вывод самой игры (сцены) на это окно
     */
    public void imgui()
    {
        // TODO: несоответсвие с оригиналом
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.MenuBar);

/*        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        ImGui.endMenuBar();*/

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        // В ImGui используется механика кисточки или же курсора, а все элементы рисуются по его текущей позиции.
        // тоесть мы выставляем координаты "кисточки"
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        // topLeft.x -= ImGui.getScrollX();
        // topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        topY = topLeft.y + windowSize.y;
        rightX = topLeft.x + windowSize.x;
        bottomY = topLeft.y;

        //int textureId = MainWindow.getFramebuffer().getTextureId();
        //ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        Mouse.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        Mouse.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    /**
     * @return вернет размеры экрана, куда будет рисоваться сцена
     */
    private ImVec2 getLargestSizeForViewport()
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize); // получает размеры окна сцены в пикселях
        // windowSize.x -= ImGui.getScrollX();
        // windowSize.y -= ImGui.getScrollY();
        //ImGui.getScrollY(); - способ получить значение, на которое проскролили экран

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / WindowSize.getTargetAspectRatio(); // делим, чтобы сохранялось соотношение сторон
        if(aspectHeight > windowSize.y)
        {
            // We must switch to pillarbox mode
            // pillarbox mode - это когда кадр раширяется до полного по ширине, а не по высоте
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * WindowSize.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    /**
     *
     * @param aspectSize - размеры картинки сцены, которая будет рисоваться в окно сцены
     * @return координаты края картинки сцены
     */
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize); // получает размеры окна сцены в пикселях
        // windowSize.x -= ImGui.getScrollX();
        // windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
        // ImGui.getCursorPosX() - это добавляем, чтобы учитывать верхний titlebar окна
        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }
}

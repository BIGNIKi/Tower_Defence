package Components;

import Core.Camera;
import Core.Component;
import Core.MainCycle;
import UI.InGameGraphic.DebugDraw;
import UI.MainWindow;
import Util.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class GridLines extends Component
{
    // отрисовка сетки в редакторе
    @Override
    public void editorUpdate(float dt)
    {
        //TODO: система всё еще не так хороша, как хотелось бы. Нужно провести мат вычисления и высчитать нормальные формулы
        //TODO: в нормальных движках при отдалении сетка становится крупнее, у нас же статичные размеры
        Camera camera = MainCycle.getScene().camera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float width = projectionSize.x * camera.getZoom()+ (5* Settings.GRID_WIDTH);
        float height = projectionSize.y * camera.getZoom()+ (5*Settings.GRID_WIDTH);

        float firstX = ((int)((cameraPos.x - width / 2) / Settings.GRID_WIDTH)-1) * Settings.GRID_WIDTH;
        float firstY = ((int)((cameraPos.y - height / 2) / Settings.GRID_HEIGHT)-1) * Settings.GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 10;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 10;


        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);

        for(int i = 0; i < maxLines; i++)
        {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if(i < numVtLines)
            {
                DebugDraw.addGridLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < numHzLines)
            {
                DebugDraw.addGridLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}

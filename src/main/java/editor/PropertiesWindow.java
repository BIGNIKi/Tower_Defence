package editor;

import imgui.ImGui;
import job.GameObject;
import job.Mouse;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow
{
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture)
    {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene)
    {
        if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT))
        {
            int x = (int)Mouse.getScreenX();
            int y = (int)Mouse.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            activeGameObject = currentScene.getGameObject(gameObjectId);

        }
    }

    public void imgui()
    {
        // TODO: �����, ���� ����� ���������� ������� - �������� ��� ���� ���������, ��� ����������� �� ���������� Game Object'�
        if(activeGameObject != null)
        {
            ImGui.begin("��������: ");
            activeGameObject.imgui();
            ImGui.end();
        }
    }
}

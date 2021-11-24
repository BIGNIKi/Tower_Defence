package editor;

import components.NonPickable;
import components.TestComponent;
import components.TowerRotate;
import imgui.ImGui;
import job.GameObject;
import job.Mouse;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

// ??? ????? ???????? ?? ????????? ???????? ?? ?????
// ????? ???????? ?? ?????????? ????? ???????????
public class PropertiesWindow
{
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture)
    {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene)
    {
        debounce -= dt;
        if(Mouse.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0)
        {
            int x = (int)Mouse.getScreenX();
            int y = (int)Mouse.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x,y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if(pickedObj != null && pickedObj.getComponent(NonPickable.class) == null)
            {
                activeGameObject = pickedObj;
            }
            else if(pickedObj == null && !Mouse.isDragging())
            {
                activeGameObject = null;
            }
            this.debounce = 0.2f;
        }
    }

    public void imgui()
    {
        // TODO: ?????, ???? ????? ?????????? ??????? - ???????? ??? ???? ?????????, ??? ??????????? ?? ?????????? Game Object'?
        if(activeGameObject != null)
        {
            ImGui.begin("Properties");
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add test component"))
                {
                    if(activeGameObject.getComponent(TestComponent.class) == null)
                    {
                        activeGameObject.addComponent(new TestComponent());
                    }
                }
                if (ImGui.menuItem("Add tower rotate"))
                {
                    if(activeGameObject.getComponent(TowerRotate.class) == null)
                    {
                        activeGameObject.addComponent(new TowerRotate());
                    }
                }
                ImGui.endPopup();
            }
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject()
    {
        return this.activeGameObject;
    }

    public void setActiveGameObject(GameObject go) {
        this.activeGameObject = go;
    }
}
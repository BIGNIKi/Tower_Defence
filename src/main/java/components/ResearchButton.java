package components;

import Util.AssetPool;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import job.Prefabs;
import org.joml.Vector2f;


public class ResearchButton extends Component
{
    private transient boolean isIamSelected = false;
    private transient boolean cantBeSelected = false;

    private String nameResearch;
    private transient static int researchPoints = 33;
    private int costResearch;

    private transient Sprite btnTexture = null;

    public ResearchButton()
    {
        btnTexture = new Sprite();
        btnTexture.setTexture(AssetPool.getTexture("assets/images/rshBtn.png"));
    }

    public void selectWindowDraw()
    {
        if(cantBeSelected)
        {
            return;
        }

        if(isIamSelected)
        {
            ImGui.setNextWindowSize(200, 270);
            ImGui.setNextWindowPos(0,810);
            ImGui.begin("Upgrade " + nameResearch, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking);

            ImGui.text("You have " + researchPoints + " research points");
            ImGui.text("Price = " + costResearch);
            genButton();
            ImGui.end();
        }
    }

    public void setSelected()
    {
        isIamSelected = true;
    }

    public void resetSelected()
    {
        isIamSelected = false;
    }

    private void genButton()
    {
        int id = btnTexture.getTexId();
        Vector2f[] texCoords = btnTexture.getTexCoords();

        ImGui.pushID(0);
        // количество пикселей в ширину
        float spriteWidth = 175;
        // в длину
        float spriteHeight = 30f;
        if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y))
        {
            if(researchPoints >=costResearch)
            {
                researchPoints-=costResearch;
                cantBeSelected = true;
                Prefabs.addCheckMark(this.gameObject.stateInWorld.getPosition());
            }
        }
        ImGui.popID();
    }
}

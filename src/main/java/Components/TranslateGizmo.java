package Components;

import Controls.Mouse;
import UI.InGameGraphic.Sprite;
import UI.PropertiesWindow;
import org.joml.Vector2f;

// штука, чтобы двигать игровые объекты в редакторе
public class TranslateGizmo extends Gizmo
{
    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow)
    {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt)
    {
        if(activeGameObject != null)
        {
            if(xAxisActive && !yAxisActive)
            {
                activeGameObject.stateInWorld.addToPosition(new Vector2f(Mouse.getDeltaWorldX(), 0));
            }
            else if(yAxisActive)
            {
                activeGameObject.stateInWorld.addToPosition(new Vector2f(0, Mouse.getDeltaWorldY()));
            }
        }

        super.editorUpdate(dt);
    }
}
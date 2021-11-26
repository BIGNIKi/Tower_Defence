package components;

import editor.PropertiesWindow;
import job.Mouse;

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
                activeGameObject.transform.position.x -= Mouse.getWorldX();
            }
            else if(yAxisActive)
            {
                activeGameObject.transform.position.y -= Mouse.getWorldY();
            }
        }

        super.editorUpdate(dt);
    }
}
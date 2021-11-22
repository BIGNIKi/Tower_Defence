package components;

import editor.PropertiesWindow;
import job.Mouse;

public class ScaleGizmo extends Gizmo {
    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= Mouse.getWorldDx();
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= Mouse.getWorldDy();
            }
        }

        super.editorUpdate(dt);
    }
}
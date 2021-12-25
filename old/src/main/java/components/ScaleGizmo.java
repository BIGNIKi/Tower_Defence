package components;

import editor.PropertiesWindow;
import job.Mouse;
import org.joml.Vector2f;

public class ScaleGizmo extends Gizmo {
    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                //activeGameObject.transform.scale.x += Mouse.getDeltaWorldX();
                Vector2f scal = activeGameObject.stateInWorld.getScale();
                scal.x+= Mouse.getDeltaWorldX();
                activeGameObject.stateInWorld.setScale(scal);
            } else if (yAxisActive) {
                Vector2f scal = activeGameObject.stateInWorld.getScale();
                scal.y += Mouse.getDeltaWorldY();
                activeGameObject.stateInWorld.setScale(scal);
            }
        }

        super.editorUpdate(dt);
    }
}

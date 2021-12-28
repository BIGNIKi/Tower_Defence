package engine.controls;

import org.lwjgl.glfw.GLFW;

public class MouseCode {
    private static int LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    private static int RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;

    public int getMouseCode(String name) throws IllegalAccessException {
        var fields = KeyCode.class.getFields();
        for (var field : fields) {
            if (field.getName().toLowerCase() == name.toLowerCase()) {
                return field.getInt(this);
            }
        }

        return -1;
    }
}

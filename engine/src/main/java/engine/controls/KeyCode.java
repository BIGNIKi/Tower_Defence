package engine.controls;

import org.lwjgl.glfw.GLFW;

public class KeyCode {
  private static int SPACE = GLFW.GLFW_KEY_SPACE;

  public int getKeyCode(String name) throws IllegalAccessException {
    var fields = KeyCode.class.getFields();
    for (var field : fields) {
      if (field.getName().toLowerCase() == name.toLowerCase()) {
        return field.getInt(this);
      }
    }

    return -1;
  }
}

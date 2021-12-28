package engine.components;

import editor.JImGui;
import engine.textures.Texture;
import entities.components.Component;
import entities.components.Sprite;
import entities.job.StateInWorld;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;


public class SpriteRenderer extends Component {

  private Vector4f color = new Vector4f(1, 1, 1, 1);
  private Sprite sprite = new Sprite();

  public int zIndex;

  private transient StateInWorld lastStateInWorld;
  private transient int lastZind;
  private transient boolean isDirty = true;

  @Override
  public void start() {
    this.lastStateInWorld = gameObject.stateInWorld.copy();
    this.lastZind = zIndex;
  }

  @Override
  public void update(float dt) {
    if (!this.lastStateInWorld.equals(this.gameObject.stateInWorld) || this.lastZind != zIndex) {
      this.gameObject.stateInWorld.copy(this.lastStateInWorld);
      this.lastZind = zIndex;
      isDirty = true;
    }
  }

  @Override
  public void editorUpdate(float dt) {
    if (!this.lastStateInWorld.equals(this.gameObject.stateInWorld) || this.lastZind != zIndex) {
      this.gameObject.stateInWorld.copy(this.lastStateInWorld);
      this.lastZind = zIndex;
      isDirty = true;
    }
  }

  @Override
  public void imgui() {
    zIndex = JImGui.dragInt("zIndex", zIndex);
    if (JImGui.colorPicker4("Color Picker", this.color)) {
      this.isDirty = true;
    }
  }

  public Vector4f getColor() {
    return this.color;
  }

  public Texture getTexture() {
    return sprite.getTexture();
  }

  public Vector2f[] getTexCoords() {
    return sprite.getTexCoords();
  }

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
    this.isDirty = true;
  }

  public void setColor(Vector4f color) {
    if (!this.color.equals(color)) {
      this.isDirty = true;
      this.color.set(color);
    }
  }

  public boolean isDirty() {
    return this.isDirty;
  }

  public void setDirty() {
    this.isDirty = true;
  }

  public void setClean() {
    this.isDirty = false;
  }

  public void setTexture(Texture texture) {
    this.sprite.setTexture(texture);
  }
}

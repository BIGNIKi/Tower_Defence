package entities.job;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Camera {

  // projectionMatrix рамеры экрана в пикселях
  @Getter
  @Setter
  private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;

  public Vector2f position;

  private final float projectionWidth = 6;
  private final float projectionHeight = 3;
  public Vector4f clearColor = new Vector4f(23f / 255f, 23f / 255f, 23f / 255f, 1);
  private final Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);

  private float zoom = 1.0f;

  public Camera(Vector2f position) {
    this.position = position;
    this.projectionMatrix = new Matrix4f();
    this.viewMatrix = new Matrix4f();
    this.inverseProjection = new Matrix4f();
    this.inverseView = new Matrix4f();
    adjuctProjection();
  }

  //если размер окна изменится, надо перенастроить проекцию
  public void adjuctProjection() {
    projectionMatrix.identity(); //делает единичную матрицу
    //sides of the screen
    //zNear - we can view any objects up to 0, zFar - we can veiw up to 100 away (units)
    //эти измерения НЕ в пикселях! Это некое разбиение экрана на "клетки" (unit'ы)
    //это создаёт аля усеченный конус в котором камера что-либо может видеть
    // эта штука отвечает за то, куда мы будем центрироваться и как широко мы вообще смотрим
    // TODO: хотелось бы возможность зумить туда, где стоит мышка, а не к центру
    projectionMatrix.ortho(-projectionSize.x / 2 * this.zoom, projectionSize.x / 2 * this.zoom,
        -projectionSize.y / 2 * this.zoom, projectionSize.y / 2 * this.zoom, 0.0f, 100f);
    projectionMatrix.invert(inverseProjection);
  }

  //it defines where the camera is in world space
  public Matrix4f getViewMatrix() {
    //where the front of the camera
    Vector3f cameraFront = new Vector3f(0.0f, 0.0f,
        -1.0f); //it means the camera are looking at negative 1 Z-direction
    //what direction is up
    Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    this.viewMatrix.identity();
    //eye - is where our camera is
    //center - where is the camera looking towards
    //up - on which direction is up
    viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
        cameraFront.add(position.x, position.y, 0.0f), //center
        cameraUp);
    this.viewMatrix.invert(inverseView);
    return this.viewMatrix;
  }

  public void addZoom(float value) {
    this.zoom += value;
  }
}
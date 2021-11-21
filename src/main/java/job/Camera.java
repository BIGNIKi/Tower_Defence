package job;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseVeiw;
    public Vector2f position;
    private Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);

    private float zoom = 1.0f;

    public Camera(Vector2f position)
    {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseVeiw = new Matrix4f();
        adjuctProjection();
    }

    //если размер окна изменится, надо перенастроить проекцию
    public void adjuctProjection()
    {
        projectionMatrix.identity(); //делает единичную матрицу
        //sides of the screen
        //zNear - we can view any objects up to 0, zFar - we can veiw up to 100 away (units)
        //эти измерения НЕ в пикселях! Это некое разбиение экрана на "клетки" (unit'ы)
        //это создаёт аля усеченный конус в котором камера что-либо может видеть
        projectionMatrix.ortho(0.0f, projectionSize.x * this.zoom, 0.0f, projectionSize.y * this.zoom, 0.0f, 100f);
        projectionMatrix.invert(inverseProjection);
    }

    //it defines where the camera is in world space
    public Matrix4f getViewMatrix()
    {
        //where the front of the camera
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f); //it means the camera are looking at negative 1 Z-direction
        //what direction is up
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();
        //eye - is where our camera is
        //center - where is the camera looking towards
        //up - on which direction is up
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                                            cameraFront.add(position.x, position.y, 0.0f), //center
                                            cameraUp);
        this.viewMatrix.invert(inverseVeiw);
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix()
    {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection()
    {
        return inverseProjection;
    }

    public Matrix4f getInverseVeiw()
    {
        return inverseVeiw;
    }

    public Vector2f getProjectionSize()
    {
        return this.projectionSize;
    }

    public float getZoom()
    {
        return zoom;
    }

    public void setZoom(float zoom)
    {
        this.zoom = zoom;
    }

    public void addZoom(float value)
    {
        this.zoom += value;
    }
}

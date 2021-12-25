package core.renderers;

import core.pools.AssetPool;
import utils.JMath;
import entities.job.Camera;
//import core.ui.MainWindow;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

  private static int MAX_LINES = 3000;

  private static Camera camera = null;

  private static List<Line2D> lines = new ArrayList<>();
  private static List<Line2D> notGridLines = new ArrayList<>();
  // 6 floats per vertex, 2 vertices per line
  private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
  private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

  private static int vaoID;
  private static int vboID;

  private static boolean started = false;


  public static void start() {
    // Generate the vao
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    // Create the vbo and buffer some memory
    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

    // Enable the vertex array attributes
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
    glEnableVertexAttribArray(1);

    glLineWidth(2.0f);
  }

  public static void beginFrame() {
    if (!started) {
      start();
      started = true;
    }

    // Remove dead lines
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).beginFrame() < 0) {
        lines.remove(i);
        i--;
      }
    }
    for (int i = 0; i < notGridLines.size(); i++) {
      if (notGridLines.get(i).beginFrame() < 0) {
        notGridLines.remove(i);
        i--;
      }
    }
  }

  public static void drawAnother(Camera camera) {
    if (notGridLines.size() <= 0) {
      return;
    }

    int index = 0;
    for (Line2D line : notGridLines) {
      for (int i = 0; i < 2; i++) {
        Vector2f position = i == 0 ? line.getFrom() : line.getTo();
        Vector3f color = line.getColor();

        // Load position
        vertexArray[index] = position.x;
        vertexArray[index + 1] = position.y;
        vertexArray[index + 2] = -10.0f;

        // Load the color
        vertexArray[index + 3] = color.x;
        vertexArray[index + 4] = color.y;
        vertexArray[index + 5] = color.z;
        index += 6;
      }
    }

    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferSubData(GL_ARRAY_BUFFER, 0,
        Arrays.copyOfRange(vertexArray, 0, notGridLines.size() * 6 * 2));

    // Use our shader
    shader.use();
    shader.uploadMat4f("uProjection", camera.getProjectionMatrix());
    shader.uploadMat4f("uView", camera.getViewMatrix());

    // Bind the vao
    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    // Draw the batch
    glDrawArrays(GL_LINES, 0, notGridLines.size());

    // Disable location
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);

    // Unbind shader
    shader.detach();
  }

  public static void drawGrid(Camera camera) {
    if (lines.size() <= 0) {
      return;
    }

    int index = 0;
    for (Line2D line : lines) {
      for (int i = 0; i < 2; i++) {
        Vector2f position = i == 0 ? line.getFrom() : line.getTo();
        Vector3f color = line.getColor();

        // Load position
        vertexArray[index] = position.x;
        vertexArray[index + 1] = position.y;
        vertexArray[index + 2] = -10.0f;

        // Load the color
        vertexArray[index + 3] = color.x;
        vertexArray[index + 4] = color.y;
        vertexArray[index + 5] = color.z;
        index += 6;
      }
    }

    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

    // Use our shader
    shader.use();
    shader.uploadMat4f("uProjection", camera.getProjectionMatrix());
    shader.uploadMat4f("uView", camera.getViewMatrix());

    // Bind the vao
    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    // Draw the batch
    glDrawArrays(GL_LINES, 0, lines.size());

    // Disable location
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);

    // Unbind shader
    shader.detach();
  }

  public static void addLine2D(Vector2f from, Vector2f to) {
    // TODO: add constants for common colors
    addLine2D(from, to, new Vector3f(0, 1, 0), 1);
  }

  public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
    addLine2D(from, to, color, 1);
  }

  public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
    if (notGridLines.size() >= MAX_LINES) {
      return;
    }
    DebugDraw.notGridLines.add(new Line2D(from, to, color, lifetime));
  }

  public static void addGridLine2D(Vector2f from, Vector2f to, Vector3f color) {
    addGridLine2D(from, to, color, 1);
  }

  public static void addGridLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
    // TODO: хорошо бы иметь проверки на линии, которые за экраном, дабы не кушать вычислительное время (не отрисовывать их)
/*        Camera camera = MainWindow.getScene().camera();
        // нижний левый угол камеры
        Vector2f cameraLeft = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.position).
                add(new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).
                add(new Vector2f(4.0f, 4.0f));
        boolean lineInView =
                ((from.x >= cameraLeft.x && from.x <= cameraRight.x) && (from.y >= cameraLeft.y && from.y <= cameraRight.y)) ||
                        ((to.x >= cameraLeft.x && to.x <= cameraRight.x) && (to.y >= cameraLeft.y && to.y <= cameraRight.y));*/
/*        if(lines.size() >= MAX_LINES || !lineInView)
        {
            return;
        }*/
    if (lines.size() >= MAX_LINES) {
      return;
    }
    DebugDraw.lines.add(new Line2D(from, to, color, lifetime));
  }

  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
    addBox2D(center, dimensions, rotation, new Vector3f(0, 1, 0), 1);
  }

  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation,
      Vector3f color) {
    addBox2D(center, dimensions, rotation, color, 1);
  }

  public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation,
      Vector3f color, int lifetime) {
    Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
    Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

    Vector2f[] vertices = {
        new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
        new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
    };

    if (rotation != 0.0f) {
      for (Vector2f vert : vertices) {
        JMath.rotate(vert, rotation, center);
      }
    }

    addLine2D(vertices[0], vertices[1], color, lifetime);
    addLine2D(vertices[0], vertices[3], color, lifetime);
    addLine2D(vertices[1], vertices[2], color, lifetime);
    addLine2D(vertices[2], vertices[3], color, lifetime);
  }

  public static void addCircle(Vector2f center, float radius) {
    addCircle(center, radius, new Vector3f(0, 1, 0), 1);
  }

  public static void addCircle(Vector2f center, float radius, Vector3f color) {
    addCircle(center, radius, color, 1);
  }

  public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime) {
    Vector2f[] points = new Vector2f[50];
    int increment = 360 / points.length;
    int currentAngle = 0;

    for (int i = 0; i < points.length; i++) {
      Vector2f tmp = new Vector2f(0, radius);
      JMath.rotate(tmp, currentAngle, new Vector2f());
      points[i] = new Vector2f(tmp).add(center);

      if (i > 0) {
        addLine2D(points[i - 1], points[i], color, lifetime);
      }
      currentAngle += increment;
    }

    addLine2D(points[points.length - 1], points[0], color, lifetime);
  }
}

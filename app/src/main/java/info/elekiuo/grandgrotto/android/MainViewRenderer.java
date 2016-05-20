package info.elekiuo.grandgrotto.android;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class MainViewRenderer implements GLSurfaceView.Renderer {

    public interface Task {
        void run(long time);
    }

    private final List<Task> taskList = new ArrayList<>();

    private final TextureAtlas textureAtlas;
    private Mesh stageMesh;
    private Mask mask;
    private final Shadow shadow = new Shadow();

    private final Collection<Sprite> sprites = new ArrayList<>();

    private Drawer drawer;
    private int blankTexture;
    private int texture;

    private final float[] worldMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    public MainViewRenderer(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
    }

    public void addTask(Task task) {
        synchronized (taskList) {
            taskList.add(task);
        }
    }

    public void removeTask(Task task) {
        synchronized (taskList) {
            taskList.remove(task);
        }
    }


    public synchronized void setWorldMatrix(float[] worldMatrix) {
        System.arraycopy(worldMatrix, 0, this.worldMatrix, 0, 16);
    }

    public synchronized void setStageMesh(Mesh stageMesh) {
        this.stageMesh = stageMesh;
    }

    public synchronized void setMask(Mask mask) {
        this.mask = mask;
    }

    public synchronized void setSprites(Collection<Sprite> sprites) {
        this.sprites.clear();
        this.sprites.addAll(sprites);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        drawer = new Drawer();
        blankTexture = GLES20Utils.createTexture(0xffffffff);
        texture = textureAtlas.createTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        shadow.change(width, height);
    }

    @Override
    public synchronized void onDrawFrame(GL10 gl) {
        List<Task> copiedTaskList = new ArrayList<>();
        synchronized (taskList) {
            copiedTaskList.addAll(taskList);
            taskList.clear();
        }

        if (!copiedTaskList.isEmpty()) {
            long time = SystemClock.uptimeMillis();
            for (Task task : copiedTaskList) {
                task.run(time);
            }
        }

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDisable(GL_BLEND);

        glClearColor(0.6f, 0.5f, 0.45f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float[] m = worldMatrix;
        float[] n = new float[16];

        drawer.useProgram();

        glBindTexture(GL_TEXTURE_2D, texture);

        if (stageMesh != null) {
            stageMesh.draw(drawer, m, n);
        }

        if (mask != null) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            glBindTexture(GL_TEXTURE_2D, blankTexture);

            mask.draw(drawer, m, n);

            glDisable(GL_BLEND);
            glBindTexture(GL_TEXTURE_2D, texture);
        }

        for (Sprite sprite : sprites) {
            sprite.draw(drawer, m, n);
        }

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glBindTexture(GL_TEXTURE_2D, blankTexture);

        shadow.draw(drawer, m, n);

        glDisable(GL_BLEND);
        glBindTexture(GL_TEXTURE_2D, texture);
    }
}

package info.elekiuo.grandgrotto.android;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

public class MainViewRenderer implements GLSurfaceView.Renderer {
    private Drawer drawer;
    private int blankTexture;

    private final float[] worldMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        drawer = new Drawer();
        blankTexture = GLES20Utils.createTexture(0xffffffff);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        glClearColor(0.6f, 0.5f, 0.45f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawer.useProgram();

        drawer.setMatrix(worldMatrix);
    }
}

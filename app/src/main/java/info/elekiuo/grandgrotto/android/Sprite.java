package info.elekiuo.grandgrotto.android;

import static android.opengl.GLES20.*;

public class Sprite {
    public final VertexTexBuffer vertexBuffer;
    public final float x;
    public final float y;
    public final boolean invert;

    public Sprite(VertexTexBuffer vertexBuffer, float x, float y, boolean invert) {
        this.vertexBuffer = vertexBuffer;
        this.x = x;
        this.y = y;
        this.invert = invert;
    }

    public void draw(Drawer drawer, float[] m, float[] n) {
        System.arraycopy(m, 0, n, 0, 16);
        for (int i = 0; i < 4; i++) {
            n[i + 12] += this.x * n[i] + this.y * n[i + 4];
        }
        if (this.invert) {
            for (int i = 0; i < 4; i++) {
                n[i] = -n[i];
            }
        }
        drawer.setMatrix(n);
        drawer.setVertexBuffer(this.vertexBuffer, 0xffffffff);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }
}

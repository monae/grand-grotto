package info.elekiuo.grandgrotto.android;

import static android.opengl.GLES20.*;

public class Shadow {
    static final int CORNERS = 6;
    private final VertexColorBuffer vertexBuffer = createVertexBuffer();

    private float sx = 1;
    private float sy = 1;

    public void change(int width, int height) {
        if (width < height) {
            sx = (float) height / width;
            sy = 1;
        } else {
            sx = 1;
            sy = (float) width / height;
        }
    }

    public void draw(Drawer drawer, float[] m, float[] n) {
        drawer.setMatrix(new float[]{
                sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1
        });

        drawer.setVertexBuffer(vertexBuffer, 0, 0);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, CORNERS * 8 + 2);
    }

    private static VertexColorBuffer createVertexBuffer() {
        float r1 = 1.5f;
        float r2 = 0.5f;
        int color1 = GLES20Utils.multiplyAlpha(0xcc332211);
        int color2 = 0;
        VertexColorBuffer vertexBuffer = new VertexColorBuffer(CORNERS * 8 + 2);
        for (int i = 0; i <= CORNERS * 4; i++) {
            double a = i * 0.5 * Math.PI / CORNERS;
            float c = (float) Math.cos(a);
            float s = (float) Math.sin(a);
            vertexBuffer.put(r1 * c, r1 * s, 0, color1);
            vertexBuffer.put(r2 * c, r2 * s, 0, color2);
        }
        return vertexBuffer;
    }
}

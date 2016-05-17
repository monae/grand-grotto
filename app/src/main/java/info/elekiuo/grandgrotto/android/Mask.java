package info.elekiuo.grandgrotto.android;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.*;

public class Mask {
    private static final int CORNERS = 6;
    private static final int COUNT = CORNERS * 4 + 8;

    private static final ByteBuffer indexBuffer = createIndexBuffer();
    private final VertexBuffer vertexBuffer;
    private final int color;

    public Mask(float left, float top, float right, float bottom, int color) {
        this.vertexBuffer = createVertexBuffer(left, top, right, bottom);
        this.color = color;
    }

    public void draw(Drawer drawer, float[] m, float[] n) {
        System.arraycopy(m, 0, n, 0, 16);
        for (int i = 0; i < 16; i += 4) {
            n[i + 2] -= n[i + 3] * 0.5f;
        }
        drawer.setMatrix(n);

        drawer.setVertexBuffer(vertexBuffer, 0, 0, color);
        glDrawElements(GL_TRIANGLES, COUNT * 3, GL_UNSIGNED_BYTE, indexBuffer);
    }

    private static VertexBuffer createVertexBuffer(float left, float top, float right, float bottom) {
        VertexBuffer vertexBuffer = new VertexBuffer(COUNT);

        float z = 0.5f;
        vertexBuffer.put(-100, -100, z);
        vertexBuffer.put(100, -100, z);
        vertexBuffer.put(100, 100, z);
        vertexBuffer.put(-100, 100, z);

        for (int i = 0; i <= CORNERS; i++) {
            double a = i * 0.5 * Math.PI / CORNERS;
            float c = (float) Math.cos(a);
            float s = (float) Math.sin(a);
            float k = 1.125f;
            vertexBuffer.put(left - 0.5f + k - c, top - 0.5f + k - s, z);
            vertexBuffer.put(right - 0.5f - k + s, top - 0.5f + k - c, z);
            vertexBuffer.put(right - 0.5f - k + c, bottom - 0.5f - k + s, z);
            vertexBuffer.put(left - 0.5f + k - s, bottom - 0.5f - k + c, z);
        }

        vertexBuffer.flip();
        return vertexBuffer;
    }

    private static ByteBuffer createIndexBuffer() {
        ByteBuffer indexBuffer = GLES20Utils.newByteBuffer(COUNT * 3);

        for (int i = 0; i < CORNERS * 4; i++) {
            indexBuffer.put(new byte[] {
                    (byte) (i % 4), (byte) (i + 4), (byte) (i + 8),
            });
        }

        indexBuffer.put(new byte[] {
                0, (byte) (COUNT - 1), 4,
                1, (byte) (COUNT - 4), 5,
                2, (byte) (COUNT - 3), 6,
                3, (byte) (COUNT - 2), 7,
                0, (byte) (COUNT - 4), 1,
                1, (byte) (COUNT - 3), 2,
                2, (byte) (COUNT - 2), 3,
                3, (byte) (COUNT - 1), 0,
        });

        indexBuffer.flip();
        return indexBuffer;
    }
}

package info.elekiuo.grandgrotto.android;

import java.nio.FloatBuffer;

public class VertexTexColorBuffer {
    final FloatBuffer buffer;

    public VertexTexColorBuffer(int capacity) {
        buffer = GLES20Utils.newFloatBuffer(capacity * 6);
    }

    public VertexTexColorBuffer put(float x, float y, float z, float u, float v, int color) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(u);
        buffer.put(v);
        buffer.put(GLES20Utils.packColor(color));
        return this;
    }

    public VertexTexColorBuffer flip() {
        buffer.flip();
        return this;
    }
}

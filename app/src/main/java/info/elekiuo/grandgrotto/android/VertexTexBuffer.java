package info.elekiuo.grandgrotto.android;

import java.nio.FloatBuffer;

public class VertexTexBuffer {
    final FloatBuffer buffer;

    public VertexTexBuffer(int capacity) {
        buffer = GLES20Utils.newFloatBuffer(capacity * 5);
    }

    public VertexTexBuffer put(float x, float y, float z, float u, float v) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(u);
        buffer.put(v);
        return this;
    }

    public VertexTexBuffer flip() {
        buffer.flip();
        return this;
    }
}

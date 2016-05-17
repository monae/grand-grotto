package info.elekiuo.grandgrotto.android;

import java.nio.FloatBuffer;

public class VertexBuffer {
    final FloatBuffer buffer;

    public VertexBuffer(int capacity) {
        buffer = GLES20Utils.newFloatBuffer(capacity * 3);
    }

    public VertexBuffer put(float x, float y, float z) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        return this;
    }

    public VertexBuffer flip() {
        buffer.flip();
        return this;
    }
}

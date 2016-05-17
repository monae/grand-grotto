package info.elekiuo.grandgrotto.android;

import java.nio.FloatBuffer;

public class VertexColorBuffer {
    final FloatBuffer buffer;

    public VertexColorBuffer(int capacity) {
        buffer = GLES20Utils.newFloatBuffer(capacity * 4);
    }

    public VertexColorBuffer put(float x, float y, float z, int color) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(GLES20Utils.packColor(color));
        return this;
    }

    public VertexColorBuffer flip() {
        buffer.flip();
        return this;
    }
}

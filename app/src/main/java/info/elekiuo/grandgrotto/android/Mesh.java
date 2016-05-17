package info.elekiuo.grandgrotto.android;

import android.graphics.Rect;

import java.nio.ShortBuffer;
import java.util.List;

import static android.opengl.GLES20.*;

public class Mesh {
    public final int count;
    public final VertexTexBuffer vertexBuffer;
    public final ShortBuffer indexBuffer;

    public Mesh(List<Plane> planes, TextureAtlas textureAtlas) {
        int size = planes.size();
        count = size * 2;

        float e = 1/256f;
        vertexBuffer = new VertexTexBuffer(size * 4);
        for (Plane p : planes) {
            Rect rect = textureAtlas.get(p.bitmap);
            vertexBuffer.put(p.v0.x, p.v0.y, p.v0.z, (rect.left + e) / (float) textureAtlas.width, (rect.top + e) / (float) textureAtlas.height);
            vertexBuffer.put(p.v1.x, p.v1.y, p.v1.z, (rect.left + e) / (float) textureAtlas.width, (rect.bottom - e) / (float) textureAtlas.height);
            vertexBuffer.put(p.v2.x, p.v2.y, p.v2.z, (rect.right - e) / (float) textureAtlas.width, (rect.top + e) / (float) textureAtlas.height);
            vertexBuffer.put(p.v3.x, p.v3.y, p.v3.z, (rect.right - e) / (float) textureAtlas.width, (rect.bottom - e) / (float) textureAtlas.height);
        }
        vertexBuffer.flip();

        indexBuffer = GLES20Utils.newShortBuffer(size * 6);
        int vertex = 0;
        for (int i = 0; i < size; i++) {
            indexBuffer.put((short) (vertex));
            indexBuffer.put((short) (vertex + 1));
            indexBuffer.put((short) (vertex + 2));
            indexBuffer.put((short) (vertex + 2));
            indexBuffer.put((short) (vertex + 1));
            indexBuffer.put((short) (vertex + 3));
            vertex += 4;
        }
        indexBuffer.position(0);
    }

    public void draw(Drawer drawer, float[] m, float[] n) {
        drawer.setMatrix(m);
        drawer.setVertexBuffer(vertexBuffer, 0xffffffff);
        glDrawElements(GL_TRIANGLES, count * 3, GL_UNSIGNED_SHORT, indexBuffer);
    }
}

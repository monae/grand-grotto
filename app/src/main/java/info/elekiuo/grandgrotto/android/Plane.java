package info.elekiuo.grandgrotto.android;

public class Plane {
    public final int bitmap;
    public final Vertex v0;
    public final Vertex v1;
    public final Vertex v2;
    public final Vertex v3;

    public Plane(int bitmap, Vertex v0, Vertex v1, Vertex v2, Vertex v3) {
        this.bitmap = bitmap;
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Plane(int bitmap, Vertex v0, Vertex v1, Vertex v2) {
        this(bitmap, v0, v1, v2, new Vertex(
                v1.x + v2.x - v0.x,
                v1.y + v2.y - v0.y,
                v1.z + v2.z - v0.z));
    }

    public static Plane x(int bitmap, Vertex v0, Vertex v3) {
        Vertex v1 = new Vertex(v0.x, v3.y, v3.z);
        Vertex v2 = new Vertex(v3.x, v0.y, v0.z);
        return new Plane(bitmap, v0, v1, v2, v3);
    }
}

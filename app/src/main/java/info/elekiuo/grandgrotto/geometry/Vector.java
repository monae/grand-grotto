package info.elekiuo.grandgrotto.geometry;

public class Vector {
    public final int dx;
    public final int dy;

    public Vector(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public String toString() {
        return "<" + dx + "," + dy + ">";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vector && equals((Vector) o);
    }

    public boolean equals(Vector other) {
        return other != null && this.dx == other.dx && this.dy == other.dy;
    }

    @Override
    public int hashCode() {
        return dx * 41 + dy;
    }

    public int manhattanNorm() {
        return Math.abs(dx) + Math.abs(dy);
    }

    public int chessNorm() {
        return Math.max(Math.abs(dx), Math.abs(dy));
    }
}

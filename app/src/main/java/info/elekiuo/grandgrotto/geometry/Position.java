package info.elekiuo.grandgrotto.geometry;

public final class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Position && equals((Position) o);
    }

    public boolean equals(Position other) {
        return other != null && this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return x * 37 + y;
    }

    public Position plus(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    public Position plus(Vector vector) {
        return plus(vector.dx, vector.dy);
    }

    public Position plus(Direction direction) {
        return plus(direction.vector);
    }

    public Position minus(int dx, int dy) {
        return new Position(this.x - dx, this.y - dy);
    }

    public Position minus(Vector vector) {
        return minus(vector.dx, vector.dy);
    }

    public Position minus(Direction direction) {
        return minus(direction.vector);
    }

    public Vector minus(Position position) {
        return new Vector(this.x - position.x, this.y - position.y);
    }
}

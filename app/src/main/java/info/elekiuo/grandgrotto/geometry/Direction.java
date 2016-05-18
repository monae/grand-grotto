package info.elekiuo.grandgrotto.geometry;

public enum Direction {
    NORTH(0, -1),
    NORTHWEST(-1, -1),
    WEST(-1, 0),
    SOUTHWEST(-1, 1),
    SOUTH(0, 1),
    SOUTHEAST(1, 1),
    EAST(1, 0),
    NORTHEAST(1, -1);

    private static final Direction[] VALUES = values();
    private static final Direction[] DICTIONARY = new Direction[9];
    static {
        for (Direction direction : VALUES) {
            Vector v = direction.vector;
            DICTIONARY[4 + v.dx + v.dy * 3] = direction;
        }
    }

    public final Vector vector;

    Direction(int dx, int dy) {
        this.vector = new Vector(dx, dy);
    }

    public static Direction[] cardinals() {
        return new Direction[]{NORTH, WEST, SOUTH, EAST};
    }

    public static Direction fromVector(int dx, int dy) {
        return DICTIONARY[4 + Integer.signum(dx) + Integer.signum(dy) * 3];
    }

    public static Direction fromVector(Vector vector) {
        return fromVector(vector.dx, vector.dy);
    }

    public Direction rotateLeft(int angle) {
        return VALUES[(ordinal() + angle) & 7];
    }

    public Direction rotateRight(int angle) {
        return rotateLeft(-angle);
    }

    public Direction frontLeft() {
        return rotateLeft(1);
    }

    public Direction left() {
        return rotateLeft(2);
    }

    public Direction rearLeft() {
        return rotateLeft(3);
    }

    public Direction rear() {
        return rotateLeft(4);
    }

    public Direction rearRight() {
        return rotateLeft(5);
    }

    public Direction right() {
        return rotateLeft(6);
    }

    public Direction frontRight() {
        return rotateLeft(7);
    }

    public boolean isCardinal() {
        return ordinal() % 2 == 0;
    }

    public boolean isOrdinal() {
        return !isCardinal();
    }
}

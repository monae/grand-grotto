package info.elekiuo.grandgrotto.core;

public enum Terrain {
    WALL,
    EMPTY,
    FLOOR;

    public boolean canStandOn() {
        switch (this) {
        case FLOOR:
            return true;
        default:
            return false;
        }
    }

    public boolean canPassThrough() {
        switch (this) {
        case FLOOR:
        case EMPTY:
            return true;
        default:
            return false;
        }
    }
}

package info.elekiuo.grandgrotto.geometry;

import android.graphics.RegionIterator;

import java.util.Iterator;

public final class Region implements Iterable<Position> {
    public final int west;
    public final int north;
    public final int east;
    public final int south;

    public Region(int west, int north, int east, int south) {
        this.west = west;
        this.north = north;
        this.east = east;
        this.south = south;
    }

    @Override
    public String toString() {
        return "(" + west + "," + north + ")...(" + east + "," + south + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Region && equals((Region) o);
    }

    public boolean equals(Region other) {
        return other != null &&
                this.west == other.west && this.north == other.north &&
                this.east == other.east && this.south == other.south;
    }

    @Override
    public int hashCode() {
        return ((west * 53 + north) * 53 + east) * 53 + south;
    }

    public int getCols() {
        return east - west;
    }

    public int getRows() {
        return south - north;
    }

    public Region shrink(int d) {
        return new Region(west + d, north + d, east - d, south - d);
    }

    public Region extend(int d) {
        return shrink(-d);
    }

    public Region withWest(int west) {
        return new Region(west, north, east, south);
    }

    public Region withNorth(int north) {
        return new Region(west, north, east, south);
    }

    public Region withEast(int east) {
        return new Region(west, north, east, south);
    }

    public Region withSouth(int south) {
        return new Region(west, north, east, south);
    }

    public Interval colInterval() {
        return new Interval(west, east);
    }

    public Interval rowInterval() {
        return new Interval(north, south);
    }

    public boolean contains(int x, int y) {
        return west <= x && x < east && north <= y && y < south;
    }

    public boolean contains(Position position) {
        return contains(position.x, position.y);
    }

    public Position center() {
        return new Position((west + east) / 2, (north + south) / 2);
    }

    @Override
    public Iterator<Position> iterator() {
        return new RegionIterator(this);
    }

    private static class RegionIterator implements Iterator<Position> {
        private final Region region;
        private int x;
        private int y;

        public RegionIterator(Region region) {
            this.region = region;
            this.x = region.west;
            this.y = region.north;
        }

        @Override
        public Position next() {
            Position result = new Position(x, y);
            x++;
            if (x >= region.east) {
                x = region.west;
                y++;
            }
            return result;
        }

        @Override
        public boolean hasNext() {
            return region.contains(x, y);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

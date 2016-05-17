package info.elekiuo.grandgrotto.geometry;

public class Interval {
    public final int from;
    public final int to;

    public Interval(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return from + "..." + to;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Interval && equals((Interval) o);
    }

    public boolean equals(Interval other) {
        return other != null && this.from == other.from && this.to == other.to;
    }

    @Override
    public int hashCode() {
        return from * 79 + to;
    }

    public boolean isEmpty() {
        return from >= to;
    }

    public int length() {
        return to - from;
    }

    public Interval join(Interval that) {
        return new Interval(Math.min(this.from, that.from), Math.max(this.to, that.to));
    }

    public Interval meet(Interval that) {
        return new Interval(Math.max(this.from, that.from), Math.min(this.to, that.to));
    }
}

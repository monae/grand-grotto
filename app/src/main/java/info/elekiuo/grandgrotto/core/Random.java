package info.elekiuo.grandgrotto.core;

import java.util.Collections;
import java.util.List;

import info.elekiuo.grandgrotto.algorithm.XorShiftAdd;
import info.elekiuo.grandgrotto.geometry.Interval;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Region;

public class Random {
    private static volatile long seedBase;

    private final XorShiftAdd engine = new XorShiftAdd();
    private final int[] state = new int[engine.length()];
    private int index;

    public Random() {
        this(System.nanoTime() + seedBase);
        seedBase++;
    }

    public Random(long seed) {
        engine.initialize(state, seed);
    }

    public int nextInt() {
        int result = state[index];
        index++;
        if (index >= engine.length()) {
            engine.next(state);
            index = 0;
        }
        return result;
    }

    public float nextFloat() {
        return nextInt() * (1 / 4294967296.0f) + 0.5f;
    }

    public int next(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(n + " is not positive");
        } else if ((n & (n - 1)) == 0) {
            // n is a power of 2
            return nextInt() & (n - 1);
        } else {
            while (true) {
                int bits = nextInt() & Integer.MAX_VALUE;
                int value = bits % n;
                if (bits - value + n - 1 >= 0) {
                    return value;
                }
            }
        }
    }

    public int next2(int n) {
        return next((n + 1) / 2) + next((n + 2) / 2);
    }

    public int choose(int from, int to) {
        return from + next(to - from);
    }

    public int choose(Interval interval) {
        return choose(interval.from, interval.to);
    }

    public Position choose(Region region) {
        return new Position(choose(region.colInterval()), choose(region.rowInterval()));
    }

    public <T> T choose(T[] array) {
        return array[next(array.length)];
    }

    public <T> T choose(List<T> list) {
        return list.get(next(list.size()));
    }

    public boolean test(float a) {
        if (a <= 0) {
            return false;
        } else if (a >= 1) {
            return true;
        } else {
            return nextFloat() < a;
        }
    }

    public void shuffle(List<?> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            Collections.swap(list, i, next(i + 1));
        }
    }
}

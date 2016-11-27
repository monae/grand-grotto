package info.elekiuo.grandgrotto.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TurnTable<T extends TurnTable.Weighted> {
    public interface Weighted {
        int getWeight();
    }

    private static class Entry<T extends TurnTable.Weighted> {
        public final T value;
        public int weight;

        private Entry(T value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.value = value;
        }
    }

    private final List<Entry<T>> entries = new ArrayList<>();
    private int cursor;

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void add(T value) {
        entries.add(cursor, new Entry<>(value));
        advance();
    }

    public void addAll(Collection<T> collection) {
        for (T value : collection) {
            add(value);
        }
    }

    public void remove(T value) {
        int size = entries.size();
        for (int i = 0; i < size; i++) {
            boolean overflowed = false;
            int targetCursor = cursor + i;
            if (targetCursor >= size) {
                overflowed = true;
                targetCursor -= size;
            }
            T targetValue = entries.get(targetCursor).value;
            if (targetValue.equals(value)) {
                entries.remove(targetCursor);
                if (overflowed) {
                    cursor--;
                    if (cursor == 0) {
                        cursor = entries.size();
                    }
                }
                return;
            }
        }
    }

    public T get() {
        return entries.isEmpty() ? null : entries.get(cursor - 1).value;
    }

    public void rotate() {
        if (!entries.isEmpty()) {
            calibrate();
            advance();
        }
    }

    private void advance() {
        Entry<T> entry = entries.get(cursor);
        entry.weight = Math.max(0, entry.value.getWeight() - 1);
        cursor++;
    }

    private void calibrate() {
        int minimumWeight = Integer.MAX_VALUE;
        for (Entry entry : entries) {
            minimumWeight = Math.min(minimumWeight, entry.weight);
        }
        if (minimumWeight > 0) {
            for (Entry entry : entries) {
                entry.weight -= minimumWeight;
            }
        }

        int size = entries.size();
        for (int i = 0; i < size; i++) {
            int nextCursor = cursor + i;
            if (nextCursor >= size) {
                nextCursor -= size;
            }
            Entry entry = entries.get(nextCursor);
            if (entry.weight == 0) {
                cursor = nextCursor;
                return;
            }
            entry.weight--;
        }

        throw new IllegalStateException();
    }

}

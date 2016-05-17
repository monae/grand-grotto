package info.elekiuo.grandgrotto.core;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TurnTableTest {
    private static class Value implements TurnTable.Weighted {
        public final String name;
        public final int weight;

        private Value(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }

    @Test
    public void testAddGet() {
        TurnTable<Value> turnTable = new TurnTable<>();

        turnTable.add(new Value("A", 2));
        assertEquals("A", turnTable.get().toString());
        turnTable.add(new Value("B", 2));
        assertEquals("B", turnTable.get().toString());
        turnTable.add(new Value("C", 2));
        assertEquals("C", turnTable.get().toString());
    }

    @Test
    public void testAddAllGet() {
        TurnTable<Value> turnTable = new TurnTable<>();

        turnTable.addAll(Arrays.asList(new Value("A", 2), new Value("B", 2), new Value("C", 2)));
        assertEquals("C", turnTable.get().toString());
    }

    @Test
    public void testAddRotate1() {
        TurnTable<Value> turnTable = new TurnTable<>();

        turnTable.add(new Value("A", 2));
        turnTable.add(new Value("B", 2));
        turnTable.add(new Value("C", 2));

        assertOutputs("ABCABCABCABCABCABCAB", 20, turnTable);

        turnTable.add(new Value("D", 2));
        turnTable.add(new Value("E", 2));

        assertOutputs("CABDECABDECABDECABDE", 20, turnTable);
    }

    @Test
    public void testAddRotate2() {
        TurnTable<Value> turnTable = new TurnTable<>();

        turnTable.add(new Value("A", 6));
        turnTable.add(new Value("B", 3));
        turnTable.add(new Value("C", 6));
        turnTable.add(new Value("D", 12));

        assertOutputs("BABCBABCDBABCBABCDBA", 20, turnTable);

        turnTable.add(new Value("E", 6));
        turnTable.add(new Value("F", 3));

        assertOutputs("BCFBAEFBCDFBAEFBCFBA", 20, turnTable);
    }

    private static void assertOutputs(String expected, int count, TurnTable<Value> turnTable) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            turnTable.rotate();
            builder.append(turnTable.get());
        }
        assertEquals(expected, builder.toString());
    }
}

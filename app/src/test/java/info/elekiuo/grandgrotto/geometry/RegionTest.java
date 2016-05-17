package info.elekiuo.grandgrotto.geometry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class RegionTest {
    @Test
    public void testIterator() {
        Region region = new Region(4, -1, 6, 3);

        List<Position> positions = new ArrayList<>();
        for (Position position : region) {
            positions.add(position);
        }

        assertEquals(Arrays.asList(
                new Position(4, -1), new Position(5, -1),
                new Position(4, 0), new Position(5, 0),
                new Position(4, 1), new Position(5, 1),
                new Position(4, 2), new Position(5, 2)), positions);
    }
}

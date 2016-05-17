package info.elekiuo.grandgrotto.geometry;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class PathTest {
    private static boolean get(Position position) {
        String map = "" +
                "..##...#.." +
                "#..##.#..#" +
                ".#.....#.#" +
                "#...#.....";

        return 0 <= position.x && position.x < 10 && 0 <= position.y && position.y < 4 &&
                map.charAt(position.x + position.y * 10) == '.';
    }

    @Test
    public void testFind1() {
        Path.Finder finder = getFinder1();
        Path path = finder.findFrom(new Position(0, 0));
        assertNotNull(path);
        assertEquals(path, finder.findFrom(new Position(0, 0)));

        assertEquals(new Position(0, 0), path.getStart());
        assertEquals(new Position(9, 0), path.getEnd());

        assertEquals(Arrays.asList(
                new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2),
                new Position(3, 2),
                new Position(4, 2),
                new Position(5, 2),
                new Position(6, 2),
                new Position(7, 1),
                new Position(8, 0),
                new Position(9, 0)
        ), path.getPositions());

        assertEquals(Direction.SOUTHEAST, path.getFirstDirection());
        assertFalse(path.isEmpty());
        assertEquals(9, path.length());

        Path nextPath = path.getNext();
        assertNotNull(nextPath);
        assertEquals(nextPath, finder.findFrom(new Position(1, 1)));
    }

    @Test
    public void testFind2() {
        Path.Finder finder = getFinder2();
        Path path = finder.findFrom(new Position(0, 0));
        assertNotNull(path);
        assertEquals(path, finder.findFrom(new Position(0, 0)));

        assertEquals(new Position(0, 0), path.getStart());
        assertEquals(new Position(9, 0), path.getEnd());

        assertEquals(Arrays.asList(
                new Position(0, 0),
                new Position(1, 0),
                new Position(1, 1),
                new Position(2, 1),
                new Position(2, 2),
                new Position(3, 2),
                new Position(4, 2),
                new Position(5, 2),
                new Position(6, 3),
                new Position(7, 3),
                new Position(8, 3),
                new Position(8, 2),
                new Position(8, 1),
                new Position(8, 0),
                new Position(9, 0)
        ), path.getPositions());

        assertEquals(Direction.EAST, path.getFirstDirection());
        assertFalse(path.isEmpty());
        assertEquals(14, path.length());

        Path nextPath = path.getNext();
        assertNotNull(nextPath);
        assertEquals(nextPath, finder.findFrom(new Position(1, 0)));
    }

    @Test
    public void testEmpty() {
        Path.Finder finder = getFinder1();
        Path path = finder.findFrom(new Position(9, 0));
        assertNotNull(path);
        assertEquals(path, finder.findFrom(new Position(9, 0)));

        assertEquals(new Position(9, 0), path.getStart());
        assertEquals(new Position(9, 0), path.getEnd());

        assertEquals(Collections.singletonList(new Position(9, 0)), path.getPositions());

        assertEquals(null, path.getFirstDirection());
        assertTrue(path.isEmpty());
        assertEquals(0, path.length());

        assertNull(path.getNext());
    }

    @Test
    public void testNull() {
        Path.Finder finder = getFinder2();
        Path path = finder.findFrom(new Position(0, 2));
        assertNull(path);
        assertNull(finder.findFrom(new Position(0, 2)));
    }

    private static Path.Finder getFinder1() {
        Path.Finder finder = new Path.Finder(new Position(9, 0), getPredicate1());
        Path path = finder.findFrom(new Position(9, 3));
        assertEquals(Arrays.asList(
                new Position(9, 3),
                new Position(8, 2),
                new Position(8, 1),
                new Position(9, 0)
        ), path.getPositions());
        return finder;
    }

    private static Path.GraphPredicate getPredicate1() {
        return new Path.GraphPredicate() {
            @Override
            public boolean hasVertex(Position position) {
                return get(position);
            }

            @Override
            public boolean hasEdge(Position position, Direction direction) {
                return true;
            }
        };
    }

    private static Path.Finder getFinder2() {
        Path.Finder finder = new Path.Finder(new Position(9, 0), getPredicate2());
        Path path = finder.findFrom(new Position(9, 3));
        assertEquals(Arrays.asList(
                new Position(9, 3),
                new Position(8, 3),
                new Position(8, 2),
                new Position(8, 1),
                new Position(8, 0),
                new Position(9, 0)
        ), path.getPositions());
        return finder;
    }

    private static Path.GraphPredicate getPredicate2() {
        return new Path.GraphPredicate() {
            @Override
            public boolean hasVertex(Position position) {
                return get(position);
            }

            @Override
            public boolean hasEdge(Position position, Direction direction) {
                if (direction.isCardinal()) {
                    return true;
                } else {
                    Position p1 = position.plus(direction.vector.dx, 0);
                    Position p2 = position.plus(0, direction.vector.dy);
                    return get(p1) && get(p2);
                }
            }
        };
    }
}

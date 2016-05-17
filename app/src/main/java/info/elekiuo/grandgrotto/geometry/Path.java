package info.elekiuo.grandgrotto.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class Path {
    private static final int CARDINAL_COST = 10000;
    private static final int ORDINAL_COST = 20000;

    public interface GraphPredicate {
        boolean hasVertex(Position position);
        boolean hasEdge(Position position, Direction direction);
    }

    private static final class Node {
        public final Node next;
        public final Position position;
        public final int cost;
        public final int length;
        public boolean closed;

        public Node(Node next, Position position, int cost, int length) {
            this.next = next;
            this.position = position;
            this.cost = cost;
            this.length = length;
        }
    }

    private final Node node;
    private final Position end;

    private Path(Node node, Position end) {
        this.node = node;
        this.end = end;
    }

    @Override
    public String toString() {
        return getPositions().toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Path && equals((Path) o);
    }

    public boolean equals(Path other) {
        if (other == null) {
            return false;
        }
        Node node1 = this.node;
        Node node2 = other.node;
        if (node1 == node2) {
            return true;
        }
        if (node1.length != node2.length) {
            return false;
        }
        do {
            if (!node1.position.equals(node2.position)) {
                return false;
            }
            node1 = node1.next;
            node2 = node2.next;
        } while (node1 != null);
        return true;
    }

    @Override
    public int hashCode() {
        int result = 12345;
        Node node = this.node;
        do {
            result = result * 101 + node.position.hashCode();
            node = node.next;
        } while (node != null);
        return result;
    }

    public Position getStart() {
        return node.position;
    }

    public Position getEnd() {
        return end;
    }

    public boolean isEmpty() {
        return node.next == null;
    }

    public int length() {
        return node.length;
    }

    public int getCost() {
        return node.cost;
    }

    public List<Position> getPositions() {
        List<Position> result = new ArrayList<>();
        Node node = this.node;
        do {
            result.add(node.position);
            node = node.next;
        } while (node != null);
        return result;
    }

    public Direction getFirstDirection() {
        return node.next == null ? null : Direction.fromVector(node.next.position.minus(node.position));
    }

    public Path getNext() {
        return node.next == null ? null : new Path(node.next, end);
    }

    public static Path find(Position start, Position end, GraphPredicate predicate) {
        return new Finder(end, predicate).findFrom(start);
    }

    private static int heuristicCost(Position start, Position end, int length) {
        int dx = Math.abs(start.x - end.x);
        int dy = Math.abs(start.y - end.y);
        int ordinal = Math.min(dx, dy);
        int cardinal = Math.abs(dx - dy);

        int additionalCost = (length * 2 + cardinal - 1) * cardinal;
        return CARDINAL_COST * cardinal + ORDINAL_COST * ordinal + additionalCost;
    }

    public static class Finder {
        private final Position end;
        private final GraphPredicate predicate;

        private Collection<Node> openSet;
        private final Map<Position, Node> minimumNodes = new HashMap<>();

        public Finder(Position end, GraphPredicate predicate) {
            this.end = end;
            this.predicate = predicate;

            if (predicate.hasVertex(end)) {
                Node node = new Node(null, end, 0, 0);
                node.closed = true;
                openSet = Collections.singletonList(node);
                minimumNodes.put(end, node);
            }
        }

        public Path findFrom(final Position start) {
            if (!predicate.hasVertex(start)) {
                return null;
            }

            Node node = minimumNodes.get(start);
            if (node != null && node.closed) {
                return new Path(node, end);
            }

            PriorityQueue<Node> openSet = createPriorityQueue(this.openSet, start);
            this.openSet = openSet;

            Direction[] directions = Direction.values();
            while (!openSet.isEmpty()) {
                Node currentNode = openSet.element();
                currentNode.closed = true;
                Position current = currentNode.position;
                if (start.equals(current)) {
                    return new Path(currentNode, end);
                }
                openSet.remove();

                for (Direction direction : directions) {
                    Position previous = current.minus(direction);
                    if (!predicate.hasVertex(previous) || !predicate.hasEdge(previous, direction)) {
                        continue;
                    }

                    int additionalCost = currentNode.length * 2;
                    int cost = currentNode.cost + (direction.isOrdinal() ? ORDINAL_COST : CARDINAL_COST + additionalCost);
                    Node minimumNode = minimumNodes.get(previous);
                    if (minimumNode != null) {
                        if (minimumNode.cost < cost) {
                            continue;
                        }
                        openSet.remove(minimumNode);
                    }

                    Node previousNode = new Node(currentNode, previous, cost, currentNode.length + 1);
                    openSet.add(previousNode);
                    minimumNodes.put(previous, previousNode);
                }
            }

            return null;
        }

        private static PriorityQueue<Node> createPriorityQueue(Collection<? extends Node> c, final Position start) {
            PriorityQueue<Node> result = new PriorityQueue<>(c.size() + 10, new Comparator<Node>() {
                @Override
                public int compare(Node lhs, Node rhs) {
                    return (lhs.cost + heuristicCost(start, lhs.position, lhs.length)) -
                            (rhs.cost + heuristicCost(start, rhs.position, rhs.length));
                }
            });
            result.addAll(c);
            return result;
        }
    }
}

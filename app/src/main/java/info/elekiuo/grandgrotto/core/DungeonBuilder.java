package info.elekiuo.grandgrotto.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import info.elekiuo.grandgrotto.algorithm.UnionFind;
import info.elekiuo.grandgrotto.geometry.MutableMatrix;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Region;

class DungeonBuilder {
    private static class Edge {
        public final int index1;
        public final int index2;
        public final int position;
        public final boolean vertical;

        private Edge(int index1, int index2, int position, boolean vertical) {
            this.index1 = index1;
            this.index2 = index2;
            this.position = position;
            this.vertical = vertical;
        }
    }

    private static class Door implements Comparable<Door> {
        public Position position;
        public Edge edge;
        public int order;

        public Door(Edge edge, int order) {
            this.edge = edge;
            this.order = order;
        }

        @Override
        public int compareTo(Door another) {
            return this.order - another.order;
        }
    }

    private static class Room {
        public Region region;
        public List<Door> westDoors;
        public List<Door> northDoors;
        public List<Door> eastDoors;
        public List<Door> southDoors;
    }

    private static final int MIN_AREA_SIZE = 5;
    private static final int MIN_EDGE_SIZE = 4;
    private static final int MIN_ROOM_SIZE = 3;

    private final Random random;

    public DungeonBuilder(Random random) {
        this.random = random;
    }

    public Board make() {
        Region whole = new Region(0, 0, 24, 40);
        List<Region> areas = new ArrayList<>();
        divideArea(areas, whole);

        List<Edge> edges = makeEdges(areas);

        List<Room> rooms = new ArrayList<>();
        int index = 0;
        for (Region area : areas) {
            Room room = new Room();
            int maxCols = area.getCols() - 2;
            int maxRows = area.getRows() - 2;
            int minCols = Math.max(MIN_ROOM_SIZE, area.getCols() / 2);
            int minRows = Math.max(MIN_ROOM_SIZE, area.getRows() / 2);
            int cols = random.choose(minCols, maxCols + 1);
            int rows = random.choose(minRows, maxRows + 1);
            int west = area.west + 1 + random.next(maxCols - cols + 1);
            int north = area.north + 1 + random.next(maxRows - rows + 1);
            room.region = new Region(west, north, west + cols, north + rows);

            room.westDoors = new ArrayList<>();
            room.northDoors = new ArrayList<>();
            room.eastDoors = new ArrayList<>();
            room.southDoors = new ArrayList<>();
            for (Edge e : edges) {
                if (e.index1 == index) {
                    if (e.vertical) {
                        room.southDoors.add(new Door(e, areas.get(e.index2).west));
                    } else {
                        room.eastDoors.add(new Door(e, areas.get(e.index2).north));
                    }
                } else if (e.index2 == index) {
                    if (e.vertical) {
                        room.northDoors.add(new Door(e, areas.get(e.index1).west));
                    } else {
                        room.westDoors.add(new Door(e, areas.get(e.index1).north));
                    }
                }
            }
            Collections.sort(room.westDoors);
            Collections.sort(room.northDoors);
            Collections.sort(room.eastDoors);
            Collections.sort(room.southDoors);

            int[] westDoorPositions = chooseDoorPositions(room.westDoors.size(), room.region.north, room.region.south);
            for (int i = 0; i < westDoorPositions.length; i++) {
                room.westDoors.get(i).position = new Position(room.region.west - 1, westDoorPositions[i]);
            }
            int[] northDoorPositions = chooseDoorPositions(room.northDoors.size(), room.region.west, room.region.east);
            for (int i = 0; i < northDoorPositions.length; i++) {
                room.northDoors.get(i).position = new Position(northDoorPositions[i], room.region.north - 1);
            }
            int[] eastDoorPositions = chooseDoorPositions(room.eastDoors.size(), room.region.north, room.region.south);
            for (int i = 0; i < eastDoorPositions.length; i++) {
                room.eastDoors.get(i).position = new Position(room.region.east, eastDoorPositions[i]);
            }
            int[] southDoorPositions = chooseDoorPositions(room.southDoors.size(), room.region.west, room.region.east);
            for (int i = 0; i < southDoorPositions.length; i++) {
                room.southDoors.get(i).position = new Position(southDoorPositions[i], room.region.south);
            }

            rooms.add(room);
            index++;
        }

        MutableMatrix<Terrain> cells = new MutableMatrix<>(whole.getCols(), whole.getRows());
        cells.fill(Terrain.WALL);

        for (Room room : rooms) {
            cells.view(room.region).fill(Terrain.FLOOR);
        }

        for (Edge e : edges) {
            Room room1 = rooms.get(e.index1);
            Room room2 = rooms.get(e.index2);
            if (e.vertical) {
                Position c1 = null;
                Position c2 = null;
                for (Door door : room1.southDoors) {
                    if (door.edge == e) {
                        c1 = door.position;
                        break;
                    }
                }
                for (Door door : room2.northDoors) {
                    if (door.edge == e) {
                        c2 = door.position;
                        break;
                    }
                }
                int yy = e.position;
                for (int x = Math.min(c1.x, c2.x); x <= Math.max(c1.x, c2.x); x++) {
                    cells.set(x, yy, Terrain.FLOOR);
                }
                for (int y = c1.y; y < yy; y++) {
                    cells.set(c1.x, y, Terrain.FLOOR);
                }
                for (int x = c2.y; x > yy; x--) {
                    cells.set(c2.x, x, Terrain.FLOOR);
                }
            } else {
                Position c1 = null;
                Position c2 = null;
                for (Door door : room1.eastDoors) {
                    if (door.edge == e) {
                        c1 = door.position;
                        break;
                    }
                }
                for (Door door : room2.westDoors) {
                    if (door.edge == e) {
                        c2 = door.position;
                        break;
                    }
                }
                int xx = e.position;
                for (int y = Math.min(c1.y, c2.y); y <= Math.max(c1.y, c2.y); y++) {
                    cells.set(xx, y, Terrain.FLOOR);
                }
                for (int x = c1.x; x < xx; x++) {
                    cells.set(x, c1.y, Terrain.FLOOR);
                }
                for (int x = c2.x; x > xx; x--) {
                    cells.set(x, c2.y, Terrain.FLOOR);
                }
            }
        }

        List<Region> regions = new ArrayList<>();
        for (Room room : rooms) {
            regions.add(room.region);
        }
        return new Board(cells, regions);
    }

    private void divideArea(List<Region> result, Region area) {
        int rows = area.getRows();
        int cols = area.getCols();

        if (random.test(cols > rows ? 0.75f : cols < rows ? 0.25f : 0.5f)) {
            divideAreaHorizontal(result, area);
        } else {
            divideAreaVertical(result, area);
        }
    }

    private void divideAreaHorizontal(List<Region> result, Region area) {
        int cols = area.getCols();
        if (random.test((cols - MIN_AREA_SIZE * 2 - 1) / 16f)) {
            int x = area.west + MIN_AREA_SIZE + random.next2(cols - MIN_AREA_SIZE * 2);
            divideArea(result, area.withWest(x + 1));
            divideArea(result, area.withEast(x));
        } else {
            result.add(area);
        }
    }

    private void divideAreaVertical(List<Region> result, Region area) {
        int rows = area.getRows();
        if (random.test((rows - MIN_AREA_SIZE * 2 - 1) / 16f)) {
            int y = area.north + MIN_AREA_SIZE + random.next2(rows - MIN_AREA_SIZE * 2);
            divideArea(result, area.withNorth(y + 1));
            divideArea(result, area.withSouth(y));
        } else {
            result.add(area);
        }
    }

    private List<Edge> makeEdges(List<Region> areas) {
        int size = areas.size();

        List<Edge> all = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Region area1 = areas.get(i);
                Region area2 = areas.get(j);
                if (area1.west - 1 == area2.east) {
                    if (area1.rowInterval().meet(area2.rowInterval()).length() >= MIN_EDGE_SIZE) {
                        all.add(new Edge(j, i, area2.east, false));
                    }
                } else if (area1.east == area2.west - 1) {
                    if (area1.rowInterval().meet(area2.rowInterval()).length() >= MIN_EDGE_SIZE) {
                        all.add(new Edge(i, j, area1.east, false));
                    }
                } else if (area1.north - 1 == area2.south) {
                    if (area1.colInterval().meet(area2.colInterval()).length() >= MIN_EDGE_SIZE) {
                        all.add(new Edge(j, i, area2.south, true));
                    }
                } else if (area1.south == area2.north - 1) {
                    if (area1.colInterval().meet(area2.colInterval()).length() >= MIN_EDGE_SIZE) {
                        all.add(new Edge(i, j, area1.south, true));
                    }
                }
            }
        }
        random.shuffle(all);

        UnionFind uf = new UnionFind(size);
        List<Edge> result = new ArrayList<>();
        for (Edge e : all) {
            if (uf.union(e.index1, e.index2) || random.test(1 / 4f)) {
                result.add(e);
            }
        }
        if (uf.getComponents() != 1) {
            // throw new RuntimeException("not connected");
        }
        return result;
    }

    private int[] chooseDoorPositions(int count, int from, int to) {
        if (count == 0) {
            return new int[0];
        } else if (count == 1) {
            return new int[]{random.choose(from, to)};
        }

        int size = to - from;
        int[] candidates = new int[size];
        for (int i = 0; i < size; i++) {
            candidates[i] = from + i;
        }

        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            int position = candidates[random.next(size)];
            result[i] = position;

            for (int j = 0; j < size; j++) {
                int value = candidates[j];
                if (value == position - 1 || value == position + 1) {
                    candidates[j] = candidates[size - 1];
                    size--;
                }
            }
        }
        Arrays.sort(result);
        return result;
    }
}

package info.elekiuo.grandgrotto.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.elekiuo.grandgrotto.geometry.Direction;
import info.elekiuo.grandgrotto.geometry.Matrix;
import info.elekiuo.grandgrotto.geometry.MutableMatrix;
import info.elekiuo.grandgrotto.geometry.Path;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Region;
import info.elekiuo.grandgrotto.geometry.Vector;

public class Board {
    private final MutableMatrix<Terrain> cells;
    private final List<Region> regions;
    private final Map<Position, Monster> monsters = new HashMap<>();

    Board(MutableMatrix<Terrain> cells, List<Region> regions) {
        this.cells = cells;
        this.regions = regions;
    }

    public Matrix<Terrain> getCells() {
        return cells;
    }

    boolean canStandOn(Position position) {
        return cells.get(position).canStandOn();
    }

    boolean canPassThrough(Position position, Direction direction) {
        if (direction.isCardinal()) {
            return true;
        }
        Vector vector = direction.vector;
        Position passPosition1 = position.plus(0, vector.dy);
        Position passPosition2 = position.plus(vector.dx, 0);
        return cells.get(passPosition1).canPassThrough() && cells.get(passPosition2).canPassThrough();
    }

    public Terrain getTerrain(Position position) {
        return cells.get(position);
    }

    void putTerrain(Position position, Terrain terrain) {
        cells.set(position, terrain);
    }

    public Collection<Monster> getMonsters() {
        return monsters.values();
    }

    public Monster getMonster(Position position) {
        return monsters.get(position);
    }

    void putMonster(Position position, Monster monster) {
        if (monster == null) {
            throw new IllegalArgumentException("monster is null");
        }
        if (position == null) {
            throw new IllegalArgumentException("position is null");
        }
        if (monster.getBoard() != null) {
            throw new IllegalArgumentException("monster is already on some board");
        }
        if (monsters.containsKey(position)) {
            throw new IllegalArgumentException(position + " is held by " + monsters.get(position));
        }
        monsters.put(position, monster);
        monster.setParentInternal(this, position);
    }

    void removeMonster(Monster monster) {
        if (monster == null) {
            throw new IllegalArgumentException("monster is null");
        }
        if (monster.getBoard() != this) {
            throw new IllegalArgumentException("monster is on another board");
        }
        monsters.remove(monster.getPosition());
        monster.setParentInternal(null, null);
    }

    public Region findRegion(Position position) {
        for (Region region : regions) {
            if (region.contains(position)) {
                return region;
            }
        }
        return new Region(position.x, position.y, position.x + 1, position.y + 1);
    }

    public Path.Finder getPathFinder(Region region, Position destination) {
        return new Path.Finder(destination, getGraphPredicate(region));
    }

    private Path.GraphPredicate getGraphPredicate(Region region) {
        final Region sight = region.extend(1);
        return new Path.GraphPredicate() {
            @Override
            public boolean hasVertex(Position position) {
                return sight.contains(position) && canStandOn(position);
            }
            @Override
            public boolean hasEdge(Position position, Direction direction) {
                return canPassThrough(position, direction);
            }
        };
    }
}

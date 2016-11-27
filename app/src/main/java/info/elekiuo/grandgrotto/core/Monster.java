package info.elekiuo.grandgrotto.core;

import java.util.ArrayList;
import java.util.List;

import info.elekiuo.grandgrotto.geometry.Path;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Direction;
import info.elekiuo.grandgrotto.geometry.Region;
import info.elekiuo.grandgrotto.geometry.Vector;

public class Monster implements TurnTable.Weighted {
    public static final int GROUP_PLAYER = 0;
    public static final int GROUP_ENEMY = 1;

    private Board board;
    private Position position;
    private Direction direction = Direction.EAST;
    int group;
    int life = 10;
    int maxLife = 10;
    private int weight = 2;

    public Board getBoard() {
        return board;
    }

    public Position getPosition() {
        return position;
    }

    // Called from Board
    void setParentInternal(Board board, Position position) {
        this.board = board;
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getLife() {
        return life;
    }

    public int getMaxLife() {
        return maxLife;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    void setWeight(int weight) {
        this.weight = weight;
    }

    private void resetPosition(Position position) {
        Board board = this.board;
        board.removeMonster(this);
        board.putMonster(position, this);
    }

    public boolean isMovableTo(Direction direction) {
        Position newPosition = position.plus(direction);
        return board.canStandOn(newPosition) && board.getMonster(newPosition) == null && board.canPassThrough(position, direction);
    }

    boolean move(Direction direction) {
        this.direction = direction;
        if (isMovableTo(direction)) {
            resetPosition(position.plus(direction));
            return true;
        } else {
            return false;
        }
    }

    void heal(int quantity) {
        life = Math.min(maxLife, life + quantity);
    }

    public Region getSight() {
        return board.findRegion(position).extend(1);
    }

    boolean isHostileTo(Monster monster) {
        return this.group != monster.group;
    }

    private List<Monster> findEnemies() {
        List<Monster> result = new ArrayList<>();
        Region sight = getSight();
        for (int y = sight.north; y < sight.south; y++) {
            for (int x = sight.west; x < sight.east; x++) {
                Monster monster = board.getMonster(new Position(x, y));
                if (monster != null && isHostileTo(monster)) {
                    result.add(monster);
                }
            }
        }
        return result;
    }

    private Monster selectReachableEnemy() {
        Region region = board.findRegion(position);
        Monster selectedEnemy = null;
        int minimumCost = Integer.MAX_VALUE;
        for (Monster enemy : findEnemies()) {
            int cost = board.getPathFinder(region, enemy.position).findFrom(position).getCost();
            if (cost < minimumCost) {
                selectedEnemy = enemy;
                minimumCost = cost;
            }
        }
        return selectedEnemy;
    }

    private Position destination;
    private Position lastPosition;
    Command doSomething(Random random) {
        Region region = board.findRegion(position);

        Monster enemy = selectReachableEnemy();
        if (enemy != null) {
            destination = enemy.position;

            Vector vector = enemy.position.minus(position);
            if (vector.chessNorm() <= 1) {
                Direction direction = Direction.fromVector(vector);
                if (board.canPassThrough(position, direction)) {
                    return new Command.Attack(direction);
                }
            }
        }

        if (destination == null || destination.equals(position)) {
            List<Position> candidates = new ArrayList<>();
            for (int x = region.west; x < region.east; x++) {
                check(candidates, new Position(x, region.north - 1));
                check(candidates, new Position(x, region.south));
            }
            for (int y = region.north; y < region.south; y++) {
                check(candidates, new Position(region.west - 1, y));
                check(candidates, new Position(region.east, y));
            }
            if (candidates.isEmpty()) {
                check(candidates, random.choose(region));
            }
            destination = candidates.isEmpty() ? lastPosition : random.choose(candidates);
            if (destination == null || destination.equals(position)) {
                lastPosition = null;
                return new Command.Rest();
            }
        }

        Direction selectedDirection = null;
        Path.Finder finder = board.getPathFinder(region, destination);
        Path path = finder.findFrom(position);
        if (path != null) {
            int minimumCost = path.getCost();
            for (Direction direction : Direction.values()) {
                if (!isMovableTo(direction)) {
                    continue;
                }
                Path path2 = finder.findFrom(position.plus(direction));
                if (path2 == null) {
                    continue;
                }
                int cost = path2.getCost();
                if (cost < minimumCost) {
                    selectedDirection = direction;
                    minimumCost = cost;
                }
            }
        }

        if (selectedDirection != null) {
            lastPosition = position;
            return new Command.Move(selectedDirection);
        } else {
            destination = null;
            lastPosition = null;
            return new Command.Rest();
        }
    }

    private void check(List<Position> doors, Position position) {
        Terrain terrain = board.getTerrain(position);
        if (terrain.canStandOn() && !position.equals(lastPosition)) {
            doors.add(position);
        }
    }

    public boolean isVisible(Position position) {
        return getSight().contains(position);
    }

    public boolean isVisible(Monster monster) {
        return this == monster || isVisible(monster.getPosition());
    }

    public boolean isVisible(Monster monster, Direction direction) {
        return this == monster || isVisible(monster.getPosition()) || isVisible(monster.getPosition().plus(direction));
    }
}

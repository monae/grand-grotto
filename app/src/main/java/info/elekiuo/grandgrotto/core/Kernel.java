package info.elekiuo.grandgrotto.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import info.elekiuo.grandgrotto.geometry.Direction;
import info.elekiuo.grandgrotto.geometry.Matrix;
import info.elekiuo.grandgrotto.geometry.Position;

class Kernel {
    private final Random random;
    private final Board board;
    private final TurnTable<Monster> turnTable = new TurnTable<>();
    private final List<Event> eventStack = new ArrayList<>();

    Kernel(Random random) {
        this.random = random;

        this.board = new DungeonBuilder(random).make();

        Direction[] directions = Direction.values();
        for (int i = 0; i < 8; i++) {
            Monster monster = new Monster();
            monster.setDirection(random.choose(directions));
            monster.setWeight(random.choose(new Integer[]{6, 12, 24}));
            monster.group = Monster.GROUP_ENEMY;
            placeMonster(monster);
        }
    }

    void placeMonster(Monster monster) {
        for (int i = 0; i < 100; i++) {
            Matrix<Terrain> cells = board.getCells();
            Position position = new Position(random.next(cells.cols), random.next(cells.rows));
            if (board.canStandOn(position) && board.getMonster(position) == null) {
                board.putMonster(position, monster);
                turnTable.add(monster);
                return;
            }
        }
        throw new RuntimeException();
    }

    Random getRandom() {
        return random;
    }

    Board getBoard() {
        return board;
    }

    Matrix<Terrain> getCells() {
        return board.getCells();
    }

    Collection<Monster> getMonsters() {
        return board.getMonsters();
    }

    Monster getActiveMonster() {
        return eventStack.isEmpty() ? turnTable.get() : null;
    }

    void execute(Command command) {
        if (!eventStack.isEmpty()) {
            throw new IllegalStateException("There are unhandled events");
        }
        if (turnTable.isEmpty()) {
            throw new IllegalStateException("No monsters");
        }
        List<? extends Event> events = command.execute(this, turnTable.get());
        eventStack.addAll(events);
        turnTable.rotate();
    }

    Event peekEvent() {
        return eventStack.isEmpty() ? null : eventStack.get(0);
    }

    Event removeEvent() {
        Event event = eventStack.remove(0);
        List<? extends Event> newEvents = event.process(this);
        if (newEvents != null) {
            eventStack.addAll(0, newEvents);
        }
        return event;
    }

    List<? extends Event> processMove(Monster monster, Direction direction) {
        monster.move(direction);
        return null;
    }

    List<? extends Event> processAttack(Monster monster, Direction direction) {
        monster.setDirection(direction);
        if (!board.canPassThrough(monster.getPosition(), direction)) {
            return null;
        }
        Monster victim = board.getMonster(monster.getPosition().plus(direction));
        if (victim == null) {
            return null;
        }
        int damage = random.choose(3, 10);
        return Collections.singletonList(new Event.Injured(victim, damage));
    }

    List<? extends Event> processInjured(Monster monster, int quantity) {
        monster.life -= quantity;
        if (monster.life > 0) {
            return null;
        } else {
            monster.life = 0;
            return Collections.<Event>singletonList(new Event.Died(monster));
        }
    }

    List<? extends Event> processDied(Monster monster) {
        board.removeMonster(monster);
        turnTable.remove(monster);
        return null;
    }
}

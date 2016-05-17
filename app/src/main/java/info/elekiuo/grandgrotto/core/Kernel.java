package info.elekiuo.grandgrotto.core;

import java.util.Collection;

import info.elekiuo.grandgrotto.geometry.Direction;
import info.elekiuo.grandgrotto.geometry.Matrix;
import info.elekiuo.grandgrotto.geometry.Position;

class Kernel {
    private final Random random;
    private final Board board;
    private final TurnTable<Monster> turnTable = new TurnTable<>();
    private Message currentMessage;

    Kernel(Random random) {
        this.random = random;

        this.board = new DungeonBuilder(random).make();

        Direction[] directions = Direction.values();
        for (int i = 0; i < 8; i++) {
            Monster monster = new Monster();
            monster.setDirection(random.choose(directions));
            monster.setWeight(random.choose(new Integer[]{6, 12, 24}));
            monster.group = Monster.GROUP_ENEMY + random.next(2);
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
        return turnTable.get();
    }

    boolean isExecutable(Command command) {
        return currentMessage == null &&
                !turnTable.isEmpty() &&
                command.isExecutable(this, turnTable.get());
    }

    void execute(Command command) {
        if (!isExecutable(command)) {
            throw new IllegalArgumentException(command + " is not executable");
        }
        currentMessage = command.toMessage(this, turnTable.get());
        turnTable.rotate();
    }

    Message peekMessage() {
        return currentMessage;
    }

    Message removeMessage() {
        Message message = currentMessage;
        currentMessage = message.process(this);
        return message;
    }

    Message processRest(Monster monster) {
        monster.heal(1);
        return null;
    }

    Message processMove(Monster monster, Direction direction) {
        monster.move(direction);
        monster.heal(random.next(4) / 3);
        return null;
    }

    Message processAttack(Monster monster, Direction direction) {
        if (!board.canPassThrough(monster.getPosition(), direction)) {
            return null;
        }
        Monster victim = board.getMonster(monster.getPosition().plus(direction));
        if (victim == null) {
            return null;
        }
        int damage = random.choose(3, 10);
        return new Message.Injured(victim, damage);
    }

    Message processInjured(Monster monster, int quantity) {
        monster.life -= quantity;
        if (monster.life > 0) {
            return null;
        } else {
            monster.life = 0;
            return new Message.Died(monster);
        }
    }

    Message processDied(Monster monster) {
        board.removeMonster(monster.getPosition(), monster);
        turnTable.remove(monster);
        return null;
    }
}

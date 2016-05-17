package info.elekiuo.grandgrotto.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import info.elekiuo.grandgrotto.geometry.Matrix;
import info.elekiuo.grandgrotto.geometry.MutableMatrix;
import info.elekiuo.grandgrotto.geometry.Position;
import info.elekiuo.grandgrotto.geometry.Region;

public class Shell {
    private final Kernel kernel;
    private final Monster player;
    private final MutableMatrix<Terrain> map;

    public Shell(Random random) {
        this.kernel = new Kernel(random);

        player = new Monster();
        player.life = player.maxLife = 100;
        player.setWeight(12);
        player.group = Monster.GROUP_PLAYER;
        kernel.placeMonster(player);

        Matrix<Terrain> cells = kernel.getCells();
        map = new MutableMatrix<>(cells.cols, cells.rows);
        updateMap();
    }

    public Monster getPlayer() {
        return player;
    }

    public Region getSight() {
        return player.getSight();
    }

    public Monster getMonster(Position position) {
        return getSight().contains(position) ? kernel.getBoard().getMonster(position) : null;
    }

    public Collection<Monster> getMonsters() {
        Region sight = getSight();
        List<Monster> result = new ArrayList<>();
        for (Monster monster : kernel.getMonsters()) {
            if (sight.contains(monster.getPosition())) {
                result.add(monster);
            }
        }
        return result;
    }

    @Deprecated
    public Collection<Monster> getAllMonsters() {
        return kernel.getMonsters();
    }

    public Matrix<Terrain> getMap() {
        return map;
    }

    public boolean isExecutable(Command command) {
        return kernel.getActiveMonster() == player && kernel.isExecutable(command);
    }

    public void execute(Command command) {
        if (!isExecutable(command)) {
            throw new IllegalArgumentException(command + " is not executable");
        }
        kernel.execute(command);
    }

    private boolean isEnd(Message message) {
        return message instanceof Message.Died && ((Message.Died) message).monster == player;
    }

    private boolean isVisible(Message message) {
        if (message == null) {
            return true;
        }
        Monster monster = message.getMonster();
        if (monster == player) {
            return true;
        }
        Region sight = getSight();
        Position position = monster.getPosition();
        if (sight.contains(position)) {
            return true;
        }
        if (message instanceof Message.Move && sight.contains(position.plus(((Message.Move) message).direction))) {
            return true;
        }
        return false;
    }

    public Message peekMessage() {
        Message message = kernel.peekMessage();
        return isEnd(message) ? null : message;
    }

    public Message removeMessage() {
        Message result = removeMessageInternal();
        while (!isVisible(peekMessage())) {
            removeMessageInternal();
        }
        return result;
    }

    private Message removeMessageInternal() {
        Message message = kernel.removeMessage();
        if (isEnd(message)) {
            throw new UnsupportedOperationException();
        }

        if (message instanceof Message.Move && ((Message.Move) message).monster == player) {
            updateMap();
        }

        if (kernel.peekMessage() == null) {
            Monster monster = kernel.getActiveMonster();
            if (monster != player) {
                Command command = monster.doSomething(kernel.getRandom());
                kernel.execute(command);
            }
        }

        return message;
    }

    private void updateMap() {
        Region sight = getSight();
        map.view(sight).copyFrom(kernel.getCells().view(sight));
    }
}

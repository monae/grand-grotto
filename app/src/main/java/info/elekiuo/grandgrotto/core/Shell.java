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

    public void execute(Command command) {
        if (kernel.getActiveMonster() != player) {
            throw new IllegalArgumentException(player + " is not active");
        }
        kernel.execute(command);
    }

    private boolean isEnd(Event event) {
        return event instanceof Event.Died && ((Event.Died) event).monster == player;
    }

    public Event peekEvent() {
        while (true) {
            Event event = peekEventInternal();
            if (event == null || event.isObservableFrom(player)) {
                return isEnd(event) ? null : event;
            }
            kernel.removeEvent();
        }
    }

    public Event popEvent() {
        Event event = peekEvent();
        if (event == null) {
            throw new UnsupportedOperationException();
        }
        kernel.removeEvent();
        if (event instanceof Event.Move && ((Event.Move) event).monster == player) {
            updateMap();
        }
        return event;
    }

    private Event peekEventInternal() {
        while (true) {
            Event event = kernel.peekEvent();
            if (event != null) {
                return event;
            }
            Monster monster = kernel.getActiveMonster();
            if (monster == player) {
                return null;
            }
            Command command = monster.doSomething(kernel.getRandom());
            kernel.execute(command);
        }
    }

    private void updateMap() {
        Region sight = getSight();
        map.view(sight).copyFrom(kernel.getCells().view(sight));
    }
}

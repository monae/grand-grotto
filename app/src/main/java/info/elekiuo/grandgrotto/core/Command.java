package info.elekiuo.grandgrotto.core;

import java.util.Collections;
import java.util.List;

import info.elekiuo.grandgrotto.geometry.Direction;

public abstract class Command {
    private Command() {
    }

    abstract List<? extends Event> execute(Kernel kernel, Monster monster);

    public static class Rest extends Command {
        @Override
        public List<? extends Event> execute(Kernel kernel, Monster monster) {
            return Collections.emptyList();
        }
    }

    public static class Move extends Command {
        public final Direction direction;

        public Move(Direction direction) {
            this.direction = direction;
        }

        @Override
        public List<? extends Event> execute(Kernel kernel, Monster monster) {
            return Collections.singletonList(new Event.Move(monster, direction));
        }
    }

    public static class Attack extends Command {
        public final Direction direction;

        public Attack(Direction direction) {
            this.direction = direction;
        }

        @Override
        public List<? extends Event> execute(Kernel kernel, Monster monster) {
            return Collections.singletonList(new Event.Attack(monster, direction));
        }
    }
}

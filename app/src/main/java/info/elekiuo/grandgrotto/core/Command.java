package info.elekiuo.grandgrotto.core;

import info.elekiuo.grandgrotto.geometry.Direction;

public abstract class Command {
    private Command() {
    }

    abstract boolean isExecutable(Kernel kernel, Monster monster);
    abstract Message toMessage(Kernel kernel, Monster monster);

    public static class Rest extends Command {
        @Override
        boolean isExecutable(Kernel kernel, Monster monster) {
            return true;
        }

        @Override
        public Message toMessage(Kernel kernel, Monster monster) {
            return new Message.Rest(monster);
        }
    }

    public static class Move extends Command {
        public final Direction direction;

        public Move(Direction direction) {
            this.direction = direction;
        }

        @Override
        boolean isExecutable(Kernel kernel, Monster monster) {
            return monster.isMovableTo(direction);
        }

        @Override
        public Message toMessage(Kernel kernel, Monster monster) {
            return new Message.Move(monster, direction);
        }
    }

    public static class Attack extends Command {
        public final Direction direction;

        public Attack(Direction direction) {
            this.direction = direction;
        }

        @Override
        boolean isExecutable(Kernel kernel, Monster monster) {
            return true;
        }

        @Override
        public Message toMessage(Kernel kernel, Monster monster) {
            return new Message.Attack(monster, direction);
        }
    }
}

package info.elekiuo.grandgrotto.core;

import info.elekiuo.grandgrotto.geometry.Direction;

public abstract class Message {
    private Message() {
    }

    abstract Monster getMonster();
    abstract Message process(Kernel kernel);

    public static class Rest extends Message {
        public final Monster monster;

        Rest(Monster monster) {
            this.monster = monster;
        }

        @Override
        public String toString() {
            return "Rest[monster=" + monster + "]";
        }

        @Override
        public Monster getMonster() {
            return monster;
        }

        @Override
        Message process(Kernel kernel) {
            return kernel.processRest(monster);
        }
    }

    public static class Move extends Message {
        public final Monster monster;
        public final Direction direction;

        Move(Monster monster, Direction direction) {
            this.monster = monster;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "Move[monster=" + monster + ",direction=" + direction + "]";
        }

        @Override
        public Monster getMonster() {
            return monster;
        }

        @Override
        Message process(Kernel kernel) {
            return kernel.processMove(monster, direction);
        }
    }

    public static class Attack extends Message {
        public final Monster monster;
        public final Direction direction;

        Attack(Monster monster, Direction direction) {
            this.monster = monster;
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "Attack[monster=" + monster + ",direction=" + direction + "]";
        }

        @Override
        public Monster getMonster() {
            return monster;
        }

        @Override
        Message process(Kernel kernel) {
            return kernel.processAttack(monster, direction);
        }
    }

    public static class Injured extends Message {
        public final Monster monster;
        public final int quantity;

        Injured(Monster monster, int quantity) {
            this.monster = monster;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "Injured[monster=" + monster + ",quantity=" + quantity + "]";
        }

        @Override
        public Monster getMonster() {
            return monster;
        }

        @Override
        Message process(Kernel kernel) {
            return kernel.processInjured(monster, quantity);
        }
    }

    public static class Died extends Message {
        public final Monster monster;

        Died(Monster monster) {
            this.monster = monster;
        }

        @Override
        public String toString() {
            return "Died[monster=" + monster + "]";
        }

        @Override
        public Monster getMonster() {
            return monster;
        }

        @Override
        Message process(Kernel kernel) {
            return kernel.processDied(monster);
        }
    }
}

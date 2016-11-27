package info.elekiuo.grandgrotto.core;

import java.util.List;

import info.elekiuo.grandgrotto.geometry.Direction;

public abstract class Event {
    private Event() {
    }

    abstract List<? extends Event> process(Kernel kernel);
    abstract boolean isObservableFrom(Monster observer);

    public static class Move extends Event {
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
        List<? extends Event> process(Kernel kernel) {
            return kernel.processMove(monster, direction);
        }

        @Override
        boolean isObservableFrom(Monster observer) {
            return observer.isVisible(monster, direction);
        }
    }

    public static class Attack extends Event {
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
        List<? extends Event> process(Kernel kernel) {
            return kernel.processAttack(monster, direction);
        }

        @Override
        boolean isObservableFrom(Monster observer) {
            return observer.isVisible(monster);
        }
    }

    public static class Injured extends Event {
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
        List<? extends Event> process(Kernel kernel) {
            return kernel.processInjured(monster, quantity);
        }

        @Override
        boolean isObservableFrom(Monster observer) {
            return observer.isVisible(monster);
        }
    }

    public static class Died extends Event {
        public final Monster monster;

        Died(Monster monster) {
            this.monster = monster;
        }

        @Override
        public String toString() {
            return "Died[monster=" + monster + "]";
        }

        @Override
        List<? extends Event> process(Kernel kernel) {
            return kernel.processDied(monster);
        }

        @Override
        boolean isObservableFrom(Monster observer) {
            return observer.isVisible(monster);
        }
    }
}

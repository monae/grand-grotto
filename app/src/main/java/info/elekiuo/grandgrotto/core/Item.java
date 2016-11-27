package info.elekiuo.grandgrotto.core;

import info.elekiuo.grandgrotto.geometry.Position;

public class Item {
    private Board board;
    private Position position;

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
}

package org.batalhanaval;

public class PlayerStatus {
    boolean isMyTurn;
    boolean isReady;
    int shipsCount;

    public PlayerStatus(boolean isMyTurn, boolean isReady) {
        this.isMyTurn = isMyTurn;
        this.isReady = isReady;
        this.shipsCount = 1;
    }

    @Override
    public String toString() {
        return "PlayerStatus{" +
                "isMyTurn=" + isMyTurn +
                ", isReady=" + isReady +
                ", shipsCount=" + shipsCount +
                '}';
    }
}

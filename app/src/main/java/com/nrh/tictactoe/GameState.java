package com.nrh.tictactoe;

public class GameState {
    public String[] board;
    public boolean isPlayerOneTurn;
    public String playerOneId;
    public String playerTwoId;

    public GameState() {
        // Default constructor required for calls to DataSnapshot.getValue(GameState.class)
    }

    public GameState(String[] board, boolean isPlayerOneTurn, String playerOneId, String playerTwoId) {
        this.board = board;
        this.isPlayerOneTurn = isPlayerOneTurn;
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
    }
}

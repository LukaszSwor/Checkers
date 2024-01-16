package com.checkers;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class is responsible for updating the game status on the UI,
 * specifically the count of pieces for each player.
 */

public class GameStatusUpdater {

    private int whitePieceCount;
    private int blackPieceCount;
    private final Label whiteCountLabel;
    private final Label blackCountLabel;

    public GameStatusUpdater(Label whiteCountLabel, Label blackCountLabel) {
        this.whiteCountLabel = whiteCountLabel;
        this.blackCountLabel = blackCountLabel;
        this.whitePieceCount = 12;
        this.blackPieceCount = 12;
    }

    public void updatePlayerPieceCount(int whiteCount, int blackCount) {
        whiteCountLabel.setText("White Pieces: " + whiteCount);
        blackCountLabel.setText("Black Pieces: " + blackCount);
    }

    public void updatePieceCount(Circle pieceRemoved) {
        if (pieceRemoved.getFill() == Color.WHITE) {
            whitePieceCount--;
        } else if (pieceRemoved.getFill() == Color.BLACK) {
            blackPieceCount--;
        }
        updatePieceCountLabels();
    }
    private void updatePieceCountLabels() {
        whiteCountLabel.setText("White Pieces: " + whitePieceCount);
        blackCountLabel.setText("Black Pieces: " + blackPieceCount);
    }

}

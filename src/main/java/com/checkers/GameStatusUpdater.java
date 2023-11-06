package com.checkers;

import javafx.scene.control.Label;

/**
 * This class is responsible for updating the game status on the UI,
 * specifically the count of pieces for each player.
 */

public class GameStatusUpdater {

    private Label whiteCountLabel;
    private Label blackCountLabel;

    /**
     * @param whiteCountLabel The label for displaying the white piece count.
     * @param blackCountLabel The label for displaying the black piece count.
     */
    public GameStatusUpdater(Label whiteCountLabel, Label blackCountLabel) {
        this.whiteCountLabel = whiteCountLabel;
        this.blackCountLabel = blackCountLabel;
    }

    /**
     * @param whiteCount The number of white pieces remaining.
     * @param blackCount The number of black pieces remaining.
     */
    public void updatePlayerPieceCount(int whiteCount, int blackCount) {
        whiteCountLabel.setText("White Pieces: " + whiteCount);
        blackCountLabel.setText("Black Pieces: " + blackCount);
    }
}

package com.checkers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * GameController is responsible for handling user interactions within the
 * Checkers game board. It controls game flow, including piece selection,
 * movement, and capturing mechanics.
 */
public class GameController {
    @FXML
    private GridPane boardGridPane;
    @FXML
    public Label whitePlayerLabel;
    @FXML
    public Label blackPlayerLabel;
    @FXML
    public Label whiteCountLabel;
    @FXML
    public Label blackCountLabel;

    private Circle selectedPiece = null;

    private PieceMovementHandler pieceMovementHandler;
    private final Map<Circle, Boolean> isKingMap = new HashMap<>();


    public void initialize() {
        new GameBoard(boardGridPane);
        GameStatusUpdater statusUpdater = new GameStatusUpdater(whiteCountLabel, blackCountLabel);
        setFieldClickListener();
        statusUpdater.updatePlayerPieceCount(12, 12);

        pieceMovementHandler = new PieceMovementHandler(boardGridPane, isKingMap, statusUpdater);
        pieceMovementHandler.whiteCountLabel = whiteCountLabel;
        pieceMovementHandler.blackCountLabel = blackCountLabel;
    }

    private void setFieldClickListener() {
        for (Node node : boardGridPane.getChildren()) {
            if (node instanceof Rectangle field) {
                field.setOnMouseClicked(event -> handleFieldClick(field));
            } else if (node instanceof Circle piece) {
                piece.setOnMouseClicked(event -> handlePieceClick(piece));
            }
        }
    }

    private void handlePieceClick(Circle piece) {
        if (selectedPiece != null) {
            pieceMovementHandler.deselectPiece();
            selectedPiece = null; // Update the selectedPiece state accordingly
        }
        if (selectedPiece != piece) {
            pieceMovementHandler.selectPiece(piece);
            selectedPiece = piece; // Keep track of the selected piece
        }
    }


    private void handleFieldClick(Rectangle field) {
        int rowFieldClicked = GridPane.getRowIndex(field);
        int colFieldClicked = GridPane.getColumnIndex(field);

        if (selectedPiece != null) {
            pieceMovementHandler.executeMoveForSelectedPiece(rowFieldClicked, colFieldClicked);
        } else {
            System.out.println("No piece selected to move.");
        }
    }
}

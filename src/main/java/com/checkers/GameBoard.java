package com.checkers;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * The GameBoard class is responsible for initializing and displaying the graphical
 * representation of the checkers game board.
 */
public class GameBoard {

    private final GridPane boardGridPane;
    private final int FIELD_SIZE = 50;
    private final int BOARD_SIZE = 8;
    private final Color FIELD_COLOR1 = Color.web("#D2691E");
    private final Color FIELD_COLOR2 = Color.web("#8B4513");

    /**
     * @param boardGridPane The GridPane that this GameBoard will be associated with.
     */
    public GameBoard(GridPane boardGridPane) {
        this.boardGridPane = boardGridPane;
        createGameBoard();
        setPieces();
    }

    private void createGameBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Rectangle field = createGameField(row, col);
                boardGridPane.add(field, col, row);
            }
        }
    }

    /**
     * @param row The row where the field will be placed.
     * @param col The column where the field will be placed.
     * @return A Rectangle representing the field.
     */
    private Rectangle createGameField(int row, int col) {
        Rectangle field = new Rectangle(FIELD_SIZE, FIELD_SIZE);
        field.setFill(getFieldColor(row, col));
        return field;
    }

    /**
     * @param row The row of the field.
     * @param col The column of the field.
     * @return The Color of the field.
     */
    private Color getFieldColor(int row, int col) {
        return (row + col) % 2 == 0 ? FIELD_COLOR1 : FIELD_COLOR2;
    }

    private void setPieces() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (row >= 5 && (row + col) % 2 != 0) {
                    addPieces(Color.WHITE, col, row);
                }
                if (row <= 2 && (row + col) % 2 != 0) {
                    addPieces(Color.BLACK, col, row);
                }
            }
        }
    }

    /**
     * @param color The color of the piece to be added.
     * @param col   The column where the piece will be placed.
     * @param row   The row where the piece will be placed.
     */
    public void addPieces(Color color, int col, int row) {
        Piece piece = new Piece(color);
        Circle circlePiece = piece.getPiece();
        boardGridPane.add(circlePiece, col, row);
        setPieceAlignment(circlePiece);
    }

    /**
     * @param piece The piece to be aligned.
     */
    public void setPieceAlignment(Circle piece) {
        GridPane.setHalignment(piece, HPos.CENTER);
    }
}


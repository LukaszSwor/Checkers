package com.checkers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;

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
    private boolean isWhitePlayerMove = true;
    private static final Color PIECE_COLOR_WHITE = Color.WHITE;
    private static final Color PIECE_COLOR_BLACK = Color.BLACK;

    private boolean isInCaptureSequence = false;
    private final Map<Circle, Boolean> isKingMap = new HashMap<>();
    private int whitePieceCount = 12;
    private int blackPieceCount = 12;

    public void initialize() {
        GameBoard gameBoard = new GameBoard(boardGridPane);
        GameStatusUpdater statusUpdater = new GameStatusUpdater(whiteCountLabel, blackCountLabel);
        setFieldClickListener();
        statusUpdater.updatePlayerPieceCount(12, 12);
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

    /**
     * @param piece The piece that was clicked.
     */
    private void handlePieceClick(Circle piece) {
        if (selectedPiece != null) {
            deselectPiece();
        }
        if (selectedPiece != piece) {
            selectPiece(piece);
        } else {
            selectedPiece = null;
        }
    }

    private void deselectPiece() {
        resetSelectedPieceSize();
        selectedPiece = null;
    }

    /**
     * @param piece The piece to be selected.
     */
    private void selectPiece(Circle piece) {
        scalePiece(piece);
        selectedPiece = piece;
    }

    /**
     * @param field The field that was clicked.
     */
    private void handleFieldClick(Rectangle field) {
        int rowFieldClicked = GridPane.getRowIndex(field);
        int colFieldClicked = GridPane.getColumnIndex(field);

        if (selectedPiece != null) {
            executeMoveForSelectedPiece(rowFieldClicked, colFieldClicked);
        }
    }

    /**
     * @param rowFieldClicked The row index of the clicked field.
     * @param colFieldClicked The column index of the clicked field.
     */
    private void executeMoveForSelectedPiece(int rowFieldClicked, int colFieldClicked) {
        boolean isKing = isKingMap.getOrDefault(selectedPiece, false);
        if (isKing) {
            handleKingMovement(selectedPiece, rowFieldClicked, colFieldClicked);
        } else {
            handlePieceMovement(rowFieldClicked, colFieldClicked);
        }
    }

    /**
     * @param rowFieldClicked The row index of the clicked field.
     * @param colFieldClicked The column index of the clicked field.
     */

    private void handlePieceMovement(int rowFieldClicked, int colFieldClicked) {
        Color selectedPieceColor = (Color) selectedPiece.getFill();
        int currentRow = GridPane.getRowIndex(selectedPiece);
        int currentCol = GridPane.getColumnIndex(selectedPiece);

        if (isInCaptureSequence && !isCaptureMove(rowFieldClicked, colFieldClicked)) {
            System.out.println("Player must continue capturing");
            return;
        }
        if (isPieceValidMove(rowFieldClicked, selectedPieceColor)) {
            processMove(rowFieldClicked, colFieldClicked, currentRow, currentCol);
        }
    }

    /**
     * @param rowFieldClicked The row index of the clicked field.
     * @param colFieldClicked The column index of the clicked field.
     * @param currentRow      The current row index of the selected piece.
     * @param currentCol      The current column index of the selected piece.
     */
    private void processMove(int rowFieldClicked, int colFieldClicked, int currentRow, int currentCol) {
        int rowDiff = Math.abs(rowFieldClicked - currentRow);
        int colDiff = Math.abs(colFieldClicked - currentCol);

        if (isPieceOnField(rowFieldClicked, colFieldClicked)) {
            System.out.println("Field is occupied, move not allowed");
            return;
        }
        if (rowDiff == 1 && colDiff == 1) {
            System.out.println("Single move");
            makeSingleMove(rowFieldClicked, colFieldClicked);
            return;
        }
        if (isCaptureMove(rowFieldClicked, colFieldClicked)) {
            System.out.println("Attempting capture");
            makeCaptureMove(rowFieldClicked, colFieldClicked);
            return;
        }
        System.out.println("Invalid move");
    }

    /**
     * @param rowFieldClicked The row index of the target field.
     * @param colFieldClicked The column index of the target field.
     * @return {@code true} if the move is a capture move, {@code false} otherwise.
     */
    private boolean isCaptureMove(int rowFieldClicked, int colFieldClicked) {
        int currentRow = GridPane.getRowIndex(selectedPiece);
        int currentCol = GridPane.getColumnIndex(selectedPiece);

        int rowDiff = Math.abs(rowFieldClicked - currentRow);
        int colDiff = Math.abs(colFieldClicked - currentCol);

        return rowDiff == 2 && colDiff == 2;
    }

    /**
     * @param rowFieldClicked    The row index of the clicked field.
     * @param selectedPieceColor The color of the selected piece.
     * @return {@code true} if the move is valid, {@code false} otherwise.
     */
    private boolean isPieceValidMove(int rowFieldClicked, Color selectedPieceColor) {
        if (isInCaptureSequence) {
            return true;
        } else {
            if (isWhitePlayerMove && selectedPieceColor == PIECE_COLOR_WHITE) {
                return rowFieldClicked < GridPane.getRowIndex(selectedPiece);
            } else if (!isWhitePlayerMove && selectedPieceColor == PIECE_COLOR_BLACK) {
                return rowFieldClicked > GridPane.getRowIndex(selectedPiece);
            }
        }
        return false;
    }

    /**
     * @param rowFieldClicked The row index of the field where the piece is moved.
     * @param colFieldClicked The column index of the field where the piece is moved.
     */

    private void makeSingleMove(int rowFieldClicked, int colFieldClicked) {
        GridPane.setRowIndex(selectedPiece, rowFieldClicked);
        GridPane.setColumnIndex(selectedPiece, colFieldClicked);

        boolean isPieceKing = checkForKing(selectedPiece, rowFieldClicked);
        if (isPieceKing) {
            markPieceAsKing(selectedPiece);
        }
        resetSelectedPieceSize();
        selectedPiece = null;
        switchPlayerTurn();
    }

    /**
     * @param newRow The row index of the field where the capturing piece will move.
     * @param newCol The column index of the field where the capturing piece will move.
     */
    private void makeCaptureMove(int newRow, int newCol) {
        removeCapturedPiece(newRow, newCol);
        movePieceToNewPosition(newRow, newCol);
        updateStatusAfterMove(newRow);
    }

    /**
     * @param newRow The row index where the capturing piece is moving to.
     * @param newCol The column index where the capturing piece is moving to.
     */
    private void removeCapturedPiece(int newRow, int newCol) {
        int middleRow = (newRow + GridPane.getRowIndex(selectedPiece)) / 2;
        int middleCol = (newCol + GridPane.getColumnIndex(selectedPiece)) / 2;
        Circle middlePiece = findPiece(middleRow, middleCol);
        if (middlePiece != null && middlePiece.getFill() != selectedPiece.getFill()) {
            boardGridPane.getChildren().remove(middlePiece);
            updatePieceCount(middlePiece);
        }
    }

    /**
     * @param pieceRemoved The piece that was removed from the board.
     */
    private void updatePieceCount(Circle pieceRemoved) {
        if (pieceRemoved.getFill() == PIECE_COLOR_WHITE) {
            whitePieceCount--;
        } else if (pieceRemoved.getFill() == PIECE_COLOR_BLACK) {
            blackPieceCount--;
        }
        updatePieceCountLabels();
    }

    /**
     * @param newRow The row index where the piece will be moved.
     * @param newCol The column index where the piece will be moved.
     */
    private void movePieceToNewPosition(int newRow, int newCol) {
        GridPane.setRowIndex(selectedPiece, newRow);
        GridPane.setColumnIndex(selectedPiece, newCol);
    }

    /**
     * @param newRow The row index where the piece has been moved.
     */
    private void updateStatusAfterMove(int newRow) {
        boolean isPieceKing = checkForKing(selectedPiece, newRow);
        if (isPieceKing) {
            markPieceAsKing(selectedPiece);
        }
        isInCaptureSequence = true;
        checkAdjacentOpponentPieces(newRow, GridPane.getColumnIndex(selectedPiece));
    }

    private void updatePieceCountLabels() {
        whiteCountLabel.setText("White Pieces: " + whitePieceCount);
        blackCountLabel.setText("Black Pieces: " + blackPieceCount);
    }

    /**
     * @param row The row index of the selected piece.
     * @param col The column index of the selected piece.
     */
    private void checkAdjacentOpponentPieces(int row, int col) {
        if (!checkForAdditionalCaptures(row, col)) {
            endCaptureSequence();
        }
    }
    /**
     * @param row The row index of the selected piece.
     * @param col The column index of the selected piece.
     * @return {@code true} if an additional capture is possible, {@code false} otherwise.
     */
    private boolean checkForAdditionalCaptures(int row, int col) {
        int[] adjacentRows = {-1, -1, 1, 1};
        int[] adjacentCols = {-1, 1, -1, 1};

        for (int i = 0; i < 4; i++) {
            if (checkDirectionForCapture(row, col, adjacentRows[i], adjacentCols[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param row The row index of the starting position.
     * @param col The column index of the starting position.
     * @param rowDelta The row difference to check for the opponent's piece.
     * @param colDelta The column difference to check for the opponent's piece.
     * @return {@code true} if moving in the specified direction results in a capture, {@code false} otherwise.
     */
    private boolean checkDirectionForCapture(int row, int col, int rowDelta, int colDelta) {
        int newRow = row + rowDelta;
        int newCol = col + colDelta;

        if (isValidMove(newRow, newCol) && isOpponentPiece(newRow, newCol)) {
            int behindOpponentRow = newRow + rowDelta;
            int behindOpponentCol = newCol + colDelta;

            if (isValidMove(behindOpponentRow, behindOpponentCol) && !isPieceOnField(behindOpponentRow, behindOpponentCol)) {
                isInCaptureSequence = true;
                return true;
            }
        }
        return false;
    }

    private void endCaptureSequence() {
        isInCaptureSequence = false;
        switchPlayerTurn();
    }

    /**
     * @param row The row index of the field to check.
     * @param col The column index of the field to check.
     * @return {@code true} if there is a piece on the field, {@code false} otherwise.
     */
    private boolean isPieceOnField(int row, int col) {
        Circle piece = findPiece(row, col);
        if (piece != null) {
            Color pieceColor = (Color) piece.getFill();
            return pieceColor == Color.WHITE || pieceColor == Color.BLACK;
        }
        return false;
    }

    /**
     * @param row The row index to check.
     * @param col The column index to check.
     * @return {@code true} if the move is within the board's bounds, {@code false} otherwise.
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * @param row The row index of the piece to check.
     * @param col The column index of the piece to check.
     * @return {@code true} if the piece at the coordinates is an opponent's piece, {@code false} otherwise.
     */
    private boolean isOpponentPiece(int row, int col) {
        Circle piece = findPiece(row, col);
        return piece != null && piece.getFill() != selectedPiece.getFill();
    }

    /**
     * @param piece The piece to scale.
     */
    private void scalePiece(Circle piece) {
        Scale scale = new Scale(1.2, 1.2, piece.getCenterX(), piece.getCenterY());
        piece.getTransforms().add(scale);
    }

    private void resetSelectedPieceSize() {
        if (selectedPiece != null) {
            Scale scale = new Scale(1.0, 1.0, selectedPiece.getCenterX(), selectedPiece.getCenterY());
            selectedPiece.getTransforms().clear();
            selectedPiece.getTransforms().add(scale);
            selectedPiece = null;
        }
    }

    /**
     * @param row The row index where the piece is located.
     * @param col The column index where the piece is located.
     * @return The piece found at the specified location, or {@code null} if no piece is present.
     */
    private Circle findPiece(int row, int col) {
        for (Node node : boardGridPane.getChildren()) {
            if (node instanceof Circle && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Circle) node;
            }
        }
        return null;
    }

    private void switchPlayerTurn() {
        isWhitePlayerMove = !isWhitePlayerMove;
        if (isWhitePlayerMove) {
            whitePlayerLabel.setFont(new Font(30));
            blackPlayerLabel.setFont(new Font(20));
        } else {
            whitePlayerLabel.setFont(new Font(20));
            blackPlayerLabel.setFont(new Font(30));
        }
    }

    /**
     * @param piece The piece to check for king status.
     * @param row The row index where the piece is located.
     * @return {@code true} if the piece has reached the opposite end of the board and should be kinged, {@code false} otherwise.
     */
    private boolean checkForKing(Circle piece, int row) {
        Color pieceColor = (Color) piece.getFill();
        return (pieceColor == PIECE_COLOR_WHITE && row == 0) || (pieceColor == PIECE_COLOR_BLACK && row == 7);
    }


    /**
     * @param piece The piece to be marked as a king.
     */
    private void markPieceAsKing(Circle piece) {
        isKingMap.put(piece, true);
        piece.setStroke(Color.GOLD);
        piece.setStrokeWidth(3.0);
    }

    /**
     * @param piece           The king piece to be moved.
     * @param rowFieldClicked The row index of the clicked field.
     * @param colFieldClicked The column index of the clicked field.
     */
    private void handleKingMovement(Circle piece, int rowFieldClicked, int colFieldClicked) {
        if (!isPlayerTurnValid(piece)) {
            System.out.println("Not your turn");
            return;
        }
        if (!isMoveDiagonalAndValid(piece, rowFieldClicked, colFieldClicked)) {
            System.out.println("Invalid move. King can only move diagonally to an empty space or capture.");
            return;
        }
        if (tryKingCapture(piece, rowFieldClicked, colFieldClicked)) {
            processKingCapture(piece, rowFieldClicked, colFieldClicked);
        } else if (!isInCaptureSequence) {
            moveKingToNewPosition(piece, rowFieldClicked, colFieldClicked);
            switchPlayerTurn();
        } else {
            System.out.println("Invalid move. You must continue capturing!");
        }
    }

    /**
     * @param piece The piece being moved.
     * @return {@code true} if it is the correct player's turn, {@code false} otherwise.
     */
    private boolean isPlayerTurnValid(Circle piece) {
        return (isWhitePlayerMove && piece.getFill() == PIECE_COLOR_WHITE) ||
                (!isWhitePlayerMove && piece.getFill() == PIECE_COLOR_BLACK);
    }

    /**
     * @param piece           The king piece being moved.
     * @param rowFieldClicked The row index of the clicked field.
     * @param colFieldClicked The column index of the clicked field.
     * @return {@code true} if the move is diagonal and to a valid location, {@code false} otherwise.
     */
    private boolean isMoveDiagonalAndValid(Circle piece, int rowFieldClicked, int colFieldClicked) {
        int colClicked = GridPane.getColumnIndex(piece);
        int rowClicked = GridPane.getRowIndex(piece);
        int colDiff = Math.abs(colClicked - colFieldClicked);
        int rowDiff = Math.abs(rowClicked - rowFieldClicked);

        return colDiff == rowDiff && colDiff > 0 && !isPieceOnField(rowFieldClicked, colFieldClicked);
    }

    /**
     * @param piece           The king piece that is trying to capture.
     * @param rowFieldClicked The row index where the piece is trying to move.
     * @param colFieldClicked The column index where the piece is trying to move.
     * @return {@code true} if a capture is made, {@code false} otherwise.
     */
    private boolean tryKingCapture(Circle piece, int rowFieldClicked, int colFieldClicked) {
        int colClicked = GridPane.getColumnIndex(piece);
        int rowClicked = GridPane.getRowIndex(piece);
        int colDiff = Math.abs(colClicked - colFieldClicked);
        int rowDiff = Math.abs(rowClicked - rowFieldClicked);
        int rowStep = (rowFieldClicked - rowClicked) / rowDiff;
        int colStep = (colFieldClicked - colClicked) / colDiff;

        for (int step = 1; step < colDiff; step++) {
            int checkRow = rowClicked + rowStep * step;
            int checkCol = colClicked + colStep * step;
            Circle opponentPiece = findPiece(checkRow, checkCol);

            if (opponentPiece != null && opponentPiece.getFill() != piece.getFill()) {
                System.out.println("Opponent's piece detected at: Row " + checkRow + ", Col " + checkCol);
                boardGridPane.getChildren().remove(opponentPiece);
                return true;
            }
        }
        return false;
    }

    /**
     * @param piece           The king piece that has made the capture.
     * @param rowFieldClicked The row index where the piece has moved.
     * @param colFieldClicked The column index where the piece has moved.
     */
    private void processKingCapture(Circle piece, int rowFieldClicked, int colFieldClicked) {
        moveKingToNewPosition(piece, rowFieldClicked, colFieldClicked);
        selectedPiece = piece;
        if (checkAdditionalKingCaptureMoves(piece)) {
            isInCaptureSequence = true;
        } else {
            isInCaptureSequence = false;
            resetSelectedPieceSize();
            selectedPiece = null;
            switchPlayerTurn();
        }
    }

    /**
     * @param piece           The king piece to move.
     * @param rowFieldClicked The row index where the piece should move to.
     * @param colFieldClicked The column index where the piece should move to.
     */
    private void moveKingToNewPosition(Circle piece, int rowFieldClicked, int colFieldClicked) {
        GridPane.setRowIndex(piece, rowFieldClicked);
        GridPane.setColumnIndex(piece, colFieldClicked);
        resetSelectedPieceSize();
        selectedPiece = null;
    }

    /**
     * @param piece The king piece to check for additional captures.
     * @return {@code true} if additional captures are possible, {@code false} otherwise.
     */
    private boolean checkAdditionalKingCaptureMoves(Circle piece) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            if (canCaptureInDirection(piece, dir[0], dir[1])) {
                return true;
            }
        }
        return false;
    }
    /**
     * @param piece         The king piece that is checking for a capture.
     * @param rowDirection  The row direction to check for a capture (-1 for up, 1 for down).
     * @param colDirection  The column direction to check for a capture (-1 for left, 1 for right).
     * @return {@code true} if a capture is possible in the given direction, {@code false} otherwise.
     */
    private boolean canCaptureInDirection(Circle piece, int rowDirection, int colDirection) {
        int checkRow = GridPane.getRowIndex(piece) + rowDirection;
        int checkCol = GridPane.getColumnIndex(piece) + colDirection;

        while (isValidMove(checkRow, checkCol) && !isPieceOnField(checkRow, checkCol)) {
            checkRow += rowDirection;
            checkCol += colDirection;
        }
        if (isValidMove(checkRow, checkCol) && isOpponentPiece(checkRow, checkCol)) {
            int nextRow = checkRow + rowDirection;
            int nextCol = checkCol + colDirection;
            return isValidMove(nextRow, nextCol) && !isPieceOnField(nextRow, nextCol);
        }
        return false;
    }
}

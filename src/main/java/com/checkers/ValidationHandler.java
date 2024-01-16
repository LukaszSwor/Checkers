package com.checkers;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ValidationHandler {
    private final BoardStateProvider boardStateProvider;
    private static final Color PIECE_COLOR_WHITE = Color.WHITE;
    private static final Color PIECE_COLOR_BLACK = Color.BLACK;

    public ValidationHandler(BoardStateProvider boardStateProvider) {
        this.boardStateProvider = boardStateProvider;
    }

    boolean isValidMove(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    boolean isCaptureMove(Circle selectedPiece, int rowFieldClicked, int colFieldClicked) {
        int currentRow = GridPane.getRowIndex(selectedPiece);
        int currentCol = GridPane.getColumnIndex(selectedPiece);

        int rowDiff = Math.abs(rowFieldClicked - currentRow);
        int colDiff = Math.abs(colFieldClicked - currentCol);

        return rowDiff == 2 && colDiff == 2;
    }

    boolean isPieceValidMove(Circle selectedPiece,int rowFieldClicked, Color selectedPieceColor, boolean isInCaptureSequence, boolean isWhitePlayerMove ) {
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

    boolean isPieceOnField(int row, int col) {

        Circle piece = boardStateProvider.findPiece(row, col);
        if (piece != null) {
            Color pieceColor = (Color) piece.getFill();
            return pieceColor == Color.WHITE || pieceColor == Color.BLACK;
        }
        return false;
    }

    boolean isMoveDiagonalAndValid(Circle piece, int rowFieldClicked, int colFieldClicked) {
        int colClicked = GridPane.getColumnIndex(piece);
        int rowClicked = GridPane.getRowIndex(piece);
        int colDiff = Math.abs(colClicked - colFieldClicked);
        int rowDiff = Math.abs(rowClicked - rowFieldClicked);

        return colDiff == rowDiff && colDiff > 0 && !isPieceOnField(rowFieldClicked, colFieldClicked);
    }

    boolean canCaptureInDirection(Circle piece, int rowDirection, int colDirection) {
        int checkRow = GridPane.getRowIndex(piece) + rowDirection;
        int checkCol = GridPane.getColumnIndex(piece) + colDirection;

        while (isValidMove(checkRow, checkCol) && !isPieceOnField(checkRow, checkCol)) {
            checkRow += rowDirection;
            checkCol += colDirection;
        }
        if (isValidMove(checkRow, checkCol) && isOpponentPiece(piece, checkRow, checkCol)) {
            int nextRow = checkRow + rowDirection;
            int nextCol = checkCol + colDirection;
            return isValidMove(nextRow, nextCol) && !isPieceOnField(nextRow, nextCol);
        }
        return false;
    }
    boolean isOpponentPiece(Circle selectedPiece, int row, int col) {
        Circle piece = boardStateProvider.findPiece(row, col);
        return piece != null && piece.getFill() != selectedPiece.getFill();
    }
    boolean checkAdditionalKingCaptureMoves(Circle piece) {
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            if (canCaptureInDirection(piece, dir[0], dir[1])) {
                return true;
            }
        }
        return false;
    }
    boolean checkForKing(Circle piece, int row) {
        Color pieceColor = (Color) piece.getFill();
        return (pieceColor == PIECE_COLOR_WHITE && row == 0) || (pieceColor == PIECE_COLOR_BLACK && row == 7);
    }
    public boolean isPlayerTurnValid(Circle piece, boolean isWhitePlayerMove) {
        return (isWhitePlayerMove && piece.getFill() == PIECE_COLOR_WHITE) ||
                (!isWhitePlayerMove && piece.getFill() == PIECE_COLOR_BLACK);
    }

    boolean checkDirectionForCapture(Circle selectedPiece, int row, int col, int rowDelta, int colDelta) {
        int newRow = row + rowDelta;
        int newCol = col + colDelta;

        if (isValidMove(newRow, newCol) && isOpponentPiece(selectedPiece, newRow, newCol)) {
            int behindOpponentRow = newRow + rowDelta;
            int behindOpponentCol = newCol + colDelta;

            return isValidMove(behindOpponentRow, behindOpponentCol) && !isPieceOnField(behindOpponentRow, behindOpponentCol);
        }
        return false;
    }

    boolean checkForAdditionalCaptures(Circle selectedPiece, int row, int col) {
        int[] adjacentRows = {-1, -1, 1, 1};
        int[] adjacentCols = {-1, 1, -1, 1};

        for (int i = 0; i < 4; i++) {
            if (checkDirectionForCapture(selectedPiece, row, col, adjacentRows[i], adjacentCols[i])) {
                return true;
            }
        }
        return false;
    }
}
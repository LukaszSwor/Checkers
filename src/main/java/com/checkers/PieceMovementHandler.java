package com.checkers;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Scale;

import java.util.Map;



public class PieceMovementHandler implements BoardStateProvider {
    private final GridPane boardGridPane;
    private final Map<Circle, Boolean> isKingMap;
    private final GameStatusUpdater gameStatusUpdater;
    private Circle selectedPiece;
    private boolean isWhitePlayerMove = true;
    private boolean isInCaptureSequence = false;
    public Label whiteCountLabel;
    public Label blackCountLabel;
    private final ValidationHandler validationHandler;

    enum MoveResult {
        SINGLE_MOVE, CAPTURE_MOVE, INVALID_MOVE
    }


    public PieceMovementHandler(GridPane boardGridPane, Map<Circle, Boolean> isKingMap, GameStatusUpdater gameStatusUpdater) {
        this.boardGridPane = boardGridPane;
        this.isKingMap = isKingMap;
        this.validationHandler = new ValidationHandler(this);
        this.gameStatusUpdater = gameStatusUpdater;

    }

    void executeMoveForSelectedPiece(int rowFieldClicked, int colFieldClicked) {
        if (selectedPiece == null) {
            System.out.println("No piece selected.");
            return;
        }

        decideMoveType(selectedPiece, rowFieldClicked, colFieldClicked);
    }

    private void decideMoveType(Circle piece, int rowFieldClicked, int colFieldClicked) {
        boolean isKing = isKingMap.getOrDefault(piece, false);
        if (isKing) {
            handleKingMovement(piece, rowFieldClicked, colFieldClicked);
        } else {
            handlePieceMovement(rowFieldClicked, colFieldClicked);
        }
    }

    private void handlePieceMovement(int rowFieldClicked, int colFieldClicked) {
        if (isMoveAttemptValid(rowFieldClicked, colFieldClicked)) {
            executePieceMove(rowFieldClicked, colFieldClicked);
        }
    }

    private boolean isMoveAttemptValid(int rowFieldClicked, int colFieldClicked) {
        return continueCaptureSequence(rowFieldClicked, colFieldClicked) &&
                isValidDestination(rowFieldClicked);
    }

    private boolean isValidDestination(int rowFieldClicked) {
        return validationHandler.isPieceValidMove(
                selectedPiece, rowFieldClicked, (Color) selectedPiece.getFill(),
                isInCaptureSequence, isWhitePlayerMove
        );
    }
    private boolean continueCaptureSequence(int rowFieldClicked, int colFieldClicked) {
        if (isInCaptureSequence && !validationHandler.isCaptureMove(selectedPiece, rowFieldClicked, colFieldClicked)) {
            System.out.println("Player must continue capturing");
            return false;
        }
        return true;
    }
    private void executePieceMove(int rowFieldClicked, int colFieldClicked) {
        int currentRow = GridPane.getRowIndex(selectedPiece);
        int currentCol = GridPane.getColumnIndex(selectedPiece);

        processMove(rowFieldClicked, colFieldClicked, currentRow, currentCol);
    }

    private void processMove(int rowFieldClicked, int colFieldClicked, int currentRow, int currentCol) {
        if (!isFieldOccupied(rowFieldClicked, colFieldClicked)) {
            executeMove(rowFieldClicked, colFieldClicked, currentRow, currentCol);
        }
    }

    private void executeMove(int rowFieldClicked, int colFieldClicked, int currentRow, int currentCol) {
        MoveResult moveResult = determineMoveType(rowFieldClicked, colFieldClicked, currentRow, currentCol);
        performMove(moveResult, rowFieldClicked, colFieldClicked);
    }

    private MoveResult determineMoveType(int rowFieldClicked, int colFieldClicked, int currentRow, int currentCol) {
        int rowDiff = Math.abs(rowFieldClicked - currentRow);
        int colDiff = Math.abs(colFieldClicked - currentCol);

        if (rowDiff == 1 && colDiff == 1) {
            return MoveResult.SINGLE_MOVE;
        } else if (validationHandler.isCaptureMove(selectedPiece, rowFieldClicked, colFieldClicked)) {
            return MoveResult.CAPTURE_MOVE;
        } else {
            return MoveResult.INVALID_MOVE;
        }
    }

    private void performMove(MoveResult moveResult, int rowFieldClicked, int colFieldClicked) {
        switch (moveResult) {
            case SINGLE_MOVE:
                System.out.println("Single move");
                makeSingleMove(rowFieldClicked, colFieldClicked);
                break;
            case CAPTURE_MOVE:
                System.out.println("Attempting capture");
                makeCaptureMove(rowFieldClicked, colFieldClicked);
                break;
            case INVALID_MOVE:
                System.out.println("Invalid move");
                break;
        }
    }

    private boolean isFieldOccupied(int rowFieldClicked, int colFieldClicked) {
        if (validationHandler.isPieceOnField(rowFieldClicked, colFieldClicked)) {
            System.out.println("Field is occupied, move not allowed");
            return true;
        }
        return false;
    }

    private void makeSingleMove(int rowFieldClicked, int colFieldClicked) {
        GridPane.setRowIndex(selectedPiece, rowFieldClicked);
        GridPane.setColumnIndex(selectedPiece, colFieldClicked);

        boolean isPieceKing = validationHandler.checkForKing(selectedPiece, rowFieldClicked);
        if (isPieceKing) {
            markPieceAsKing(selectedPiece);
        }
        resetSelectedPieceSize();
        selectedPiece = null;
        switchPlayerTurn();
    }

    private void makeCaptureMove(int newRow, int newCol) {
        removeCapturedPiece(newRow, newCol);
        movePieceToNewPosition(newRow, newCol);
        postCaptureUpdates(newRow);
    }

    private void postCaptureUpdates(int newRow) {
        updateStatusAfterMove(newRow);
        checkForAdditionalCaptures(newRow);
    }

    private void updateStatusAfterMove(int newRow) {
        boolean isPieceKing = validationHandler.checkForKing(selectedPiece, newRow);
        if (isPieceKing) {
            markPieceAsKing(selectedPiece);
        }
        isInCaptureSequence = true;
    }

    private void checkForAdditionalCaptures(int newRow) {
        checkAdjacentOpponentPieces(newRow, GridPane.getColumnIndex(selectedPiece));
    }

    private void removeCapturedPiece(int newRow, int newCol) {
        int middleRow = (newRow + GridPane.getRowIndex(selectedPiece)) / 2;
        int middleCol = (newCol + GridPane.getColumnIndex(selectedPiece)) / 2;
        Circle middlePiece = findPiece(middleRow, middleCol);
        if (middlePiece != null && middlePiece.getFill() != selectedPiece.getFill()) {
            boardGridPane.getChildren().remove(middlePiece);
            gameStatusUpdater.updatePieceCount(middlePiece);
        }
    }

    private void movePieceToNewPosition(int newRow, int newCol) {
        GridPane.setRowIndex(selectedPiece, newRow);
        GridPane.setColumnIndex(selectedPiece, newCol);
    }

    private void checkAdjacentOpponentPieces(int row, int col) {
        boolean canCaptureMore = validationHandler.checkForAdditionalCaptures(selectedPiece, row, col);
        if (!canCaptureMore) {
            endCaptureSequence();
        } else {
            isInCaptureSequence = true;
        }
    }

    private void endCaptureSequence() {
        isInCaptureSequence = false;
        switchPlayerTurn();
    }

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

    @Override
    public Circle findPiece(int row, int col) {
        for (Node node : boardGridPane.getChildren()) {
            if (node instanceof Circle && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Circle) node;
            }
        }
        return null;
    }

    private void switchPlayerTurn() {
        isWhitePlayerMove = !isWhitePlayerMove;
    }

    private void markPieceAsKing(Circle piece) {
        isKingMap.put(piece, true);
        piece.setStroke(Color.GOLD);
        piece.setStrokeWidth(3.0);
    }

    void deselectPiece() {
        resetSelectedPieceSize();
        selectedPiece = null;
    }

    void selectPiece(Circle piece) {
        scalePiece(piece);
        selectedPiece = piece;
    }
        private void handleKingMovement(Circle piece, int rowFieldClicked, int colFieldClicked) {
        if (!canKingMove(piece, rowFieldClicked, colFieldClicked)) {
            return;
        }
        performKingMove(piece, rowFieldClicked, colFieldClicked);
    }

    private void performKingMove(Circle piece, int rowFieldClicked, int colFieldClicked) {
        if (isInCaptureSequence) {
            System.out.println("Invalid move. You must continue capturing!");
            return;
        }

        if (!attemptKingCapture(piece, rowFieldClicked, colFieldClicked)) {
            moveKingToNewPosition(piece, rowFieldClicked, colFieldClicked);
            switchPlayerTurn();
        }
    }

    private boolean attemptKingCapture(Circle piece, int rowFieldClicked, int colFieldClicked) {
        return handleKingCaptureMove(piece, rowFieldClicked, colFieldClicked);
    }

    private boolean canKingMove(Circle piece, int rowFieldClicked, int colFieldClicked) {
        if (!validationHandler.isPlayerTurnValid(piece, isWhitePlayerMove)) {
            System.out.println("Not your turn");
            return false;
        }
        if (!validationHandler.isMoveDiagonalAndValid(piece, rowFieldClicked, colFieldClicked)) {
            System.out.println("Invalid move. King can only move diagonally to an empty space or capture.");
            return false;
        }
        return true;
    }

    private boolean handleKingCaptureMove(Circle piece, int rowFieldClicked, int colFieldClicked) {
        if (handleKingCapture(piece, rowFieldClicked, colFieldClicked)) {
            processKingCapture(piece, rowFieldClicked, colFieldClicked);
            return true;
        }
        return false;
    }

    private boolean handleKingCapture(Circle piece, int rowFieldClicked, int colFieldClicked) {
        int colClicked = GridPane.getColumnIndex(piece);
        int rowClicked = GridPane.getRowIndex(piece);

        int colDiff = Math.abs(colClicked - colFieldClicked);
        int rowDiff = Math.abs(rowClicked - rowFieldClicked);
        int rowStep = (rowFieldClicked - rowClicked) / rowDiff;
        int colStep = (colFieldClicked - colClicked) / colDiff;

        return checkPathAndCapture(piece, rowClicked, colClicked, rowStep, colStep, colDiff);
    }

    private boolean checkPathAndCapture(Circle piece, int rowClicked, int colClicked, int rowStep, int colStep, int diff) {
        for (int step = 1; step < diff; step++) {
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

    private void processKingCapture(Circle piece, int rowFieldClicked, int colFieldClicked) {
        moveAndSelectKing(piece, rowFieldClicked, colFieldClicked);
        finalizeKingMove(piece);
    }

    private void moveAndSelectKing(Circle piece, int rowFieldClicked, int colFieldClicked) {
        moveKingToNewPosition(piece, rowFieldClicked, colFieldClicked);
        selectedPiece = piece;
    }

    private void finalizeKingMove(Circle piece) {
        if (validationHandler.checkAdditionalKingCaptureMoves(piece)) {
            isInCaptureSequence = true;
        } else {
            isInCaptureSequence = false;
            resetSelectedPieceSize();
            selectedPiece = null;
            switchPlayerTurn();
        }
    }

    private void moveKingToNewPosition(Circle piece, int rowFieldClicked, int colFieldClicked) {
        GridPane.setRowIndex(piece, rowFieldClicked);
        GridPane.setColumnIndex(piece, colFieldClicked);
        resetSelectedPieceSize();
        selectedPiece = null;
    }
}





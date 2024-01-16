package com.checkers;

import javafx.scene.shape.Circle;

public interface BoardStateProvider {
    Circle findPiece(int row, int col);
}

    package com.checkers;

    import javafx.scene.paint.Color;
    import javafx.scene.shape.Circle;

    /**
     * This class represents a piece in a checkers game.
     * It holds a JavaFX Circle object that can be displayed on the game board.
     */
    public class Piece {
        private final Circle piece;

        /**
         * @param color The color of the piece.
         */

        public Piece(Color color){
            this.piece = new Circle(20);
            piece.setFill(color);
        }

        /**
         * @return The Circle object for this piece.
         */

        public Circle getPiece() {
            return piece;
        }
    }


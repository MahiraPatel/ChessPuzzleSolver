/**
 * Piece.java
 * Abstract base class for all chess pieces in a chess game.
 * 
 * This class defines the common properties and behaviors of chess pieces, 
 * including their color and position on the board. It also provides 
 * abstract methods that must be implemented by specific piece types 
 * (e.g., Pawn, Rook, Knight) to determine valid moves and attack capabilities.
 * 
 */

import java.util.List;

public abstract class Piece implements Cloneable {
    protected PieceColor color;
    protected Position position;

    // Constructor
    public Piece(PieceColor color, Position position) {
        this.color = color;
        this.position = position;
    }

    /**
     * Gets the color of the chess piece.
     *
     * @return the color of the piece (either white or black).
     */
    public PieceColor getColor() {
        return color;
    }

    /**
     * Gets the current position of the chess piece on the board.
     *
     * @return the current position of the piece.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets a new position for the chess piece on the board.
     *
     * @param newPosition the new position to set for the piece.
     */
    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    /**
     * Abstract method to get a list of valid moves for the piece.
     *
     * @param board the current state of the chess board.
     * @return a list of valid positions the piece can move to.
     */
    public abstract List<Position> getValidMoves(Board board);

    /**
     * Abstract method to check if a move to a target position is valid for the piece.
     *
     * @param board the current state of the chess board.
     * @param targetPosition the position to check for validity.
     * @return true if the move is valid, false otherwise.
     */
    public abstract boolean isValidMove(Board board, Position targetPosition);

    /**
     * Abstract method to check if the piece can attack a specific position on the board.
     *
     * @param board the current state of the chess board.
     * @param targetPosition the position to check for attack capability.
     * @return true if the piece can attack the position, false otherwise.
     */
    public abstract boolean canAttackPosition(Board board, Position targetPosition);

    /**
     * Creates a clone of the current piece.
     *
     * @return a clone of the current piece.
     * @throws AssertionError if cloning fails (should not happen since this class implements Cloneable).
     */
    @Override
    public Piece clone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we are Cloneable
            throw new AssertionError();
        }
    }
}

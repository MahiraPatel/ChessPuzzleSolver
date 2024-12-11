/**
 * Pawn.java
 * 
 * Represents a Pawn piece in a chess game.
 * The Pawn can move forward one square, or two squares from its starting position,
 * and can attack diagonally. It can also be promoted when it reaches the opposite end of the board.
 */
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    
    /**
     * Constructs a Pawn with the specified color and position.
     *
     * @param color   The color of the Pawn (WHITE or BLACK).
     * @param position The initial position of the Pawn on the board.
     */
    public Pawn(PieceColor color, Position position) {
        super(color, position);
    }

    /**
     * Returns a list of valid moves for the Pawn based on its current position and the state of the board.
     *
     * @param board The current state of the chess board.
     * @return A list of valid positions the Pawn can move to.
     */
    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int direction = (color == PieceColor.WHITE) ? -1 : 1; // White moves up, Black moves down

        // Pawn moves one square forward
        Position oneSquareForward = new Position(position.getRow() + direction, position.getColumn());
        if (isValidMove(board, oneSquareForward)) {
            validMoves.add(oneSquareForward);
        }

        // Pawn moves two squares forward from starting position
        if ((color == PieceColor.WHITE && position.getRow() == 1) ||
                (color == PieceColor.BLACK && position.getRow() == 6)) {
            Position twoSquaresForward = new Position(position.getRow() + (2 * direction), position.getColumn());
            if (isValidMove(board, twoSquaresForward)) {
                validMoves.add(twoSquaresForward);
            }
        }

        // Pawn attacks diagonally
        int[] attackCols = {-1, 1}; // Attack left and right diagonally
        for (int colDiff : attackCols) {
            Position attackPosition = new Position(position.getRow() + direction, position.getColumn() + colDiff);
            if (isValidMove(board, attackPosition)) {
                validMoves.add(attackPosition);
            }
        }

        return validMoves;
    }

    /**
     * Checks if the specified target position is a valid move for the Pawn.
     *
     * @param board The current state of the chess board.
     * @param targetPosition The position to check for validity.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(Board board, Position targetPosition) {
        if (targetPosition.getRow() < 0 || targetPosition.getRow() > 7 ||
                targetPosition.getColumn() < 0 || targetPosition.getColumn() > 7) {
            return false; // Out of bounds
        }

        int rowDiff = Math.abs(targetPosition.getRow() - position.getRow());
        int colDiff = Math.abs(targetPosition.getColumn() - position.getColumn());

        // Pawn moves forward one square, or two squares if on starting position
        if (colDiff == 0 && rowDiff == 1) {
            return true;
        } else if (colDiff == 0 && rowDiff == 2 && (position.getRow() == 1 || position.getRow() == 6)) {
            return true;
        }

        // Pawn attacks diagonally
        if (colDiff == 1 && rowDiff == 1) {
            Piece targetPiece = board.getPieceAt(targetPosition);
            return targetPiece != null && targetPiece.getColor() != this.color;
        }

        return false;
    }

    /**
     * Determines if the Pawn can be promoted based on its current position.
     *
     * @return true if the Pawn can be promoted, false otherwise.
     */
    public boolean canPromote() {
        return (color == PieceColor.WHITE && position.getRow() == 0) || (color == PieceColor.BLACK && position.getRow() == 7);
    }

    /**
     * Checks if the Pawn can attack the specified position.
     *
     * @param board The current state of the chess board.
     * @param targetPosition The position to check for attack validity.
     * @return true if the Pawn can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position targetPosition) {
        return isValidMove(board, targetPosition);
    }
}

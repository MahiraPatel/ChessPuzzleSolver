/**
 * Bishop.java
 * 
 * Represents a Bishop chess piece.
 * The Bishop can move diagonally any number of squares, as long as it is not blocked by other pieces.
 */

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    /**
     * Constructs a Bishop with the specified color and position.
     *
     * @param color   The color of the Bishop (either white or black).
     * @param position The initial position of the Bishop on the board.
     */
    public Bishop(PieceColor color, Position position) {
        super(color, position);
    }

    /**
     * Returns a list of valid moves for the Bishop on the given board.
     * The Bishop can move diagonally in all four directions until it is blocked by another piece or reaches the edge of the board.
     *
     * @param board The current state of the chess board.
     * @return A list of valid positions the Bishop can move to.
     */
    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonal directions
        };

        // Loop through each diagonal direction
        for (int[] dir : directions) {
            for (int distance = 1; distance <= 7; distance++) {
                int newRow = position.getRow() + (dir[0] * distance);
                int newCol = position.getColumn() + (dir[1] * distance);
                Position targetPosition = new Position(newRow, newCol);

                if (!isValidMove(board, targetPosition)) {
                    break; // Stop if the move is invalid
                }

                validMoves.add(targetPosition);
            }
        }

        return validMoves;
    }

    /**
     * Checks if the specified target position is a valid move for the Bishop.
     * A move is valid if it is within the bounds of the board, moves diagonally, and the path is clear.
     *
     * @param board         The current state of the chess board.
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

        // Bishop moves diagonally
        if (rowDiff != colDiff) {
            return false;
        }

        // Check if the path is clear
        int rowStep = Integer.compare(targetPosition.getRow(), position.getRow());
        int colStep = Integer.compare(targetPosition.getColumn(), position.getColumn());

        int currentRow = position.getRow() + rowStep;
        int currentCol = position.getColumn() + colStep;

        while (currentRow != targetPosition.getRow() || currentCol != targetPosition.getColumn()) {
            Piece blockingPiece = board.getPieceAt(new Position(currentRow, currentCol));
            if (blockingPiece != null) {
                return false; // Path is blocked
            }
            currentRow += rowStep;
            currentCol += colStep;
        }

        Piece targetPiece = board.getPieceAt(targetPosition);
        return targetPiece == null || targetPiece.getColor() != this.color;
    }

    /**
     * Determines if the Bishop can attack the specified position.
     * The Bishop can attack a position if it can move to that position according to its movement rules.
     *
     * @param board         The current state of the chess board.
     * @param targetPosition The position to check for attack capability.
     * @return true if the Bishop can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position targetPosition) {
        return isValidMove(board, targetPosition);
    }
}

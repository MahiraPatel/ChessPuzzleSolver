/**
 * Knight.java
 * 
 * Represents a Knight piece in a chess game.
 * The Knight moves in an "L" shape: two squares in one direction and one square perpendicular.
 */

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    /**
     * Constructs a Knight with the specified color and position.
     *
     * @param color   the color of the Knight (either white or black)
     * @param position the initial position of the Knight on the board
     */
    public Knight(PieceColor color, Position position) {
        super(color, position);
    }

    /**
     * Returns a list of valid moves for the Knight based on its current position.
     *
     * @param board the current state of the chess board
     * @return a list of valid positions the Knight can move to
     */
    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int[][] knightMoves = {
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}, // 2 squares in row and 1 in column
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1}      // 1 square in row and 2 in column
        };

        for (int[] move : knightMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getColumn() + move[1];
            Position targetPosition = new Position(newRow, newCol);

            if (isValidMove(board, targetPosition)) {
                validMoves.add(targetPosition);
            }
        }

        return validMoves;
    }

    /**
     * Checks if the move to the specified target position is valid for the Knight.
     *
     * @param board the current state of the chess board
     * @param targetPosition the position to check for validity
     * @return true if the move is valid, false otherwise
     */
    @Override
    public boolean isValidMove(Board board, Position targetPosition) {
        if (targetPosition.getRow() < 0 || targetPosition.getRow() > 7 ||
                targetPosition.getColumn() < 0 || targetPosition.getColumn() > 7) {
            return false; // Out of bounds
        }

        // A Knight move must be exactly an "L" shape
        int rowDiff = Math.abs(targetPosition.getRow() - position.getRow());
        int colDiff = Math.abs(targetPosition.getColumn() - position.getColumn());

        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    /**
     * Determines if the Knight can attack the specified position.
     *
     * @param board the current state of the chess board
     * @param targetPosition the position to check for attack capability
     * @return true if the Knight can attack the position, false otherwise
     */
    @Override
    public boolean canAttackPosition(Board board, Position targetPosition) {
        return isValidMove(board, targetPosition);
    }
}

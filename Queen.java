/**
 * Queen.java
 * 
 * This class represents a Queen chess piece in a chess game. The Queen can move any number of squares 
 * vertically, horizontally, or diagonally. This class provides methods to determine valid moves and 
 * whether the Queen can attack a given position on the board.
 */
import java.util.ArrayList;
import java.util.List;

// Queen piece implementation
public class Queen extends Piece {

    /**
     * Constructs a Queen piece with the specified color and position.
     * 
     * @param color The color of the Queen (either white or black).
     * @param position The initial position of the Queen on the board.
     */
    public Queen(PieceColor color, Position position) {
        super(color, position);
    }

    /**
     * Returns a list of valid moves for the Queen on the given board.
     * The Queen can move any number of squares in any direction (vertically, horizontally, or diagonally).
     * 
     * @param board The current state of the chess board.
     * @return A list of valid positions the Queen can move to.
     */
    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            for (int distance = 1; distance <= 7; distance++) {
                int newRow = position.getRow() + (dir[0] * distance);
                int newCol = position.getColumn() + (dir[1] * distance);
                Position targetPosition = new Position(newRow, newCol);

                if (isValidMove(board, targetPosition)) {
                    validMoves.add(targetPosition);
                } else {
                    break; // Stop if path is blocked
                }
            }
        }

        return validMoves;
    }

    /**
     * Checks if the specified target position is a valid move for the Queen.
     * A move is valid if it is within the board bounds, follows the movement rules of the Queen,
     * and does not have any pieces blocking the path.
     * 
     * @param board The current state of the chess board.
     * @param targetPosition The position to check for validity.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(Board board, Position targetPosition) {
        // Check if target position is within board bounds
        if (targetPosition.getRow() < 0 || targetPosition.getRow() > 7 ||
                targetPosition.getColumn() < 0 || targetPosition.getColumn() > 7) {
            return false;
        }

        // Check if move is diagonal, horizontal, or vertical
        int rowDiff = Math.abs(targetPosition.getRow() - position.getRow());
        int colDiff = Math.abs(targetPosition.getColumn() - position.getColumn());

        if (rowDiff != colDiff && rowDiff != 0 && colDiff != 0) {
            return false;
        }

        // Check if path is clear
        int rowStep = Integer.compare(targetPosition.getRow(), position.getRow());
        int colStep = Integer.compare(targetPosition.getColumn(), position.getColumn());

        int currentRow = position.getRow() + rowStep;
        int currentCol = position.getColumn() + colStep;

        while (currentRow != targetPosition.getRow() || currentCol != targetPosition.getColumn()) {
            Piece blockingPiece = board.getPieceAt(new Position(currentRow, currentCol));
            if (blockingPiece != null) {
                return false;
            }

            currentRow += rowStep;
            currentCol += colStep;
        }

        // Check if target square is empty or has an opponent's piece
        Piece targetPiece = board.getPieceAt(targetPosition);
        return targetPiece == null || targetPiece.getColor() != this.color;
    }

    /**
     * Determines if the Queen can attack the specified position.
     * This is equivalent to checking if the move to that position is valid.
     * 
     * @param board The current state of the chess board.
     * @param targetPosition The position to check for attack capability.
     * @return true if the Queen can attack the position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position targetPosition) {
        return isValidMove(board, targetPosition);
    }
}
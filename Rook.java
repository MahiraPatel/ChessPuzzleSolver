
/** Rook.java 
 * 
 * Represents a Rook chess piece.
 * The Rook can move any number of squares along a row or column.
 */
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    
    /**
     * Constructs a Rook piece with the specified color and position.
     *
     * @param color the color of the Rook (either white or black)
     * @param position the initial position of the Rook on the board
     */
    public Rook(PieceColor color, Position position) {
        super(color, position);
    }

    /**
     * Returns a list of valid moves for the Rook on the given board.
     * The Rook can move horizontally or vertically any number of squares,
     * stopping if it encounters another piece.
     *
     * @param board the current state of the chess board
     * @return a list of valid positions the Rook can move to
     */
    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

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
     * Checks if the specified target position is a valid move for the Rook.
     * A move is valid if it is within the board bounds, is either horizontal
     * or vertical, and the path to the target position is clear.
     * Additionally, the target position must either be empty or occupied by
     * an opponent's piece.
     *
     * @param board the current state of the chess board
     * @param targetPosition the position to check for validity
     * @return true if the move to the target position is valid, false otherwise
     */
    @Override
    public boolean isValidMove(Board board, Position targetPosition) {
        // Check if target position is within board bounds
        if (targetPosition.getRow() < 0 || targetPosition.getRow() > 7 ||
                targetPosition.getColumn() < 0 || targetPosition.getColumn() > 7) {
            return false;
        }

        // Check if move is horizontal or vertical
        int rowDiff = Math.abs(targetPosition.getRow() - position.getRow());
        int colDiff = Math.abs(targetPosition.getColumn() - position.getColumn());

        if (rowDiff != 0 && colDiff != 0) {
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
     * Determines if the Rook can attack the specified target position.
     * This is equivalent to checking if the move to that position is valid.
     *
     * @param board the current state of the chess board
     * @param targetPosition the position to check for attack capability
     * @return true if the Rook can attack the target position, false otherwise
     */
    @Override
    public boolean canAttackPosition(Board board, Position targetPosition) {
        return isValidMove(board, targetPosition);
    }
}
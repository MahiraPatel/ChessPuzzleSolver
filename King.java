/**
 * King.java
 * 
 * Represents a King piece in a chess game.
 * The King can move one square in any direction and cannot move into check.
 */

import java.util.ArrayList;
import java.util.List;

// King piece implementation
public class King extends Piece {
    
    /**
     * Constructs a King piece with the specified color and position.
     *
     * @param color   The color of the King (either WHITE or BLACK).
     * @param position The initial position of the King on the board.
     */
    public King(PieceColor color, Position position) {
        super(color, position);
    }

    /**
     * Returns a list of valid moves for the King based on the current board state.
     * If the King is in check, only moves that get the King out of check are allowed.
     *
     * @param board The current state of the chess board.
     * @return A list of valid positions the King can move to.
     */
    @Override
    public List<Position> getValidMoves(Board board) {
        List<Position> validMoves = new ArrayList<>();
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},  // Upper-left, Up, Upper-right
                {0, -1}, {0, 1},              // Left, Right
                {1, -1}, {1, 0}, {1, 1}       // Lower-left, Down, Lower-right
        };

        // Check if the King is in check
        boolean inCheck = isKingInCheck(board);

        if (inCheck) {
            // Handle check scenario: Only moves that get the King out of check are allowed
            for (int[] dir : directions) {
                int newRow = position.getRow() + dir[0];
                int newCol = position.getColumn() + dir[1];

                // Skip if the new position is out of bounds
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                    continue;
                }

                Position targetPosition = new Position(newRow, newCol);

                // Skip if the target position is occupied by a friendly piece
                Piece targetPiece = board.getPieceAt(targetPosition);
                if (targetPiece != null && targetPiece.getColor() == this.color) {
                    continue;
                }

                // Ensure the King does not move into check
                if (isMoveIntoCheck(board, targetPosition)) {
                    continue; // Skip if the move puts the King in check
                }

                // Add the move if it's valid and gets out of check
                validMoves.add(targetPosition);
            }
        } else {
            // If the King is not in check, allow all normal valid moves
            for (int[] dir : directions) {
                int newRow = position.getRow() + dir[0];
                int newCol = position.getColumn() + dir[1];

                // Skip if the new position is out of bounds
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) {
                    continue;
                }

                Position targetPosition = new Position(newRow, newCol);

                // Skip if the target position is occupied by a friendly piece
                Piece targetPiece = board.getPieceAt(targetPosition);
                if (targetPiece != null && targetPiece.getColor() == this.color) {
                    continue;
                }

                // Ensure the King does not move into check
                if (isMoveIntoCheck(board, targetPosition)) {
                    continue; // Skip if the move puts the King in check
                }

                validMoves.add(targetPosition);
            }
        }

        return validMoves;
    }

    /**
     * Checks if the specified target position is a valid move for the King.
     * The move is valid if it is within bounds, does not move into check,
     * and does not place the King adjacent to the opponent's King.
     *
     * @param board         The current state of the chess board.
     * @param targetPosition The position to check for validity.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(Board board, Position targetPosition) {
        // Ensure the target position is within board bounds
        if (targetPosition.getRow() < 0 || targetPosition.getRow() > 7 ||
                targetPosition.getColumn() < 0 || targetPosition.getColumn() > 7) {
            return false;
        }

        // The King can move only one square in any direction
        int rowDiff = Math.abs(targetPosition.getRow() - position.getRow());
        int colDiff = Math.abs(targetPosition.getColumn() - position.getColumn());
        if (rowDiff > 1 || colDiff > 1) {
            return false;
        }

        // Check if the target square is empty or contains an opponent's piece
        Piece targetPiece = board.getPieceAt(targetPosition);
        if (targetPiece != null && targetPiece.getColor() == this.color) {
            return false; // Cannot move to a square occupied by your own piece
        }

        // Ensure the King does not move adjacent to the opponent's King (Kings cannot be adjacent)
        if (isKingAdjacent(board, targetPosition)) {
            return false;
        }

        // Special handling for check scenarios
        if (isKingInCheck(board)) {
            // If the king is in check, the move MUST get the king out of check
            if (!willGetKingOutOfCheck(board, targetPosition)) {
                return false;
            }
        } else {
            // Prevent moving into check when not already in check
            if (isMoveIntoCheck(board, targetPosition)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the King is currently in check.
     * A King is in check if any opponent's piece can attack its current position.
     *
     * @param board The current state of the chess board.
     * @return true if the King is in check, false otherwise.
     */
    public boolean isKingInCheck(Board board) {
        // Check if any opponent's piece can attack the King's current position
        for (Piece piece : board.getActivePieces()) {
            if (piece.getColor() != this.color && piece.canAttackPosition(board, position)) {
                return true; // The King is in check
            }
        }
        return false; // The King is safe
    }

    /**
     * Determines if moving the King to the specified target position
     * will get it out of check.
     *
     * @param board         The current state of the chess board.
     * @param targetPosition The position to check.
     * @return true if the move will get the King out of check, false otherwise.
     */
    private boolean willGetKingOutOfCheck(Board board, Position targetPosition) {
        // Temporarily move the King to the target position
        Position originalPosition = this.position;
        this.position = targetPosition;

        // Check if the King is still in check after the move
        boolean inCheck = isInCheck(board, this.color);

        // Revert the King's position
        this.position = originalPosition;

        return !inCheck;
    }

    /**
     * Checks if the King is adjacent to the opponent's King.
     * Kings cannot be next to each other.
     *
     * @param board         The current state of the chess board.
     * @param targetPosition The position to check for adjacency.
     * @return true if the King is adjacent to the opponent's King, false otherwise.
     */
    private boolean isKingAdjacent(Board board, Position targetPosition) {
        for (Piece piece : board.getActivePieces()) {
            if (piece instanceof King && piece.getColor() != this.color) {
                Position enemyKingPos = piece.getPosition();
                // Check if the target position is adjacent to the opponent's King
                int rowDiff = Math.abs(targetPosition.getRow() - enemyKingPos.getRow());
                int colDiff = Math.abs(targetPosition.getColumn() - enemyKingPos.getColumn());
                if (rowDiff <= 1 && colDiff <= 1) {
                    return true; // The King is too close to the opponent's King
                }
            }
        }
        return false;
    }

    /**
     * Checks if moving to the target position would put the King in check.
     *
     * @param board         The current state of the chess board.
     * @param targetPosition The position to check.
     * @return true if the move puts the King in check, false otherwise.
     */
    private boolean isMoveIntoCheck(Board board, Position targetPosition) {
        // Temporarily move the King to the target position
        Position originalPosition = this.position;
        this.position = targetPosition;

        // Check if the King is in check after the move
        boolean inCheck = isKingInCheck(board);

        // Restore the King's position
        this.position = originalPosition;

        return inCheck;
    }


    /**
     * Checks if the King is currently in check based on the specified color.
     *
     * @param board The current state of the chess board.
     * @param color The color of the King to check.
     * @return true if the King of the specified color is in check, false otherwise.
     */
    public boolean isInCheck(Board board, PieceColor color) {
        King king = board.findKing(color);  // Find the King of the given color
        if (king == null) return false;  // If no King, it's not in check

        // Get all opponent pieces
        List<Piece> opponentPieces = board.getPiecesByColor(color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE);

        // Check if any opponent piece can attack the King's position
        for (Piece opponentPiece : opponentPieces) {
            if (opponentPiece.canAttackPosition(board, king.getPosition())) {
                return true;  // King is in check if any opponent can attack it
            }
        }

        return false;  // No opponent can attack the King, so it's not in check
    }

    /**
     * Checks if the King can attack a specific position.
     * The King can attack one square in any direction.
     *
     * @param board         The current state of the chess board.
     * @param targetPosition The position to check for attack capability.
     * @return true if the King can attack the specified position, false otherwise.
     */
    @Override
    public boolean canAttackPosition(Board board, Position targetPosition) {
        // The King can attack one square in any direction
        int rowDiff = Math.abs(targetPosition.getRow() - position.getRow());
        int colDiff = Math.abs(targetPosition.getColumn() - position.getColumn());

        return rowDiff <= 1 && colDiff <= 1;
    }
}

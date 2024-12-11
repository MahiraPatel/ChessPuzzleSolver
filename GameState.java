/**
 * GameState.java
 * 
 * Represents the state of a chess game, including the current board,
 * the player's turn, the selected piece, highlighted moves, and the game status.
 */

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Board board;
    private PieceColor currentTurn;
    private Piece selectedPiece;
    private List<Position> highlightedMoves;
    private GameStatus gameStatus;

    /**
     * Enum to represent the current status of the game.
     */
    public enum GameStatus {
        IN_PROGRESS,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    /**
     * Constructs a GameState with the specified board.
     *
     * @param board The board representing the current state of the game.
     */
    public GameState(Board board) {
        this.board = board;
        this.currentTurn = PieceColor.WHITE;
        this.highlightedMoves = new ArrayList<>();
        this.gameStatus = GameStatus.IN_PROGRESS;
    }

    /**
     * Selects a piece for the current player.
     *
     * @param piece The piece to be selected.
     * @return true if the piece was successfully selected; false otherwise.
     */
    public boolean selectPiece(Piece piece) {
        // Ensure the piece belongs to the current player
        if (piece.getColor() != currentTurn) {
            return false;
        }

        this.selectedPiece = piece;
        // Calculate and store valid moves
        this.highlightedMoves = piece.getValidMoves(board);
        return true;
    }

    /**
     * Attempts to move the selected piece to the specified target position.
     *
     * @param targetPosition The position to move the selected piece to.
     * @return true if the move was successful; false otherwise.
     */
    public boolean movePiece(Position targetPosition) {
        if (selectedPiece == null) {
            return false;
        }

        // Check if target position is in highlighted moves
        if (!highlightedMoves.contains(targetPosition)) {
            return false;
        }

        // Attempt to move the piece
        boolean moveSuccessful = board.movePiece(selectedPiece, targetPosition);

        if (moveSuccessful) {
            // Switch turns
            currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

            // Clear selection and highlighted moves
            clearSelection();

            // Update game status
            updateGameStatus();
        }

        return moveSuccessful;
    }

    /**
     * Clears the selection of the current piece and highlighted moves.
     */
    public void clearSelection() {
        this.selectedPiece = null;
        this.highlightedMoves.clear();
    }

    /**
     * Checks if the king of the specified color is in check.
     *
     * @param color The color of the king to check.
     * @return true if the king is in check; false otherwise.
     */
    public boolean isKingInCheck(PieceColor color) {
        King king = board.findKing(color);
        if (king == null) {
            // Ideally, the game should not proceed without both kings
            return false;
        }

        // Check if any opponent piece can attack the king
        List<Piece> opponentPieces = board.getPiecesByColor(
                color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE
        );

        for (Piece piece : opponentPieces) {
            if (piece.canAttackPosition(board, king.getPosition())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the specified player is in checkmate.
     *
     * @param color The color of the player to check for checkmate.
     * @return true if the player is in checkmate; false otherwise.
     */
    public boolean isCheckmate(PieceColor color) {
        // A player is in checkmate if:
        // 1. Their king is in check
        // 2. They have no valid moves to escape check
        if (!isKingInCheck(color)) {
            return false;
        }
        return hasNoValidMovesToEscapeCheck();
    }

    /**
     * Updates the game status based on the current state of the game.
     */
    private void updateGameStatus() {
        King currentKing = board.findKing(currentTurn);

        // Check if king is in check
        if (isKingInCheck(currentTurn)) {
            // Check for checkmate
            if (hasNoValidMovesToEscapeCheck()) {
                gameStatus = GameStatus.CHECKMATE;
            } else {
                gameStatus = GameStatus.CHECK;
            }
        } else if (hasNoValidMoves()) {
            // Stalemate if no valid moves and not in check
            gameStatus = GameStatus.STALEMATE;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }
    }

    /**
     * Checks if there are any valid moves to escape check for the current player.
     *
     * @return true if there are no valid moves to escape check; false otherwise.
     */
    private boolean hasNoValidMovesToEscapeCheck() {
        List<Piece> currentPlayerPieces = board.getPiecesByColor(currentTurn);

        for (Piece piece : currentPlayerPieces) {
            List<Position> validMoves = piece.getValidMoves(board);
            for (Position move : validMoves) {
                // Simulate the move and check if king is still in check
                Board simulatedBoard = board.clone(); // Assuming a clone method exists
                Piece simulatedPiece = simulatedBoard.getPieceAt(piece.getPosition());
                simulatedBoard.movePiece(simulatedPiece, move);

                if (!isKingInCheckAfterMove(simulatedBoard, currentTurn)) {
                    return false; // Found a move that escapes check
                }
            }
        }

        return true; // No moves can escape check
    }

    /**
     * Checks if the king of the specified color is in check after a simulated move.
     *
     * @param simulatedBoard The board state after the simulated move.
     * @param color The color of the king to check.
     * @return true if the king is in check; false otherwise.
     */
    private boolean isKingInCheckAfterMove(Board simulatedBoard, PieceColor color) {
        King king = simulatedBoard.findKing(color);
        if (king == null) {
            return false;
        }

        List<Piece> opponentPieces = simulatedBoard.getPiecesByColor(
                color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE
        );

        for (Piece piece : opponentPieces) {
            if (piece.canAttackPosition(simulatedBoard, king.getPosition())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the current player has any valid moves left.
     *
     * @return true if the current player has no valid moves; false otherwise.
     */
    private boolean hasNoValidMoves() {
        List<Piece> currentPlayerPieces = board.getPiecesByColor(currentTurn);

        for (Piece piece : currentPlayerPieces) {
            List<Position> validMoves = piece.getValidMoves(board);
            if (!validMoves.isEmpty()) {
                return false; // Found at least one valid move
            }
        }

        return true; // No valid moves left
    }

    /**
     * Checks if the game has started.
     *
     * @return true if the game has started; false otherwise.
     */
    public boolean isGameStarted() {
        return gameStatus != GameStatus.IN_PROGRESS;
    }

    /**
     * Resets the game state to the initial configuration.
     */
    public void resetGame() {
        board.resetBoard();
        currentTurn = PieceColor.WHITE;
        selectedPiece = null;
        highlightedMoves.clear();
        gameStatus = GameStatus.IN_PROGRESS;
    }

    // Getters
    /**
     * Gets the currently selected piece.
     *
     * @return The selected piece.
     */
    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    /**
     * Gets the list of highlighted moves for the selected piece.
     *
     * @return The list of highlighted moves.
     */
    public List<Position> getHighlightedMoves() {
        return highlightedMoves;
    }

    /**
     * Gets the color of the current player's turn.
     *
     * @return The current turn color.
     */
    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Gets the current status of the game.
     *
     * @return The game status.
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }
}

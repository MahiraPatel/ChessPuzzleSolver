/**
 * Board.java
 * 
 * Represents a chess board with methods to manage pieces 
 * and their movements.
 */
import javax.swing.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Board implements Cloneable {
    // Board Colors
    private final Color brown = new Color(150, 75, 0);       // #964B00
    private final Color pastel = new Color(255, 222, 173);   // Navajo White #FFDEAD
    private final Color intermediate = new Color(255, 255, 153);
    private static final Color infoColour = new Color(51, 51, 51);

    // Board dimensions
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 65;

    // 2D array to represent the board state
    private Piece[][] board;

    // List to track all pieces on the board
    private List<Piece> activePieces;

    /**
     * Constructor to initialize the chess board.
     */
    public Board() {
        // Initialize the board array
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        activePieces = new ArrayList<>();
    }

    /**
     * Places a piece on the board at the specified position.
     *
     * @param piece    The piece to be placed on the board.
     * @param position The position on the board where the piece will be placed.
     */
    public void placePiece(Piece piece, Position position) {
        // Remove piece from previous position if it exists
        if (piece.getPosition() != null) {
            board[piece.getPosition().getRow()][piece.getPosition().getColumn()] = null;
        }

        // Place piece on the board
        board[position.getRow()][position.getColumn()] = piece;
        piece.setPosition(position);

        // Add to active pieces if not already present
        if (!activePieces.contains(piece)) {
            activePieces.add(piece);
        }
    }

    /**
     * Removes a piece from the board at the specified position.
     *
     * @param position The position on the board from which the piece will be removed.
     */
    public void removePiece(Position position) {
        Piece piece = getPieceAt(position);
        if (piece != null) {
            board[position.getRow()][position.getColumn()] = null;
            activePieces.remove(piece);
        }
    }

    /**
     * Gets the piece at the specified position on the board.
     *
     * @param position The position on the board.
     * @return The piece at the specified position, or null if no piece is present.
     */
    public Piece getPieceAt(Position position) {
        // Check if position is within board bounds
        if (isValidPosition(position)) {
            return board[position.getRow()][position.getColumn()];
        }
        return null;
    }

    /**
     * Checks if a position is valid on the board.
     *
     * @param position The position to check.
     * @return true if the position is valid, false otherwise.
     */
    public boolean isValidPosition(Position position) {
        return position.getRow() >= 0 && position.getRow() < BOARD_SIZE &&
                position.getColumn() >= 0 && position.getColumn() < BOARD_SIZE;
    }

    /**
     * Gets all pieces of a specific color on the board.
     *
     * @param color The color of the pieces to retrieve.
     * @return A list of pieces of the specified color.
     */
    public List<Piece> getPiecesByColor(PieceColor color) {
        List<Piece> colorPieces = new ArrayList<>();
        for (Piece piece : activePieces) {
            if (piece.getColor() == color) {
                colorPieces.add(piece);
            }
        }
        return colorPieces;
    }

    /**
     * Finds the king of a specific color on the board.
     *
     * @param color The color of the king to find.
     * @return The king piece of the specified color, or null if not found.
     */
    public King findKing(PieceColor color) {
        for (Piece piece : activePieces) {
            if (piece instanceof King && piece.getColor() == color) {
                return (King) piece;
            }
        }
        return null;
    }

    /**
     * Checks if the board has a valid configuration of pieces.
     *
     * @return true if the board configuration is valid, false otherwise.
     */
    public boolean isValidBoardConfiguration() {
        int whiteKings = 0;
        int blackKings = 0;
        int otherPieces = 0; // Track other pieces besides the kings

        // Check each piece on the board
        for (Piece piece : activePieces) {
            if (piece instanceof King) {
                if (piece.getColor() == PieceColor.WHITE) {
                    whiteKings++;
                } else if (piece.getColor() == PieceColor.BLACK) {
                    blackKings++;
                }
            } else {
                otherPieces++; // Count non-king pieces
            }
        }

        // Valid configuration requires exactly one white king, one black king,
        // and at least one additional piece on the board.
        return whiteKings == 1 && blackKings == 1 && otherPieces >= 1;
    }

    /**
     * Moves a piece from its current position to a target position.
     *
     * @param piece          The piece to move.
     * @param targetPosition The target position to move the piece to.
     * @return true if the move was successful, false otherwise.
     */
    public boolean movePiece(Piece piece, Position targetPosition) {
        // Validate the move first, including checking if the move is a valid capture
        if (!piece.isValidMove(this, targetPosition)) {
            return false;  // Invalid move, either out of range or blocked, etc.
        }

        // Check if the target position is occupied by an opponent's piece
        Piece targetPiece = getPieceAt(targetPosition);
        if (targetPiece != null && targetPiece.getColor() == piece.getColor()) {
            return false;  // Can't capture own pieces
        }

        // If a piece is present in the target position and is an opponent's piece, capture it
        if (targetPiece != null) {
            // Remove the captured piece from the board and active pieces list
            removePiece(targetPosition);  // Remove captured piece
        }

        // Now move the piece to the target position
        placePiece(piece, targetPosition);

        // Check for pawn promotion if the piece is a pawn
        if (piece instanceof Pawn && ((Pawn) piece).canPromote()) {
            promotePawn((Pawn) piece);  // Handle the pawn promotion
        }

        return true;
    }

    /**
     * Promotes a pawn to another piece based on player choice.
     *
     * @param pawn The pawn to be promoted.
     */
    private void promotePawn(Pawn pawn) {
        // Prompt the player to choose the piece for promotion
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose a piece to promote the pawn to:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        Piece promotedPiece = null;
        switch (choice) {
            case 0: // Queen
                promotedPiece = new Queen(pawn.getColor(), pawn.getPosition());
                break;
            case 1: // Rook
                promotedPiece = new Rook(pawn.getColor(), pawn.getPosition());
                break;
            case 2: // Bishop
                promotedPiece = new Bishop(pawn.getColor(), pawn.getPosition());
                break;
            case 3: // Knight
                promotedPiece = new Knight(pawn.getColor(), pawn.getPosition());
                break;
            default:
                break;
        }

        // Replace the pawn with the newly promoted piece
        if (promotedPiece != null) {
            // Remove the pawn and place the new piece on the board
            removePiece(pawn.getPosition());  // Remove the original pawn
            placePiece(promotedPiece, pawn.getPosition());  // Place the promoted piece
        }
    }

    /**
     * Gets a list of all active pieces on the board.
     *
     * @return A list of active pieces.
     */
    public List<Piece> getActivePieces() {
        return new ArrayList<>(activePieces);
    }

    /**
     * Renders the board in the console with chess notation.
     */
    public void renderBoard() {
        // Print the column labels (a-h)
        System.out.print("   ");
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print(c + " ");
        }
        System.out.println(); // Newline after column labels

        for (int row = 0; row < BOARD_SIZE; row++) {
            // Print the row number (1-8, reversed)
            System.out.print(8 - row + " "); // Print the row number (1-8, reversed)

            for (int col = 0; col < BOARD_SIZE; col++) {
                Position position = new Position(row, col);
                Piece piece = board[row][col];

                // Print the piece on this position (or an empty space)
                if (piece != null) {
                    System.out.print(piece.getClass().getSimpleName().charAt(0) + " ");  // Show the first letter of the piece type
                } else {
                    System.out.print("- ");  // Empty space
                }
            }
            System.out.println(); // Newline after each row
        }
    }

    /**
     * Clears the board by removing all pieces.
     */
    public void clearBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
        activePieces.clear();
    }

    /**
     * Resets the board to its initial state with no pieces.
     */
    public void resetBoard() {
        // Clear all pieces from the board
        activePieces.clear();  // Assuming activePieces is a list of currently placed pieces
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[row][col] = null;
            }
        }
    }

    /**
     * Gets the size of each tile on the board.
     *
     * @return The size of the tiles.
     */
    public static int getTileSize() {
        return TILE_SIZE;
    }

    /**
     * Gets the brown color used for the board.
     *
     * @return The brown color.
     */
    public Color getBrownColor() {
        return brown;
    }

    /**
     * Gets the pastel color used for the board.
     *
     * @return The pastel color.
     */
    public Color getPastelColor() {
        return pastel;
    }

    /**
     * Gets the intermediate color used for the board.
     *
     * @return The intermediate color.
     */
    public Color getIntermediateColor() {
        return intermediate;
    }

    /**
     * Gets the information color used for the board.
     *
     * @return The information color.
     */
    public static Color getInfoColor() {
        return infoColour;
    }

    /**
     * Clones the current board state.
     *
     * @return A deep copy of the board.
     */
    @Override
    public Board clone() {
        try {
            Board clonedBoard = (Board) super.clone();
            // Deep copy the activePieces list
            clonedBoard.activePieces = new ArrayList<>();
            for (Piece piece : this.activePieces) {
                clonedBoard.activePieces.add(piece.clone()); // Assuming each Piece has a clone method
            }

            // Deep copy the board 2D array
            clonedBoard.board = new Piece[BOARD_SIZE][BOARD_SIZE];
            for (Piece piece : clonedBoard.activePieces) {
                if (piece != null && piece.getPosition() != null) {
                    Position pos = piece.getPosition();
                    clonedBoard.board[pos.getRow()][pos.getColumn()] = piece;
                }
            }

            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}

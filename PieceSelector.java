/**
 * PieceSelector.java
 * 
 * PieceSelector is a UI component that allows users to select chess pieces 
 * for placement on a chess board. It provides a graphical interface with 
 * buttons representing different types of chess pieces.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// PieceSelector UI Component
class PieceSelector extends JPanel {
    private Board board;
    private GameState gameState;

    // Available pieces for selection
    private List<Piece> availablePieces;

    // UI components
    private JPanel pieceSelectorPanel;
    private List<JButton> pieceButtons;

    /**
     * Constructs a PieceSelector with the specified board and game state.
     *
     * @param board the chess board where pieces will be placed
     * @param gameState the current state of the game
     */
    public PieceSelector(Board board, GameState gameState) {
        this.board = board;
        this.gameState = gameState;
        this.availablePieces = new ArrayList<>();
        this.pieceButtons = new ArrayList<>();

        initializeSelector();
    }

    /**
     * Initializes the piece selector UI by creating buttons for each 
     * available piece type and adding them to the panel.
     */
    private void initializeSelector() {
        setLayout(new FlowLayout());

        // Piece types to select from
        Piece[] pieceTypes = {
                new King(PieceColor.WHITE, null),
                new Queen(PieceColor.WHITE, null),
                new Rook(PieceColor.WHITE, null),
                new Bishop(PieceColor.WHITE, null),
                new Knight(PieceColor.WHITE, null),
                new Pawn(PieceColor.WHITE, null),
                new King(PieceColor.BLACK, null),
                new Queen(PieceColor.BLACK, null),
                new Rook(PieceColor.BLACK, null),
                new Bishop(PieceColor.BLACK, null),
                new Knight(PieceColor.BLACK, null),
                new Pawn(PieceColor.BLACK, null)
        };

        // Create buttons for each piece type
        for (Piece piece : pieceTypes) {
            JButton pieceButton = createPieceButton(piece);
            pieceButtons.add(pieceButton);
            add(pieceButton);
        }
    }

    /**
     * Creates a button for a specific piece type and adds an action listener 
     * to handle piece selection.
     *
     * @param piece the piece for which the button is created
     * @return the JButton representing the piece
     */
    private JButton createPieceButton(Piece piece) {
        JButton button = new JButton(getPieceIcon(piece));
        button.setPreferredSize(new Dimension(Board.getTileSize(), Board.getTileSize()));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When piece is selected, open board for placement
                openBoardForPlacement(piece);
            }
        });

        return button;
    }

    /**
     * Retrieves the icon representation for a given piece based on its color 
     * and type.
     *
     * @param piece the piece for which the icon is retrieved
     * @return the Icon representing the piece
     */
    private Icon getPieceIcon(Piece piece) {

        String path = piece.getColor() == PieceColor.WHITE ? "W" : "B";
        path += piece.getClass().getSimpleName() + ".png";
        return new ImageIcon(path);
    }

    /**
     * Opens a dialog prompting the user to click on the board to place the 
     * selected piece.
     *
     * @param piece the piece to be placed on the board
     */
    private void openBoardForPlacement(Piece piece) {
        // Prompt user to click on board to place the piece
        JOptionPane.showMessageDialog(this,
                "Click on the board to place the " +
                        piece.getColor() + " " +
                        piece.getClass().getSimpleName());

        // Add board click listener for placement
        addBoardPlacementListener(piece);
    }

    /**
     * Adds a listener for piece placement on the board. This method should 
     * be implemented to handle the actual placement logic.
     *
     * @param piece the piece to be placed on the board
     */
    private void addBoardPlacementListener(Piece piece) {

    }

    /**
     * Validates the current board configuration before starting the game.
     *
     * @return true if the board configuration is valid, false otherwise
     */
    public boolean validateBoardConfiguration() {
        return board.isValidBoardConfiguration();
    }

    /**
     * Resets the piece selector, clearing the available pieces and resetting 
     * the UI state.
     */
    public void reset() {
        availablePieces.clear();
        // Reset UI state
    }
}
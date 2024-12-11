/**
 * ChessPuzzleSolver.java (Main class)
 * 
 * ChessPuzzleSolver is a graphical user interface application 
 * that allows users to solve chess puzzles.
 * It provides functionality to place pieces on a chessboard,
 * set goals for the game, and check for 
 * checkmate or stalemate conditions.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessPuzzleSolver extends JFrame {
    // Game components
    private Board board;
    private GameState gameState;
    private PieceSelector whitePieceSelector;
    private PieceSelector blackPieceSelector;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton startGameButton;
    private JButton quitGameButton;
    private JButton resetGameButton;

    // Piece placement mode
    private Piece pieceToPlace = null;

    // Goal Validator components
    private int currentMoveCount = 0;
    private Goal currentGoal = null;

    // Game colors and dimensions
    private static final Color BACKGROUND_COLOR = new Color(51, 51, 51);

    // GoalType Enum
    private enum GoalType {
        CHECKMATE_IN_X_MOVES,
        AVOID_CHECKMATE_IN_X_MOVES
    }

    /**
     * Represents a goal for the chess puzzle game.
     */
    private class Goal {
        private GoalType type;
        private int moves;
        private PieceColor targetColor;

        /**
         * Constructs a Goal with the specified type, number of moves, and target color.
         *
         * @param type        the type of the goal (checkmate or avoid checkmate)
         * @param moves       the number of moves to achieve the goal
         * @param targetColor the color of the player to whom the goal applies
         */
        public Goal(GoalType type, int moves, PieceColor targetColor) {
            this.type = type;
            this.moves = moves;
            this.targetColor = targetColor;
        }

        public GoalType getType() {
            return type;
        }

        public int getMoves() {
            return moves;
        }

        public PieceColor getTargetColor() {
            return targetColor;
        }
    }

    /**
     * Constructs a ChessPuzzleSolver instance and initializes the game components.
     */
    public ChessPuzzleSolver() {
        // Initialize game components
        initializeGame();

        // Setup main window
        setupMainWindow();

        // Create game layout
        createGameLayout();
    }

    /**
     * Initializes the game components, including the board and game state.
     */
    private void initializeGame() {
        // Create game objects
        board = new Board();
        gameState = new GameState(board);
    }

    /**
     * Sets up the main window properties, including title and close operation.
     */
    private void setupMainWindow() {
        setTitle("Chess Puzzle Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setResizable(false); // Disable resizing
    }

     /**
     * Creates the layout for the game interface, 
     * including the board and control panels.
     */
    private void createGameLayout() {
        // Main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the main panel

        // Initialize piece selectors
        whitePieceSelector = new PieceSelector(PieceColor.WHITE);
        blackPieceSelector = new PieceSelector(PieceColor.BLACK);

        // Create board with labels
        JPanel boardWithLabels = createBoardWithLabels();

        // Status label
        statusLabel = new JLabel("Place at least 3 pieces to start the game");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setPreferredSize(new Dimension(0, 30)); // Ensure sufficient height

        // Create control panel with buttons
        JPanel controlPanel = createControlPanel();

        // Add components to main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);          // Control buttons at the top
        mainPanel.add(whitePieceSelector, BorderLayout.WEST);    // White pieces on the left
        mainPanel.add(boardWithLabels, BorderLayout.CENTER);      // Board in the center
        mainPanel.add(blackPieceSelector, BorderLayout.EAST);    // Black pieces on the right
        mainPanel.add(statusLabel, BorderLayout.SOUTH);           // Status at the bottom

        // Add main panel to frame
        add(mainPanel);

        // Pack the frame to fit the preferred sizes of added components
        pack();
    }

    /**
     * Creates a control panel with buttons for starting, resetting, and quitting the game.
     *
     * @return the control panel containing the game control buttons
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        controlPanel.setBackground(BACKGROUND_COLOR);

        // Create the start game button
        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> startGame());

        // Create the quit game button
        quitGameButton = new JButton("Quit Game");
        quitGameButton.addActionListener(e -> quitGame());
        quitGameButton.setEnabled(false);  // Initially disabled

        // Create the reset game button
        resetGameButton = new JButton("Reset Game");
        resetGameButton.addActionListener(e -> resetGame());
        resetGameButton.setEnabled(false);  // Initially disabled

        // Add buttons to control panel
        controlPanel.add(startGameButton);
        controlPanel.add(resetGameButton);
        controlPanel.add(quitGameButton);

        return controlPanel;
    }

    /**
     * Creates a board panel with row and column labels for the chessboard.
     *
     * @return the panel containing the chessboard and its labels
     */
    private JPanel createBoardWithLabels() {
        // Main container for board and labels
        JPanel boardContainer = new JPanel(new BorderLayout());

        // Column labels (a-h) at the top
        JPanel columnLabelsPanel = new JPanel(new GridLayout(1, 8));
        columnLabelsPanel.setBackground(BACKGROUND_COLOR);
        for (int col = 0; col < 8; col++) {
            char columnLabel = (char) ('a' + col);
            JLabel label = new JLabel(String.valueOf(columnLabel), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14)); // Reduced font size
            columnLabelsPanel.add(label);
        }

        // Column labels (a-h) at the bottom
        JPanel columnLabelsBottom = new JPanel(new GridLayout(1, 8));
        columnLabelsBottom.setBackground(BACKGROUND_COLOR);
        for (int col = 0; col < 8; col++) {
            char columnLabel = (char) ('a' + col);
            JLabel label = new JLabel(String.valueOf(columnLabel), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14)); // Reduced font size
            columnLabelsBottom.add(label);
        }

        // Row labels (1-8) on the left
        JPanel rowLabelsLeft = new JPanel(new GridLayout(8, 1));
        rowLabelsLeft.setBackground(BACKGROUND_COLOR);
        for (int row = 0; row < 8; row++) {
            JLabel label = new JLabel(String.valueOf(8 - row), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14)); // Reduced font size
            rowLabelsLeft.add(label);
        }

        // Row labels (1-8) on the right
        JPanel rowLabelsRight = new JPanel(new GridLayout(8, 1));
        rowLabelsRight.setBackground(BACKGROUND_COLOR);
        for (int row = 0; row < 8; row++) {
            JLabel label = new JLabel(String.valueOf(8 - row), SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14)); // Reduced font size
            rowLabelsRight.add(label);
        }

        // Create board panel
        boardPanel = createBoardPanel();

        // Add components to board container
        boardContainer.add(columnLabelsPanel, BorderLayout.NORTH); // Column labels on top
        boardContainer.add(columnLabelsBottom, BorderLayout.SOUTH); // Column labels on bottom
        boardContainer.add(rowLabelsLeft, BorderLayout.WEST);       // Row labels on left
        boardContainer.add(boardPanel, BorderLayout.CENTER);        // Board in center
        boardContainer.add(rowLabelsRight, BorderLayout.EAST);      // Row labels on right

        return boardContainer;
    }

    /**
     * Creates the board panel that represents the chessboard.
     *
     * @return the panel containing the chessboard
     */
    private JPanel createBoardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChessBoard(g);
                drawPieces(g);
            }
        };
        panel.setPreferredSize(new Dimension(Board.getTileSize() * 8, Board.getTileSize() * 8));
        panel.addMouseListener(new BoardMouseListener());
        return panel;
    }

    /**
     * Draws the chessboard squares on the panel.
     *
     * @param g the Graphics object used for drawing
     */
    private void drawChessBoard(Graphics g) {
        // Draw the squares of the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Alternate colors
                Color tileColor = (row + col) % 2 == 0 ?
                        board.getPastelColor() :
                        board.getBrownColor();

                g.setColor(tileColor);
                g.fillRect(
                        col * Board.getTileSize(),
                        row * Board.getTileSize(),
                        Board.getTileSize(),
                        Board.getTileSize()
                );
            }
        }
    }

    /**
    * Draws the pieces on the chessboard.
    *
    * @param g the Graphics object used for drawing
    */
    private void drawPieces(Graphics g) {
        for (Piece piece : board.getActivePieces()) {
            Position pos = piece.getPosition();
            Icon icon = getPieceIcon(piece);

            if (icon instanceof ImageIcon) {
                Image img = ((ImageIcon) icon).getImage();
                g.drawImage(img,
                        pos.getColumn() * Board.getTileSize(),
                        pos.getRow() * Board.getTileSize(),
                        Board.getTileSize(),
                        Board.getTileSize(),
                        null
                );
            }
        }

        // Highlight selected piece moves
        if (gameState.getSelectedPiece() != null) {
            g.setColor(new Color(0, 255, 0, 100)); // Transparent green
            for (Position move : gameState.getHighlightedMoves()) {
                g.fillRect(
                        move.getColumn() * Board.getTileSize(),
                        move.getRow() * Board.getTileSize(),
                        Board.getTileSize(),
                        Board.getTileSize()
                );
            }
        }
    }

    /**
     * Gets the icon representation for a given piece.
     *
     * @param piece the piece for which to get the icon
     * @return the icon representing the piece
     */
    private Icon getPieceIcon(Piece piece) {
        String path = piece.getColor() == PieceColor.WHITE ? "W" : "B";
        path += piece.getClass().getSimpleName() + ".png";
        return new ImageIcon(path);
    }

    /**
     * PieceSelector class modified to handle either white or black pieces.
     */
    private class PieceSelector extends JPanel {
        private PieceColor selectorColor;
        private JButton[] pieceButtons; // Array to hold references to buttons

        /**
         * Constructs a PieceSelector for the specified color.
         *
         * @param color the color of the pieces to select
         */
        public PieceSelector(PieceColor color) {
            this.selectorColor = color;
            setLayout(new GridLayout(0, 1, 10, 10)); // Arrange pieces in a single column with gaps
            setBackground(BACKGROUND_COLOR);
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.WHITE),
                    color == PieceColor.WHITE ? "White Pieces" : "Black Pieces",
                    0, 0, new Font("Arial", Font.BOLD, 14),
                    Color.WHITE
            ));

            // Initialize piece types based on selector color
            Piece[] pieceTypes = {
                    color == PieceColor.WHITE ? new King(PieceColor.WHITE, null) : new King(PieceColor.BLACK, null),
                    color == PieceColor.WHITE ? new Queen(PieceColor.WHITE, null) : new Queen(PieceColor.BLACK, null),
                    color == PieceColor.WHITE ? new Rook(PieceColor.WHITE, null) : new Rook(PieceColor.BLACK, null),
                    color == PieceColor.WHITE ? new Bishop(PieceColor.WHITE, null) : new Bishop(PieceColor.BLACK, null),
                    color == PieceColor.WHITE ? new Knight(PieceColor.WHITE, null) : new Knight(PieceColor.BLACK, null),
                    color == PieceColor.WHITE ? new Pawn(PieceColor.WHITE, null) : new Pawn(PieceColor.BLACK, null)
            };

            pieceButtons = new JButton[pieceTypes.length]; // Initialize button array

            // Create buttons for each piece type
            for (int i = 0; i < pieceTypes.length; i++) {
                JButton pieceButton = createPieceButton(pieceTypes[i]);
                pieceButtons[i] = pieceButton; // Store reference
                add(pieceButton);
            }
        }

        /**
         * Creates a button for a given piece.
         *
         * @param piece the piece for which to create a button
         * @return the button representing the piece
         */
        private JButton createPieceButton(Piece piece) {
            JButton button = new JButton(getPieceIcon(piece));
            button.setPreferredSize(new Dimension(Board.getTileSize(), Board.getTileSize()));
            button.setToolTipText(piece.getClass().getSimpleName());

            button.addActionListener(e -> {
                // Set the piece to place
                pieceToPlace = piece;
                statusLabel.setText("Select a position on the board to place the " +
                        piece.getColor() + " " + piece.getClass().getSimpleName());
            });

            return button;
        }

        /**
         * Disables all piece buttons in the selector.
         */
        public void disableAllButtons() {
            for (JButton button : pieceButtons) {
                button.setEnabled(false);
            }
        }

         /**
         * Enables all piece buttons in the selector.
         */
        public void enableAllButtons() {
            for (JButton button : pieceButtons) {
                button.setEnabled(true);
            }
        }
    }

    /**
     * Mouse listener for handling piece placement and movement on the board.
     */
    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // Calculate clicked board position
            int col = e.getX() / Board.getTileSize();
            int row = e.getY() / Board.getTileSize();
            Position clickedPosition = new Position(row, col);

            // Handle piece placement first
            if (pieceToPlace != null) {
                // Check if the position is already occupied
                if (board.getPieceAt(clickedPosition) != null) {
                    statusLabel.setText("Position already occupied. Choose another position.");
                } else {
                    // Create a new piece with the same type and color as selected
                    Piece newPiece = createNewPiece(pieceToPlace.getClass(), pieceToPlace.getColor(), clickedPosition);

                    if (newPiece != null) {
                        // Place the piece on the board
                        board.placePiece(newPiece, clickedPosition);

                        // Reset piece placement mode
                        pieceToPlace = null;
                        statusLabel.setText("Piece placed. Select another piece or start the game.");
                    } else {
                        statusLabel.setText("Error placing piece. Please try again.");
                    }
                }
            } else {
                // Existing piece selection and movement logic
                Piece clickedPiece = board.getPieceAt(clickedPosition);

                if (clickedPiece != null) {
                    // Select piece
                    gameState.selectPiece(clickedPiece);
                } else {
                    // Attempt to move selected piece
                    if (gameState.getSelectedPiece() != null) {
                        gameState.movePiece(clickedPosition);
                        currentMoveCount++; // Increment move count after a successful move
                        checkGoalStatus();    // Check goal after the move
                    }
                }
            }

            // Update status and repaint
            updateGameStatus();
            boardPanel.repaint();
        }

        /**
         * Creates a new piece based on the specified class type, color, and position.
         *
         * @param pieceClass the class type of the piece
         * @param color      the color of the piece
         * @param position   the position to place the piece
         * @return the newly created piece, or null if an error occurred
         */
        private Piece createNewPiece(Class<? extends Piece> pieceClass, PieceColor color, Position position) {
            try {
                return pieceClass.getDeclaredConstructor(PieceColor.class, Position.class)
                        .newInstance(color, position);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null; // Unexpected piece type or error
            }
        }
    }

    /**
     * Updates the game status display based on the current game state.
     */
    private void updateGameStatus() {
        switch (gameState.getGameStatus()) {
            case CHECK:
                statusLabel.setText("Check! " + gameState.getCurrentTurn() + " is in check.");
                break;
            case CHECKMATE:
                statusLabel.setText("Checkmate! " +
                        (gameState.getCurrentTurn() == PieceColor.WHITE ? "Black" : "White") +
                        " wins!");
                showGameOverDialog("Checkmate! " + (gameState.getCurrentTurn() == PieceColor.WHITE ? "Black" : "White") + " wins!");
                break;
            case STALEMATE:
                statusLabel.setText("Stalemate! The game is a draw.");
                showGameOverDialog("Stalemate! The game is a draw.");
                break;
            default:
                statusLabel.setText("Current Turn: " +
                        (gameState.getCurrentTurn() == PieceColor.WHITE ? "White" : "Black"));
        }
    }

    /**
     * Shows a game over dialog with options to restart or quit the game /**
     * Shows a game over dialog with options to restart or quit the game.
     *
     * @param message the message to display in the dialog
     */
    private void showGameOverDialog(String message) {
        int option = JOptionPane.showOptionDialog(
                this,
                message + "\nWould you like to restart or quit?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{ "Restart", "Quit" },
                "Restart"
        );

        if (option == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            quitGame();
        }
    }



    /**
     * Starts the game by showing an initial dialog and handling goal setting.
     */
    private void startGame() {
        // Show initial dialog with options to proceed or set goal
        String[] options = { "Proceed to Game", "Set Goal" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Would you like to set a goal before starting the game?",
                "Start Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 1) { // User chose to set a goal
            setGoal();
            // If goal setting was cancelled, do not proceed
            if (currentGoal == null) {
                return;
            }
        }

        // Proceed to start the game
        proceedToGame();
    }

    /**
     * Sets the goal for the game based on user input.
     */
    private void setGoal() {
        // Create a panel for goal settings
        JPanel goalPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Goal Type Dropdown
        JLabel goalTypeLabel = new JLabel("Select Goal Type:");
        String[] goalTypes = { "Checkmate in X Moves", "Avoid Checkmate in X Moves" };
        JComboBox<String> goalTypeCombo = new JComboBox<>(goalTypes);

        // Number of Moves Input
        JLabel movesLabel = new JLabel("Number of Moves (X):");
        JTextField movesField = new JTextField(10);

        // Target Player Dropdown
        JLabel targetPlayerLabel = new JLabel("Target Player:");
        String[] players = { "White", "Black" };
        JComboBox<String> targetPlayerCombo = new JComboBox<>(players);

        // Add components to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        goalPanel.add(goalTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        goalPanel.add(goalTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        goalPanel.add(movesLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        goalPanel.add(movesField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        goalPanel.add(targetPlayerLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        goalPanel.add(targetPlayerCombo, gbc);

        // Show the goal setting dialog
        int result = JOptionPane.showConfirmDialog(
                this,
                goalPanel,
                "Set Goal",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedGoalType = (String) goalTypeCombo.getSelectedItem();
            String movesText = movesField.getText().trim();
            String selectedPlayer = (String) targetPlayerCombo.getSelectedItem();

            // Validate the number of moves
            int moves;
            try {
                moves = Integer.parseInt(movesText);
                if (moves <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid positive integer for the number of moves.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
                setGoal(); // Retry setting the goal
                return;
            }

            // Determine the goal type
            GoalType goalType;
            if (selectedGoalType.equals("Checkmate in X Moves")) {
                goalType = GoalType.CHECKMATE_IN_X_MOVES;
            } else {
                goalType = GoalType.AVOID_CHECKMATE_IN_X_MOVES;
            }

            // Determine the target color
            PieceColor targetColor = selectedPlayer.equals("White") ? PieceColor.WHITE : PieceColor.BLACK;

            // Set the current goal
            currentGoal = new Goal(goalType, moves, targetColor);
        } else {
            // User cancelled goal setting
            currentGoal = null;
        }
    }

    /**
     * Proceeds to start the game by enabling the necessary controls and updating the status.
     */
    private void proceedToGame() {
        // Disable piece selectors and start game button
        whitePieceSelector.disableAllButtons();
        blackPieceSelector.disableAllButtons();
        startGameButton.setEnabled(false);

        // Enable the new control buttons
        quitGameButton.setEnabled(true);
        resetGameButton.setEnabled(true);

        // Reset move count
        currentMoveCount = 0;

        // Enable board for gameplay
        if (currentGoal != null) {
            statusLabel.setText("Game Started with Goal. " + (gameState.getCurrentTurn() == PieceColor.WHITE ? "White's" : "Black's") + " turn.");
        } else {
            statusLabel.setText("Game Started. " + (gameState.getCurrentTurn() == PieceColor.WHITE ? "White's" : "Black's") + " turn.");
        }
    }

    /**
     * Checks the goal status after each move to determine if the goal has been achieved or failed.
     */
    private void checkGoalStatus() {
        if (currentGoal == null) {
            return; // No goal set
        }

        // Check if the number of moves has reached the goal
        if (currentMoveCount >= currentGoal.getMoves()) {
            // Evaluate the goal based on its type
            if (currentGoal.getType() == GoalType.CHECKMATE_IN_X_MOVES) {
                // Check if the target player is in checkmate
                if (gameState.isCheckmate(currentGoal.getTargetColor())) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Goal Achieved: " + currentGoal.getTargetColor() + " has been checkmated in " + currentGoal.getMoves() + " moves!",
                            "Goal Achieved",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    currentGoal = null; // Reset the goal
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Goal Failed: " + currentGoal.getTargetColor() + " was not checkmated in " + currentGoal.getMoves() + " moves.",
                            "Goal Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                    currentGoal = null; // Reset the goal
                }
            } else if (currentGoal.getType() == GoalType.AVOID_CHECKMATE_IN_X_MOVES) {
                // Check if the target player is NOT in checkmate
                if (gameState.isCheckmate(currentGoal.getTargetColor())) {
                    // Goal Failed
                    int option = JOptionPane.showOptionDialog(
                            this,
                            "Goal Failed: " + currentGoal.getTargetColor() + " was checkmated in " + currentGoal.getMoves() + " moves.\nWould you like to proceed normally or restart the game?",
                            "Goal Failed",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            new Object[]{ "Proceed Normally", "Restart Game" },
                            "Proceed Normally"
                    );

                    if (option == JOptionPane.NO_OPTION) {
                        resetGame();
                    }
                } else {
                    // Goal Achieved
                    JOptionPane.showMessageDialog(
                            this,
                            "Goal Achieved: " + currentGoal.getTargetColor() + " has avoided checkmate in " + currentGoal.getMoves() + " moves!",
                            "Goal Achieved",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    currentGoal = null; // Reset the goal
                }
            }
        }
    }

    /**
     * Resets the game to its initial state, clearing the board and resetting controls.
     */
    private void resetGame() {
        // Reset the board and game state
        board.resetBoard();
        gameState.resetGame();

        // Re-enable piece selectors
        whitePieceSelector.enableAllButtons();
        blackPieceSelector.enableAllButtons();

        // Reset buttons' enabled state
        startGameButton.setEnabled(true);
        quitGameButton.setEnabled(false);
        resetGameButton.setEnabled(false);

        // Reset move count and goal
        currentMoveCount = 0;
        currentGoal = null;

        // Reset status label
        statusLabel.setText("Place at least 3 pieces to start the game.");

        // Repaint the board
        boardPanel.repaint();
    }

    /**
     * Quits the game application.
     */
    private void quitGame() {
        System.exit(0);
    }

    /**
     * The main method to start the ChessPuzzleSolver application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessPuzzleSolver game = new ChessPuzzleSolver();
            game.setVisible(true);
        });
    }
}

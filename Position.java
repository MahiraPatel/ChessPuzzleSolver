/**
 * Position.java
 * 
 * The Position class represents a position on a chessboard using row and column coordinates.
 * The row is represented as an integer from 0 to 7 (where 0 corresponds to the 8th rank and 7 to the 1st rank),
 * and the column is represented as an integer from 0 to 7 (where 0 corresponds to 'a' and 7 to 'h').
 */
public class Position {
    private int row;    // 0-7 representation of 1-8
    private int column; // 0-7 representation of a-h

    /**
     * Constructs a Position with the specified row and column.
     *
     * @param row    the row of the position (0-7)
     * @param column the column of the position (0-7)
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row of the position.
     *
     * @return the row of the position (0-7)
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column of the position.
     *
     * @return the column of the position (0-7)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Converts the position to standard chess notation (e.g., e4, h8).
     *
     * @return a string representing the position in chess notation
     */
    public String toChessNotation() {
        char columnLetter = (char) ('a' + column);  // Convert column index to letter (0 = 'a', 7 = 'h')
        return columnLetter + String.valueOf(8 - row);  // Row 0 becomes 8, row 7 becomes 1
    }

    /**
     * Returns a string representation of the position in chess notation.
     *
     * @return a string representing the position
     */
    @Override
    public String toString() {
        return toChessNotation(); // Use chess notation for a clear and recognizable format
    }

    /**
     * Compares this position to the specified object for equality.
     *
     * @param o the object to compare this position against
     * @return true if the specified object is equal to this position, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    /**
     * Returns a hash code value for this position.
     *
     * @return a hash code value for this position
     */
    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}

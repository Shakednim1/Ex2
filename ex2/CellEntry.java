package assignments.ex2;

public class CellEntry implements Index2D {
    private int x;
    private int y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        char columnLetter = (char) ('A' + this.x);
        String rowNumber = Integer.toString(this.y);
        return columnLetter + rowNumber;
    }

    @Override
    public boolean isValid() {
        return isValid(x, y);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CellEntry other = (CellEntry) obj;
        return this.toString().equals(other.toString());
    }

    private static boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && y < 26 && x < 100; // Valid range: X in [0-99], Y in [0-25] (A-Z)
    }

    public static CellEntry parseCellIndex(String cellIndex) {
        if (cellIndex == null || cellIndex.length() < 2) {
            return null;
        }

        char columnLetter = cellIndex.charAt(0); // Extract the column letter (e.g., 'A', 'B', etc.)
        int rowIndex;
        try {
            rowIndex = Integer.parseInt(cellIndex.substring(1));
        } catch (NumberFormatException ex) {
            return null;
        }
        int columnIndex = Character.toUpperCase(columnLetter) - 'A';

        if (!isValid(columnIndex, rowIndex)) {
            return null;
        }

        return new CellEntry(columnIndex, rowIndex);
    }
}
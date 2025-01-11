package assignments.ex2;
import java.io.*;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];     // Create a 2D table with dimensions x by y
        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++)
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
        eval();
    }

    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null;
    }

    @Override
    public Cell get(String cords) {
        CellEntry entry = CellEntry.parseCellIndex(cords);  // Parse the coordinates string into a CellEntry object
        if (entry == null || !entry.isValid()) {
            return null;
        }
        return get(entry.getX(), entry.getY());
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int row, int column, String value) {
        if (!isIn(row, column)) {
            return;
        }
        table[row][column] = new SCell(value);
        eval();
    }

    public String locateCell(Cell target) {
        for (int rowIndex = 0; rowIndex < width(); rowIndex++) {
            for (int colIndex = 0; colIndex < height(); colIndex++) {
                Cell current = get(rowIndex, colIndex);
                if (current != null && current.equals(target)) {
                    return new CellEntry(rowIndex, colIndex).toString();
                }
            }
        }
        return null;
    }

    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) {
            return Ex2Utils.EMPTY_CELL;
        }
        Cell cell = get(x, y);
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;
        }
        switch (cell.getType()) {
            case Ex2Utils.TEXT:
                return ((SCell) cell).getShownValue(); // Return raw data for TEXT type

            case Ex2Utils.NUMBER:
                return String.valueOf(Double.parseDouble(cell.getData())); // Convert NUMBER to string

            case Ex2Utils.FORM:
                try {
                    return ((SCell) cell).evalForm(this); // Evaluate the formula
                } catch (StackOverflowError e) {
                    cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Set cycle error type
                    return Ex2Utils.ERR_CYCLE;
                } catch (IllegalArgumentException e) {
                    cell.setType(Ex2Utils.ERR_FORM_FORMAT); // Set formula error type
                    return Ex2Utils.ERR_FORM;
                }
            case Ex2Utils.ERR_CYCLE_FORM:
                return Ex2Utils.ERR_CYCLE; // Return cycle error message

            case Ex2Utils.ERR_FORM_FORMAT:
                return Ex2Utils.ERR_FORM; // Return formula error message

            default:
                return Ex2Utils.EMPTY_CELL; // Return empty for unexpected types
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < width() && yy < height();
    }

    @Override
    public int[][] depth() {
        int rows = width();
        int cols = height();
        int[][] ordersMatrix = new int[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell currentCell = get(row, col);
                if (currentCell != null) {
                    ordersMatrix[row][col] = currentCell.getOrder();
                }
            }
        }
        return ordersMatrix;

    }
    @Override
    public void eval() {
        // Iterate through all cells to resolve formulas, update orders, and detect errors
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell current = get(x, y);
                if (current != null && current.getType() == Ex2Utils.FORM) {
                    try {
                        current.setData(value(x, y));
                    } catch (StackOverflowError e) {
                        current.setType(Ex2Utils.ERR_CYCLE_FORM);
                    } catch (Exception e) {
                        current.setType(Ex2Utils.ERR_FORM_FORMAT);
                    }
                }
            }
        }
    }


    @Override
    public void load(String filePath) throws IOException {
        // Use a BufferedReader to read the file line by line
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            int currentRow = 0;
            while ((currentLine = reader.readLine()) != null && currentRow < height()) {
                String[] cellValues = currentLine.split(",");
                for (int currentCol = 0; currentCol < cellValues.length && currentCol < width(); currentCol++) {
                    set(currentRow, currentCol, cellValues[currentCol]);
                }
                currentRow++;
            }
        }
    }

    @Override
    public void save(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int totalRows = width();
            int totalColumns = height();

            for (int currentRow = 0; currentRow < totalRows; currentRow++) {
                StringBuilder rowContent = new StringBuilder();

                for (int currentColumn = 0; currentColumn < totalColumns; currentColumn++) {
                    Cell cell = get(currentRow, currentColumn);
                    rowContent.append(cell != null ? cell.getData() : "");

                    if (currentColumn < totalColumns - 1) {
                        rowContent.append(",");
                    }
                }
                writer.write(rowContent.toString());
                writer.newLine();
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        return value(x, y);
    }

}

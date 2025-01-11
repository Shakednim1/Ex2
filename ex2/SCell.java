package assignments.ex2;
import java.util.*;

public class SCell implements Cell {
    private String line; // Holds the raw data
    private int type; // The type of the cell
    private int order; // evaluation order
    private String setString; //  string representation of the cell's data

    public SCell(String s) {
        setString = s;
        setData(s);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Cell) && this.toString().equals(object.toString());  // Compare the current cell with another object

    }
    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        line = isNumber(s) ? String.valueOf(Double.parseDouble(s)) : s;
        if (type != Ex2Utils.FORM) {
            setType(detType(s));
        }
    }

    @Override
    public String getData() {
        return setString;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    public boolean isForm(String text) {
        return text != null && text.length() > 1 && text.charAt(0) == '=';
    }

    public String evalForm(Ex2Sheet sheet) {
        Set<String> visited = new HashSet<>();
        return evalForm(sheet, visited);
    }

    public String getShownValue() {
        return line;
    }

    int locateMainOperator(String expression, String targetOperator) {
        int parenthesisDepth = 0;

        for (int index = 0; index < expression.length(); index++) {
            char currentChar = expression.charAt(index);

            if (currentChar == '(') {
                parenthesisDepth++; // Increment depth for opening parenthesis
            } else if (currentChar == ')') {
                parenthesisDepth--; // Decrement depth for closing parenthesis
            }

            if (parenthesisDepth == 0 && expression.startsWith(targetOperator, index)) {
                return index; // Found the operator outside parentheses
            }
        }

        return -1; // Return -1 if no main operator is found
    }

    boolean validCell(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }

        char column = input.charAt(0);
        if (!Character.isLetter(column)) {
            return false;
        }

        String row = input.substring(1);
        return row.chars().allMatch(Character::isDigit);
    }

    boolean areParenthesesBalanced(String text) {
        int openCount = 0;
        for (char ch : text.toCharArray()) {
            if (ch == '(') {
                openCount++; // Increment for an opening parenthesis
            } else if (ch == ')') {
                openCount--; // Decrement for a closing parenthesis
            }
            if (openCount < 0) {
                return false; // More closing parentheses than opening
            }
        }

        return openCount == 0; // Balanced if all opening parentheses are matched
    }

    boolean hasSurroundingParentheses(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.charAt(0) == '(' && text.charAt(text.length() - 1) == ')'; // Check if parentheses surround the text
    }


    private int calculateOrder(String expression, Ex2Sheet sheet) {
        int highestOrder = 0;

        // Split the expression into individual tokens
        String[] components = splitToken(expression);
        for (String component : components) {
            if (validCell(component)) {
                CellEntry cellReference = CellEntry.fromString(component);
                if (sheet.isIn(cellReference.getX(), cellReference.getY())) {
                    Cell referencedCell = sheet.get(cellReference.getX(), cellReference.getY());
                    highestOrder = Math.max(highestOrder, referencedCell.getOrder()); // Update highest order
                }
            }
        }

        return highestOrder; // Return the highest order found
    }

    String[] splitToken(String expression) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (char character : expression.toCharArray()) {
            if (Character.isLetterOrDigit(character)) {
                buffer.append(character); // Add to the current token if alphanumeric
            } else {
                if (!buffer.isEmpty()) {
                    parts.add(buffer.toString()); // Add the token to the list
                    buffer.setLength(0); // Clear the buffer
                }
                if (!Character.isWhitespace(character)) {
                    parts.add(String.valueOf(character)); // Add the operator or special character
                }
            }
        }
        if (!buffer.isEmpty()) {
            parts.add(buffer.toString()); // Add the last token
        }

        return parts.toArray(new String[0]); // Convert the list to an array
    }

    public void handleCycleError(String message) {
        throw new RuntimeException("Cycle Error: " + message); // Throw an error when a cycle is detected
    }

    public void handleFormulaError(String message) {
        throw new RuntimeException("Formula Error: " + message); // Throw an error for invalid formulas
    }

    public boolean isNumber(String text) {
        try {
            Double d = Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isText(String text) {
        return !isNumber(text) && !isForm(text);
    }

    private int detType(String data) {
        return this.isForm(data) ? Ex2Utils.FORM :
                (this.isNumber(data) ? Ex2Utils.NUMBER : Ex2Utils.TEXT); // Determine the cell's type based on its content
    }
    public String evalForm(Ex2Sheet sheet, Set<String> visited) {
        if (type != Ex2Utils.FORM) {
            return line;
        }

        Set<String> currentVisited = visited == null ? new HashSet<>() : visited; // Initialize visited set

        CellEntry currentCell = CellEntry.fromString(sheet.locateCell(this)); // Locate the current cell
        if (currentCell != null) {
            if (currentVisited.contains(currentCell.toString())) {
                type = Ex2Utils.ERR_CYCLE_FORM; // Mark as a cycle error if already visited
                handleCycleError("Cycle detected in formula for cell: " + currentCell);
            }
            currentVisited.add(currentCell.toString()); // Mark the current cell as visited
        }

        String formula = getData().startsWith("=") ? getData().substring(1) : getData(); // Remove the "=" prefix
        String result = String.valueOf(Form(formula, sheet, currentVisited)); // Evaluate the formula recursively

        order = calculateOrder(formula, sheet); // Update the evaluation order based on dependencies

        if (currentCell != null) {
            currentVisited.remove(currentCell.toString()); // Remove the current cell from visited after evaluation
        }

        line = result; // Update the cell's data with the evaluated result
        return result;
    }
    private double Form(String form, Ex2Sheet sheet, Set<String> visited) {
        String trimmedFormula = form.trim(); // Remove leading and trailing spaces

        // Check if the formula is a number
        if (isNumber(trimmedFormula)) {
            return Double.parseDouble(trimmedFormula);
        }

        // Check if the formula is a valid cell reference
        if (validCell(trimmedFormula)) {
            CellEntry cellReference = CellEntry.fromString(trimmedFormula);
            if (cellReference == null || !sheet.isIn(cellReference.getX(), cellReference.getY())) {
                handleFormulaError("Invalid cell reference: " + trimmedFormula);
            }

            Cell referencedCell = sheet.get(cellReference.getX(), cellReference.getY());
            if (referencedCell.getType() == Ex2Utils.NUMBER) {
                return Double.parseDouble(referencedCell.getData()); // Return the cell's value as a number
            }
            if (referencedCell.getType() == Ex2Utils.FORM) {
                return Double.parseDouble(((SCell) referencedCell).evalForm(sheet, visited)); // Recursively evaluate the formula
            }
            if (referencedCell.getType() == Ex2Utils.ERR_CYCLE_FORM ||
                    Objects.equals(((SCell) referencedCell).getShownValue(), Ex2Utils.ERR_CYCLE)) {
                handleCycleError("Recursive cell reference: " + trimmedFormula); // Handle cycles
            }
            if (referencedCell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                handleFormulaError("Invalid or unresolved cell reference: " + trimmedFormula);
            }
        }

        if (hasSurroundingParentheses(trimmedFormula) && // Handle formulas enclosed in parentheses
                areParenthesesBalanced(trimmedFormula.substring(1, trimmedFormula.length() - 1))) {
            return Form(trimmedFormula.substring(1, trimmedFormula.length() - 1), sheet, visited); // Evaluate the inner formula
        }

        for (String operator : Ex2Utils.M_OPS) {      // Handle binary operations

            int index = locateMainOperator(trimmedFormula, operator);
            if (index != -1) {
                double leftValue = Form(trimmedFormula.substring(0, index).trim(), sheet, visited); // Evaluate the left side
                double rightValue = Form(trimmedFormula.substring(index + 1).trim(), sheet, visited); // Evaluate the right side
                switch (operator) {
                    case "+":
                        return leftValue + rightValue;
                    case "-":
                        return leftValue - rightValue;
                    case "*":
                        return leftValue * rightValue;
                    case "/":
                        if (rightValue == 0) {
                            throw new IllegalArgumentException("Division by zero"); // Handle division by zero
                        }
                        return leftValue / rightValue;
                }
            }
        }

        throw new IllegalArgumentException("Invalid form syntax: " + trimmedFormula); // Handle invalid formula syntax
    }


}

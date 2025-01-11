package assignments.ex2;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SCellTest {

    @Test
    void testIsForm() {
        SCell cell = new SCell("=A1+B1");
        assertTrue(cell.isForm("=A1+B1"));
        assertFalse(cell.isForm("A1+B1"));
        assertFalse(cell.isForm(null));
        assertFalse(cell.isForm(""));
    }

    @Test
    void testValidCell() {
        SCell cell = new SCell("");
        assertTrue(cell.validCell("A1"));
        assertTrue(cell.validCell("Z999"));
        assertFalse(cell.validCell("1A"));
        assertFalse(cell.validCell("AA1"));
        assertFalse(cell.validCell(""));
        assertFalse(cell.validCell(null));
    }

    @Test
    void testLocateMainOperator() {
        SCell cell = new SCell("");
        assertEquals(1, cell.locateMainOperator("A+B", "+"));
        assertEquals(5, cell.locateMainOperator("(A+B)*C", "*"));
        assertEquals(-1, cell.locateMainOperator("A+B", "-"));
    }

    @Test
    void testAreParenthesesBalanced() {
        SCell cell = new SCell("");
        assertTrue(cell.areParenthesesBalanced("(A+B)"));
        assertTrue(cell.areParenthesesBalanced("((A+B)*C)"));
        assertFalse(cell.areParenthesesBalanced("(A+B"));
        assertFalse(cell.areParenthesesBalanced("A+B)"));
    }

    @Test
    void testEvalFormSimple() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10"); // A1
        sheet.set(1, 0, "20"); // B1
        sheet.set(2, 0, "=A1+B1"); // C1

        SCell formulaCell = (SCell) sheet.get(2, 0);
        assertEquals("30.0", formulaCell.evalForm(sheet));
    }

    @Test
    void testEvalFormWithNestedReferences() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "5"); // A1
        sheet.set(0, 1, "=A1*2"); // B1
        sheet.set(0, 2, "=B1+3"); // C1

        SCell formulaCell = (SCell) sheet.get(0, 2);
        assertEquals("13.0", formulaCell.evalForm(sheet));
    }

    @Test
    void testEvalFormWithCycle() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=B1"); // A1
        sheet.set(0, 1, "=A1"); // B1

        SCell formulaCell = (SCell) sheet.get(0, 0);}

    @Test
    void testSplitToken() {
        SCell cell = new SCell("");
        String[] tokens = cell.splitToken("A1+B1*C1");
        assertArrayEquals(new String[]{"A1", "+", "B1", "*", "C1"}, tokens);

        tokens = cell.splitToken("(A1+B1)*C1");
        assertArrayEquals(new String[]{"(", "A1", "+", "B1", ")", "*", "C1"}, tokens);
    }

    @Test
    void testHandleCycleError() {
        SCell cell = new SCell("");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cell.handleCycleError("Cycle detected"));
        assertEquals("Cycle Error: Cycle detected", exception.getMessage());
    }

    @Test
    void testHandleFormulaError() {
        SCell cell = new SCell("");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> cell.handleFormulaError("Invalid formula"));
        assertEquals("Formula Error: Invalid formula", exception.getMessage());
    }
    @Test
    void testSet() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        assertNotNull(sheet.get(0, 0));
        assertEquals("10", sheet.get(0, 0).getData()); // וודא שהערך נשמר
    }

}

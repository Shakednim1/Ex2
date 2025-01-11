package assignments.ex2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SCellTest {

    @Test
    void testConstructorAndGetters() {
        SCell cell = new SCell("123");
        assertEquals("123", cell.getData(), "Cell data should be '123'");
        assertEquals(Ex2Utils.NUMBER, cell.getType(), "Cell type should be NUMBER");

        SCell textCell = new SCell("Hello");
        assertEquals("Hello", textCell.getData(), "Cell data should be 'Hello'");
        assertEquals(Ex2Utils.TEXT, textCell.getType(), "Cell type should be TEXT");
    }

    @Test
    void testSetData() {
        SCell cell = new SCell("=A1+B1");
        assertEquals("=A1+B1", cell.getData(), "Cell data should be '=A1+B1'");
        assertEquals(Ex2Utils.FORM, cell.getType(), "Cell type should be FORM");

        cell.setData("42");
        assertEquals("42", cell.getData(), "Cell data should be updated to '42'");
        assertEquals(Ex2Utils.NUMBER, cell.getType(), "Cell type should be updated to NUMBER");
    }

    @Test
    void testIsNumber() {
        SCell cell = new SCell("123");
        assertTrue(cell.isNumber("123"), "'123' should be identified as a number");
        assertFalse(cell.isNumber("abc"), "'abc' should not be identified as a number");
        assertFalse(cell.isNumber("=1+2"), "'=1+2' should not be identified as a number");
    }

    @Test
    void testIsForm() {
        SCell cell = new SCell("=1+2");
        assertTrue(cell.isForm("=1+2"), "'=1+2' should be identified as a formula");
        assertFalse(cell.isForm("123"), "'123' should not be identified as a formula");
        assertFalse(cell.isForm("abc"), "'abc' should not be identified as a formula");
    }

    @Test
    void testAreParenthesesBalanced() {
        SCell cell = new SCell("=1+2");
        assertTrue(cell.areParenthesesBalanced("(1+2)"), "'(1+2)' should be balanced");
        assertFalse(cell.areParenthesesBalanced("(1+2"), "'(1+2' should not be balanced");
        assertTrue(cell.areParenthesesBalanced(""), "Empty string should be balanced");
    }

    @Test
    void testEvalForm_CycleDetection() {
        Ex2Sheet sheet = new Ex2Sheet(10, 10);
        sheet.set(0, 0, "=B1");
        sheet.set(0, 1, "=A1");
        SCell cell = new SCell("=A1");

        assertThrows(RuntimeException.class, () -> cell.evalForm(sheet),
                "A cycle between A1 and B1 should throw a RuntimeException");
    }

    @Test
    void testLocateMainOperator() {
        SCell cell = new SCell("=1+2");
        assertEquals(1, cell.locateMainOperator("1+2", "+"),
                "Main operator '+' should be located at index 1");
        assertEquals(-1, cell.locateMainOperator("(1+2)", "+"),
                "Main operator '+' inside parentheses should not be considered");
    }

    @Test
    void testSplitToken() {
        SCell cell = new SCell("=1+2");
        String[] tokens = cell.splitToken("1+2*3");
        assertArrayEquals(new String[]{"1", "+", "2", "*", "3"}, tokens,
                "Tokens should be split correctly as ['1', '+', '2', '*', '3']");
    }

    @Test
    void testHandleCycleError() {
        SCell cell = new SCell("=A1");
        assertThrows(RuntimeException.class, () -> cell.handleCycleError("Cycle detected"),
                "handleCycleError should throw a RuntimeException with the given message");
    }

    @Test
    void testHandleFormulaError() {
        SCell cell = new SCell("=A1");
        assertThrows(RuntimeException.class, () -> cell.handleFormulaError("Invalid formula"),
                "handleFormulaError should throw a RuntimeException with the given message");
    }
}

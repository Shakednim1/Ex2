package assignments.ex2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellEntryTest {

    @Test
    void testConstructorAndGetters() {
        CellEntry cell = new CellEntry(3, 5);
        assertEquals(3, cell.getX(), "X value should be 3");
        assertEquals(5, cell.getY(), "Y value should be 5");
    }

    @Test
    void testToString() {
        CellEntry cell = new CellEntry(0, 1); // 0 -> 'A', 1 remains as '1'
        assertEquals("A1", cell.toString(), "toString() should return 'A1'");
    }

    @Test
    void testEquals() {
        CellEntry cell1 = new CellEntry(2, 3); // C3
        CellEntry cell2 = new CellEntry(2, 3); // C3
        CellEntry cell3 = new CellEntry(1, 3); // B3

        assertEquals(cell1, cell2, "Cells with the same X and Y should be equal");
        assertNotEquals(cell1, cell3, "Cells with different X or Y should not be equal");
    }

    @Test
    void testParseCellIndex_ValidInput() {
        CellEntry cell = CellEntry.parseCellIndex("B4");
        assertNotNull(cell, "parseCellIndex should return a valid CellEntry for 'B4'");
        assertEquals(1, cell.getX(), "Column B should map to index 1");
        assertEquals(4, cell.getY(), "Row 4 should map to index 4");
    }

    @Test
    void testParseCellIndex_InvalidInput() {
        assertNull(CellEntry.parseCellIndex(null), "parseCellIndex should return null for null input");
        assertNull(CellEntry.parseCellIndex(""), "parseCellIndex should return null for empty string");
        assertNull(CellEntry.parseCellIndex("A"), "parseCellIndex should return null for single letter input");
        assertNull(CellEntry.parseCellIndex("1"), "parseCellIndex should return null for single digit input");
        assertNull(CellEntry.parseCellIndex("Z100"), "parseCellIndex should return null for out-of-range input");
        assertNull(CellEntry.parseCellIndex("AA"), "parseCellIndex should return null for invalid format input");
    }

    @Test
    void testIsValid() {
        CellEntry validCell = new CellEntry(10, 20); // Valid range
        assertTrue(validCell.isValid(), "Cell within valid range should be valid");

        CellEntry invalidCell1 = new CellEntry(-1, 5); // Negative X
        CellEntry invalidCell2 = new CellEntry(10, -5); // Negative Y
        CellEntry invalidCell3 = new CellEntry(100, 5); // X out of range
        CellEntry invalidCell4 = new CellEntry(10, 26); // Y out of range

        assertFalse(invalidCell1.isValid(), "Cell with negative X should not be valid");
        assertFalse(invalidCell2.isValid(), "Cell with negative Y should not be valid");
        assertFalse(invalidCell3.isValid(), "Cell with X out of range should not be valid");
        assertFalse(invalidCell4.isValid(), "Cell with Y out of range should not be valid");
    }
}

package assignments.ex2;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
public class Ex2SheetTest {


    @Test
    void testTextCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "Hello World");
        assertEquals("Hello World", sheet.value(0, 0), "A1 should evaluate to 'Hello World'");
    }

    @Test
    void testInvalidFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=INVALID");
        assertEquals("ERR_FORM!", sheet.value(0, 0), "A1 should evaluate to 'ERR_FORM'");
    }


    @Test
    void testEmptyCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals("", sheet.value(0, 0), "Empty cell should return an empty string");
    }

    @Test
    void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "10");
        sheet.set(0, 1, "=A1+5");
        sheet.set(0, 2, "Hello");

        String filePath = "testSheet.csv";
        sheet.save(filePath);

        Ex2Sheet loadedSheet = new Ex2Sheet(5, 5);
        loadedSheet.load(filePath);

        assertEquals("10", loadedSheet.get(0, 0).getData(), "Loaded A1 should be 10");
        assertEquals("=A1+5", loadedSheet.get(0, 1).getData(), "Loaded B1 should be '=A1+5'");
        assertEquals("Hello", loadedSheet.get(0, 2).getData(), "Loaded C1 should be 'Hello'");
    }
}

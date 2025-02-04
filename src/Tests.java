import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @Test
    void testisText() {
        SCell sCell = new SCell("");
        assertEquals(false, sCell.isText("1"));
        assertEquals(false, sCell.isText("=1"));
        assertEquals(true, sCell.isText("fh"));
        assertEquals(true, sCell.isText("88A"));
    }


    @Test
    void testisNumber() {
        SCell sCell = new SCell("1");
        assertEquals(true, sCell.isNumber("1"));
        assertEquals(false, sCell.isNumber(" "));
        assertEquals(false, sCell.isNumber(""));
        assertEquals(false, sCell.isNumber("T"));
        assertEquals(true, sCell.isNumber("1.2"));
        assertEquals(true, sCell.isNumber("-1.2"));
    }

    @Test
    void testisForm () {
        SCell sCell = new SCell("1");
        assertEquals(true, sCell.isForm("=1"));
        assertEquals(false, sCell.isForm("fh"));
        assertEquals(true, sCell.isForm("=(4*2+5)+5*4"));
        assertEquals(true, sCell.isForm("=(4*2+(4+5))"));
        assertEquals(true, sCell.isForm("=(4*2)+(5+4)"));
        assertEquals(true, sCell.isForm("=(4*2+(4+5))+(5+5)"));
        assertEquals(false, sCell.isForm("=4*2+(4+5))+(5+5)"));
        assertEquals(false, sCell.isForm("=(4*2+4+5))+(5+5)"));
        assertEquals(true, sCell.isForm("=(4*2+(+5))+(5+5)"));
        assertEquals(true, sCell.isForm("=(4*2-(+5))+(-5)"));
        assertEquals(false, sCell.isForm("=(4**2+(4+5))+(5+5)"));
        assertEquals(true, sCell.isForm("=-(4+5)"));
        assertEquals(true, sCell.isForm("=-(4*2+(4+5))+(5+5)"));
        assertEquals(false, sCell.isForm("=(4*2a+(4+5))+(5+5)"));
        assertEquals(true, sCell.isForm("=(4*2+ 4+5)+(5+5)"));
        assertEquals(true, sCell.isForm("=+(4*2+4+5)+(5+5)"));
        assertEquals(true, sCell.isForm("=+(4*2+4+5)+(-5+5)"));
        assertEquals(false, sCell.isForm("=*(4)"));
        assertEquals(false, sCell.isForm("=*(4*2-(+5))+(-5)"));
        assertEquals(true, sCell.isForm("=A1*(3-A2)"));
        assertEquals(true, sCell.isForm("=A1*(3-d2)"));
        assertEquals(false, sCell.isForm("=A1*(3-d211)"));
        assertEquals(false, sCell.isForm("=1A*(3-d2)"));
    }

    @Test
    void testWhatType() {
        SCell sCell = new SCell();
        assertEquals(Ex2Utils.NUMBER, sCell.whatType("123"));
        assertEquals(Ex2Utils.TEXT, sCell.whatType("hello"));
        assertEquals(Ex2Utils.FORM, sCell.whatType("=A1+B2"));

    }

    @Test
    void testToString() {
        SCell sCell1 = new SCell("123");
        assertEquals("123.0", sCell1.toString());

        SCell sCell2 = new SCell("");
        sCell2.setLine("");
        assertEquals("", sCell2.toString());

        SCell sCell3 = new SCell("=5+5");
        assertEquals("=5+5", sCell3.toString());
    }

    @Test
    void testSetDataAndGetData() {
        SCell sCell = new SCell();
        sCell.setLine("123");
        assertEquals("123.0", sCell.getData());

        sCell.setLine("hello");
        assertEquals("hello", sCell.getData());
    }

    @Test
    void testGetTypeAndSetType() {
        SCell sCell = new SCell();
        sCell.setType(Ex2Utils.TEXT);
        assertEquals(Ex2Utils.TEXT, sCell.getType());

        sCell.setType(Ex2Utils.NUMBER);
        assertEquals(Ex2Utils.NUMBER, sCell.getType());

        sCell.setType(Ex2Utils.ERR_CYCLE_FORM);
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, sCell.getType());

        sCell.setType(Ex2Utils.ERR_FORM_FORMAT);
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, sCell.getType());
    }

    @Test
    void testConstructor() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(5, sheet.width());
        assertEquals(5, sheet.height());
        for (int i = 0; i < sheet.width(); i++) {
            for (int j = 0; j < sheet.height(); j++) {
                assertEquals("", sheet.get(i, j).getData());
            }
        }
    }

    @Test
    void testSetAndGet() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "42");
        sheet.set(1, 1, "=A1+10");
        assertEquals("42.0", sheet.get(0, 0).getData());
        assertEquals("=A1+10", sheet.get(1, 1).getData());
    }

    @Test
    void testValue() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0+10");
        sheet.eval();
        assertEquals("5.0", sheet.value(0, 0));
        assertEquals("15.0", sheet.value(1, 1));
        sheet.set(0,0,"6");
        sheet.set(1,1,"=A0+10");
        sheet.eval();
        assertEquals("6.0", sheet.value(0, 0));
        assertEquals("16.0", sheet.value(1, 1));
    }

    @Test
    void testInvalidCellAccess() {
        Ex2Sheet sheet = new Ex2Sheet(4, 2);
        assertThrows(IndexOutOfBoundsException.class, () -> sheet.get(3, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> sheet.set(3, 3, "10"));
    }

    @Test
    void testEvalCycleDetection() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=B0+1");
        sheet.set(1, 0, "=A0");
        sheet.eval();
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0));
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(1, 0));
    }

    @Test
    void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A1+10");
        String fileName = "test_sheet.csv";
        sheet.save(fileName);

        Ex2Sheet loadedSheet = new Ex2Sheet(3, 3);
        loadedSheet.load(fileName);

        assertEquals("5.0", loadedSheet.get(0, 0).getData());
        assertEquals("=A1+10", loadedSheet.get(1, 1).getData());
        new File(fileName).delete(); // Clean up
    }

    @Test
    void testComputeFormulaWithValues() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(1, 1, "=A0+10");
        sheet.set(2, 2, "=B1*2");
        sheet.eval();
        assertEquals("15.0", sheet.value(1, 1));
        assertEquals("30.0", sheet.value(2, 2));
    }

    @Test
    void testDepthCalculation() {
        Ex2Sheet sheet = new Ex2Sheet(4, 4);
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "=A0+5");
        sheet.set(2, 0, "=B0*2");
        sheet.set(3 ,0, "=D0");
        int[][] depths = sheet.depth();
        sheet.eval();
        assertEquals(0, depths[0][0]);
        assertEquals(1, depths[1][0]);
        assertEquals(2, depths[2][0]);
        assertEquals(-1, depths[3][0]);
    }

    @Test
    void testIsIn() {
        Ex2Sheet sheet = new Ex2Sheet(2, 2);
        assertTrue(sheet.isIn(0, 0));
        assertFalse(sheet.isIn(2, 3));
    }

    @Test
    void testComputeForm (){
       Ex2Sheet sheet = new Ex2Sheet(2,5);
       assertEquals(6.0, sheet.computeForm("=2*3"));
       assertEquals(-7.0, sheet.computeForm("=-(4*2+(4+5))+(5+5)"));
       assertEquals(0.0, sheet.computeForm("=(2-2)*3"));
       assertEquals(-6.0, sheet.computeForm("=-(2-2+2)*3"));
       assertEquals(7.0, sheet.computeForm("=1+2*3"));
       assertEquals(5.0, sheet.computeForm("=-1+2*3"));
       assertEquals(-7.0, sheet.computeForm("=-1+2*3/(3-4)"));
       assertEquals(-5.0, sheet.computeForm("=+1+2*3/(3-4)"));
       assertEquals(9.0, sheet.computeForm("=-(1+2)*3/(3-4)"));
       assertEquals(-4.5, sheet.computeForm("=-(5+4)*(1-0.5)"));
       assertEquals(15.0, sheet.computeForm("=3*(2+3)"));
       assertEquals(-24.0, sheet.computeForm("=-(5+3)*3"));
       assertEquals(3.0, sheet.computeForm("=2-(3-4)"));
       assertEquals(12.0, sheet.computeForm("=2*(3+3)"));
       assertEquals(-12.0, sheet.computeForm("=-(2+2+2)*2"));
       assertEquals(0.0, sheet.computeForm("=(4-4)*5"));
       assertEquals(5.0, sheet.computeForm("=-(10/2)+10"));
       assertEquals(-5.0, sheet.computeForm("=5*(2-3)"));
       assertEquals(10.0, sheet.computeForm("=(1+2)*4-2"));
       assertEquals(-10.0, sheet.computeForm("=-(2+3)*2"));
       assertEquals(12.0, sheet.computeForm("=2+2*(3+2)"));
       assertEquals(-12.0, sheet.computeForm("=-(2+3)*2-2"));
       assertEquals(10.0, sheet.computeForm("=2*(3+3)-2"));
       assertEquals(-14.0, sheet.computeForm("=-(6-4)*7"));
       assertEquals(24.0, sheet.computeForm("=4*(2+2+2)"));
       assertEquals(24.0, sheet.computeForm("=24"));
       assertThrows(NumberFormatException.class, () -> sheet.computeForm("invalid"));
    }

    @Test
    void testSimpleFormula() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "=A0+5");
        assertEquals("10.0", sheet.computeFormulaWithValues("=A0+5"), "Simple addition should be calculated correctly.");
    }

    @Test
    void testDivisionByZero() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        assertEquals("Infinity", sheet.computeFormulaWithValues("=A0/0"), "Division by zero should return 'Infinity'.");
    }


    @Test
    void testInvalidFormula() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        assertEquals(Ex2Utils.ERR_FORM, sheet.computeFormulaWithValues("=A0+B#"), "Invalid formula should return 'ERR'.");
    }

    @Test
    void testCyclicDependency() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=A1+1");
        sheet.set(0, 1, "=A0+1");
        assertEquals(Ex2Utils.ERR_FORM, sheet.computeFormulaWithValues("=A0"), "Cyclic dependency should return 'ERR'.");
    }

    @Test
    void testMultipleDependencies() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "10");
        sheet.set(0, 2, "=A0+A1");
        sheet.eval();
        assertEquals("15.0", sheet.computeFormulaWithValues("=A0+A1"), "Formula with multiple dependencies should be calculated correctly.");
    }

    @Test
    void testNestedFormula() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "=A0+5");
        sheet.set(0, 2, "=A1*2");
        sheet.eval();
        assertEquals("20.0", sheet.computeFormulaWithValues("=A1*2"), "Nested formulas should be calculated correctly.");
    }

    @Test
    void testFormulaWithParentheses() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "10");
        assertEquals("30.0", sheet.computeFormulaWithValues("=(A0+A1)*2"), "Formula with parentheses should be calculated correctly.");
    }

    @Test
    void testComplexFormula() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "12");
        sheet.set(0, 1, "1.5");
        sheet.set(0, 2, "=A0*A1+20/(A0-2)");
        sheet.eval();
        assertEquals("20.0", sheet.computeFormulaWithValues("=A2"), "Complex formulas should be calculated correctly.");
    }

    @Test
    void testisValid(){
        CellEntry cellEntry = new CellEntry();
        assertEquals(false, cellEntry.isValid("=4*(2+2+2)"));
        assertEquals(false, cellEntry.isValid("0A"));
        assertEquals(true, cellEntry.isValid("A0"));
        assertEquals(true, cellEntry.isValid("a0"));
        assertEquals(false, cellEntry.isValid("a"));
        assertEquals(false, cellEntry.isValid("7"));
        assertEquals(true, cellEntry.isValid("g77"));
        assertEquals(false, cellEntry.isValid("5E"));
        assertEquals(false, cellEntry.isValid("F6="));
        assertEquals(false, cellEntry.isValid("F6&"));
        assertEquals(false, cellEntry.isValid(""));
        assertEquals(true, cellEntry.isValid("Y9"));
        assertEquals(false, cellEntry.isValid("Y319"));
        assertEquals(false, cellEntry.isValid("Aa2"));
        assertEquals(false, cellEntry.isValid("2a"));
        assertEquals(false, cellEntry.isValid(null));
    }

    @Test
    void testgetX(){
        CellEntry cellEntry = new CellEntry();
        assertEquals(0, cellEntry.getX("A2"));
        assertEquals(1, cellEntry.getX("B27"));
        assertEquals(25, cellEntry.getX("Z27"));
        assertEquals(-1, cellEntry.getX("AA27"));
        assertEquals(-1, cellEntry.getX("AAA27"));
        assertEquals(3, cellEntry.getX("d4"));
    }

    @Test
    void testgetY(){
        CellEntry cellEntry = new CellEntry();
        assertEquals(2, cellEntry.getY("A2"));
        assertEquals(27, cellEntry.getY("B27"));
        assertEquals(27, cellEntry.getY("Z27"));
        assertEquals(-1, cellEntry.getY("AA27"));
        assertEquals(-1, cellEntry.getY("AAA27"));
        assertEquals(4, cellEntry.getY("d4"));
        assertEquals(1, cellEntry.getY("a1"));
        assertEquals(99, cellEntry.getY("j99"));
    }


    @Test
    void testcomputeFormulaWithValues(){
        Ex2Sheet ex2Sheet = new Ex2Sheet(2,5);

    }

    @Test
    public void testDepth_EmptyTable() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3); // טבלה ריקה 3x3

        int[][] result = sheet.depth();
        int[][] expected = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };

        assertArrayEquals(expected, result);
    }

    @Test
    public void testDepth_SimpleDependencies() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        SCell sCell = new SCell("");
        // הגדרת תאים עם תלות פשוטה
        sheet.set(0, 0, "1");  //  A0
        sheet.set(0, 1, "=A0+2"); // A1
        sheet.set(0, 2, "=A1+3"); // A2

        int[][] result = sheet.depth();
        int[][] expected = {
                {0, 1, 2},
                {0, 0, 0},
                {0, 0, 0}
        };

        assertArrayEquals(expected, result);
    }

    @Test
    public void testDepth_ComplexDependencies() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // הגדרת תאים עם תלות מורכבת
        sheet.set(0, 0, "=1+1");   // A0
        sheet.set(0, 1, "=A0+2");  // A1
        sheet.set(1, 0, "=A1+3");  // B0
        sheet.set(1, 1, "=A0+B0"); // B1

        int[][] result = sheet.depth();
        int[][] expected = {
                {0, 1, 0},
                {2, 3, 0},
                {0, 0, 0}
        };

        assertArrayEquals(expected, result);
    }

    @Test
    public void testDepth_Cycle() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // הגדרת תאים שיוצרים מעגל
        sheet.set(0, 0, "=B0"); // A0
        sheet.set(1, 0, "=A0"); // B0
        sheet.set(1,2, "=B3"); // B3

        int[][] result = sheet.depth();
        int[][] expected = {
                {-1, 0, 0},
                {-1, 0, -1},
                {0, 0, 0}
        };

        assertArrayEquals(expected, result, "Cycle detection failed");
    }

    @Test
    void testValueForNumberCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(2, 3, "42");
        assertEquals("42.0", sheet.value(2, 3), "Number printed as double.");
        sheet.set(0,0,".2");
        assertEquals("0.2", sheet.value(0, 0), "Number printed as double.");

    }

    @Test
    void testValueForTextCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(1, 1, "Hello");
        assertEquals("Hello", sheet.value(1, 1), "The value of a text cell should match its content.");
    }

    @Test
    void testValueForFormulaCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "5");
        sheet.set(0, 1, "10");
        sheet.set(0, 2, "=A0+A1");
        sheet.eval();
        assertEquals("15.0", sheet.value(0, 2), "The value of a formula cell should be the calculated result.");
    }


    @Test
    void testValueForEmptyCell() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals("",sheet.value(0, 0), "The value of an empty cell should be empty");
    }

    @Test
    void testValueForInvalidFormula() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=A1+B2"); // Invalid because A1 and B2 are undefined
        sheet.eval();
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0), "An invalid formula should return an error string.");
    }

    @Test
    void testValueForCyclicDependency() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=A0+1");
        sheet.set(0, 1, "=A1+4");
        sheet.eval();
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0), "A cyclic dependency should return a cycle error.");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 1), "A cyclic dependency should return a cycle error.");
    }


    @Test
    void testMoreValueForCyclicDependency() {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "=A2");
        sheet.set(0, 1, "=A0");
        sheet.set(0, 2, "=A1");
        sheet.eval();
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 0), "A cyclic dependency should return a cycle error.");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 1), "A cyclic dependency should return a cycle error.");
        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0, 2), "A cyclic dependency should return a cycle error.");
    }

}



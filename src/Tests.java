import org.junit.Assert.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @Test
    void testisText() {
        SCell sCell = new SCell("1");
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
    }

    @Test
    void testComputeForm (){
       Ex2Sheet ex2Sheet = new Ex2Sheet(2,5);
       assertEquals(6.0, ex2Sheet.computeForm("=2*3"));
       assertEquals(-7.0, ex2Sheet.computeForm("=-(4*2+(4+5))+(5+5)"));
       assertEquals(0.0, ex2Sheet.computeForm("=(2-2)*3"));
       assertEquals(-6.0, ex2Sheet.computeForm("=-(2-2+2)*3"));
       assertEquals(7.0, ex2Sheet.computeForm("=1+2*3"));
       assertEquals(5.0, ex2Sheet.computeForm("=-1+2*3"));
       assertEquals(-7.0, ex2Sheet.computeForm("=-1+2*3/(3-4)"));
       assertEquals(-5.0, ex2Sheet.computeForm("=+1+2*3/(3-4)"));
       assertEquals(9.0, ex2Sheet.computeForm("=-(1+2)*3/(3-4)"));
       assertEquals(-4.5, ex2Sheet.computeForm("=-(5+4)*(1-0.5)"));
       assertEquals(15.0, ex2Sheet.computeForm("=3*(2+3)"));
       assertEquals(-24.0, ex2Sheet.computeForm("=-(5+3)*3"));
       assertEquals(3.0, ex2Sheet.computeForm("=2-(3-4)"));
       assertEquals(12.0, ex2Sheet.computeForm("=2*(3+3)"));
       assertEquals(-12.0, ex2Sheet.computeForm("=-(2+2+2)*2"));
       assertEquals(0.0, ex2Sheet.computeForm("=(4-4)*5"));
       assertEquals(5.0, ex2Sheet.computeForm("=-(10/2)+10"));
       assertEquals(-5.0, ex2Sheet.computeForm("=5*(2-3)"));
       assertEquals(10.0, ex2Sheet.computeForm("=(1+2)*4-2"));
       assertEquals(-10.0, ex2Sheet.computeForm("=-(2+3)*2"));
       assertEquals(12.0, ex2Sheet.computeForm("=2+2*(3+2)"));
       assertEquals(-12.0, ex2Sheet.computeForm("=-(2+3)*2-2"));
       assertEquals(10.0, ex2Sheet.computeForm("=2*(3+3)-2"));
       assertEquals(-14.0, ex2Sheet.computeForm("=-(6-4)*7"));
       assertEquals(24.0, ex2Sheet.computeForm("=4*(2+2+2)"));
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
}

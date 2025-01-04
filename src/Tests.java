import org.junit.Assert;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {
    @Test
    void testIsForm() {
        SCell sCell = new SCell("=1");
        Assert.assertEquals(true, sCell.isForm("=1"));

    }

    @Test
    void testisText() {
        SCell sCell = new SCell("1");
        Assert.assertEquals(false, sCell.isText("1"));
        Assert.assertEquals(false, sCell.isText("=1"));
        Assert.assertEquals(true, sCell.isText("fh"));
        Assert.assertEquals(true, sCell.isText("88A"));
    }


    @Test
    void testisNumber() {
        SCell sCell = new SCell("1");
        Assert.assertEquals(true, sCell.isNumber("1"));
        Assert.assertEquals(false, sCell.isNumber(" "));
        Assert.assertEquals(false, sCell.isNumber(""));
        Assert.assertEquals(false, sCell.isNumber("T"));
        Assert.assertEquals(true, sCell.isNumber("1.2"));
        Assert.assertEquals(true, sCell.isNumber("-1.2"));
    }

    @Test
    void testisForm () {
        SCell sCell = new SCell("1");
        Assert.assertEquals(true, sCell.isForm("=1"));
        Assert.assertEquals(false, sCell.isForm("fh"));
        Assert.assertEquals(true, sCell.isForm("=(4*2+5)+5*4"));
        Assert.assertEquals(true, sCell.isForm("=(4*2+(4+5))"));
        Assert.assertEquals(true, sCell.isForm("=(4*2)+(5+4)"));
        Assert.assertEquals(true, sCell.isForm("=(4*2+(4+5))+(5+5)"));
        Assert.assertEquals(false, sCell.isForm("=4*2+(4+5))+(5+5)"));
        Assert.assertEquals(false, sCell.isForm("=(4*2+4+5))+(5+5)"));
        Assert.assertEquals(true, sCell.isForm("=(4*2+(+5))+(5+5)"));
        Assert.assertEquals(true, sCell.isForm("=(4*2-(+5))+(-5)"));
        Assert.assertEquals(false, sCell.isForm("=(4**2+(4+5))+(5+5)"));
        Assert.assertEquals(true, sCell.isForm("=-(4+5)"));
        Assert.assertEquals(true, sCell.isForm("=-(4*2+(4+5))+(5+5)"));
        Assert.assertEquals(false, sCell.isForm("=(4*2a+(4+5))+(5+5)"));
        Assert.assertEquals(true, sCell.isForm("=(4*2+ 4+5)+(5+5)"));
        Assert.assertEquals(true, sCell.isForm("=+(4*2+4+5)+(5+5)"));
        Assert.assertEquals(true, sCell.isForm("=+(4*2+4+5)+(-5+5)"));
        Assert.assertEquals(false, sCell.isForm("=*(4)"));
        Assert.assertEquals(false, sCell.isForm("=*(4*2-(+5))+(-5)"));
    }
}


// Add your documentation below:

import java.util.ArrayList;
import java.util.List;

public class SCell implements Cell {

    private String line;
    private int type;
    private int Order;
    // Add your code here

    public SCell(String s) {
        setData(s);
    }

    public int whatType(String line) {
        if (isText(line)) {
            type = Ex2Utils.TEXT;
        } else if (isNumber(line)) {
            type = Ex2Utils.NUMBER;
        } else if (isForm(line)) {
            type = Ex2Utils.FORM;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
        }
        return type;
    }

    CellEntry entry = new CellEntry();

    @Override
    public int getOrder(String ord) {
        Order = -2;

        if (ord == null || ord.isEmpty()) {
            Order = 0;
        }
        else if (isNumber(ord) || isText(ord)) {
            Order = 0;
        }
        else if (isForm(ord)) {
            List<String> dependentCells = findDependentCells(ord);
            int minOrder = 0;
            int maxOrder = 0;
            for (String dependentCell : dependentCells) {
                if (entry.isValid(dependentCell)) {
                    // חישוב הסדר עבור כל תא תלוי
                    int dependentOrder = getOrder(dependentCell);
                    minOrder = Math.min(minOrder,dependentOrder);
                    if(dependentOrder==-2){
                        return Ex2Utils.ERR_CYCLE_FORM;
                    }
                    maxOrder = Math.max(maxOrder, dependentOrder);
                } else {
                    throw new IllegalArgumentException("Invalid dependent cell: " + dependentCell);
                }
            }
            return maxOrder + 1;
        }
        return Order;

        //אם הסטרינג של התא מכיל בתוכו תא אחר אז אז הסדר שווה לסדר של התא שבפנים ועוד 1
        //אם הסטרינג של התא מכיל בתוכו כמה תאים אז ניקח את התא שהסדר שלו הוא המקסימלי מבינהם ונוסיף לו אחד

    }

    private List<String> findDependentCells(String ord) {
        List<String> dependentCells = new ArrayList<>();

        // מפצלים את הסטרינג לפי "=" "+", "-", "*", "/", ")", "(" בלבד
        String[] tokens = ord.split("[=+\\-*/)(]+");

        for (String token : tokens) {
            if (entry.isValid(token)) {
                dependentCells.add(token);
            }
        }
        return dependentCells;
    }


    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        // Add your code here
        this.line = s;
        this.type = whatType(s);
        this.Order = getOrder(s);
    }

    @Override
    public String getData() {//מה שאני קולטת מהמתא
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }

    @Override
    public void setOrder(int t) {
        this.Order = t;

    }

    public boolean isNumber(String text) {

        try{
            Double.parseDouble(text);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }



    public boolean isText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
       // boolean result = true;
        if (text.charAt(0)=='=' || isNumber(text)) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isForm(String form) {

        if (form == null || form.isEmpty()) {
            return false;
        }

        //The first char must be "="
        if (!form.startsWith("=")) {
            return false;
        }

        //In the case of an operation that comes immediately after an operation
        for (int i = 1; i < form.length(); i++) {
            if ("*/+-".indexOf(form.charAt(i)) != -1 && "*/+-".indexOf(form.charAt(i - 1)) != -1) {
                return false;
            }
        }

        // Remove the '=' at the start
        form = form.substring(1).trim();

        // If text is empty after removing '=', return false
        if (form.isEmpty()) {
            return false;
        }

        //If text is a number then it is a formula
        CellEntry entry = new CellEntry();

        if (isNumber(form)||entry.isValid(form)) {
            return true;
        }

        //In the case of '-' or '+' come immediately after '='
        if (form.charAt(0)=='-'|| form.charAt(0)=='+') {
            return isForm('=' + form.substring(1));
        }

        int counter = 0;
        //Search for one of the actions +-*/
        for (int i = 0; i < form.length(); i++) {
            char c = form.charAt(i);

            //Splitting is not allowed inside parentheses, so we will define a counter
            if (c == '(') {
                counter++;
            } else if (c == ')') {
                counter--;
            }


            //If there is an operation not in parentheses, split it into two strings.
            if ((c == '+' || c == '-' || c == '*' || c == '/') && counter == 0) {
                String right = form.substring(0, i).trim();
                String left = form.substring(i + 1).trim();

                //Check whether the strings before and after the operation isForm.
                return isForm('=' + right) && isForm('=' + left);
            }
        }

        //In the case of ( ), we check whether the expression inside is a formula.
        if (form.charAt(0)== '(' && form.charAt(form.length()-1)==')') {
                return isForm('=' + form.substring(1, form.length()-1));
        }

        //Otherwise it is not a formula.
        return false;
    }

}

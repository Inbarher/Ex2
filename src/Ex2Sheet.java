import java.io.IOException;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    //Holds all cells in the sheet
    private Cell[][] table;
    //Holds computed values sheet
    private Double [][] values;
    // Add your code here

    // ///////////////////
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell("");
            }
        }
        eval();
    }
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        // Add your code here קליטת המחרוזת מהמשתמש מה שהוא מקליד

        Cell c = get(x,y);
        if(c!=null) {ans = c.toString();}

        /////////////////////
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        // Add your code hereA1==01

        /////////////////////
        return ans;
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
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
        // Add your code here התא שהמשתמש בוחר קליטת מהמשתמש

        /////////////////////
    }
    @Override
    public void eval() {
        int[][] dd = depth();
        // Add your code here

        // ///////////////////
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx>=0 && yy>=0;
        // Add your code here A-I //1-16

        /////////////////////
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        // Add your code here

        // ///////////////////
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public void save(String fileName) throws IOException {
        // Add your code here

        /////////////////////
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if(get(x,y)!=null) {ans = get(x,y).toString();}
        Cell cell = get(x, y);
        if (cell != null && cell.getType() == Ex2Utils.FORM) {
            try {
                return String.valueOf(computeForm(cell.getData()));
            } catch (Exception e) {
                return Ex2Utils.ERR_FORM;
            }
        }
        // Add your code hereחישוב ערך התא מחזיר מספר
        /////////////////////
        return ans;
        }




    public Double computeForm(String form) {
        // In order to calculate, remove the char '='.
        if (form.startsWith("=")) {
            form = form.substring(1).trim();
        }

        // Handle the case where the first character is '-' or '+'
        if (form.startsWith("+")) {
            return computeForm(form.substring(1));
        }
        //Finding the end of the minus's range of influence
        if (form.startsWith("-") && form.length()>1 && form.charAt(1) == '(') {
            int counter = 1;
            int endIndex = form.length();

            //Finding the first plus or minus outside the parentheses
            for (int i = 2; i < form.length(); i++) {
                if (form.charAt(i) == '(') {
                    counter++;
                } else if (form.charAt(i) == ')') {
                    counter--;
                } else if ((form.charAt(i) == '+' || form.charAt(i) == '-') && counter == 0) {
                    endIndex = i;
                    break;
                }
            }

            //Calculate the full influence of minus
            double insideValue = computeForm(form.substring(1, endIndex));

            if (endIndex < form.length()) {
                return -insideValue + computeForm(form.substring(endIndex));
            } else {
                return -insideValue;
            }
        }


        //Search and calculate '+' and '-'
        int counter = 0;
        for (int i = form.length() - 1; i >= 0; i--) {
            char c = form.charAt(i);

            if (c == ')') {
                counter++;
            } else if (c == '(') {
                counter--;
            }

            //Only if the operator out of parentheses
            if (counter == 0 && (c == '+' || c == '-') && i!=0) {
                String leftPart = form.substring(0, i);
                String rightPart = form.substring(i + 1);

                if (c == '+') {
                    return computeForm(leftPart) + computeForm(rightPart);
                } else { // c == '-'
                    return computeForm(leftPart) - computeForm(rightPart);
                }
            }
        }

        //Search and calculate '*' and '/'
        counter = 0;
        for (int i = form.length() - 1; i >= 0; i--) {
            char c = form.charAt(i);

            if (c == ')') {
                counter++;
            } else if (c == '(') {
                counter--;
            }

            //Only if the operator out of parentheses
            if (counter == 0 && (c == '*' || c == '/')) {
                String leftPart = form.substring(0, i);
                String rightPart = form.substring(i + 1);

                if (c == '*') {
                    return computeForm(leftPart) * computeForm(rightPart);
                } else { // c == '/'
                    double denominator = computeForm(rightPart);
                    if (denominator == 0) {
                        throw new ArithmeticException("Infinity");
                    }
                    return computeForm(leftPart) / denominator;
                }
            }
        }

        //In the case of '(', ')'
        if (form.startsWith("(") && form.endsWith(")") ) {
            return computeForm(form.substring(1, form.length() - 1));
        }

        //Otherwise the expression is a number
        return Double.parseDouble(form);
    }

}

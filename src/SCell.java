
// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;
    // Add your code here

    public SCell(String s) {
        line = s;
        type = whatType(line);
      //  setData(s);
    }
    public int whatType(String line){
        if (isText(line)){
            type = Ex2Utils.TEXT;
        }
        else if (isNumber(line)){
            type = Ex2Utils.NUMBER;
        }
        else if (isForm(line)){
            type = Ex2Utils.FORM;
        }
        else {
            type = Ex2Utils.ERR;
        }
        return type;
    }

    @Override
    public int getOrder() {
        // Add your code here

        return 0;
        // ///////////////////
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        // Add your code here
        line = s;
        type = whatType(line);
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
        type = t;
    }

    @Override
    public void setOrder(int t) {
        // Add your code here

    }

    public boolean isNumber(String text) {
        boolean result = false;
        try{
            Double.parseDouble(text);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }



    public boolean isText(String text) {
       // boolean result = true;
        if (text.charAt(0)=='=' || isNumber(text)) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isForm(String text) {

        if (text == null || text.isEmpty()) {
            return false;
        }


        //The first char must be "="
        if (!text.startsWith("=")) {
            return false;
        }

        //In the case of an operation that comes immediately after an operation
        for (int i = 1; i < text.length(); i++) {
            if ("*/+-".indexOf(text.charAt(i)) != -1 && "*/+-".indexOf(text.charAt(i - 1)) != -1) {
                return false;
            }
        }

        // Remove the '=' at the start
        text = text.substring(1).trim();

        // If text is empty after removing '=', return false
        if (text.isEmpty()) {
            return false;
        }

        //If text is a number then it is a formula
        if (isNumber(text)) {
            return true;
        }

        //In the case of '-' or '+' come immediately after '='
        if (text.charAt(0)=='-'||text.charAt(0)=='+') {
            return isForm('=' + text.substring(1));
        }

        int counter = 0;
        //Search for one of the actions +-*/
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            //Splitting is not allowed inside parentheses, so we will define a counter
            if (c == '(') {
                counter++;
            } else if (c == ')') {
                counter--;
            }


            //If there is an operation not in parentheses, split it into two strings.
            if ((c == '+' || c == '-' || c == '*' || c == '/') && counter == 0) {
                String right = text.substring(0, i).trim();
                String left = text.substring(i + 1).trim();

                //Check whether the strings before and after the operation isForm.
                return isForm('=' + right) && isForm('=' + left);
            }
        }

        //In the case of ( ), we check whether the expression inside is a formula.
        if (text.charAt(0)== '(' && text.charAt(text.length()-1)==')') {
                return isForm('=' + text.substring(1, text.length()-1));
        }

        //Otherwise it is not a formula.
        return false;
    }

}

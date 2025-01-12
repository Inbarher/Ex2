import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
    //הערך שצריך להיות מוצג בתא. אם זה נוסחה אז החישוב הסופי
    @Override
    public String value(int x, int y) {
        Cell c = get(x, y);
        List<String> dependencies = findDependentCells(c.getData());
        for (String dep : dependencies) {
            int depX = new CellEntry().getX(dep);
            int depY = new CellEntry().getY(dep);
            if (get(depX, depY).getData().equals("")) {
                c.setType(Ex2Utils.ERR_FORM_FORMAT);
                return Ex2Utils.ERR_FORM;
            }
        }
        if (c != null) {
            return eval(x,y);
        }
        return null;
    }

    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) {
            throw new IndexOutOfBoundsException("Cell coordinates are out of bounds.");
        }
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
      CellEntry entry = new CellEntry();
        if (entry.isValid(cords)){
            int x = entry.getX(cords);
            int y = entry.getY(cords);
            return get(x,y);
        }
        Cell ans = null;

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
        if (!isIn(x,y)) {
            throw new IndexOutOfBoundsException("Cell coordinates are out of the table's bounds.");
        }
        Cell c = new SCell(s);
        table[x][y] = c;
    }

    @Override
    public void eval() {
        int[][] dd = depth();

        int maxDepth = 0;
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if (dd [i][j] > maxDepth) {
                    maxDepth = dd[i][j];
                }
            }
        }
        values = new Double[width()][height()];

        for (int i = 0; i < maxDepth; i++) {
            for (int j = 0; j < width(); j++) {
                for (int k = 0; k < height(); k++) {
                    if (dd[j][k]==0){
                        String ans = null;
                        Cell c = get(j,k);
                        if (c != null){
                            ans = c.toString();
                        }
                        if (c.getType() == Ex2Utils.NUMBER) {
                            values [j][k] = Double.parseDouble(ans);
                        }
                        else if (c.getType() == Ex2Utils.FORM){
                            try {
                                values[j][k] = computeFormWithDependencies(ans);
                            }
                            catch (Exception e){
                                   // c.setData(Ex2Utils.ERR_CYCLE);
//                                c.setType(Ex2Utils.ERR_CYCLE_FORM);
//                                List<String> dependencies = findDependentCells(ans);
//                                for (String dep : dependencies) {
//                                    int depX = new CellEntry().getX(dep);
//                                    int depY = new CellEntry().getY(dep);
//                                    //if (isIn(depX, depY))
//                                        get(depX,depY).setType(Ex2Utils.ERR_CYCLE_FORM);
//                                }

                                    //values[j][k] = null;

                            }
                        }

                    }
                }
            }
        }

        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if(dd[i][j]==-1){
                    if(get(i,j).toString().isEmpty()) {
                        //  get(i,j).setData(Ex2Utils.ERR_CYCLE);

                        get(i,j).setType(Ex2Utils.ERR_CYCLE_FORM);
                        List<String> dependencies = findDependentCells(get(i,j).getData());
                        for (String dep : dependencies) {
                            int depX = new CellEntry().getX(dep);
                            int depY = new CellEntry().getY(dep);

                                get(depX,depY).setType(Ex2Utils.ERR_CYCLE_FORM);
                        }
                        markCyclicError(i,j);
                    }
                }
            }
        }
    }

    private void markCyclicError(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null || cell.getType() != Ex2Utils.ERR_CYCLE_FORM) {
            return; // Exit if the cell is not marked with a cyclic error
        }

        // Find dependent cells
        List<String> dependencies = findDependentCells(cell.getData());
        for (String dep : dependencies) {
            CellEntry entry = new CellEntry();
            int depX = entry.getX(dep);
            int depY = entry.getY(dep);

            // Check if the dependent cell is within bounds
            if (isIn(depX, depY)) {
                Cell dependentCell = get(depX, depY);

                // Mark the dependent cell with a cyclic error if not already marked
                if (dependentCell != null && dependentCell.getType() != Ex2Utils.ERR_CYCLE_FORM) {
                    dependentCell.setType(Ex2Utils.ERR_CYCLE_FORM);
                    markCyclicError(depX, depY); // Recursively mark all dependent cells
                }
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx >= 0 && xx < width() && yy >= 0 && yy < height();

        /////////////////////
        return ans;
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        int depth = 0, count = 0, max = width() * height();
        boolean flagC = true;

        // אתחול המטריצה לערכי -1
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -1;
            }
        }

        // לולאה ראשית לחישוב עומקי התאים
        while (count < max && flagC) {
            flagC = false;

            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    if (canBeComputedNow(x, y, ans)) { // בדיקה אם ניתן לחשב תא זה
                        ans[x][y] = depth;
                        count++;
                        flagC = true;
                    }
                }
            }

            depth++;
        }

        return ans;
    }

    public  Double computeFormWithDependencies(String formula) {
        List<String> dependencies = findDependentCells(formula);
        for (String dep : dependencies) {
            int depX = new CellEntry().getX(dep);
            int depY = new CellEntry().getY(dep);
            if (depX >= 0 && depY >= 0 && isIn(depX, depY)) {
                return values[depX][depY]; // השתמש בערך המחושב
            }

        }
        return computeForm(formula);
    }


    //  פונקציה הבודקת אם ניתן לחשב את התא בהתחשב בתלות בתאים אחרים

    private boolean canBeComputedNow(int x, int y, int[][] ans) {

        Cell cell = get(x, y);

        if ((cell == null || cell.getType() != Ex2Utils.FORM)) {
            return true; // תא שאינו נוסחה ניתן לחשב מיד
        }



        // קבלת רשימת התאים עליהם תלוי התא הנוכחי
        String formula = cell.getData();
        //SCell tempCell = new SCell(formula);
        List<String> dependencies = findDependentCells(formula);

        CellEntry entry = new CellEntry();
        for (String dep : dependencies) {
            int depX = entry.getX(dep), depY = entry.getY(dep);

            if (!isIn(depX, depY) || ans[depX][depY] == -1)  {
                return false; // לא ניתן לחשב אם אחד התאים עליהם תלוי התא טרם חושב
            }

        }

        return true;
    }

    @Override
    public void load(String fileName) throws IOException {

        // איפוס כל התאים לפני טעינת המידע מחדש
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                set(i, j, ""); // אפס את התאים עם ערך ריק או ברירת מחדל
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // פיצול השורה לפי פסיקים
                    String[] parts = line.split(",");
                    if (parts.length != 3) {
                        throw new IOException("Invalid file format: each line must contain row,col,value.");
                    }

                    // פירוק השדות
                    int row = Integer.parseInt(parts[0]); // אינדקס שורה
                    int col = Integer.parseInt(parts[1]); // אינדקס עמודה
                    String value = parts[2];             // הערך של התא

                    // בדיקת תקינות הקואורדינטות
                    if (!isIn(row, col)) {
                        throw new IndexOutOfBoundsException("Cell coordinates out of bounds in file: " + row + "," + col);
                    }

                    // עדכון התא בגיליון
                    set(row, col, value);
                }
            }

    }

    @Override
    public void save(String fileName) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int row = 0; row < height(); row++) {
                for (int col = 0; col < width(); col++) {
                    Cell cell = get(row, col);

                    if (cell != null && !cell.getData().isEmpty()) {
                        // שמירת ה-value, שורת המערך, ועמודת המערך
                        String line =  row + "," + col + "," + cell.getData();
                        writer.write(line);
                        writer.newLine(); // מעבר לשורה חדשה
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if(get(x,y)!=null) {
            ans = get(x,y).toString();
        }
        Cell cell = get(x, y);
        if (cell != null && cell.getType() == Ex2Utils.ERR_CYCLE_FORM){
//            List<String> dependencies = findDependentCells(cell.getData());
//            for (String dep : dependencies) {
//                int depX = new CellEntry().getX(dep);
//                int depY = new CellEntry().getY(dep);
//                if (depX >= 0 && depY >= 0 && isIn(depX, depY))
//                    get(depX,depY).setType(Ex2Utils.ERR_CYCLE_FORM);
//            }
            markCyclicError(x, y);
            return Ex2Utils.ERR_CYCLE;

//        }
//        else if (cell != null && cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
//            return Ex2Utils.ERR_FORM;
        }
        else if (cell != null && cell.getType() == Ex2Utils.NUMBER) {
            return String.valueOf(cell.getData());
        }
        else if (cell != null && cell.getType() == Ex2Utils.FORM) {
            try {
                return String.valueOf(computeFormulaWithValues(cell.getData()));
            }
            catch (Exception e) {
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                return Ex2Utils.ERR_FORM;
            }
        }
        else if (cell != null && cell.getType() == Ex2Utils.TEXT) {
            return ans;
        }
        else if (cell != null && cell.getData() == "") {
            return ans;
        }
        else if (cell != null && cell.getType() == Ex2Utils.ERR_FORM_FORMAT){
            return Ex2Utils.ERR_FORM;
        }
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

    public String computeFormulaWithValues(String formula) {
//        if (!formula.startsWith("=")) {
//            return formula; // אם לא נוסחה, פשוט להחזיר את התוכן המקורי
//        }

        // הסרת הסימן '=' מהנוסחה
        formula = formula.substring(1).trim();

        // זיהוי התאים התלויים בנוסחה
        //SCell tempCell = new SCell(formula);
        List<String> dependencies = findDependentCells(formula);

        // החלפת כל שם תא בערך שלו
        for (String dep : dependencies) {
            CellEntry entry = new CellEntry();
            int depX = entry.getX(dep);
            int depY = entry.getY(dep);

            if (isIn(depX, depY)) {
                String value = value(depX, depY); // קבלת הערך של התא התלוי
                if (value == null || value.isEmpty()) {
                    value = ""; // ברירת מחדל אם התא ריק
                }
                formula = formula.replace(dep, value);
            } else {
                throw new IllegalArgumentException("Dependent cell out of bounds: " + dep);
            }
        }

        // חישוב הערך של הנוסחה לאחר ההחלפה

        Double result = computeForm(formula);
        return result.toString();
    }

    public List<String> findDependentCells(String ord) {
        List<String> dependentCells = new ArrayList<>();

        // מפצלים את הסטרינג לפי "=" "+", "-", "*", "/", ")", "(" בלבד
        String[] tokens = ord.split("[=+\\-*/)(]+");
        CellEntry entry = new CellEntry();
        for (String token : tokens) {
            if (entry.isValid(token)) {
                dependentCells.add(token);
            }
        }
        return dependentCells;
    }
}

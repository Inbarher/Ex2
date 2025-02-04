import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ex2Sheet implements Sheet {
    //Holds all cells in the sheet
    private Cell[][] table;



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

    //The value that should be displayed in the cell. If it is a formula then the final calculation
    @Override
    public String value(int x, int y) {
        Cell c = get(x, y);
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
        get(x,y).setLine(s);
    }

    //A function that calculates the value of any cell that is a formula By depth
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
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                if(dd[i][j]==-1){
                    if(!get(i,j).toString().isEmpty()) {
                        get(i,j).setType(Ex2Utils.ERR_CYCLE_FORM);
                    }
                }
            }
        }


        for (int i = 0; i <= maxDepth; i++) {
            for (int j = 0; j < width(); j++) {
                for (int k = 0; k < height(); k++) {
                    if (dd[j][k]==i){
                        String ans = null;
                        Cell c = get(j,k);
                        if (c != null){
                            ans = c.toString();
                        }
                        if (c.getType() == Ex2Utils.NUMBER) {
                            c.setValue(Double.parseDouble(ans));
                        }
                        else if (c.getType() == Ex2Utils.FORM){
                            try {
                                c.setValue(Double.valueOf(computeFormulaWithValues(ans)));
                            }
                            catch (Exception e){
                                c.setType(Ex2Utils.ERR_FORM_FORMAT);
                            }
                        }

                    }
                }
            }
        }

    }

    //Calculates the value of a formula, by placing the values of the cells on which it depends
    public String computeFormulaWithValues(String formula) {
        try {
            formula = formula.substring(1).trim();
            List<String> dependencies = findDependentCells(formula);

            for (String dep : dependencies) {
                CellEntry entry = new CellEntry();
                int depX = entry.getX(dep);
                int depY = entry.getY(dep);

                if (isIn(depX, depY)) {
                    String a = String.valueOf(get(depX, depY).getValue());
                    formula = formula.replace(dep, a);
                }
                else {
                    return Ex2Utils.ERR_FORM;
                }
            }

            Double result = computeForm(formula);

            if (result.isInfinite()) return "Infinity";
            return result.toString();
        } catch (ArithmeticException e) {
            return "Infinity"; // חלוקה באפס
        } catch (Exception e) {
            return Ex2Utils.ERR_FORM; // שגיאה כללית
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        boolean ans = xx >= 0 && xx < width() && yy >= 0 && yy < height();

        return ans;
    }
    // Checks the depth of each cell.
    // Depth function checks for circular dependence.
    // When the depth of a cell remains -1 then we know that the cell type is a cycle error.
    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];

        // Initialize the matrix to values of -1
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -1;
            }
        }
        int depth = 0, count = 0, max = width() * height();
        boolean flagC = true;

        // Main loop for calculating cell depths
        while (count < max && flagC) {
            flagC = false;

            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    if (ans[x][y] == -1 && canBeComputedNow(x, y, ans, depth)) {
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


    // Checks whether the cell can be calculated considering dependencies on other cells.
    private boolean canBeComputedNow(int x, int y, int[][] ans, int depth) {

        Cell cell = get(x, y);
        String line = cell.getData();

        cell.setData();
        //A cell that is not a formula can be calculated immediately.
        if (line == null || cell.getType() != Ex2Utils.FORM) {
            return true;
        }

        // Get the list of cells on which the current cell depends
        List<String> dependencies = findDependentCells(line);
        if (dependencies.isEmpty()) {
            return true;
        }
        CellEntry entry = new CellEntry();
        for (String dep : dependencies) {
            int depX = entry.getX(dep), depY = entry.getY(dep);

            //Cannot be calculated if one of the cells on which the cell depends has not yet been calculated.
            if (!isIn(depX, depY) || ans[depX][depY] == -1 || ans[depX][depY] == depth)  {
                return false;
            }
        }
        return true;
    }

    @Override
    public void load(String fileName) throws IOException {

        // Reset all cells before reloading information
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                set(i, j, ""); // Reset cells with a blank or default value
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
              String line;
              while ((line = reader.readLine()) != null) {
                  // Split the line by commas
                  String[] parts = line.split(",");
                  if (parts.length != 3) {
                      throw new IOException("Invalid file format: each line must contain row,col,value.");
                  }

                  // split
                  int row = Integer.parseInt(parts[0]); // Row index
                  int col = Integer.parseInt(parts[1]); // Column index
                  String value = parts[2];             // Value of index

                  // Checking the correctness of the coordinates
                  if (!isIn(row, col)) {
                      throw new IndexOutOfBoundsException("Cell coordinates out of bounds in file: " + row + "," + col);
                  }

                  // Update the cell in the sheet
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

                        String line =  row + "," + col + "," + cell.getData();
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        eval();
        Cell cell = get(x, y);

        if (cell != null && cell.getType() == Ex2Utils.FORM) {
            return String.valueOf(cell.getValue());
        }
        if (cell != null && cell.getData() == "") {
            return cell.toString();
        }
        if (cell != null && cell.getType() == Ex2Utils.ERR_FORM_FORMAT){
            return Ex2Utils.ERR_FORM;
        }
        if (cell != null && cell.getType() == Ex2Utils.ERR_CYCLE_FORM){
            return Ex2Utils.ERR_CYCLE;
        }
        return cell.toString();
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




    //Finds the cells that the cell depends on
    public List<String> findDependentCells(String ord) {
        List<String> dependentCells = new ArrayList<>();

        // Split the string by "=", "+", "-", "*", "/", ")", "(" only
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

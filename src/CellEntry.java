// Add your documentation below:

public class CellEntry  implements Index2D {
    private String cords;
    private int x;
    private int y;

    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
        this.cords = getCords(x,y);
    }

    public String toString() {
        return this.cords;
    }

    public CellEntry() {
        this.cords = "";
    }


    @Override
    public boolean isValid(String s) {
        this.cords = s;
        if (s == null || s.isEmpty()) {
            return false;
        }
        //Number of char in the string must be between 2 and 3.
        if (s.length() < 2 || s.length() > 3) {
            return false;
        }
        String ABC = String.join("", Ex2Utils.ABC);
        char c = s.charAt(0);
        //If the first char is a lowercase letter, we convert it to an uppercase letter.
        if (Character.isLowerCase(c)) {
            c = Character.toUpperCase(c);
        }
        //If the first char is not in ABC.
        if (ABC.indexOf(c) == -1) {
            return false;
        }
        //Check that the rest of the string is int.
        String num = s.substring(1);
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int getX(String A0) {
        if (isValid(A0)) {
            char c = A0.charAt(0);

            //Convert it to an uppercase letter.
            if (Character.isLowerCase(c)) {
                c = Character.toUpperCase(c);
            }
            String ABC = String.join("", Ex2Utils.ABC);
            //Convert from A-Z to index
            x = ABC.indexOf(c);

            return x;
        }
        return Ex2Utils.ERR;
    }

    @Override
    public int getY(String A0) {

        if (isValid(A0)) {
            y = Integer.parseInt(A0.substring(1));
            return y;
        }
        return Ex2Utils.ERR;
    }

    public String getCords (int x, int y) {
    String ans = "";
        if(x >= 0 && x < 26 && y >=0 && y<100) {
            String ABC = String.join("", Ex2Utils.ABC);
            char xC = ABC.charAt(x);
            ans = xC +""+ y;
        }
    return ans;

    }


}

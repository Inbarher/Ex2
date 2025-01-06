// Add your documentation below:

public class CellEntry  implements Index2D {

    @Override
    public boolean isValid(String s) {
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
        if (ABC.indexOf(c)==-1){
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
            int x = ABC.indexOf(c);

            return x;
        }
        return Ex2Utils.ERR;
    }

    @Override
    public int getY(String A0) {
        if (isValid(A0)) {
            int y = Integer.parseInt(A0.substring(1));
            return y;
        }
        return Ex2Utils.ERR;
        }
}

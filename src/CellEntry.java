// Add your documentation below:

public class CellEntry  implements Index2D {

    @Override
    public boolean isValid(String s) {
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
    public int getX() {return Ex2Utils.ERR;}

    @Override
    public int getY() {return Ex2Utils.ERR;}
}

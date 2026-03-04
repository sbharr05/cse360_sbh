package admin;

import java.security.SecureRandom;


//Utility to generate new passwords
public class PasswordUtil {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz"; //String of all lower case letters
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //String of all upper case letters
    private static final String DIGIT = "0123456789"; //String of numerical digits
    private static final String ALL   = LOWER + UPPER + DIGIT; // add specials later if desired
    private static final SecureRandom RNG = new SecureRandom();

    // Generates an 8–32 char password with at least one upper, one lower, one digit
    public static String generateStrongTemp(int length) {
        int n = Math.max(8, Math.min(32, length));
        StringBuilder sb = new StringBuilder(n);
        sb.append(randomChar(UPPER));
        sb.append(randomChar(LOWER));
        sb.append(randomChar(DIGIT));
        while (sb.length() < n) sb.append(randomChar(ALL));
        return sb.toString();
    }
    private static char randomChar(String alphabet) {
        return alphabet.charAt(RNG.nextInt(alphabet.length()));
    }
}

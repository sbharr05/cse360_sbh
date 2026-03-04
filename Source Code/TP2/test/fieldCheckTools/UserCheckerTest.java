package fieldCheckTools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserCheckerTest {

    @Test
    void empty_isError() {
        assertTrue(userChecker.checkForValidUserName("").startsWith("The user input is empty!"));
    }

    @Test
    void mustStartWithLetter() {
        String msg = userChecker.checkForValidUserName("1abc");
        assertTrue(msg.contains("must start"));
    }

    @Test
    void minLength_is4_byMessage() {
        // Code checks <3 then says at least 4. So length 3 should error.
        String msg = userChecker.checkForValidUserName("Abc");
        assertTrue(msg.contains("at least 4"));
    }

    @Test
    void underscoreAllowed_notConsecutive() {
        assertEquals("", userChecker.checkForValidUserName("Abc_def"));
    }

    @Test
    void lengthOver16_isError() {
        String msg = userChecker.checkForValidUserName("Abcdefghijklmnop"); // 16? 17? ensure >16
        assertTrue(msg.contains("no more than 16"));
    }

    @Test
    void validLettersAndDigits_ok() {
        assertEquals("", userChecker.checkForValidUserName("User1234"));
    }
}

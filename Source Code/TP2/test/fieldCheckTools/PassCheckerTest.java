package fieldCheckTools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PassCheckerTest {

    @Test
    void emptyGivesMessage() {
        assertEquals("The password is empty!", passChecker.evaluatePassword(""));
    }

    @Test
    void invalidCharFails() {
        String msg = passChecker.evaluatePassword("Abcdef1!");
        assertEquals("An invalid character has been found!", msg);
    }

    @Test
    void tooShort_needsFlags() {
        String msg = passChecker.evaluatePassword("Abc1def"); // 7 chars
        assertTrue(msg.contains("Between 8-32 Chars"));
    }

    @Test
    void tooLong_over32_failsLength() {
        String msg = passChecker.evaluatePassword("A" + "b".repeat(31) + "1"); // 33rd index triggers >32 check
        assertTrue(msg.contains("Between 8-32 Chars"));
    }

    @Test
    void valid_ok() {
        assertEquals("", passChecker.evaluatePassword("StrongPass1"));
    }

    @Test
    void missingUppercase_flag() {
        String msg = passChecker.evaluatePassword("lowercase1");
        assertTrue(msg.contains("Upper case"));
    }

    @Test
    void missingLowercase_flag() {
        String msg = passChecker.evaluatePassword("UPPERCASE1");
        assertTrue(msg.contains("Lower case"));
    }

    @Test
    void missingDigit_flag() {
        String msg = passChecker.evaluatePassword("NoDigitsHereA");
        assertTrue(msg.contains("Numeric digit"));
    }
}

package fieldCheckTools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EmailCheckerTest {

    @Test
    void valid_basic() {
        assertEquals("", emailChecker.evaluateEmail("alice@example.com"));
    }

    @Test
    void valid_subdomains() {
        assertEquals("", emailChecker.evaluateEmail("bob.smith@dept.cs.school.edu"));
    }

    @Test
    void invalid_missingAt() {
        String msg = emailChecker.evaluateEmail("alice.example.com");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_missingDot() {
        String msg = emailChecker.evaluateEmail("alice@localhost");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_withSpace() {
        String msg = emailChecker.evaluateEmail("al ice@example.com");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_localEndsWithSpecial() {
        String msg = emailChecker.evaluateEmail("alice.@example.com");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_localStartsWithSpecial() {
        String msg = emailChecker.evaluateEmail(".alice@example.com");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_doubleDotInLocal() {
        String msg = emailChecker.evaluateEmail("al..ice@example.com");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_doubleDotInDomain() {
        String msg = emailChecker.evaluateEmail("alice@ex..ample.com");
        assertTrue(msg.startsWith("Invalid Email"));
    }

    @Test
    void invalid_domainLabelNonAlnum() {
        String msg = emailChecker.evaluateEmail("alice@exa-mple.com"); // '-' not allowed by your checker
        assertTrue(msg.startsWith("Invalid Email"));
    }
}

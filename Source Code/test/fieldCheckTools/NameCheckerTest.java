package fieldCheckTools;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NameCheckerTest {

    @Test
    void empty_isError() {
        assertEquals("Input Empty", nameChecker.evaluateName(""));
    }

    @Test
    void tooLong_isError() {
        String longName = "a".repeat(256);
        assertEquals("Input Too Long", nameChecker.evaluateName(longName));
    }

    @Test
    void dashAtEnds_orDoubleDash_isError() {
        assertEquals("Invalid Use of Dashes", nameChecker.evaluateName("-Anna"));
        assertEquals("Invalid Use of Dashes", nameChecker.evaluateName("Anna-"));
        assertEquals("Invalid Use of Dashes", nameChecker.evaluateName("Anna--Marie"));
    }

    @Test
    void nonAlpha_isError() {
        assertEquals("Contains Non-Alphabetical Characters", nameChecker.evaluateName("Ann4"));
        assertEquals("Contains Non-Alphabetical Characters", nameChecker.evaluateName("Ann_"));
    }

    @Test
    void validWithDash_ok() {
        assertEquals("", nameChecker.evaluateName("anna-marie"));
        assertEquals("Anna-Marie", nameChecker.formatName("anna-marie"));
    }

    @Test
    void format_singleSegment_capsFirst() {
        assertEquals("Anna", nameChecker.formatName("anna"));
    }
}

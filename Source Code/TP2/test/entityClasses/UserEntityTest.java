package entityClasses;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserEntityTest {

    @Test
    void constructorAndGetters() {
        User u = new User("u", "p", "f", "m", "l", "pf", "e@x.com", true, false, true);
        assertEquals("u", u.getUserName());
        assertEquals("p", u.getPassword());
        assertEquals("f", u.getFirstName());
        assertEquals("m", u.getMiddleName());
        assertEquals("l", u.getLastName());
        assertEquals("pf", u.getPreferredFirstName());
        assertEquals("e@x.com", u.getEmailAddress());
        assertTrue(u.getAdminRole());
        assertFalse(u.getNewRole1());
        assertTrue(u.getNewRole2());
        assertEquals(2, u.getNumRoles());
    }

    @Test
    void settersMutate() {
        User u = new User();
        u.setUserName("alice");
        u.setPassword("pw");
        u.setFirstName("Alice");
        u.setMiddleName("B");
        u.setLastName("Carroll");
        u.setPreferredFirstName("Ali");
        u.setEmailAddress("alice@example.com");

        assertEquals("alice", u.getUserName());
        assertEquals("pw", u.getPassword());
        assertEquals("Alice", u.getFirstName());
        assertEquals("B", u.getMiddleName());
        assertEquals("Carroll", u.getLastName());
        assertEquals("Ali", u.getPreferredFirstName());
        assertEquals("alice@example.com", u.getEmailAddress());
    }
}

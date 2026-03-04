package database;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.*;

import entityClasses.User;

public class DatabaseTest {

    Database db;

    static boolean h2Available() {
        try {
            Class.forName("org.h2.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @BeforeEach
    void setup() throws SQLException {
        assumeTrue(h2Available(), "H2 driver not on classpath; skipping DB tests.");
        db = new Database();
        db.connectToDatabase();
    }

    @AfterEach
    void tearDown() {
        if (db != null) db.closeConnection();
    }

    @Test
    void connectAndMaybeEmpty() {
        assumeTrue(h2Available());
        assertNotNull(db);
        assertTrue(db.getNumberOfUsers() >= 0);
    }

    @Test
    void registerAndLoginAdmin() throws SQLException {
        assumeTrue(h2Available());
        String uname = "adminTestUser";
        if (!db.doesUserExist(uname)) {
            User u = new User(uname, "StrongPass1", "F", "", "L", "F", "f@x.com", true, false, false);
            db.register(u);
        }
        assertTrue(db.getUserAccountDetails(uname));
        User admin = new User(uname, "StrongPass1", "F", "", "L", "F", "f@x.com", true, false, false);
        assertTrue(db.loginAdmin(admin));
    }

    @Test
    void invitationLifecycle() {
        assumeTrue(h2Available());
        String email = "invitee@example.com";
        String role = "Role1";
        String code = db.generateInvitationCode(email, role);
        assertNotNull(code);
        assertTrue(db.getNumberOfInvitations() >= 1);
        assertEquals(role, db.getRoleGivenAnInvitationCode(code));
        assertEquals(email, db.getEmailAddressUsingCode(code));
        db.removeInvitationAfterUse(code);
        assertEquals("", db.getRoleGivenAnInvitationCode(code));
    }
}

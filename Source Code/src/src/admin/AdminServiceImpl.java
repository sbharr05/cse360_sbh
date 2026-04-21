package admin;

import java.util.*;
import applicationMain.FoundationsMain;
import database.Database;

public class AdminServiceImpl implements AdminService {
    private final Database db;
    private final Map<String, UserSummary> users = new LinkedHashMap<>();

    /*hw3lines ...---------------------*/
    public String resetPasswordAs(String actingUserId, boolean actingIsAdmin, String targetUserId) {
        if (targetUserId == null || !users.containsKey(targetUserId)) return null;

        boolean actingOwnAccount = actingUserId != null && actingUserId.equals(targetUserId);
        if (!actingIsAdmin && !actingOwnAccount) return null;

        if (actingUserId == null && actingIsAdmin) return null;

        String actor = actingUserId != null ? actingUserId : targetUserId;

        String newPass = PasswordUtil.generateStrongTemp(12);
        if (!db.updatePasswordAuthorized(targetUserId, actor, newPass)) {
            return null;
        }
        db.updateUserPasswordReset(targetUserId, true);
        return newPass;
    }

    public boolean changeRoleAs(String actingUserId, boolean actingIsAdmin, String targetUserId, Role newRole) {
        if (!actingIsAdmin || actingUserId == null || targetUserId == null || newRole == null) return false;

        UserSummary u = users.get(targetUserId);
        if (u == null) {
            loadUsersFromDatabase();
            u = users.get(targetUserId);
            if (u == null) {
                return false;
            }
        }

        Role currRole = u.getRole();
        if (currRole == newRole) return true;
        if (currRole == Role.ADMIN && newRole != Role.ADMIN && countActiveAdmins() == 1) return false;

        boolean removed = true;
        if (currRole != Role.NONE) {
            removed = db.updateUserRoleAuthorized(targetUserId, actingUserId, currRole, false);
        }
        if (!removed) {
            return false;
        }

        boolean added = true;
        if (newRole != Role.NONE) {
            added = db.updateUserRoleAuthorized(targetUserId, actingUserId, newRole, true);
        }

        if (!added) {
            if (currRole != Role.NONE) {
                db.updateUserRoleAuthorized(targetUserId, actingUserId, currRole, true);
            }
            return false;
        }

        users.put(targetUserId, new UserSummary(u.getUserId(), u.getDisplayName(), newRole, u.isActive()));
        return true;
    }
    /*hw3 lines*/
    //Sets up the admin service page
    public AdminServiceImpl() {
    	this(FoundationsMain.database);
    }

    public AdminServiceImpl(Database database) {
        this.db = database;
        loadUsersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        users.clear();
        List<String> userNames = db.getUserList(); //Get the list of all user names
        for (int i = 0; i < userNames.size(); i ++) 
        {
        	String username = userNames.get(i);
            String prefFirst = db.getPreferredFirstName(username); //Get Pref First Name
            String displayName = (prefFirst == null || prefFirst.isEmpty())
                    ? db.getFirstName(username)
                    : prefFirst; //If the user hasn't set a pref. First name then attempt to use norm first name.
            UserSummary newSummary = new UserSummary(
                            username,
                            displayName,
                            db.getRoleByUser(username),
                            db.getActiveStatus(username));
            users.put(username, newSummary);
        }
    }

    //Lists all of the current users in the database
    @Override
    public List<UserSummary> listUsers(Optional<Role> roleFilter, Optional<Boolean> activeOnly) {
        List<UserSummary> out = new ArrayList<>(users.values());
        roleFilter.ifPresent(r -> out.removeIf(u -> u.getRole() != r));
        activeOnly.ifPresent(a -> { if (a) out.removeIf(u -> !u.isActive()); });
        return out;
    }

    
    //Sets a user active
    @Override
    public boolean setActive(String userId, boolean active) {
        UserSummary u = users.get(userId);
        if (u == null) return false;
        if (!active && isLastActiveAdmin(userId)) return false; // protect last active ADMIN
        users.put(userId, new UserSummary(u.getUserId(), u.getDisplayName(), u.getRole(), active)); //Update List
        db.updateActiveStatus(u.getUserId(), active); //Update Database
        return true;
    }

    
    //Changes a current users role
    @Override
    public boolean changeRole(String userId, Role newRole) {
    	 return changeRoleAs(db.getCurrentUsername(), db.getCurrentAdminRole(), userId, newRole);
    }

    
    //Resets a password and generates a new one
    @Override
    public String resetPassword(String userId) {
    	 return resetPasswordAs(db.getCurrentUsername(), db.getCurrentAdminRole(), userId);
    }

    /* Helpers */

    //Counts the currently active admins
    private int countActiveAdmins() {
        int n = 0;
        for (UserSummary u : users.values()) {
            if (u.isActive() && u.getRole() == Role.ADMIN) n++;
        }
        return n;
    }

    //Determines if the last admin is active
    private boolean isLastActiveAdmin(String userId) {
        UserSummary u = users.get(userId);
        if (u == null) return false;
        if (u.getRole() != Role.ADMIN || !u.isActive()) return false;
        return countActiveAdmins() == 1;
    }
}

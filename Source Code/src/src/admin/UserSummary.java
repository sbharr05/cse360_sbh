package admin;

public class UserSummary {
    private final String userId;      // make private
    private final String displayName; // make private
    private final Role role;
    private final boolean active;

    public UserSummary(String userId, String displayName, Role role, boolean active) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
        this.active = active;
    }

    // ---- getters needed by PropertyValueFactory ----
    public String getUserId()      { return userId; }
    public String getDisplayName() { return displayName; }
    public Role   getRole()        { return role; }
    public boolean isActive()      { return active; }
}

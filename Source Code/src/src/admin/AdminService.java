package admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    // ADM-01
    List<UserSummary> listUsers(Optional<Role> roleFilter, Optional<Boolean> activeOnly);

    // ADM-02
    boolean setActive(String userId, boolean active);   // false = deactivate

    // ADM-03
    boolean changeRole(String userId, Role newRole);    // must protect last ADMIN

    // ADM-04
    String resetPassword(String userId);                // return temp password/token
    
}

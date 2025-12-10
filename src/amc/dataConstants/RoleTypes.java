package amc.dataConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** This class holds role strings used across the app. It should not be instantiated. */
public final class RoleTypes {
    private RoleTypes() {} // Prevents creating this utility class.

    public static final String manager  = "MANAGER";
    public static final String staff    = "STAFF";
    public static final String doctor   = "DOCTOR";
    public static final String customer = "CUSTOMER";

    // A set of all valid roles for quick lookup.
    private static final Set<String> VALID_ROLES = new HashSet<>(Arrays.asList(
            manager, staff, doctor, customer
    ));

    /**
     * Checks if the given role string is one of the predefined valid roles.
     *
     * @param role The role string to validate.
     * @return true if the role is valid, false otherwise.
     */
    public static boolean isValidRole(String role) {
        return role != null && VALID_ROLES.contains(role.toUpperCase());
    }
}
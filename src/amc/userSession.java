package amc;

import amc.dataModels.User;

// This class keeps the logged-in user's basic info in memory.
public final class userSession {
    private static String userId;
    private static String role;
    private static String name;
    private static String email;

    private userSession(){}

    // Copy key fields after a successful login.
    public static void setFromUser(User user) {
        userId = user.getUserId();
        role   = user.getRole();
        name   = user.getName();
        email  = user.getEmail();
    }

    // Tell if someone is logged in.
    public static boolean isLoggedIn() { return userId != null && userId.trim().length() > 0; }

    // Clear all session fields.
    public static void clear() {
        userId = null; role = null; name = null; email = null;
    }

    // Getters for screens that need them.
    public static String getUserId() { return userId; }
    public static String getRole()   { return role; }
    public static String getName()   { return name; }
    public static String getEmail()  { return email; }
}
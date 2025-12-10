package amc.roleModules.Customer.customerScreens;

import amc.dataModels.User;

import java.time.Instant;


public final class SessionBus {

    private static final Object LOCK = new Object();

    private static User currentUser;
    private static Instant loginTime;
    private static Instant lastUpdated;

    private SessionBus() {}


    public static void set(User u) {
        synchronized (LOCK) {
            if (u == null) {
                currentUser = null;
                loginTime   = null;
                lastUpdated = Instant.now();
                return;
            }
            boolean hadNoSession = (currentUser == null);
            currentUser = copy(u);
            Instant now = Instant.now();
            if (hadNoSession) loginTime = now;
            lastUpdated = now;
        }
    }




    public static User get() {
        synchronized (LOCK) {
            return copy(currentUser);
        }
    }

    public static void clear() {
        set(null);
    }


    public static String getUserId() {
        synchronized (LOCK) {
            return currentUser != null ? currentUser.getUserId() : null;
        }
    }


    private static User copy(User src) {
        if (src == null) return null;
        User dst = new User();
        dst.setUserId(src.getUserId());
        dst.setName(src.getName());
        dst.setEmail(src.getEmail());
        dst.setPhone(src.getPhone());
        dst.setAddress(src.getAddress());
        dst.setRole(src.getRole());
        dst.setPassword(src.getPassword());

        return dst;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
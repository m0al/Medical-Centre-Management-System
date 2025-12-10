package amc.roleModules.Customer.customerScreens;

import amc.dataModels.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;



final class CurrentUserResolver {

    private CurrentUserResolver() {}


    private static volatile User cached;


    static void set(User user) {
        cached = (user == null) ? null : copy(user);
    }


    static void clear() {
        cached = null;
    }


    static User resolve() {

        if (cached != null) return copy(cached);


        User u = trySessionBus();
        if (u != null) return rememberAndCopy(u);


        String[] candidates = new String[]{

                "amc.userSession",
                "amc.roleModules.Universal.userSession",
                "amc.roleModules.Universal.UserSession",


                "amc.universalUtil.LoginController",
                "amc.roleModules.Universal.universalUtil.LoginController",
                "userSession"
        };

        for (String cn : candidates) {
            u = tryClassCandidates(cn);
            if (u != null) return rememberAndCopy(u);
        }


        return null;
    }


    private static User trySessionBus() {
        try {
            Class<?> sb = Class.forName("amc.roleModules.Customer.customerScreens.SessionBus");
            Method get = sb.getMethod("get");
            Object obj = get.invoke(null);
            if (obj instanceof User) return (User) obj;
        } catch (Throwable ignored) {}
        return null;
    }


    private static User tryClassCandidates(String className) {
        Class<?> cls = safeClassForName(className);
        if (cls == null) return null;

        User u = tryStaticMethodReturningUser(cls, "getCurrentUser");
        if (u != null) return u;

        u = tryStaticUserField(cls, "currentUser");
        if (u != null) return u;


        String id = tryStaticStringMethod(cls, "getUserId");
        if (id != null && !id.isBlank()) {
            User tmp = new User();
            tmp.setUserId(id);

            String name = tryStaticStringMethod(cls, "getName");
            if (name != null) tmp.setName(name);

            String email = tryStaticStringMethod(cls, "getEmail");
            if (email != null) tmp.setEmail(email);

            return tmp;
        }

        return null;
    }


    private static Class<?> safeClassForName(String cn) {
        try { return Class.forName(cn); } catch (Throwable ignored) { return null; }
    }

    private static User tryStaticMethodReturningUser(Class<?> cls, String method) {
        try {
            Method m = cls.getMethod(method);
            Object v = m.invoke(null);
            return (v instanceof User) ? (User) v : null;
        } catch (NoSuchMethodException ignored) {
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static User tryStaticUserField(Class<?> cls, String field) {
        try {
            Field f = cls.getField(field);
            Object v = f.get(null);
            return (v instanceof User) ? (User) v : null;
        } catch (NoSuchFieldException ignored) {
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String tryStaticStringMethod(Class<?> cls, String method) {
        try {
            Method m = cls.getMethod(method);
            Object v = m.invoke(null);
            return (v != null) ? String.valueOf(v) : null;
        } catch (NoSuchMethodException ignored) {
            return null;
        } catch (Throwable ignored) {
            return null;
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

        return dst;
    }

    private static User rememberAndCopy(User u) {
        cached = copy(u);
        return copy(cached);
    }
}
package amc.roleModules.Customer.customerScreens;

import amc.dataModels.Appointment;
import amc.dataModels.Feedback;
import amc.dataModels.User;
import amc.logicControllers.AppointmentController;
import amc.logicControllers.FeedbackController;
import amc.logicControllers.UserController;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@SuppressWarnings({"unchecked","rawtypes"})
public class CustomerService {

    private final AppointmentController appointmentController;
    private final FeedbackController feedbackController;
    private final UserController userController;
    private final User currentUser;

    public CustomerService(User currentUser,
                           AppointmentController apptCtl,
                           FeedbackController fbCtl,
                           UserController userCtl) {
        this.currentUser = currentUser;
        this.appointmentController = apptCtl;
        this.feedbackController = fbCtl;
        this.userController = userCtl;
    }

    public User getCurrentUser() { return currentUser; }


    public User reloadCurrentUser() {
        try {
            if (currentUser == null || currentUser.getUserId() == null || currentUser.getUserId().isBlank()) {
                return currentUser;
            }
            for (String name : new String[]{"findById","getById","loadById"}) {
                try {
                    Method m = userController.getClass().getMethod(name, String.class);
                    Object r = m.invoke(userController, currentUser.getUserId());
                    User full = unwrapUser(r);
                    if (full != null) {
                        try {
                            Class<?> sb = Class.forName("amc.roleModules.Customer.customerScreens.SessionBus");
                            sb.getMethod("set", amc.dataModels.User.class).invoke(null, full);
                        } catch (Throwable ignored) {}
                        return full;
                    }
                } catch (NoSuchMethodException ignored) {}
            }
        } catch (Throwable ignored) {}
        return currentUser;
    }

    // ===================== Appointments =====================

    public List<Appointment> listAppointmentsByCustomer(String customerId) {
        if (customerId == null || customerId.isBlank() || appointmentController == null) {
            return Collections.emptyList();
        }
        String[] candidates = {
                "listByCustomer","getByCustomer","findByCustomer",
                "listAppointmentsForCustomer","getAppointmentsForCustomer","getAppointmentsByCustomer"
        };
        for (String name : candidates) {
            try {
                Method m = appointmentController.getClass().getMethod(name, String.class);
                Object r = m.invoke(appointmentController, customerId);
                return unwrapAppointmentList(r);
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) { t.printStackTrace(); }
        }


        try {
            Method m = appointmentController.getClass().getMethod("listAll");
            Object r = m.invoke(appointmentController);
            List<Appointment> all = unwrapAppointmentList(r);
            List<Appointment> out = new ArrayList<>();
            for (Appointment a : all) {
                if (a != null && customerId.equalsIgnoreCase(a.getCustomerId())) out.add(a);
            }
            return out;
        } catch (Throwable ignored) {}

        return Collections.emptyList();
    }

    public List<Appointment> listCompletedAppointments(String customerId) {
        List<Appointment> src = listAppointmentsByCustomer(customerId);
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : src) {
            String st = (a != null ? a.getStatus() : null);
            if (st != null && st.equalsIgnoreCase("COMPLETED")) out.add(a);
        }
        return out;
    }

    public boolean updateNoteForAppointment(String appointmentId, String notes) {
        if (appointmentId == null || appointmentId.isBlank()) return false;

        try {
            Method m = appointmentController.getClass().getMethod("updateNote", String.class, String.class);
            Object res = m.invoke(appointmentController, appointmentId, notes);
            return !(res instanceof Boolean) || (Boolean) res;
        } catch (NoSuchMethodException ignored) {
        } catch (Throwable t) { t.printStackTrace(); }

        Appointment a = findAppointmentById(appointmentId);
        if (a == null) return false;
        try { a.setNote(notes); } catch (Throwable ignored) {}

        for (String name : new String[]{"save","update","saveAppointment"}) {
            try {
                Method m = appointmentController.getClass().getMethod(name, Appointment.class);
                m.invoke(appointmentController, a);
                return true;
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) { t.printStackTrace(); }
        }
        try {
            Method listAll = appointmentController.getClass().getMethod("listAll");
            List<Appointment> all = (List<Appointment>) listAll.invoke(appointmentController);
            Method saveAll = appointmentController.getClass().getMethod("saveAll", List.class);
            saveAll.invoke(appointmentController, all);
            return true;
        } catch (Throwable ignored) {}

        return true;
    }

    private Appointment findAppointmentById(String appointmentId) {
        try {
            Method m = appointmentController.getClass().getMethod("findById", String.class);
            Object r = m.invoke(appointmentController, appointmentId);
            if (r instanceof Appointment) return (Appointment) r;
            Appointment a = tryUnwrapAppointment(r);
            if (a != null) return a;
        } catch (Throwable ignored) {}

        try {
            Method m = appointmentController.getClass().getMethod("listAll");
            Object r = m.invoke(appointmentController);
            List<Appointment> all = unwrapAppointmentList(r);
            for (Appointment a : all) if (a != null && appointmentId.equals(a.getAppointmentId())) return a;
        } catch (Throwable ignored) {}
        return null;
    }

    // ===================== Feedback =====================

    public List<Feedback> listFeedbacksForCustomer(String customerId) {
        if (feedbackController == null || customerId == null || customerId.isBlank()) return new ArrayList<>();


        String[] methods = {"listForCustomer","getForCustomer","findForCustomer","listByCustomer"};
        for (String name : methods) {
            try {
                Method m = feedbackController.getClass().getMethod(name, String.class);
                Object r = m.invoke(feedbackController, customerId);
                return unwrapFeedbackList(r);
            } catch (NoSuchMethodException ignored) {
            } catch (Throwable t) { t.printStackTrace(); }
        }

        try {
            Method m = feedbackController.getClass().getMethod("listAll");
            Object r = m.invoke(feedbackController);
            List<Feedback> all = unwrapFeedbackList(r);
            List<Feedback> out = new ArrayList<>();
            for (Feedback f : all) {
                if (f != null && customerId.equalsIgnoreCase(safeGetString(f, "getCustomerId"))) {
                    out.add(f);
                }
            }
            return out;
        } catch (Throwable ignored) {}

        return new ArrayList<>();
    }

    public Feedback submitFeedback(String customerId, String doctorId, String appointmentId, int rating, String comment) {
        if (feedbackController == null) return null;


        try {
            Method m = feedbackController.getClass().getMethod(
                    "create", String.class, String.class, String.class, int.class, String.class);
            Object r = m.invoke(feedbackController, customerId, doctorId, appointmentId, rating, comment);
            return (r instanceof Feedback) ? (Feedback) r : null;
        } catch (NoSuchMethodException ignored) {
        } catch (Throwable t) { t.printStackTrace(); }


        try {
            Feedback f = new Feedback();
            safeSetString(f, "setCustomerId", customerId);
            safeSetString(f, "setDoctorId", doctorId);
            safeSetString(f, "setAppointmentId", appointmentId);
            safeSetInt(f, "setRating", rating);
            safeSetString(f, "setComment", comment);

            Method m = feedbackController.getClass().getMethod("create", Feedback.class);
            Object r = m.invoke(feedbackController, f);
            return (r instanceof Feedback) ? (Feedback) r : null;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    // ===================== Doctor display =====================

    public String doctorNameOrPlaceholder(String doctorId) {
        if (doctorId == null || doctorId.isBlank()) return "Doctor";
        try {
            for (String name : new String[]{"findById","getById"}) {
                try {
                    Method m = userController.getClass().getMethod(name, String.class);
                    Object r = m.invoke(userController, doctorId);
                    User u = unwrapUser(r);
                    if (u != null && u.getName() != null && !u.getName().isBlank()) return u.getName();
                } catch (NoSuchMethodException ignored) {}
            }
   
            try {
                Method m = userController.getClass().getMethod("findByEmail", String.class);
                Object r = m.invoke(userController, doctorId);
                User u = unwrapUser(r);
                if (u != null && u.getName() != null && !u.getName().isBlank()) return u.getName();
            } catch (NoSuchMethodException ignored) {}
        } catch (Throwable ignored) {}
        return "Doctor";
    }

    // ===================== Helpers =====================

    private User unwrapUser(Object r) {
        if (r == null) return null;
        if (r instanceof User) return (User) r;
        if (r instanceof Optional) {
            try { return ((Optional<User>) r).orElse(null); } catch (Throwable ignored) {}
        }
        try {
            Method isPresent = r.getClass().getMethod("isPresent");
            Method get = r.getClass().getMethod("get");
            Object present = isPresent.invoke(r);
            if (present instanceof Boolean && (Boolean) present) {
                Object u = get.invoke(r);
                if (u instanceof User) return (User) u;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private Appointment tryUnwrapAppointment(Object r) {
        if (r instanceof Appointment) return (Appointment) r;
        try {
            Method get = r.getClass().getMethod("get");
            Object o = get.invoke(r);
            if (o instanceof Appointment) return (Appointment) o;
        } catch (Throwable ignored) {}
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Appointment> unwrapAppointmentList(Object r) {
        if (r == null) return Collections.emptyList();
        if (r instanceof List) return (List<Appointment>) r;
        if (r instanceof Optional) {
            try { return (List<Appointment>) ((Optional) r).orElse(Collections.emptyList()); }
            catch (Throwable ignored) {}
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Feedback> unwrapFeedbackList(Object r) {
        if (r == null) return new ArrayList<>();
        if (r instanceof List) return (List<Feedback>) r;
        if (r instanceof Optional) {
            try { return (List<Feedback>) ((Optional) r).orElse(new ArrayList<>()); }
            catch (Throwable ignored) {}
        }
        return new ArrayList<>();
    }

    private String safeGetString(Object target, String getter) {
        try {
            Method m = target.getClass().getMethod(getter);
            Object v = m.invoke(target);
            return (v != null) ? String.valueOf(v) : null;
        } catch (Throwable ignored) { return null; }
    }

    private void safeSetString(Object target, String setter, String value) {
        try {
            Method m = target.getClass().getMethod(setter, String.class);
            m.invoke(target, value);
        } catch (Throwable ignored) {}
    }

    private void safeSetInt(Object target, String setter, int value) {
        try {
            Method m = target.getClass().getMethod(setter, int.class);
            m.invoke(target, value);
        } catch (Throwable ignored) {}
    }

    // -------- Date/time parsing helpers for UIs --------
    public LocalDateTime resolveDateTimeForUi(Appointment a) {
        if (a == null) return null;
        try {
            Method m = a.getClass().getMethod("getDateTime");
            Object v = m.invoke(a);
            if (v instanceof LocalDateTime) return (LocalDateTime) v;
            if (v instanceof String) {
                LocalDateTime p = tryParseDateTime((String) v);
                if (p != null) return p;
            }
        } catch (Throwable ignored) {}

        for (String g : new String[]{"getDateTimeIso","getDatetimeIso","getDateTimeISO","getDatetimeISO"}) {
            try {
                Method m = a.getClass().getMethod(g);
                Object v = m.invoke(a);
                if (v instanceof String) {
                    try { return LocalDateTime.parse((String) v, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
                    catch (Throwable ignored) { return tryParseDateTime((String) v); }
                }
            } catch (Throwable ignored) {}
        }

        try {
            LocalDate d = null; LocalTime t = LocalTime.MIDNIGHT;
            try {
                Method md = a.getClass().getMethod("getDate");
                Object vd = md.invoke(a);
                if (vd instanceof LocalDate) d = (LocalDate) vd;
                else if (vd instanceof String) d = tryParseDate((String) vd);
            } catch (Throwable ignored) {}
            try {
                Method mt = a.getClass().getMethod("getTime");
                Object vt = mt.invoke(a);
                if (vt instanceof LocalTime) t = (LocalTime) vt;
                else if (vt instanceof String) t = tryParseTime((String) vt);
            } catch (Throwable ignored) {}
            if (d != null) return LocalDateTime.of(d, (t != null ? t : LocalTime.MIDNIGHT));
        } catch (Throwable ignored) {}
        return null;
    }

    private LocalDate tryParseDate(String s) {
        if (s == null || s.isBlank()) return null;
        String[] fmts = {"yyyy-MM-dd","dd/MM/yyyy","MM/dd/yyyy"};
        for (String f : fmts) {
            try { return LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern(f)); }
            catch (Throwable ignored) {}
        }
        try { return LocalDate.parse(s); } catch (Throwable ignored) {}
        return null;
    }

    private LocalTime tryParseTime(String s) {
        if (s == null || s.isBlank()) return null;
        String[] fmts = {"HH:mm","HH:mm:ss","h:mm a"};
        for (String f : fmts) {
            try { return LocalTime.parse(s, java.time.format.DateTimeFormatter.ofPattern(f)); }
            catch (Throwable ignored) {}
        }
        try { return LocalTime.parse(s); } catch (Throwable ignored) {}
        return null;
    }

    private LocalDateTime tryParseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        String[] fmts = {
                "yyyy-MM-dd'T'HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "dd/MM/yyyy HH:mm",
                "MM/dd/yyyy HH:mm"
        };
        for (String f : fmts) {
            try { return LocalDateTime.parse(s, java.time.format.DateTimeFormatter.ofPattern(f)); }
            catch (Throwable ignored) {}
        }
        try { return LocalDateTime.parse(s); } catch (Throwable ignored) {}
        return null;
    }
    // ===================== Profile =====================


    public void updateOwnProfile(String name, String email, String phone, String address, String password) {
        if (userController == null || currentUser == null) return;

        User u = new User();
        u.setUserId(currentUser.getUserId());
        u.setName(name);
        u.setEmail(email);
        u.setPhone(phone);
        u.setAddress(address);
        u.setPassword(password);


        for (String method : new String[]{
                "updateOwnProfile",      
                "updateProfile",          
                "update",                 
                "saveProfile"            
        }) {
            try {
                for (var m : userController.getClass().getMethods()) {
                    if (!m.getName().equals(method)) continue;
                    Class<?>[] types = m.getParameterTypes();

                    if (types.length == 5) {
                        m.invoke(userController, name, email, phone, address, password);
                        afterProfileSaved(u);
                        return;
                    } else if (types.length == 2 && types[0] == String.class) {
                        m.invoke(userController, currentUser.getUserId(), u);
                        afterProfileSaved(u);
                        return;
                    } else if (types.length == 1 && types[0] == User.class) {
                        m.invoke(userController, u);
                        afterProfileSaved(u);
                        return;
                    }
                }
            } catch (Throwable ignored) {}
        }
    }


    private void afterProfileSaved(User saved) {
        try {
            if (saved.getName() != null) currentUser.setName(saved.getName());
            if (saved.getEmail() != null) currentUser.setEmail(saved.getEmail());
            if (saved.getPhone() != null) currentUser.setPhone(saved.getPhone());
            if (saved.getAddress() != null) currentUser.setAddress(saved.getAddress());
            if (saved.getPassword() != null) currentUser.setPassword(saved.getPassword());


            Class<?> sb = Class.forName("amc.roleModules.Customer.customerScreens.SessionBus");
            sb.getMethod("set", User.class).invoke(null, currentUser);
        } catch (Throwable ignored) {}
    }
}
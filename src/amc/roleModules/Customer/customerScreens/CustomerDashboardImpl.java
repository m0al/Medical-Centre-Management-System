package amc.roleModules.Customer.customerScreens;

import amc.dataModels.Appointment;
import amc.dataModels.User;
import amc.logicControllers.AppointmentController;
import amc.logicControllers.FeedbackController;
import amc.logicControllers.UserController;
import amc.roleModules.Customer.customerScreens.Universal.universalScreens.LoginScreen;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CustomerDashboardImpl extends JFrame {

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private CustomerService service;
    private String customerId;
    private String customerName;

    private CardLayout cardLayout;
    private JPanel cardHost;
    private JLabel lblWelcome;
    private JLabel lblNextAppt;

    public CustomerDashboardImpl() {

        User current = CurrentUserResolver.resolve();
        if (current == null || current.getUserId() == null || current.getUserId().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "No logged-in user found. Please login first.",
                    "Customer Dashboard", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }


        this.service = new CustomerService(
                current,
                new AppointmentController(),
                new FeedbackController(),
                new UserController()
        );


        User fresh = service.reloadCurrentUser();
        User effective = (fresh != null ? fresh : current);


        this.customerId   = (effective.getUserId() != null ? effective.getUserId() : "");
        this.customerName = (effective.getName() != null && !effective.getName().isBlank())
                ? effective.getName() : null;

        initUI();
        loadOverview();
    }

    private void initUI() {
        setTitle("AMC — Customer Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String displayName = (customerName != null && !customerName.isBlank())
                ? customerName
                : (customerId != null && !customerId.isBlank() ? customerId : "Customer");
        lblWelcome = new JLabel("Welcome, " + displayName);

        JPanel sidebar = new JPanel(new GridLayout(8, 1, 0, 8));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JButton btnOverview = new JButton("Overview");
        JButton btnHistory  = new JButton("Appointments");
        JButton btnFeedback = new JButton("Feedback");
        JButton btnProfile  = new JButton("Profile");
        JButton btnLogout   = new JButton("Logout");
        sidebar.add(btnOverview); sidebar.add(btnHistory);
        sidebar.add(btnFeedback); sidebar.add(btnProfile);
        sidebar.add(new JLabel()); sidebar.add(btnLogout);

        cardLayout = new CardLayout();
        cardHost = new JPanel(cardLayout);

        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout(new BoxLayout(overviewPanel, BoxLayout.Y_AXIS));
        overviewPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        lblWelcome = new JLabel("Welcome, " + displayName);
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 20f));
        lblNextAppt = new JLabel("Next appointment: —");
        overviewPanel.add(lblWelcome);
        overviewPanel.add(Box.createVerticalStrut(16));
        overviewPanel.add(lblNextAppt);

        AppointmentHistoryPanel historyPanel = new AppointmentHistoryPanel(service, customerId);
        FeedbackPanel feedbackPanel         = new FeedbackPanel(service, customerId);
        CustomerProfilePanel profilePanel   = new CustomerProfilePanel(service);

        cardHost.add(overviewPanel, "OVERVIEW");
        cardHost.add(historyPanel, "HISTORY");
        cardHost.add(feedbackPanel, "FEEDBACK");
        cardHost.add(profilePanel, "PROFILE");

        btnOverview.addActionListener(_ -> cardLayout.show(cardHost, "OVERVIEW"));
        btnHistory .addActionListener(_ -> { historyPanel.refresh(); cardLayout.show(cardHost, "HISTORY"); });
        btnFeedback.addActionListener(_ -> { feedbackPanel.refresh(); cardLayout.show(cardHost, "FEEDBACK"); });
        btnProfile .addActionListener(_ -> cardLayout.show(cardHost, "PROFILE"));
        btnLogout  .addActionListener(_ -> {
            try { SessionBus.clear(); } catch (Throwable ignored) {}
            dispose();
            SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
        });

        add(sidebar, BorderLayout.WEST);
        add(cardHost, BorderLayout.CENTER);
    }

    private void loadOverview() {
        List<Appointment> list = service.listAppointmentsByCustomer(customerId);
        LocalDateTime now = LocalDateTime.now();
        Appointment next = list.stream()
                .map(a -> new Object[]{a, resolveDateTime(a)})
                .filter(arr -> arr[1] != null && ((LocalDateTime)arr[1]).isAfter(now))
                .sorted(Comparator.comparing(arr -> (LocalDateTime)arr[1]))
                .map(arr -> (Appointment)arr[0])
                .findFirst()
                .orElse(null);

        if (next == null) {
            lblNextAppt.setText("Next appointment: none scheduled");
        } else {
            LocalDateTime dt = resolveDateTime(next);
            String when   = (dt != null) ? dt.format(DISPLAY_FMT) : "—";
            String doctor = service.doctorNameOrPlaceholder(next.getDoctorId());
            String status = next.getStatus() != null ? " [" + next.getStatus() + "]" : "";
            lblNextAppt.setText("Next appointment: " + when + " with " + doctor + status);
        }
    }


    private String resolveDisplayName(User current) {
        try {
            if (current == null) return null;
            if (current.getName() != null && !current.getName().isBlank()) return current.getName();

            UserController uc = new UserController();
            try {
                Method m = uc.getClass().getMethod("findById", String.class);
                Object r = m.invoke(uc, current.getUserId());
                User u = unwrapUser(r);
                if (u != null && u.getName() != null && !u.getName().isBlank()) return u.getName();
            } catch (NoSuchMethodException ignored) {}
            try {
                Method m = uc.getClass().getMethod("getById", String.class);
                Object r = m.invoke(uc, current.getUserId());
                User u = unwrapUser(r);
                if (u != null && u.getName() != null && !u.getName().isBlank()) return u.getName();
            } catch (NoSuchMethodException ignored) {}
            if (current.getEmail() != null && !current.getEmail().isBlank()) {
                try {
                    Method m = uc.getClass().getMethod("findByEmail", String.class);
                    Object r = m.invoke(uc, current.getEmail());
                    User u = unwrapUser(r);
                    if (u != null && u.getName() != null && !u.getName().isBlank()) return u.getName();
                } catch (NoSuchMethodException ignored) {}
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private User unwrapUser(Object r) {
        if (r == null) return null;
        if (r instanceof User) return (User) r;
        if (r instanceof Optional) {
            try { return ((Optional<User>) r).orElse(null); }
            catch (Throwable ignored) {}
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

    private LocalDateTime resolveDateTime(Appointment a) {
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

        String[] isoGetters = {"getDateTimeIso", "getDatetimeIso", "getDateTimeISO", "getDatetimeISO"};
        for (String g : isoGetters) {
            try {
                Method m = a.getClass().getMethod(g);
                Object v = m.invoke(a);
                if (v instanceof String) {
                    String s = (String) v;
                    try { return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME); }
                    catch (Throwable ignored) {
                        LocalDateTime p = tryParseDateTime(s);
                        if (p != null) return p;
                    }
                }
            } catch (Throwable ignored) {}
        }

        try {
            Method md = a.getClass().getMethod("getDate");
            Object vd = md.invoke(a);
            LocalDate d = null;
            if (vd instanceof LocalDate) d = (LocalDate) vd;
            else if (vd instanceof String) d = tryParseDate((String) vd);

            LocalTime t = LocalTime.MIDNIGHT;
            try {
                Method mt = a.getClass().getMethod("getTime");
                Object vt = mt.invoke(a);
                if (vt instanceof LocalTime) t = (LocalTime) vt;
                else if (vt instanceof String) t = tryParseTime((String) vt);
            } catch (Throwable ignored2) {}

            if (d != null) return LocalDateTime.of(d, t != null ? t : LocalTime.MIDNIGHT);
        } catch (Throwable ignored) {}

        return null;
    }

    private LocalDate tryParseDate(String s) {
        if (s == null || s.isBlank()) return null;
        String[] fmts = {"yyyy-MM-dd","dd/MM/yyyy","MM/dd/yyyy"};
        for (String f: fmts) {
            try { return LocalDate.parse(s, DateTimeFormatter.ofPattern(f)); } catch (Throwable ignored) {}
        }
        try { return LocalDate.parse(s); } catch (Throwable ignored) {}
        return null;
    }

    private LocalTime tryParseTime(String s) {
        if (s == null || s.isBlank()) return null;
        String[] fmts = {"HH:mm","HH:mm:ss","h:mm a"};
        for (String f: fmts) {
            try { return LocalTime.parse(s, DateTimeFormatter.ofPattern(f)); } catch (Throwable ignored) {}
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
        for (String f: fmts) {
            try { return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(f)); } catch (Throwable ignored) {}
        }
        try { return LocalDateTime.parse(s); } catch (Throwable ignored) {}
        return null;
    }
}
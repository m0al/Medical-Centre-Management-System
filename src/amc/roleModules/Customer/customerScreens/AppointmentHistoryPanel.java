package amc.roleModules.Customer.customerScreens;

import amc.dataModels.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Method;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class AppointmentHistoryPanel extends JPanel {
    private final CustomerService service;
    private final String customerId;

    private JComboBox<String> statusFilter;
    private JSpinner startDate;
    private JSpinner endDate;
    private DefaultTableModel tableModel;
    private JTable table;

    private boolean useDateFilter = true;

    private static final DateTimeFormatter DISPLAY_DT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AppointmentHistoryPanel(CustomerService service, String customerId) {
        this.service = service;
        this.customerId = customerId;
        initUI();
        refresh();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusFilter = new JComboBox<>(new String[]{"All","PENDING","CONFIRMED","COMPLETED","CANCELLED"});

        startDate = new JSpinner(new SpinnerDateModel());
        endDate   = new JSpinner(new SpinnerDateModel());
        startDate.setEditor(new JSpinner.DateEditor(startDate, "yyyy-MM-dd"));
        endDate.setEditor(new JSpinner.DateEditor(endDate, "yyyy-MM-dd"));


        LocalDate today = LocalDate.now();
        LocalDate first = today.minusDays(30);
        LocalDate last  = today;
        startDate.setValue(java.sql.Timestamp.valueOf(first.atStartOfDay()));
        endDate.setValue(java.sql.Timestamp.valueOf(last.atTime(23, 59)));

        JButton btnApply = new JButton("Apply");
        JButton btnClear = new JButton("Clear");
        JButton btnView  = new JButton("View Note");

        filters.add(new JLabel("Status:"));
        filters.add(statusFilter);
        filters.add(new JLabel("From:"));
        filters.add(startDate);
        filters.add(new JLabel("To:"));
        filters.add(endDate);
        filters.add(btnApply);
        filters.add(btnClear);
        filters.add(btnView);

        tableModel = new DefaultTableModel(
                new Object[]{"Date/Time","Doctor","Status","Charge","Note"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);

        btnApply.addActionListener(_ -> {
            useDateFilter = true;
            java.util.Date from = (java.util.Date) startDate.getValue();
            java.util.Date to   = (java.util.Date) endDate.getValue();
            if (from != null && to != null && from.after(to)) {
                java.util.Date tmp = from;
                startDate.setValue(to);
                endDate.setValue(tmp);
            }
            refresh();
        });

        btnClear.addActionListener(_ -> {

            useDateFilter = false;
            statusFilter.setSelectedIndex(0);
            LocalDate t = LocalDate.now();
            LocalDate f = t.minusDays(30);
            startDate.setValue(java.sql.Timestamp.valueOf(f.atStartOfDay()));
            endDate.setValue(java.sql.Timestamp.valueOf(t.atTime(23, 59)));
            refresh();
        });

        btnView.addActionListener(_ -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an appointment first");
                return;
            }
            String notes = Objects.toString(table.getValueAt(row, 4), "");
            JOptionPane.showMessageDialog(
                    this,
                    notes.isBlank() ? "No note recorded." : notes,
                    "Doctor's Note",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        add(filters, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

public void refresh() {
    List<Appointment> all = service.listAppointmentsByCustomer(customerId);
    // اختَر القيمة من الكومبو ونظّفها
    String s = Objects.toString(statusFilter.getSelectedItem(), "All").trim();

    final LocalDateTime fromDtF;
    final LocalDateTime toDtF;
    if (useDateFilter) {
        java.util.Date from = (java.util.Date) startDate.getValue();
        java.util.Date to   = (java.util.Date) endDate.getValue();
        fromDtF = (from != null)
                ? LocalDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault())
                : null;
        toDtF = (to != null)
                ? LocalDateTime.ofInstant(to.toInstant(), ZoneId.systemDefault())
                : null;
    } else {
        fromDtF = null;
        toDtF = null;
    }

    var filtered = all.stream()
           
            .filter(a -> {
                if ("All".equalsIgnoreCase(s)) return true;
                String st = (a != null && a.getStatus() != null) ? a.getStatus().trim() : "";
                return s.equalsIgnoreCase(st);
            })
            // ✅ فلتر التاريخ (إن كان مفعّل)
            .filter(a -> {
                if (!useDateFilter) return true;
                LocalDateTime dt = resolveDateTime(a);
                boolean okFrom = (fromDtF == null) || (dt != null && !dt.isBefore(fromDtF));
                boolean okTo   = (toDtF   == null) || (dt != null && !dt.isAfter(toDtF.with(LocalTime.MAX)));
                return okFrom && okTo;
            })
            // الأحدث أولاً
            .sorted((x, y) -> {
                LocalDateTime dx = resolveDateTime(x);
                LocalDateTime dy = resolveDateTime(y);
                if (dx == null && dy == null) return 0;
                if (dx == null) return 1;
                if (dy == null) return -1;
                return dy.compareTo(dx);
            })
            .toList();

    // تعبئة الجدول
    tableModel.setRowCount(0);
    for (Appointment a : filtered) {
        LocalDateTime dt = resolveDateTime(a);
        String when   = (dt != null) ? dt.format(DISPLAY_DT) : "—";
        String doctor = service.doctorNameOrPlaceholder(a.getDoctorId());
        tableModel.addRow(new Object[]{ when, doctor, a.getStatus(), a.getCharge(), safeNote(a) });
    }
}

    private String safeNote(Appointment a) {
        if (a == null) return "";
        try {
            String n = a.getNote();
            if (n != null) return n;
        } catch (Throwable ignored) {}
        try { var m = a.getClass().getMethod("getDoctorNote"); Object v = m.invoke(a); return v != null ? v.toString() : ""; } catch (Throwable ignored) {}
        try { var m = a.getClass().getMethod("getDiagnosis");  Object v = m.invoke(a); return v != null ? v.toString() : ""; } catch (Throwable ignored) {}
        return "";
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
        for (String f : fmts) {
            try { return LocalDate.parse(s, DateTimeFormatter.ofPattern(f)); }
            catch (Throwable ignored) {}
        }
        try { return LocalDate.parse(s); } catch (Throwable ignored) {}
        return null;
    }

    private LocalTime tryParseTime(String s) {
        if (s == null || s.isBlank()) return null;
        String[] fmts = {"HH:mm","HH:mm:ss","h:mm a"};
        for (String f : fmts) {
            try { return LocalTime.parse(s, DateTimeFormatter.ofPattern(f)); }
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
            try { return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(f)); }
            catch (Throwable ignored) {}
        }
        try { return LocalDateTime.parse(s); } catch (Throwable ignored) {}
        return null;
    }
}
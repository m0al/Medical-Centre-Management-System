package amc.roleModules.Customer.customerScreens;

import amc.dataModels.Appointment;
import amc.dataModels.Feedback;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class FeedbackPanel extends JPanel {
    private final CustomerService service;
    private final String customerId;

    private JComboBox<Appointment> apptCombo;
    private JTextField doctorField;
    private JSpinner ratingSpinner;
    private JTextArea commentArea;
    private DefaultTableModel tableModel;
    private JButton submitBtn;

    public FeedbackPanel(CustomerService service, String customerId) {
        this.service   = service;
        this.customerId = customerId;
        initUI();
        reloadAppointmentsForFeedback();
        loadFeedbacks();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        apptCombo = new JComboBox<>();
        apptCombo.addActionListener(_ -> onApptChange());

        doctorField = new JTextField();
        doctorField.setEditable(false);

        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        commentArea   = new JTextArea(4, 30);

        submitBtn = new JButton("Submit Feedback");
        submitBtn.addActionListener(_ -> submit());

        int row = 0;
        c.gridy = row;   c.gridx = 0; c.weightx = 0; form.add(new JLabel("Completed Appointment:"), c);
        c.gridx = 1; c.weightx = 1; form.add(apptCombo, c);

        c.gridy = ++row; c.gridx = 0; c.weightx = 0; form.add(new JLabel("Doctor:"), c);
        c.gridx = 1; c.weightx = 1; form.add(doctorField, c);

        c.gridy = ++row; c.gridx = 0; c.weightx = 0; form.add(new JLabel("Rating (1–5):"), c);
        c.gridx = 1; c.weightx = 1; form.add(ratingSpinner, c);

        c.gridy = ++row; c.gridx = 0; c.weightx = 0; form.add(new JLabel("Comment:"), c);
        c.gridx = 1; c.weightx = 1; form.add(new JScrollPane(commentArea), c);

        c.gridy = ++row; c.gridx = 1; c.weightx = 1; form.add(submitBtn, c);

        tableModel = new DefaultTableModel(new Object[]{"When", "To Doctor", "Rating", "Comment"}, 0) {
            @Override public boolean isCellEditable(int r, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void onApptChange() {
        Appointment a = (Appointment) apptCombo.getSelectedItem();
        doctorField.setText(a != null ? service.doctorNameOrPlaceholder(a.getDoctorId()) : "");
    }


    public void refresh() {
        reloadAppointmentsForFeedback();
        loadFeedbacks();
    }

    private void reloadAppointmentsForFeedback() {

        List<Appointment> completed = service.listCompletedAppointments(customerId);


        List<Feedback> myFbs = service.listFeedbacksForCustomer(customerId);

        Set<String> alreadyRated = new HashSet<>();
        for (Feedback f : myFbs) {
            if (f != null && f.getAppointmentId() != null) {
                alreadyRated.add(f.getAppointmentId());
            }
        }

        List<Appointment> available = completed.stream()
                .filter(a -> a != null && a.getAppointmentId() != null)
                .filter(a -> !alreadyRated.contains(a.getAppointmentId()))
                .collect(Collectors.toList());


        DefaultComboBoxModel<Appointment> model = new DefaultComboBoxModel<>();
        for (Appointment a : available) model.addElement(a);
        apptCombo.setModel(model);


        onApptChange();
        submitBtn.setEnabled(!available.isEmpty());
    }

    private void loadFeedbacks() {
        List<Feedback> my = service.listFeedbacksForCustomer(customerId);
        tableModel.setRowCount(0);
        for (Feedback f : my) {
            tableModel.addRow(new Object[]{
                    f.getTimestampIso(),
                    service.doctorNameOrPlaceholder(f.getToUserId()),
                    f.getRating(),
                    f.getComment()
            });
        }
    }

    private void submit() {
        Appointment a = (Appointment) apptCombo.getSelectedItem();
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Choose a completed appointment first.");
            return;
        }

        String apptId = a.getAppointmentId();
        boolean alreadyRated = service.listFeedbacksForCustomer(customerId).stream()
                .anyMatch(f -> f != null && apptId.equals(f.getAppointmentId()));
        if (alreadyRated) {
            JOptionPane.showMessageDialog(this, "You already submitted feedback for this appointment.");
            reloadAppointmentsForFeedback();
            return;
        }

        int rating = (int) ratingSpinner.getValue();
        String comment = commentArea.getText().trim();

        if (rating < 1 || rating > 5) {
            JOptionPane.showMessageDialog(this, "Rating must be between 1 and 5.");
            return;
        }
        if (comment.isBlank()) {
            JOptionPane.showMessageDialog(this, "Comment cannot be empty.");
            return;
        }

        try {
            Feedback saved = service.submitFeedback(customerId, a.getDoctorId(), apptId, rating, comment);
            if (saved == null) {
                JOptionPane.showMessageDialog(this, "Failed to submit feedback.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Thanks! Feedback submitted.");
            commentArea.setText("");
            ratingSpinner.setValue(5);

            loadFeedbacks();
            reloadAppointmentsForFeedback();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit feedback: " + ex.getMessage());
        }
    }
}
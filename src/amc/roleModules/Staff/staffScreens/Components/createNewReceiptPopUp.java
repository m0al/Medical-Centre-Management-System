package amc.roleModules.Staff.staffScreens.Components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import amc.roleModules.Staff.staffUtil.createNewItem;
import amc.dataModels.Appointment;
import amc.dataAccess.AppointmentRepository;

public class createNewReceiptPopUp extends JFrame {
    private JTextField appointmentIdField, amountField;
    private JComboBox<String> paymentMethodCombo;
    private JButton submitButton;

    public createNewReceiptPopUp() {
        setTitle("Create Receipt");
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 6));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Appointment ID
        mainPanel.add(new JLabel("Appointment ID:"));
        appointmentIdField = new JTextField();
        mainPanel.add(appointmentIdField);

        // Amount
        mainPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        mainPanel.add(amountField);

        // Payment Method (dropdown)
        mainPanel.add(new JLabel("Payment Method:"));
        String[] methods = {"EWALLET", "CASH", "CARD"};
        paymentMethodCombo = new JComboBox<>(methods);
        mainPanel.add(paymentMethodCombo);

        // Submit Button
        submitButton = new JButton("Generate Receipt");
        submitButton.setBackground(Color.decode("#1E90FF"));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        mainPanel.add(submitButton);
        mainPanel.add(new JLabel("")); // filler for grid alignment

        add(mainPanel);

        // Button Action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateForm();
            }
        });
    }

    private void validateForm() {
        String appointmentId = appointmentIdField.getText().trim();
        String amountText = amountField.getText().trim();
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

        if (appointmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Appointment ID is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Amount is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get current date & time in ISO 8601
        LocalDateTime now = LocalDateTime.now();
        String isoDateTime = now.format(DateTimeFormatter.ISO_DATE_TIME);

        JOptionPane.showMessageDialog(this,
                "Receipt Created:\nAppointment ID: " + appointmentId +
                        "\nAmount: " + amount +
                        "\nPayment Method: " + paymentMethod +
                        "\nDateTime: " + isoDateTime,
                "Success", JOptionPane.INFORMATION_MESSAGE);

        // Checks if the appointment exists or not
        AppointmentRepository appRepo = new AppointmentRepository();
        List<Appointment> allAppointment = appRepo.listAll();

        boolean found = false;
        for (Appointment a: allAppointment) {
            if (a.getAppointmentId().equals(appointmentId)) {
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "This appointment does not exist!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } else {
            createNewItem.createNewReceipt(appointmentId, amount, paymentMethod, isoDateTime);
        }

        this.dispose();
    }
}

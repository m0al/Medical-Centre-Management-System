package amc.roleModules.Staff.staffScreens.Components;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import com.toedter.calendar.JDateChooser;
import amc.dataConstants.RoleTypes;
import amc.userSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import amc.roleModules.Staff.staffUtil.createNewItem;
import amc.dataConstants.AppointmentStatusTypes;
public class createNewAppointmentPopUp extends JFrame {

    private JComboBox<String> doctorNameField, customerNameField;
    private JTextField chargeField;
    private JSpinner timeSpinner;
    private JButton submitButton;
    private JDateChooser dateChooser;
    private JTextArea noteArea; // Note field

    public createNewAppointmentPopUp() {
        setTitle("New Appointment");
        setSize(500, 450);
        setLocationRelativeTo(null); // Center the frame

        JPanel mainPanel = new JPanel(new GridLayout(7, 2, 10, 6));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // All the users in the database
        UserRepository userRepo = new UserRepository();
        List<User> allUsers = userRepo.findAll();
        // Customer ID
        mainPanel.add(new JLabel("Customer ID:"));

        List<String> customersArray = new ArrayList<>();

        for (User u: allUsers) {
            if (u.getRole().equals(RoleTypes.customer)) {
                customersArray.add(u.getName());
            }
        }

        String[] customerOptions = customersArray.toArray(new String[0]);

        customerNameField = new JComboBox<>(customerOptions);
        mainPanel.add(customerNameField);


        // Doctor ID
        List<String> doctorsArray = new ArrayList<>();

        for (User u: allUsers) {
            if (u.getRole().equals(RoleTypes.doctor)) {
                doctorsArray.add(u.getName());
            }
        }

        String[] doctorOptions = doctorsArray.toArray(new String[0]);

        mainPanel.add(new JLabel("Doctor ID:"));
        doctorNameField = new JComboBox<>(doctorOptions);
        mainPanel.add(doctorNameField);

        // Date Picker
        mainPanel.add(new JLabel("Date:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // ISO-style date
        mainPanel.add(dateChooser);

        // Time Picker
        mainPanel.add(new JLabel("Time:"));
        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm"); // 24-hour format
        timeSpinner.setEditor(timeEditor);
        mainPanel.add(timeSpinner);

        // Charge input
        mainPanel.add(new JLabel("Charge (RM):"));
        chargeField = new JTextField();
        mainPanel.add(chargeField);

        // Note (multi-line)
        mainPanel.add(new JLabel("Note:"));
        noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        mainPanel.add(noteScroll);

        // Submit Button
        submitButton = new JButton("Book Appointment");
        styleButton(submitButton);
        mainPanel.add(submitButton);
        mainPanel.add(new JLabel("")); // Empty for alignment

        add(mainPanel);

        // Action Listener for button
        submitButton.addActionListener((ActionEvent e) -> validateForm());
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.decode("#1E90FF"));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
    }

    private void validateForm() {
        String customerName = customerNameField.getSelectedItem().toString();
        String doctorName = doctorNameField.getSelectedItem().toString();
        Date date = dateChooser.getDate();
        Date time = (Date) timeSpinner.getValue();
        String chargeText = chargeField.getText().trim();
        String note = noteArea.getText().trim();

        if (customerName.isEmpty() || doctorName.isEmpty() || date == null || time == null || chargeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields except Note are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double charge;
        try {
            charge = Double.parseDouble(chargeText);
            if (charge < 0) throw new NumberFormatException("Negative not allowed");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for Charge!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Format date and time into ISO 8601
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String isoDateTime = sdfDate.format(date) + "T" + sdfTime.format(time);

        JOptionPane.showMessageDialog(this,
                "Appointment Created:\nCustomer: " + customerName +
                        "\nDoctor: " + doctorName +
                        "\nDateTime: " + isoDateTime +
                        "\nCharge: RM " + charge +
                        (note.isEmpty() ? "" : "\nNote: " + note),
                "Success", JOptionPane.INFORMATION_MESSAGE
        );

        UserRepository repo = new UserRepository();
        String customerId = String.valueOf(repo.findByName(customerName).get().getUserId());
        String doctorId = String.valueOf(repo.findByName(doctorName).get().getUserId());

        createNewItem.createNewAppointment(customerId, doctorId, isoDateTime, note, AppointmentStatusTypes.confirmed, charge, userSession.getName());
        dispose();
    }

}

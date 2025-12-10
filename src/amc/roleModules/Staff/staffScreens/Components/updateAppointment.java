package amc.roleModules.Staff.staffScreens.Components;


import amc.dataAccess.AppointmentRepository;
import amc.dataAccess.UserRepository;
import amc.dataConstants.RoleTypes;
import amc.dataConstants.AppointmentStatusTypes;
import amc.dataModels.User;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import amc.dataModels.Appointment;

public class updateAppointment extends JFrame {
    private JComboBox<String> customerDropdown, doctorDropdown, statusDropdown;
    private JDateChooser dateChooser;
    private JTextField timeField, chargeField;
    private JTextArea noteArea;
    private JButton saveButton, cancelButton;

    // Store original values
    private String appointmetnID, originalStatus, originalCustomerId, originalDoctorId, originalTime, originalNote;
    private double originalCharge;
    private Date originalDate;

    public updateAppointment(Appointment app) throws ParseException {
        setTitle("Update Appointment");
        setSize(480, 420);
        setLocationRelativeTo(null); // Center window
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        UserRepository userRepo = new UserRepository();
        List<User> allUsers = userRepo.findAll();


        appointmetnID = app.getAppointmentId();
        String customerId = app.getCustomerId();
        String doctorId = app.getDoctorId();
        isoTimeSplitter timeSplitter = new isoTimeSplitter(app.getDateTimeIso());
        Date date = timeSplitter.getDate();
        String time = timeSplitter.getTime();
        double charge = app.getCharge();
        String note = app.getNotes();
        String status = app.getStatus();

        // Save original values
        this.originalCustomerId = customerId;
        this.originalDoctorId = doctorId;
        this.originalDate = date;
        this.originalTime = time;
        this.originalCharge = charge;
        this.originalNote = note;
        this.originalStatus = status;

        JPanel mainPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));


        List<String> customersArray = new ArrayList<>();
        String currentCustomer = "";

        for (User u: allUsers) {
            if (u.getRole().equals(RoleTypes.customer)) {
                customersArray.add(u.getName());
            }

            if (u.getUserId() != null && u.getUserId().equals(customerId)) {
                currentCustomer = u.getName();
            }
        }
        String[] customerOptions = customersArray.toArray(new String[0]);

        // Customer dropdown
        mainPanel.add(new JLabel("Customer Name:"));
        customerDropdown = new JComboBox<>(customerOptions);

        customerDropdown.setSelectedItem(currentCustomer);
        mainPanel.add(customerDropdown);

        // Doctor dropdown
        List<String> doctorsArray = new ArrayList<>();
        String currentDoctor = "";

        for (User u: allUsers) {
            if (u.getRole().equals(RoleTypes.doctor)) {
                doctorsArray.add(u.getName());
            }

            if (u.getUserId() != null && u.getUserId().equals(doctorId)) {
                currentDoctor = u.getName();
            }
        }

        String[] doctorOptions = doctorsArray.toArray(new String[0]);
        mainPanel.add(new JLabel("Doctor Name:"));
        doctorDropdown = new JComboBox<>(doctorOptions);
        doctorDropdown.setSelectedItem(currentDoctor);
        mainPanel.add(doctorDropdown);

        // Date
        mainPanel.add(new JLabel("Date:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(date);
        mainPanel.add(dateChooser);

        // Status
        mainPanel.add(new JLabel("Status:"));
        String[] statusType = {AppointmentStatusTypes.confirmed, AppointmentStatusTypes.completed, AppointmentStatusTypes.cancelled, AppointmentStatusTypes.pending};
        statusDropdown = new JComboBox<>(statusType);
        statusDropdown.setSelectedItem(this.originalStatus); // pre-select current status
        mainPanel.add(statusDropdown);

        // Time
        mainPanel.add(new JLabel("Time (HH:mm):"));
        timeField = new JTextField(time);
        mainPanel.add(timeField);

        // Charge
        mainPanel.add(new JLabel("Charge:"));
        chargeField = new JTextField(String.valueOf(charge));
        mainPanel.add(chargeField);

        // Note
        mainPanel.add(new JLabel("Note:"));
        noteArea = new JTextArea(note, 3, 20);
        JScrollPane scrollPane = new JScrollPane(noteArea);
        mainPanel.add(scrollPane);

        // Buttons
        saveButton = new JButton("Save Changes");
        styleBlueButton(saveButton);

        cancelButton = new JButton("Cancel");
        styleBlueButton(cancelButton);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        saveButton.addActionListener(this::handleSave);
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setBackground(Color.GRAY);
    }

    private void styleBlueButton(JButton button) {
        button.setBackground(Color.decode("#1E90FF"));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
    }

    private void styleRedButton(JButton button) {
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 30));
    }

    private void handleSave(ActionEvent e) {
        Map<String, String> changes = new HashMap<>();
        SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat isoTime = new SimpleDateFormat("HH:mm");

// Get selected values
        Date selectedDate = dateChooser.getDate();
        String selectedTime = timeField.getText().trim();

        // Compare fields
        if (!customerDropdown.getSelectedItem().toString().equals(originalCustomerId)) {
            changes.put("customerId", customerDropdown.getSelectedItem().toString());
        }
        if (!doctorDropdown.getSelectedItem().toString().equals(originalDoctorId)) {
            changes.put("doctorId", doctorDropdown.getSelectedItem().toString());
        }
        if (dateChooser.getDate() != null && !dateChooser.getDate().equals(originalDate)) {
            changes.put("date", isoDate.format(dateChooser.getDate()));
        }
        if (!timeField.getText().trim().equals(originalTime)) {
            changes.put("time", timeField.getText().trim());
        }
        if (!chargeField.getText().trim().equals(originalCharge)) {
            changes.put("charge", chargeField.getText().trim());
        }
        if (!noteArea.getText().trim().equals(originalNote)) {
            changes.put("note", noteArea.getText().trim());
        }
        if (!statusDropdown.getSelectedItem().toString().equals(originalStatus)) {
            changes.put("status", statusDropdown.getSelectedItem().toString());
        }

        if (changes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No changes detected.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // TODO: integrate with save/update logic
            JOptionPane.showMessageDialog(this, "Changes to apply:\n" + changes, "Updated Fields", JOptionPane.INFORMATION_MESSAGE);
        }


        UserRepository userRepo = new UserRepository();

        String customerID = userRepo.findByName(customerDropdown.getSelectedItem().toString()).get().getUserId();
        String doctorID = userRepo.findByName(doctorDropdown.getSelectedItem().toString()).get().getUserId();
        String isoDateTime = isoDate.format(selectedDate) + "T" + selectedTime;

        AppointmentRepository appRepo = new AppointmentRepository();
        Appointment newAppointment = new Appointment(appointmetnID, customerID, doctorID, isoDateTime, noteArea.getText().trim().toString(), statusDropdown.getSelectedItem().toString(), Double.parseDouble(chargeField.getText().trim().toString()), "X");

        appRepo.saveOrUpdate(newAppointment);

        dispose();
    }

}

// Simple class to split the date and the time from an iso format
class isoTimeSplitter {

    String timePart;
    Date datePart;
    public isoTimeSplitter(String isoDateTime) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        datePart = dateFormat.parse(isoDateTime.substring(0, 10));

        // Extract the time part (HH:mm:ss) as String
        timePart = isoDateTime.substring(11, 16); // "14:30"

    }

    public String getTime() {
        return timePart;
    }

    public Date getDate() {
        return datePart;
    }

}
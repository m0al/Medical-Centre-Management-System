// This file defines a dialog window for displaying the detailed information of a single appointment.
package amc.roleModules.Manager.managerScreens;

// Standard Java library imports for UI, date/time, and data structures.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// Project-specific imports.
import amc.dataModels.Appointment;
import amc.roleModules.Manager.managerUtil.AppointmentUtil; // For printAppointment

/**
 * PURPOSE: To display the full details of a selected appointment in a read-only format.
 * It also provides a button to print the appointment details to the console.
 */
public class AppointmentDetailsDialog extends JDialog {

    // STEP 1: Declare fields to hold the appointment data and name mappings.
    private final Appointment appointmentToDisplay;
    private final Map<String, String> doctorNameMap;
    private final Map<String, String> patientNameMap;

    /**
     * PURPOSE: The constructor for the AppointmentDetailsDialog.
     * INPUTS:
     *   owner: The parent window of this dialog.
     *   appointmentToDisplay: The Appointment object whose details are to be shown.
     *   doctorNameMap: A map of doctor IDs to their full names.
     *   patientNameMap: A map of patient IDs to their full names.
     * OUTPUT: A new instance of AppointmentDetailsDialog.
     */
    public AppointmentDetailsDialog(Window owner, Appointment appointmentToDisplay, Map<String, String> doctorNameMap, Map<String, String> patientNameMap) {
        // STEP 1: Call the super constructor to set up the dialog's basic properties.
        super(owner, "Appointment Details", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(owner); // Center dialog relative to its parent window.

        // STEP 2: Store the provided appointment data and name maps in instance fields.
        this.appointmentToDisplay = appointmentToDisplay;
        this.doctorNameMap = doctorNameMap;
        this.patientNameMap = patientNameMap;

        // STEP 3: Initialize and configure the UI components of the dialog.
        initializeUi();
    }

    /**
     * PURPOSE: To initialize and configure the UI components of the dialog.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void initializeUi() {
        // STEP 1: Create the main content panel with padding and a white background.
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // STEP 2: Create the panel that will display the appointment details.
        JPanel detailsPanel = createDetailsPanel();
        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        // STEP 3: Create the button panel at the bottom.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // STEP 3A: Create the "Print" button and add its action listener.
        JButton printButton = new JButton("Print");
        styleButton(printButton);
        printButton.addActionListener(event -> AppointmentUtil.printAppointment(appointmentToDisplay));
        buttonPanel.add(printButton);

        // STEP 3B: Create the "Close" button and add its action listener.
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(event -> dispose()); // Closes the dialog.
        buttonPanel.add(closeButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // STEP 4: Add the main content panel to the dialog.
        add(contentPanel);
    }

    /**
     * PURPOSE: To create the panel that displays all the appointment details.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the formatted appointment details.
     */
    private JPanel createDetailsPanel() {
        // STEP 1: Create the panel using GridBagLayout for precise component placement.
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components.
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // STEP 2: Add each detail row (label-value pair) to the panel.
        int currentRow = 0;
        currentRow = addDetailRow(panel, gbc, currentRow, "Appointment ID:", appointmentToDisplay.getAppointmentId());

        // STEP 3: Format and add the date and time.
        String dateTimeFormatted = "N/A";
        if (appointmentToDisplay.getDateTimeIso() != null) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(appointmentToDisplay.getDateTimeIso(), DateTimeFormatter.ISO_DATE_TIME);
                dateTimeFormatted = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception exception) {
                System.err.println("Error parsing date/time for display: " + exception.getMessage());
            }
        }
        currentRow = addDetailRow(panel, gbc, currentRow, "Date & Time:", dateTimeFormatted);

        // STEP 4: Get and add patient and doctor names using the provided maps.
        String patientName = patientNameMap.getOrDefault(appointmentToDisplay.getCustomerId(), "Unknown Patient");
        currentRow = addDetailRow(panel, gbc, currentRow, "Patient Name:", patientName);

        String doctorName = doctorNameMap.getOrDefault(appointmentToDisplay.getDoctorId(), "Unknown Doctor");
        currentRow = addDetailRow(panel, gbc, currentRow, "Doctor Name:", doctorName);

        currentRow = addDetailRow(panel, gbc, currentRow, "Status:", appointmentToDisplay.getStatus());

        // STEP 5: Add the Notes section using a JTextArea for multi-line text.
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align label to top-left.
        panel.add(notesLabel, gbc);

        JTextArea notesArea = new JTextArea(appointmentToDisplay.getNotes());
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(300, 60)); // Fixed size for notes area.
        gbc.gridx = 1;
        gbc.gridy = currentRow;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Allow notes area to expand vertically.
        gbc.fill = GridBagConstraints.BOTH; // Fill both horizontal and vertical space.
        panel.add(notesScrollPane, gbc);
        currentRow++;

        // STEP 6: Add the Charge detail.
        currentRow = addDetailRow(panel, gbc, currentRow, "Charge:", String.format("MYR %.2f", appointmentToDisplay.getCharge()));

        return panel;
    }

    /**
     * PURPOSE: A private helper method to add a label-value pair to a panel using GridBagLayout.
     * INPUTS:
     *   targetPanel: The JPanel to which components will be added.
     *   gbc: The GridBagConstraints object to configure layout.
     *   currentRow: The current row index in the layout.
     *   labelText: The text for the label (e.g., "Name:").
     *   valueText: The text for the value (e.g., "John Doe").
     * OUTPUT: The next available row index after adding the components.
     */
    private int addDetailRow(JPanel targetPanel, GridBagConstraints gbc, int currentRow, String labelText, String valueText) {
        // STEP 1: Create and style the label.
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.anchor = GridBagConstraints.WEST; // Align label to the left.
        targetPanel.add(label, gbc);

        // STEP 2: Create and style the value label.
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = currentRow;
        gbc.anchor = GridBagConstraints.WEST; // Align value to the left.
        targetPanel.add(value, gbc);

        // STEP 3: Return the next row index.
        return currentRow + 1;
    }

    /**
     * PURPOSE: A private helper to apply a consistent style to a JButton.
     * INPUTS: The JButton to be styled.
     * OUTPUT: None.
     */
    private void styleButton(JButton buttonToStyle) {
        // STEP 1: Set the font, colors, and remove the focus paint ring for a modern look.
        buttonToStyle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buttonToStyle.setBackground(new Color(0, 122, 255));
        buttonToStyle.setForeground(Color.WHITE);
        buttonToStyle.setFocusPainted(false);
    }
}
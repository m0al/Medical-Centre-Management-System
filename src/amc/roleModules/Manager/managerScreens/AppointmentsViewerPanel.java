// This file defines the user interface panel for viewing and filtering appointments.
package amc.roleModules.Manager.managerScreens;

// Third-party import for the date chooser component.
import com.toedter.calendar.JDateChooser;

// Standard Java library imports for UI, events, and data structures.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Vector;

// Project-specific imports.
import amc.dataConstants.AppointmentStatusTypes;
import amc.dataModels.Appointment;
import amc.roleModules.Manager.managerUtil.AppointmentUtil;
import amc.userSession;

/**
 * PURPOSE: To create a view-only user interface panel for displaying, searching,
 * and filtering all appointment records in the system.
 */
public class AppointmentsViewerPanel extends JPanel {

    // STEP 1: Declare all UI components as private fields.
    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;
    private JComboBox<String> doctorFilterComboBox;
    private JDateChooser fromDateField;
    private JDateChooser toDateField;
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private JButton viewPrintButton;

    // STEP 2: Declare maps to cache user IDs to names for efficient display.
    private final Map<String, String> doctorIdToName;
    private final Map<String, String> patientIdToName;

    /**
     * PURPOSE: The main constructor for the panel.
     * INPUTS: None.
     * OUTPUT: A fully initialized AppointmentsViewerPanel.
     */
    public AppointmentsViewerPanel() {
        // STEP 1: Load the user ID-to-name mappings once to improve performance.
        doctorIdToName = AppointmentUtil.loadDoctorIdToName();
        patientIdToName = AppointmentUtil.loadPatientIdToName();

        // STEP 2: Build the user interface.
        initializeUi();
        
        // STEP 3: Load the initial data into the table.
        refreshTable();
    }

    /**
     * PURPOSE: To set up the main layout and add the primary sub-panels.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void initializeUi() {
        // STEP 1: Set the main layout to a vertical box layout, and set background and padding.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // STEP 2: Add all the major UI sections to the panel.
        add(createHeaderPanel());
        add(Box.createVerticalStrut(10));
        add(createSearchFilterPanel());
        add(Box.createVerticalStrut(10));
        add(createTablePanel());
        add(createBottomPanel());
    }

    /**
     * PURPOSE: To create the top panel containing the title and welcome message.
     * INPUTS: None.
     * OUTPUT: A JPanel configured as the header.
     */
    private JPanel createHeaderPanel() {
        // STEP 1: Create the panel with a border layout and styling.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // STEP 2: Create and add the main title label.
        JLabel titleLabel = new JLabel("Appointments Viewer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // STEP 3: Create and add the welcome message.
        JLabel welcomeLabel = new JLabel("Welcome, " + userSession.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.GRAY);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * PURPOSE: To create the panel containing all the search and filter controls.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the interactive controls.
     */
    private JPanel createSearchFilterPanel() {
        // STEP 1: Create a flow layout panel for the controls.
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

        // STEP 2: Create the main search field and add a listener to it.
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createTitledBorder("Search (Patient, Doctor, ID)"));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { refreshTable(); }
            public void removeUpdate(DocumentEvent event) { refreshTable(); }
            public void changedUpdate(DocumentEvent event) { refreshTable(); }
        });
        panel.add(searchField);

        // STEP 3: Create the "Date From" chooser and add a listener.
        fromDateField = new JDateChooser();
        fromDateField.setDateFormatString("yyyy-MM-dd");
        fromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fromDateField.setBorder(BorderFactory.createTitledBorder("Date From"));
        fromDateField.getDateEditor().addPropertyChangeListener("date", event -> refreshTable());
        panel.add(fromDateField);

        // STEP 4: Create the "Date To" chooser and add a listener.
        toDateField = new JDateChooser();
        toDateField.setDateFormatString("yyyy-MM-dd");
        toDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toDateField.setBorder(BorderFactory.createTitledBorder("Date To"));
        toDateField.getDateEditor().addPropertyChangeListener("date", event -> refreshTable());
        panel.add(toDateField);

        // STEP 5: Create the status filter dropdown.
        String[] statuses = {"All", AppointmentStatusTypes.pending, AppointmentStatusTypes.confirmed, AppointmentStatusTypes.completed, AppointmentStatusTypes.cancelled};
        statusFilterComboBox = new JComboBox<>(statuses);
        statusFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilterComboBox.setBorder(BorderFactory.createTitledBorder("Status"));
        statusFilterComboBox.addActionListener(event -> refreshTable());
        panel.add(statusFilterComboBox);

        // STEP 6: Create the doctor filter dropdown.
        Vector<String> doctorNames = new Vector<>();
        doctorNames.add("All");
        doctorIdToName.values().forEach(doctorNames::add);
        doctorFilterComboBox = new JComboBox<>(doctorNames);
        doctorFilterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doctorFilterComboBox.setBorder(BorderFactory.createTitledBorder("Doctor"));
        doctorFilterComboBox.addActionListener(event -> refreshTable());
        panel.add(doctorFilterComboBox);
        
        // STEP 7: Create the "Clear Dates" button.
        JButton clearDatesButton = new JButton("Clear Dates");
        styleButton(clearDatesButton);
        clearDatesButton.addActionListener(event -> {
            fromDateField.setDate(null);
            toDateField.setDate(null);
            refreshTable();
        });
        panel.add(clearDatesButton);

        return panel;
    }

    /**
     * PURPOSE: To create the panel that contains the main appointment data table.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the appointment table within a scroll pane.
     */
    private JPanel createTablePanel() {
        // STEP 1: Create the panel with a border layout.
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // STEP 2: Define the table columns and create a non-editable table model.
        String[] columnNames = {"Appointment ID", "Date", "Time", "Patient Name", "Doctor Name", "Status", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentsTable = new JTable(tableModel);
        styleTable(appointmentsTable);

        // STEP 3: Add a listener to enable the "View Details" button only when one row is selected.
        appointmentsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                viewPrintButton.setEnabled(appointmentsTable.getSelectedRowCount() == 1);
            }
        });

        // STEP 4: Place the table inside a scroll pane.
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * PURPOSE: To create the bottom panel containing action buttons.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the bottom buttons.
     */
    private JPanel createBottomPanel() {
        // STEP 1: Create the panel with a border layout and padding.
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // STEP 2: Create the "View Details" button, initially disabled.
        viewPrintButton = new JButton("View Details");
        styleButton(viewPrintButton);
        viewPrintButton.setEnabled(false);
        viewPrintButton.addActionListener(event -> {
            // When clicked, get the selected appointment and show its details in a dialog.
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow != -1) {
                String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
                Appointment selectedAppointment = AppointmentUtil.getAppointmentById(appointmentId);
                if (selectedAppointment != null) {
                    AppointmentDetailsDialog detailsDialog = new AppointmentDetailsDialog(
                        SwingUtilities.getWindowAncestor(this), 
                        selectedAppointment, 
                        doctorIdToName, 
                        patientIdToName
                    );
                    detailsDialog.setVisible(true);
                }
            }
        });

        // STEP 3: Create the "Return to Dashboard" button.
        JButton returnButton = new JButton("Return to Dashboard");
        styleButton(returnButton);
        returnButton.addActionListener(event -> {
            // This action closes the current window.
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        });
        
        // STEP 4: Add the buttons to the panel.
        panel.add(viewPrintButton, BorderLayout.WEST);
        panel.add(returnButton, BorderLayout.EAST);

        return panel;
    }

    /**
     * PURPOSE: To refresh the data in the table based on all current filter settings.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void refreshTable() {
        // STEP 1: Read all appointments from the data source.
        List<Appointment> allAppointments = AppointmentUtil.readAppointments();

        // STEP 2: Get the current values from all filter components.
        String query = searchField.getText();
        String status = (String) statusFilterComboBox.getSelectedItem();
        String selectedDoctorName = (String) doctorFilterComboBox.getSelectedItem();

        // STEP 3: Convert the selected doctor's name back to an ID for filtering.
        String doctorId = null;
        if (selectedDoctorName != null && !selectedDoctorName.equalsIgnoreCase("All")) {
            for (Map.Entry<String, String> entry : doctorIdToName.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(selectedDoctorName)) {
                    doctorId = entry.getKey();
                    break;
                }
            }
        }

        // STEP 4: Get the selected dates from the date choosers.
        LocalDate fromDate = null;
        if (fromDateField.getDate() != null) {
            fromDate = fromDateField.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        LocalDate toDate = null;
        if (toDateField.getDate() != null) {
            toDate = toDateField.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        // STEP 5: Get the filtered list of appointments from the utility class.
        List<Appointment> filteredAppointments = AppointmentUtil.filterAppointments(
                allAppointments, query, fromDate, toDate, status, doctorId);

        // STEP 6: Clear the table and repopulate it with the filtered data.
        tableModel.setRowCount(0);
        for (Appointment app : filteredAppointments) {
            String patientName = patientIdToName.getOrDefault(app.getCustomerId(), "Unknown Patient");
            String doctorName = doctorIdToName.getOrDefault(app.getDoctorId(), "Unknown Doctor");

            tableModel.addRow(new Object[]{
                app.getAppointmentId(),
                app.getDateTimeIso() != null ? app.getDateTimeIso().substring(0, 10) : "N/A",
                app.getDateTimeIso() != null ? app.getDateTimeIso().substring(11, 16) : "N/A",
                patientName,
                doctorName,
                app.getStatus(),
                app.getNotes() != null ? app.getNotes() : ""
            });
        }
    }

    /**
     * PURPOSE: A private helper to apply a consistent style to a JTable.
     * INPUTS: The JTable to be styled.
     * OUTPUT: None.
     */
    private void styleTable(JTable tableToStyle) {
        // STEP 1: Set basic visual properties like font, row height, and grid color.
        tableToStyle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableToStyle.setRowHeight(30);
        tableToStyle.setGridColor(new Color(230, 230, 230));
        tableToStyle.setFillsViewportHeight(true);

        // STEP 2: Style the table's header for a professional look.
        JTableHeader header = tableToStyle.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
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

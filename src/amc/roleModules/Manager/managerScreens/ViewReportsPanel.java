// This file defines the user interface panel for viewing and generating reports.
package amc.roleModules.Manager.managerScreens;

// Third-party import for the date chooser component.
import com.toedter.calendar.JDateChooser;

// Standard Java library imports for UI, data models, and utilities.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Project-specific imports for data models and utility classes.
import amc.dataModels.Report;
import amc.roleModules.Manager.managerUtil.ViewReportsUtil;

/**
 * PURPOSE: Creates the Swing JPanel that allows managers to view a list of historical
 * reports, see the details of a selected report, generate a new report for the
 * current day, and view summary statistics.
 */
public class ViewReportsPanel extends JPanel {

    // STEP 1: Declare all UI components as private fields.
    // These are not renamed to maintain compatibility with UI designers and for clarity.
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private JTextArea reportDetailsArea;
    private JButton generateReportButton;
    private JButton viewReportButton;
    private JDateChooser dateChooser;
    private JLabel monthlyAppointmentsLabel;
    private JLabel monthlyRevenueLabel;

    // STEP 2: Declare the utility class that handles business logic.
    private final ViewReportsUtil util;

    /**
     * PURPOSE: The main constructor for the panel.
     * INPUTS: None.
     * OUTPUT: A fully initialized ViewReportsPanel.
     */
    public ViewReportsPanel() {
        // STEP 1: Instantiate the utility class to handle data operations.
        this.util = new ViewReportsUtil();
        // STEP 2: Build the user interface.
        initializeUi();
        // STEP 3: Load the initial list of reports into the table.
        refreshReportList();
        // STEP 4: Load the initial summary statistics.
        updateMonthlyStats();
    }

    /**
     * PURPOSE: To set up the main layout and add the primary sub-panels.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void initializeUi() {
        // STEP 1: Set the main layout, background color, and padding for the panel.
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // STEP 2: Add the header, main content, and footer panels to the layout.
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createMonthlyStatsPanel(), BorderLayout.SOUTH);
    }

    /**
     * PURPOSE: To create the top panel containing the title and a return button.
     * INPUTS: None.
     * OUTPUT: A JPanel configured as the header.
     */
    private JPanel createHeaderPanel() {
        // STEP 1: Create the panel with a border layout.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        // STEP 2: Create and style the main title label.
        JLabel titleLabel = new JLabel("View & Generate Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // STEP 3: Create, style, and add a listener to the return button.
        JButton returnButton = new JButton("Return to Dashboard");
        styleButton(returnButton);
        returnButton.addActionListener(event -> {
            // This action closes the current window.
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        });
        headerPanel.add(returnButton, BorderLayout.EAST);

        // STEP 4: Return the completed header panel.
        return headerPanel;
    }

    /**
     * PURPOSE: To create the central panel that holds the controls and the report display.
     * INPUTS: None.
     * OUTPUT: A JPanel configured as the main content area.
     */
    private JPanel createMainPanel() {
        // STEP 1: Create the panel with a border layout.
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        // STEP 2: Add the controls and the display panels to it.
        mainPanel.add(createControlsPanel(), BorderLayout.NORTH);
        mainPanel.add(createReportsDisplayPanel(), BorderLayout.CENTER);

        // STEP 3: Return the completed main panel.
        return mainPanel;
    }

    /**
     * PURPOSE: To create the panel with buttons and a date chooser for user interaction.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the user controls.
     */
    private JPanel createControlsPanel() {
        // STEP 1: Create a flow layout panel with a titled border.
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        // STEP 2: Create the "Generate Today's Report" button and add its action.
        generateReportButton = new JButton("Generate Today's Report");
        styleButton(generateReportButton);
        generateReportButton.addActionListener(event -> generateTodaysReport());
        controlsPanel.add(generateReportButton);

        // STEP 3: Create the date chooser component.
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        controlsPanel.add(dateChooser);

        // STEP 4: Create the "View Report" button and add its action.
        viewReportButton = new JButton("View Report for Selected Date");
        styleButton(viewReportButton);
        viewReportButton.addActionListener(event -> viewReportForSelectedDate());
        controlsPanel.add(viewReportButton);

        // STEP 5: Return the completed controls panel.
        return controlsPanel;
    }

    /**
     * PURPOSE: To create the panel that shows the list of reports and the details of a selected report.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the report table and details area.
     */
    private JPanel createReportsDisplayPanel() {
        // STEP 1: Create a grid layout panel to hold the table and text area side-by-side.
        JPanel displayPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        displayPanel.setBackground(Color.WHITE);
        displayPanel.setBorder(BorderFactory.createTitledBorder("Reports"));

        // STEP 2: Set up the table model, making cells non-editable.
        String[] columnNames = {"Report ID", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        // STEP 3: Create the table, style it, and add a listener for row selection.
        reportsTable = new JTable(tableModel);
        styleTable(reportsTable);
        reportsTable.getSelectionModel().addListSelectionListener(event -> {
            // When a row is selected, display that report's details.
            if (!event.getValueIsAdjusting() && reportsTable.getSelectedRow() != -1) {
                String reportId = (String) tableModel.getValueAt(reportsTable.getSelectedRow(), 0);
                displayReportDetails(reportId);
            }
        });
        displayPanel.add(new JScrollPane(reportsTable));

        // STEP 4: Set up the text area for displaying report details.
        reportDetailsArea = new JTextArea();
        reportDetailsArea.setEditable(false);
        reportDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        displayPanel.add(new JScrollPane(reportDetailsArea));

        // STEP 5: Return the completed display panel.
        return displayPanel;
    }

    /**
     * PURPOSE: To create the bottom panel that displays summary statistics for the last 30 days.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the statistics labels.
     */
    private JPanel createMonthlyStatsPanel() {
        // STEP 1: Create a flow layout panel with a titled border.
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Last 30 Days Summary"));

        // STEP 2: Create and style the labels for appointments and revenue.
        Font statsFont = new Font("Segoe UI", Font.BOLD, 16);
        monthlyAppointmentsLabel = new JLabel("Total Appointments: 0");
        monthlyAppointmentsLabel.setFont(statsFont);
        statsPanel.add(monthlyAppointmentsLabel);

        monthlyRevenueLabel = new JLabel("Total Revenue: $0.00");
        monthlyRevenueLabel.setFont(statsFont);
        statsPanel.add(monthlyRevenueLabel);

        // STEP 3: Return the completed statistics panel.
        return statsPanel;
    }

    /**
     * PURPOSE: To handle the logic for generating a report for the current day.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void generateTodaysReport() {
        // STEP 1: Check if a report for today already exists.
        if (util.doesReportExistForToday()) {
            // STEP 2: If it exists, ask the user for confirmation to overwrite it.
            int choice = JOptionPane.showConfirmDialog(this, 
                "A report for today already exists. Do you want to overwrite it?", 
                "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
            // STEP 3: If the user chooses not to overwrite, stop the process.
            if (choice == JOptionPane.NO_OPTION) {
                return;
            }
        }

        // STEP 4: Tell the utility class to generate the report.
        util.generateTodaysReport();
        // STEP 5: Show a success message and refresh the list of reports.
        JOptionPane.showMessageDialog(this, "Successfully generated today's report.", "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshReportList();
    }

    /**
     * PURPOSE: To handle viewing a report for a date selected in the JDateChooser.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void viewReportForSelectedDate() {
        // STEP 1: Check if a date has been selected.
        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select a date to view the report.", "No Date Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // STEP 2: Convert the selected date to a LocalDate object.
        LocalDate selectedDate = dateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        // STEP 3: Ask the utility class to find the report for that date.
        Report report = util.getReportByDate(selectedDate);
        // STEP 4: If a report is found, display its details. Otherwise, show a "not found" message.
        if (report != null) {
            displayReportDetails(report.getReportId());
        } else {
            reportDetailsArea.setText("No report found for " + selectedDate.toString());
        }
    }

    /**
     * PURPOSE: To clear and reload the list of reports in the main table.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void refreshReportList() {
        // STEP 1: Clear all existing rows from the table model.
        tableModel.setRowCount(0);
        // STEP 2: Get the latest list of all reports, sorted by date.
        List<Report> reports = util.getAllReportsSorted();
        // STEP 3: Loop through the reports and add each one as a new row in the table.
        for (Report report : reports) {
            tableModel.addRow(new Object[]{
                report.getReportId(),
                report.getGeneratedAtIso().substring(0, 10) // Display only the date part.
            });
        }
    }

    /**
     * PURPOSE: To display the full details of a specific report in the text area.
     * INPUTS: The ID of the report to display.
     * OUTPUT: None.
     */
    private void displayReportDetails(String reportId) {
        // STEP 1: Get the full report object from the utility class using its ID.
        Report report = util.getReportById(reportId);
        // STEP 2: If the report is found, build a formatted string with its details.
        if (report != null) {
            StringBuilder details = new StringBuilder();
            details.append("Report ID: ").append(report.getReportId()).append("\n");
            details.append("Title: ").append(report.getTitle()).append("\n");
            details.append("Generated At: ").append(report.getGeneratedAtIso()).append("\n");
            details.append("Generated By: ").append(report.getGeneratedByUserId()).append("\n\n");
            details.append("-------------------------------------").append("\n");
            details.append("Total Appointments: ").append(report.getTotalAppointments()).append("\n");
            details.append("Total Revenue: ").append(String.format("$%.2f", report.getTotalRevenue()));
            // STEP 3: Set the text of the details area to the formatted string.
            reportDetailsArea.setText(details.toString());
        }
    }

    /**
     * PURPOSE: To update the summary statistics labels at the bottom of the panel.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void updateMonthlyStats() {
        // STEP 1: Get the latest statistics from the utility class.
        Map<String, Number> stats = util.getMonthlyStatistics();
        long totalAppointments = (long) stats.get("totalAppointments");
        double totalRevenue = (double) stats.get("totalRevenue");

        // STEP 2: Update the text of the labels with the new formatted statistics.
        monthlyAppointmentsLabel.setText(String.format("Last 30 Days Appointments: %d", totalAppointments));
        monthlyRevenueLabel.setText(String.format("Last 30 Days Revenue: $%.2f", totalRevenue));
    }

    /**
     * PURPOSE: A private helper to apply a consistent style to a JTable.
     * INPUTS: The JTable to be styled.
     * OUTPUT: None.
     */
    private void styleTable(JTable tableToStyle) {
        // STEP 1: Set the font, row height, grid color, and other visual properties of the table.
        tableToStyle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableToStyle.setRowHeight(30);
        tableToStyle.setGridColor(new Color(230, 230, 230));
        tableToStyle.setFillsViewportHeight(true);
        
        // STEP 2: Style the table's header separately.
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
        // STEP 1: Set the font, background and foreground colors, and remove the focus paint ring.
        buttonToStyle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buttonToStyle.setBackground(new Color(0, 122, 255));
        buttonToStyle.setForeground(Color.WHITE);
        buttonToStyle.setFocusPainted(false);
    }
}

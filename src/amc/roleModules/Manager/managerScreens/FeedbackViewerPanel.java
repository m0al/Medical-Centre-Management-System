// This file defines the user interface panel for viewing and filtering user feedback.
package amc.roleModules.Manager.managerScreens;

// Project-specific imports for data models, utilities, and the user session.
import amc.dataModels.Feedback;
import amc.roleModules.Manager.managerUtil.FeedbackViewerUtil;
import amc.userSession;

// Standard Java library imports for UI, events, and data structures.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * PURPOSE: To create the Swing JPanel that allows managers to view, search, filter,
 * and sort all user-submitted feedback. It also displays summary statistics.
 */
public class FeedbackViewerPanel extends JPanel {

    // STEP 1: Declare all UI components as private fields.
    // These are not renamed to maintain compatibility with UI designers and for clarity.
    private JTable feedbackTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> userFilterComboBox;
    private JComboBox<String> ratingSortComboBox;

    private final JLabel avgTodayLabel = new JLabel();
    private final JLabel avgWeekLabel = new JLabel();
    private final JLabel avgMonthLabel = new JLabel();
    private final JLabel avgAllTimeLabel = new JLabel();

    // STEP 2: Declare the utility class that handles business logic.
    private final FeedbackViewerUtil util;

    /**
     * PURPOSE: The main constructor for the panel.
     * INPUTS: None.
     * OUTPUT: A fully initialized FeedbackViewerPanel.
     */
    public FeedbackViewerPanel() {
        // STEP 1: Instantiate the utility class to handle data operations.
        this.util = new FeedbackViewerUtil();
        // STEP 2: Build the user interface.
        initializeUi();
        // STEP 3: Load the initial data into the table and statistics panels.
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

        // STEP 2: Add all the major UI sections to the panel, separated by vertical struts for spacing.
        add(createHeaderPanel());
        add(Box.createVerticalStrut(10));
        add(createSearchFilterPanel());
        add(Box.createVerticalStrut(10));
        add(createTablePanel());
        add(Box.createVerticalStrut(10));
        add(createStatsPanel());
    }

    /**
     * PURPOSE: To create the top panel containing the title and welcome message.
     * INPUTS: None.
     * OUTPUT: A JPanel configured as the header.
     */
    private JPanel createHeaderPanel() {
        // STEP 1: Create the panel with a border layout and padding.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // STEP 2: Create and style the main title label.
        JLabel titleLabel = new JLabel("Feedback Viewer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // STEP 3: Create and style the welcome message using the current user's name.
        JLabel welcomeLabel = new JLabel("Welcome, " + userSession.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.GRAY);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        // STEP 4: Return the completed header panel.
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

        // STEP 2: Create the search text field and add a listener to refresh the table on any change.
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createTitledBorder("Search Comments"));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { refreshTable(); }
            public void removeUpdate(DocumentEvent event) { refreshTable(); }
            public void changedUpdate(DocumentEvent event) { refreshTable(); }
        });
        panel.add(searchField);

        // STEP 3: Create the user filter dropdown, populating it with all unique user names.
        Vector<String> userNames = new Vector<>();
        userNames.add("All Users");
        util.getUserIdToNameMap().values().stream().distinct().sorted().forEach(userNames::add);
        userFilterComboBox = new JComboBox<>(userNames);
        userFilterComboBox.setBorder(BorderFactory.createTitledBorder("Filter by User"));
        userFilterComboBox.addActionListener(event -> refreshTable());
        panel.add(userFilterComboBox);

        // STEP 4: Create the rating sort dropdown.
        String[] sortOptions = {"Default", "Rating: Ascending", "Rating: Descending"};
        ratingSortComboBox = new JComboBox<>(sortOptions);
        ratingSortComboBox.setBorder(BorderFactory.createTitledBorder("Sort by Rating"));
        ratingSortComboBox.addActionListener(event -> refreshTable());
        panel.add(ratingSortComboBox);
        
        // STEP 5: Create the "Return to Dashboard" button.
        JButton returnButton = new JButton("Return to Dashboard");
        styleButton(returnButton);
        returnButton.addActionListener(event -> {
            // This action closes the current window.
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        });
        panel.add(returnButton);

        // STEP 6: Return the completed controls panel.
        return panel;
    }

    /**
     * PURPOSE: To create the panel that contains the main feedback data table.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the feedback table within a scroll pane.
     */
    private JPanel createTablePanel() {
        // STEP 1: Create the panel with a border layout.
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // STEP 2: Define the table columns and create a non-editable table model.
        String[] columnNames = {"Feedback ID", "From User", "To User", "Rating", "Comment", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        feedbackTable = new JTable(tableModel);
        styleTable(feedbackTable);

        // STEP 3: Place the table inside a scroll pane to allow scrolling.
        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        // STEP 4: Return the completed table panel.
        return panel;
    }

    /**
     * PURPOSE: To create the bottom panel that displays summary statistics.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the statistics labels.
     */
    private JPanel createStatsPanel() {
        // STEP 1: Create a flow layout panel with a titled border.
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Average Ratings"));

        // STEP 2: Set a consistent font for all statistics labels.
        Font statsFont = new Font("Segoe UI", Font.BOLD, 14);
        avgTodayLabel.setFont(statsFont);
        avgWeekLabel.setFont(statsFont);
        avgMonthLabel.setFont(statsFont);
        avgAllTimeLabel.setFont(statsFont);

        // STEP 3: Add the labels to the panel. Their text will be set later.
        statsPanel.add(avgTodayLabel);
        statsPanel.add(avgWeekLabel);
        statsPanel.add(avgMonthLabel);
        statsPanel.add(avgAllTimeLabel);

        // STEP 4: Return the completed statistics panel.
        return statsPanel;
    }

    /**
     * PURPOSE: To refresh the data in the table and statistics panels based on current filter settings.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void refreshTable() {
        // STEP 1: Get the current values from all filter and sort components.
        String query = searchField.getText().toLowerCase();
        String selectedUser = (String) userFilterComboBox.getSelectedItem();
        String sortOption = (String) ratingSortComboBox.getSelectedItem();

        // STEP 2: Get the filtered and sorted list of feedback from the utility class.
        List<Feedback> filteredFeedback = util.getFilteredAndSortedFeedback(query, selectedUser, sortOption);

        // STEP 3: Clear the table and repopulate it with the new data.
        tableModel.setRowCount(0);
        Map<String, String> userIdToNameMap = util.getUserIdToNameMap();
        for (Feedback feedback : filteredFeedback) {
            tableModel.addRow(new Object[]{
                feedback.getFeedbackId(),
                userIdToNameMap.getOrDefault(feedback.getFromUserId(), "Unknown"),
                userIdToNameMap.getOrDefault(feedback.getToUserId(), "Unknown"),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getTimestampIso() != null ? feedback.getTimestampIso().substring(0, 10) : "N/A"
            });
        }
        
        // STEP 4: Update the statistics display based on the newly filtered list.
        updateStatistics(filteredFeedback);
    }

    /**
     * PURPOSE: To update the text of the statistics labels based on a list of feedback.
     * INPUTS: The list of feedback to be analyzed.
     * OUTPUT: None.
     */
    private void updateStatistics(List<Feedback> feedbackToAnalyze) {
        // STEP 1: Get the calculated statistics from the utility class.
        Map<String, Double> stats = util.calculateStatistics(feedbackToAnalyze);
        
        // STEP 2: Update the text of each label with the formatted statistical data.
        avgTodayLabel.setText(String.format("Today: %.2f", stats.get("today")));
        avgWeekLabel.setText(String.format("This Week: %.2f", stats.get("week")));
        avgMonthLabel.setText(String.format("This Month: %.2f", stats.get("month")));
        avgAllTimeLabel.setText(String.format("All Time: %.2f", stats.get("allTime")));
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

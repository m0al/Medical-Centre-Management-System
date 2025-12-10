// This file defines the main dashboard window for the manager role.
package amc.roleModules.Manager.managerScreens;

// Project-specific imports.
import amc.dataConstants.AppointmentStatusTypes;
import amc.dataModels.Feedback;
import amc.logicControllers.AppointmentController;
import amc.logicControllers.FeedbackController;
import amc.logicControllers.UserController;
import amc.roleModules.Universal.universalScreens.LoginScreen;
import amc.roleModules.Universal.universalScreens.ProfileUpdater;
import amc.userSession;

// Standard Java library imports.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * PURPOSE: To create the main graphical user interface (GUI) for managers. It serves as a
 * central hub, providing an overview of system statistics and navigation to other
 * management screens.
 */
public class ManagerDashboard extends JFrame {

    // STEP 1: Declare controllers for accessing application data.
    private final AppointmentController appointmentController = new AppointmentController();
    private final FeedbackController feedbackController = new FeedbackController();
    private final UserController userController = new UserController();

    // STEP 2: Declare a UI component field to keep track of the currently active menu item.
    private JPanel activeMenuItem;

    /**
     * PURPOSE: The main constructor for the ManagerDashboard.
     * INPUTS: None.
     * OUTPUT: A fully initialized ManagerDashboard window.
     */
    public ManagerDashboard() {
        // STEP 1: Build and configure all UI components.
        initializeUi();
    }

    /**
     * PURPOSE: To initialize and configure the main JFrame and its contents.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void initializeUi() {
        // STEP 1: Attempt to set a modern "Nimbus" look and feel.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception exception) {
            // If Nimbus fails, the application will fall back to the default look and feel.
            exception.printStackTrace();
        }

        // STEP 2: Configure the main window (JFrame) properties.
        setTitle("Manager Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // This centers the window on the screen.
        getContentPane().setLayout(new BorderLayout());

        // STEP 3: Create and add the main UI sections: the sidebar and the main content area.
        getContentPane().add(createSidebarPanel(), BorderLayout.WEST);
        getContentPane().add(createMainContentPanel(), BorderLayout.CENTER);
    }

    /**
     * PURPOSE: To create the left sidebar panel used for navigation.
     * INPUTS: None.
     * OUTPUT: A JPanel configured as the navigation sidebar.
     */
    private JPanel createSidebarPanel() {
        // STEP 1: Create the main container for the sidebar.
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(240, 240, 240));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // STEP 2: Add the application title at the top of the sidebar.
        JLabel appTitle = new JLabel("AMC");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        appTitle.setBorder(new EmptyBorder(20, 20, 20, 20));
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(appTitle);

        // STEP 3: Create and add each navigation menu item.
        String[] menuItems = {"Dashboard", "Users", "Appointments", "Feedback", "Reports", "Profile"};
        for (String itemName : menuItems) {
            JPanel menuItem = createMenuItem(itemName);
            sidebarPanel.add(menuItem);
            // The "Dashboard" item is set as active by default.
            if ("Dashboard".equals(itemName)) {
                setActive(menuItem);
                activeMenuItem = menuItem;
            }
        }

        // STEP 4: Add a flexible spacer to push the subsequent items to the bottom.
        sidebarPanel.add(Box.createVerticalGlue());

        // STEP 5: Create and add the "Logout" button at the bottom.
        JPanel logoutItem = createMenuItem("Logout");
        logoutItem.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                // Ask for confirmation before logging out.
                int response = JOptionPane.showConfirmDialog(
                    ManagerDashboard.this,
                    "Are you sure you want to log out?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );

                if (response == JOptionPane.YES_OPTION) {
                    // Close this dashboard and open a new login screen.
                    dispose();
                    new LoginScreen().setVisible(true);
                }
            }
        });
        sidebarPanel.add(logoutItem);

        // STEP 6: Return the fully constructed sidebar panel.
        return sidebarPanel;
    }

    /**
     * PURPOSE: To create a single clickable menu item for the sidebar.
     * INPUTS: The text to be displayed on the menu item.
     * OUTPUT: A JPanel representing the menu item.
     */
    private JPanel createMenuItem(String itemText) {
        // STEP 1: Create the panel for the menu item with styling.
        JPanel menuItem = new JPanel(new BorderLayout());
        menuItem.setBackground(new Color(240, 240, 240));
        menuItem.setBorder(new EmptyBorder(15, 20, 15, 20));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // STEP 2: Create the text label for the item.
        JLabel label = new JLabel(itemText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.DARK_GRAY);
        menuItem.add(label, BorderLayout.CENTER);

        // STEP 3: Add mouse listeners to handle clicks and hover effects.
        menuItem.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                // STEP 3A: When clicked, set this item as the active one.
                setActive(menuItem);
                
                // STEP 3B: Perform an action based on which item was clicked.
                if ("Profile".equals(itemText)) {
                    ProfileUpdater.showAsPopup(ManagerDashboard.this);
                } else if ("Users".equals(itemText)) {
                    new ManageUsers().setVisible(true);
                } else if ("Appointments".equals(itemText)) {
                    SwingUtilities.invokeLater(() -> {
                        JFrame appointmentFrame = new JFrame("Appointments Viewer");
                        appointmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        appointmentFrame.setSize(1200, 800);
                        appointmentFrame.setLocationRelativeTo(null);
                        appointmentFrame.getContentPane().add(new AppointmentsViewerPanel());
                        appointmentFrame.setVisible(true);
                    });
                } else if ("Feedback".equals(itemText)) {
                    SwingUtilities.invokeLater(() -> {
                        JFrame feedbackFrame = new JFrame("Feedback Viewer");
                        feedbackFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        feedbackFrame.setSize(1200, 800);
                        feedbackFrame.setLocationRelativeTo(null);
                        feedbackFrame.getContentPane().add(new FeedbackViewerPanel());
                        feedbackFrame.setVisible(true);
                    });
                } else if ("Reports".equals(itemText)) {
                    SwingUtilities.invokeLater(() -> {
                        JFrame reportsFrame = new JFrame("View Reports");
                        reportsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        reportsFrame.setSize(1200, 800);
                        reportsFrame.setLocationRelativeTo(null);
                        reportsFrame.getContentPane().add(new ViewReportsPanel());
                        reportsFrame.setVisible(true);
                    });
                }
            }

            public void mouseEntered(MouseEvent event) {
                // STEP 3C: On hover, change the background color if it's not the active item.
                if (menuItem != activeMenuItem) {
                    menuItem.setBackground(new Color(225, 225, 225));
                }
            }

            public void mouseExited(MouseEvent event) {
                // STEP 3D: When the mouse leaves, revert the background color.
                if (menuItem != activeMenuItem) {
                    menuItem.setBackground(new Color(240, 240, 240));
                }
            }
        });

        // STEP 4: Return the created menu item panel.
        return menuItem;
    }

    /**
     * PURPOSE: To set a menu item as active, which highlights it visually.
     * INPUTS: The JPanel of the menu item to be activated.
     * OUTPUT: None.
     */
    private void setActive(JPanel itemToActivate) {
        // STEP 1: Reset the previously active menu item to its default state.
        if (activeMenuItem != null) {
            activeMenuItem.setBackground(new Color(240, 240, 240));
            ((JLabel) activeMenuItem.getComponent(0)).setForeground(Color.DARK_GRAY);
        }
        // STEP 2: Set the new active menu item and apply active styling.
        activeMenuItem = itemToActivate;
        activeMenuItem.setBackground(new Color(0, 122, 255));
        ((JLabel) activeMenuItem.getComponent(0)).setForeground(Color.WHITE);
    }

    /**
     * PURPOSE: To create the main content panel where all dashboard information is displayed.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the main content.
     */
    private JPanel createMainContentPanel() {
        // STEP 1: Create the main container panel with a border layout and padding.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // STEP 2: Add the header to the top of the panel.
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // STEP 3: Create a grid to hold the statistics and feedback panels.
        JPanel contentGrid = new JPanel(new BorderLayout(20, 20));
        contentGrid.setBackground(Color.WHITE);
        contentGrid.add(createStatsPanel(), BorderLayout.NORTH);
        contentGrid.add(createFeedbackPanel(), BorderLayout.CENTER);

        // STEP 4: Add the content grid to the main panel.
        mainPanel.add(contentGrid, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * PURPOSE: To create the header for the main content area, including title and welcome message.
     * INPUTS: None.
     * OUTPUT: A JPanel configured as the main content header.
     */
    private JPanel createHeaderPanel() {
        // STEP 1: Create the panel with a border layout and padding.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // STEP 2: Create and add the title label.
        JLabel titleLabel = new JLabel("Manager Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // STEP 3: Create and add the welcome message, personalized with the user's name.
        JLabel welcomeLabel = new JLabel("Welcome, " + userSession.getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(Color.GRAY);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * PURPOSE: To create the panel that displays key statistics in a series of cards.
     * INPUTS: None.
     * OUTPUT: A JPanel containing a grid of stat cards.
     */
    private JPanel createStatsPanel() {
        // STEP 1: Create a grid layout panel for the stat cards.
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        // STEP 2: Fetch the data required for the statistics from the controllers.
        int staffCount = userController.listByRole("Staff").size() + userController.listByRole("Doctor").size();
        int appointmentCount = appointmentController.listAll().size();
        List<Feedback> feedbackList = feedbackController.listAll();
        int feedbackCount = feedbackList.size();
        
        // STEP 3: Calculate the number of upcoming appointments.
        long upcomingAppointments = appointmentController.listAll().stream()
                .filter(appointment -> {
                    // An appointment is upcoming if it has a date and status.
                    if (appointment.getDateTimeIso() == null || appointment.getStatus() == null) {
                        return false;
                    }
                    // It must not be completed or cancelled.
                    boolean isCompleted = appointment.getStatus().equals(AppointmentStatusTypes.completed);
                    boolean isCancelled = appointment.getStatus().equals(AppointmentStatusTypes.cancelled);
                    // Its date must be in the future.
                    boolean isFuture = LocalDateTime.parse(appointment.getDateTimeIso(), DateTimeFormatter.ISO_DATE_TIME).isAfter(LocalDateTime.now());
                    
                    return !isCompleted && !isCancelled && isFuture;
                })
                .count();

        // STEP 4: Create and add a styled card for each statistic.
        statsPanel.add(createStatCard("Total Staff", String.valueOf(staffCount), new Color(255, 223, 186)));
        statsPanel.add(createStatCard("Total Appointments", String.valueOf(appointmentCount), new Color(173, 216, 230)));
        statsPanel.add(createStatCard("Upcoming Appointments", String.valueOf(upcomingAppointments), new Color(144, 238, 144)));
        statsPanel.add(createStatCard("Feedback", String.valueOf(feedbackCount), new Color(255, 223, 186)));

        return statsPanel;
    }

    /**
     * PURPOSE: To create a single, styled panel (a "card") for displaying one statistic.
     * INPUTS: A title, a value, and a background color for the card.
     * OUTPUT: A JPanel styled as a stat card.
     */
    private JPanel createStatCard(String cardTitle, String cardValue, Color cardColor) {
        // STEP 1: Create the card panel with a border layout, background color, and padding.
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        // STEP 2: Create and style the large label for the statistic's value.
        JLabel valueLabel = new JLabel(cardValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.DARK_GRAY);
        card.add(valueLabel, BorderLayout.CENTER);

        // STEP 3: Create and style the smaller label for the statistic's title.
        JLabel titleLabel = new JLabel(cardTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(Color.DARK_GRAY);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * PURPOSE: To create the panel that displays recent feedback entries in a table.
     * INPUTS: None.
     * OUTPUT: A JPanel containing the feedback table.
     */
    private JPanel createFeedbackPanel() {
        // STEP 1: Create the main container for the feedback section.
        JPanel feedbackPanel = new JPanel(new BorderLayout(0, 10));
        feedbackPanel.setBackground(Color.WHITE);
        feedbackPanel.setBorder(BorderFactory.createTitledBorder("System Feedback"));

        // STEP 2: Define the table columns and create a non-editable table model.
        String[] columnNames = {"Date", "Feedback Comment"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable feedbackTable = new JTable(tableModel);

        // STEP 3: Apply custom styling to the table.
        styleTable(feedbackTable);

        // STEP 4: Fetch the latest feedback and populate the table with up to 5 entries.
        List<Feedback> feedbackList = feedbackController.listAll();
        for (int i = 0; i < Math.min(5, feedbackList.size()); i++) {
            Feedback feedback = feedbackList.get(i);
            String date = feedback.getTimestampIso() != null ? feedback.getTimestampIso().substring(0, 10) : "N/A";
            String comment = feedback.getComment() != null ? feedback.getComment() : "";
            tableModel.addRow(new Object[]{date, comment});
        }

        // STEP 5: Place the table inside a scroll pane and add it to the panel.
        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        feedbackPanel.add(scrollPane, BorderLayout.CENTER);

        return feedbackPanel;
    }

    /**
     * PURPOSE: A private helper to apply a consistent style to a JTable.
     * INPUTS: The JTable to be styled.
     * OUTPUT: None.
     */
    private void styleTable(JTable tableToStyle) {
        // STEP 1: Set the font, row height, and colors for the table body.
        tableToStyle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableToStyle.setRowHeight(30);
        tableToStyle.setGridColor(new Color(230, 230, 230));
        tableToStyle.setFillsViewportHeight(true);

        // STEP 2: Style the table's header separately for emphasis.
        JTableHeader header = tableToStyle.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
    }

    /**
     * PURPOSE: The main entry point to run this window as a standalone application for testing.
     * INPUTS: Command line arguments (not used).
     * OUTPUT: None.
     */
    public static void main(String[] commandLineArgs) {
        // STEP 1: Ensure the UI is created and made visible on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> {
            new ManagerDashboard().setVisible(true);
        });
    }
}

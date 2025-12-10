// This file defines the main window for the user management interface.
package amc.roleModules.Manager.managerScreens;

// Project-specific imports for data models, constants, and utilities.
import amc.dataModels.User;
import amc.roleModules.Manager.managerUtil.ManageUsersUtil;
import amc.dataConstants.RoleTypes;

// Standard Java library imports for UI and data structures.
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * PURPOSE: To provide a graphical user interface for managers to perform CRUD (Create, Read,
 * Update, Delete) operations on user accounts. It is a standalone window (JFrame).
 */
public class ManageUsers extends JFrame {

    // STEP 1: Declare all UI components and the data model for the table.
    // These are not renamed to maintain compatibility with any potential UI designer tools.
    private JTextField txtSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnReturn;
    private JTable userTable;
    private DefaultTableModel tableModel;

    // STEP 2: Declare the utility class that handles all business logic.
    private final ManageUsersUtil manageUsersUtil;

    /**
     * PURPOSE: The main constructor for the ManageUsers window.
     * INPUTS: None.
     * OUTPUT: A fully initialized and visible ManageUsers window.
     */
    public ManageUsers() {
        // STEP 1: Set the title of the window using the super constructor.
        super("Manage Users");
        
        // STEP 2: Instantiate the business logic utility.
        manageUsersUtil = new ManageUsersUtil();
        
        // STEP 3: Build the user interface components and layout.
        initializeUi();
        
        // STEP 4: Perform the initial data load to populate the user table.
        loadUsersTable(null);
    }

    /**
     * PURPOSE: To initialize and configure the main JFrame and all its contents.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void initializeUi() {
        // STEP 1: Attempt to set a modern "Nimbus" look and feel for the UI.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception exception) {
            // If Nimbus is not available, the default look and feel will be used.
            exception.printStackTrace();
        }

        // STEP 2: Configure the main window's properties.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ensures only this window closes.
        setSize(900, 600);
        setLocationRelativeTo(null); // Centers the window on the screen.
        setLayout(new BorderLayout());

        // STEP 3: Create the main content panel with padding and a white background.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel, BorderLayout.CENTER);

        // STEP 4: Create and add the header label.
        JLabel lblHeader = new JLabel("Manage Users", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblHeader.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblHeader, BorderLayout.NORTH);

        // STEP 5: Create the panel for search and action buttons using GridBagLayout for alignment.
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Actions", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // STEP 5A: Add the search text field.
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; // Takes up remaining horizontal space.
        actionPanel.add(txtSearch, gbc);

        // STEP 5B: Add the "Add User" button.
        btnAdd = new JButton("Add User");
        styleButton(btnAdd);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.0; // Does not take extra space.
        actionPanel.add(btnAdd, gbc);

        // STEP 5C: Add the "Edit User" button.
        btnEdit = new JButton("Edit User");
        styleButton(btnEdit);
        gbc.gridx = 2; gbc.gridy = 0;
        actionPanel.add(btnEdit, gbc);

        // STEP 5D: Add the "Delete User" button.
        btnDelete = new JButton("Delete User");
        styleButton(btnDelete);
        gbc.gridx = 3; gbc.gridy = 0;
        actionPanel.add(btnDelete, gbc);

        // STEP 6: Create the user data table.
        String[] columnNames = {"User ID", "Name", "Email", "Phone", "Address", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Makes the entire table read-only.
            }
        };
        userTable = new JTable(tableModel);
        styleTable(userTable);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // STEP 7: Use a wrapper panel to correctly position the action panel and the table.
        JPanel contentWrapper = new JPanel(new BorderLayout(0, 10));
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.add(actionPanel, BorderLayout.NORTH);
        contentWrapper.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        // STEP 8: Create the bottom panel for the return button.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        btnReturn = new JButton("Return to Dashboard");
        styleButton(btnReturn);
        bottomPanel.add(btnReturn);
        add(bottomPanel, BorderLayout.SOUTH);

        // STEP 9: Attach all event listeners to the UI components.
        wireEvents();
    }

    /**
     * PURPOSE: To attach all event listeners to the interactive UI components.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void wireEvents() {
        // STEP 1: Add listener for the return button to close the window.
        btnReturn.addActionListener(event -> dispose());

        // STEP 2: Add a key listener to the search field to trigger search on every key press.
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent event) {
                searchUsers();
            }
        });

        // STEP 3: Add listeners for the CRUD buttons.
        btnAdd.addActionListener(event -> addUser());
        btnEdit.addActionListener(event -> editUser());
        btnDelete.addActionListener(event -> deleteUser());
    }

    /**
     * PURPOSE: To load or refresh the user data displayed in the main table.
     * INPUTS: A list of users to display. If the list is null, all users will be fetched.
     * OUTPUT: None.
     */
    private void loadUsersTable(List<User> usersToLoad) {
        // STEP 1: Clear any existing data from the table.
        tableModel.setRowCount(0);
        
        // STEP 2: If no specific list is provided, get all users. Otherwise, use the provided list.
        List<User> userList = (usersToLoad == null) ? manageUsersUtil.getAllUsers() : usersToLoad;

        // STEP 3: Iterate through the list of users and add each one as a row to the table.
        for (User user : userList) {
            tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getAddress(),
                    user.getRole()
            });
        }
    }

    /**
     * PURPOSE: To handle the "Add User" action, which opens a dialog to create a new user.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void addUser() {
        // STEP 1: Create a new, empty User object to hold the new user's data.
        User newUser = new User();
        
        // STEP 2: Show the user dialog in "new user" mode.
        if (showUserDialog(newUser, true)) {
            // STEP 3: If the user clicked "OK", attempt to add the user via the utility class.
            Optional<String> error = manageUsersUtil.addUser(newUser);
            
            // STEP 4: Show an error or success message and refresh the table.
            if (error.isPresent()) {
                JOptionPane.showMessageDialog(this, error.get(), "Error Adding User", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable(null);
            }
        }
    }

    /**
     * PURPOSE: To handle the "Edit User" action for the currently selected user in the table.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void editUser() {
        // STEP 1: Get the index of the selected row in the table.
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            // STEP 2: If no row is selected, show a warning message and exit.
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No User Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // STEP 3: Get the user ID from the selected row.
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        Optional<User> userOptional = manageUsersUtil.getUserById(userId);

        // STEP 4: If the user exists, show the dialog in "edit" mode.
        if (userOptional.isPresent()) {
            User userToEdit = userOptional.get();
            if (showUserDialog(userToEdit, false)) {
                // STEP 5: If the user clicked "OK", attempt to update the user.
                Optional<String> error = manageUsersUtil.updateUser(userToEdit);
                
                // STEP 6: Show an error or success message and refresh the table.
                if (error.isPresent()) {
                    JOptionPane.showMessageDialog(this, error.get(), "Error Updating User", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsersTable(null);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selected user not found in data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * PURPOSE: To handle the "Delete User" action for the currently selected user.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void deleteUser() {
        // STEP 1: Get the index of the selected row.
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            // STEP 2: If no row is selected, show a warning and exit.
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No User Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // STEP 3: Get the user ID and ask for confirmation before deleting.
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + userId + "'?", "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        // STEP 4: If the user confirmed, attempt to delete the user.
        if (confirm == JOptionPane.YES_OPTION) {
            Optional<String> error = manageUsersUtil.deleteUser(userId);
            
            // STEP 5: Show an error or success message and refresh the table.
            if (error.isPresent()) {
                JOptionPane.showMessageDialog(this, error.get(), "Error Deleting User", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsersTable(null);
            }
        }
    }

    /**
     * PURPOSE: To filter the users shown in the table based on the text in the search box.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private void searchUsers() {
        // STEP 1: Get the search query from the text field.
        String query = txtSearch.getText().trim();
        
        // STEP 2: Get the search results from the utility class.
        List<User> searchResults = manageUsersUtil.searchUsers(query);
        
        // STEP 3: Load the filtered results into the table.
        loadUsersTable(searchResults);
    }

    /**
     * PURPOSE: To display a dialog for adding a new user or editing an existing one.
     * INPUTS: The User object to be edited, and a boolean flag indicating if it's a new user.
     * OUTPUT: True if the user clicked "OK" and data is valid, otherwise false.
     */
    private boolean showUserDialog(User userToDisplay, boolean isNewUserMode) {
        // STEP 1: Create all the input components for the dialog.
        JTextField userIdField = new JTextField(userToDisplay.getUserId());
        JTextField nameField = new JTextField(userToDisplay.getName());
        JTextField emailField = new JTextField(userToDisplay.getEmail());
        JTextField phoneField = new JTextField(userToDisplay.getPhone());
        JTextField addressField = new JTextField(userToDisplay.getAddress());
        JPasswordField passwordField = new JPasswordField(userToDisplay.getPassword());
        JPasswordField confirmPasswordField = new JPasswordField(userToDisplay.getPassword());
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
                RoleTypes.manager, RoleTypes.staff, RoleTypes.doctor, RoleTypes.customer
        });
        roleComboBox.setSelectedItem(userToDisplay.getRole() != null ? userToDisplay.getRole() : RoleTypes.customer);

        // STEP 2: Create the panel and use GridBagLayout for a structured form layout.
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // STEP 3: Add each labeled field to the panel, row by row.
        // The User ID field is only shown and is non-editable when editing an existing user.
        if (!isNewUserMode) {
            gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("User ID:"), gbc);
            gbc.gridx = 1; gbc.gridy = row++; panel.add(userIdField, gbc);
            userIdField.setEditable(false);
            userIdField.setBackground(Color.LIGHT_GRAY);
        }

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(roleComboBox, gbc);

        // STEP 4: Show the dialog to the user.
        int result = JOptionPane.showConfirmDialog(this, panel,
                (isNewUserMode ? "Add New User" : "Edit User"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // STEP 5: If the user clicked "OK", process the input.
        if (result == JOptionPane.OK_OPTION) {
            // STEP 5A: Populate the user object with the data from the form fields.
            if (!isNewUserMode) {
                userToDisplay.setUserId(userIdField.getText().trim());
            }
            userToDisplay.setName(nameField.getText().trim());
            userToDisplay.setEmail(emailField.getText().trim());
            userToDisplay.setPhone(phoneField.getText().trim());
            userToDisplay.setAddress(addressField.getText().trim());
            userToDisplay.setRole((String) roleComboBox.getSelectedItem());

            // STEP 5B: Handle password validation.
            String newPassword = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Abort the save.
                }
                userToDisplay.setPassword(newPassword);
            } else if (isNewUserMode) {
                JOptionPane.showMessageDialog(this, "Password is required for new users.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false; // Abort the save.
            }
            return true; // Data is valid and ready to be saved.
        }
        // STEP 6: If the user clicked "Cancel", return false.
        return false;
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

    /**
     * PURPOSE: The main entry point to run this window as a standalone application for testing.
     * INPUTS: Command line arguments (not used).
     * OUTPUT: None.
     */
    public static void main(String[] commandLineArgs) {
        // STEP 1: Ensure the UI is created and updated on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> new ManageUsers().setVisible(true));
    }
}

package amc.roleModules.Universal.universalScreens;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import amc.userSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * This class provides a reusable profile editor as a popup panel
 * and the logic to update the logged-in user's details in the JSON file.
 * Any dashboard can call ProfileUpdater.showAsPopup(parent) to open it.
 */
public final class ProfileUpdater extends JPanel {

    // Input fields that collect user details from the interface.
    private JTextField txtProfileName;
    private JTextField txtProfileEmail;
    private JTextField txtProfilePhone;
    private JTextField txtProfileAddress;
    private JPasswordField txtProfilePassword;
    private JPasswordField txtProfileConfirmPassword;

    // Controls for showing the password and for for saving or cancelling the edit.
    private JCheckBox cbShowPassword;
    private JButton btnSaveProfile;
    private JButton btnCancelProfile;

    // This private constructor builds the UI, preloads values, and wires events.
    private ProfileUpdater() {
        buildUi();
        preloadFromRepository();
        wireEvents();
    }

    /**
     * This static method shows the profile panel as a modal popup dialog.
     * It blocks the caller until the user clicks Save or Cancel.
     */
    public static void showAsPopup(Component parent) {
        ProfileUpdater panel = new ProfileUpdater();

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(parent),
                "Edit Profile",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * This method creates and arranges the UI controls using GridBagLayout
     * so that labels and fields align neatly and resize well.
     */
    private void buildUi() {
        // Set the overall layout for the ProfileUpdater panel.
        setLayout(new BorderLayout());
        // Add an empty border for consistent spacing around the entire panel.
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create a panel to hold the input fields and labels.
        JPanel contentPanel = new JPanel(new GridBagLayout());
        // Set a titled border for the content panel to visually group the profile fields.
        contentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "My Profile", TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints gc = new GridBagConstraints();
        // Set insets for consistent spacing between components within the content panel.
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Add Name field
        JLabel lblName = new JLabel("Name:");
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.0;
        contentPanel.add(lblName, gc);
        txtProfileName = new JTextField(20); // Set preferred column width
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0;
        contentPanel.add(txtProfileName, gc);

        // Add Email field
        JLabel lblEmail = new JLabel("Email:");
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.0;
        contentPanel.add(lblEmail, gc);
        txtProfileEmail = new JTextField(20);
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0;
        contentPanel.add(txtProfileEmail, gc);

        // Add Phone field
        JLabel lblPhone = new JLabel("Phone:");
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.0;
        contentPanel.add(lblPhone, gc);
        txtProfilePhone = new JTextField(20);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1.0;
        contentPanel.add(txtProfilePhone, gc);

        // Add Address field
        JLabel lblAddress = new JLabel("Address:");
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.0;
        contentPanel.add(lblAddress, gc);
        txtProfileAddress = new JTextField(20);
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1.0;
        contentPanel.add(txtProfileAddress, gc);

        // Add New Password field
        JLabel lblPassword = new JLabel("New Password:");
        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0.0;
        contentPanel.add(lblPassword, gc);
        txtProfilePassword = new JPasswordField(20);
        gc.gridx = 1; gc.gridy = 4; gc.weightx = 1.0;
        contentPanel.add(txtProfilePassword, gc);

        // Add Confirm Password field
        JLabel lblConfirm = new JLabel("Confirm Password:");
        gc.gridx = 0; gc.gridy = 5; gc.weightx = 0.0;
        contentPanel.add(lblConfirm, gc);
        txtProfileConfirmPassword = new JPasswordField(20);
        gc.gridx = 1; gc.gridy = 5; gc.weightx = 1.0;
        contentPanel.add(txtProfileConfirmPassword, gc);

        // Add Show Password checkbox
        cbShowPassword = new JCheckBox("Show Password");
        gc.gridx = 1; gc.gridy = 6; gc.weightx = 1.0;
        gc.anchor = GridBagConstraints.WEST; // Align checkbox to the left
        contentPanel.add(cbShowPassword, gc);

        // Add the content panel to the center of the main ProfileUpdater panel.
        add(contentPanel, BorderLayout.CENTER);

        // Create a panel for buttons and align them to the right.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSaveProfile = new JButton("Save");
        btnCancelProfile = new JButton("Cancel");
        buttonPanel.add(btnSaveProfile);
        buttonPanel.add(btnCancelProfile);

        // Add the button panel to the bottom of the main ProfileUpdater panel.
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * This method fills the fields using the latest data from storage.
     * It reads by userId from userSession, then loads the matching user.
     * Name and email are also available in userSession but we prefer storage
     * to ensure we always show the newest values on disk.
     */
    private void preloadFromRepository() {
        if (!userSession.isLoggedIn()) {
            // If nobody is logged in, leave everything empty.
            return;
        }

        String currentUserId = userSession.getUserId();
        User user = loadByUserId(currentUserId);

        if (user != null) {
            txtProfileName.setText(safe(user.getName()));
            txtProfileEmail.setText(safe(user.getEmail()));
            txtProfilePhone.setText(safe(user.getPhone()));
            txtProfileAddress.setText(safe(user.getAddress()));
        } else {
            // If the record is missing, fall back to session values if any.
            txtProfileName.setText(safe(userSession.getName()));
            txtProfileEmail.setText(safe(userSession.getEmail()));
            txtProfilePhone.setText("");
            txtProfileAddress.setText("");
        }

        // Password fields are intentionally empty so the user types a new password only if they want to change it.
        txtProfilePassword.setText("");
        txtProfileConfirmPassword.setText("");
    }

    /**
     * This method wires the password visibility toggle and the Save/Cancel buttons.
     * It keeps the UI responsive and easy to understand for the user.
     */
    private void wireEvents() {
        final char echoDefault = txtProfilePassword.getEchoChar();
        cbShowPassword.addActionListener(_ -> {
            boolean show = cbShowPassword.isSelected();
            txtProfilePassword.setEchoChar(show ? (char) 0 : echoDefault);
            txtProfileConfirmPassword.setEchoChar(show ? (char) 0 : echoDefault);
        });

        btnCancelProfile.addActionListener(_ -> closeAncestorWindow());

        btnSaveProfile.addActionListener(_ -> {
            if (validateInputs()) {
                doSave();
            }
        });
    }

    /**
     * This method validates the fields and shows small warnings if something is missing.
     * It returns true if everything is acceptable and the save can proceed.
     */
    private boolean validateInputs() {
        String name = txtProfileName.getText().trim();
        String email = txtProfileEmail.getText().trim();

        if (name.isEmpty()) {
            warn("Please enter your name.");
            txtProfileName.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            warn("Please enter your email.");
            txtProfileEmail.requestFocus();
            return false;
        }

        String password = new String(txtProfilePassword.getPassword());
        String confirmPassword = new String(txtProfileConfirmPassword.getPassword());

        // If the user typed a new password in either box, both must match exactly.
        if (!password.isEmpty() || !confirmPassword.isEmpty()) {
            if (!password.equals(confirmPassword)) {
                warn("The passwords do not match. Please re-enter them.");
                txtProfilePassword.requestFocus();
                return false;
            }
        }
        return true;
    }

    /**
     * This method applies the edits to the User object, saves the record using the repository,
     * refreshes the session, and closes the popup on success.
     */
    private void doSave() {
        try {
            if (!userSession.isLoggedIn()) {
                error("No user is currently logged in.");
                return;
            }

            String currentUserId = userSession.getUserId();
            UserRepository userRepository = new UserRepository();
            User user = loadByUserId(userRepository, currentUserId);

            if (user == null) {
                error("We could not find your user record in the data file.");
                return;
            }

            // Apply the edits from the form to the loaded user object.
            user.setName(txtProfileName.getText().trim());
            user.setEmail(txtProfileEmail.getText().trim());
            user.setPhone(txtProfilePhone.getText().trim());
            user.setAddress(txtProfileAddress.getText().trim());

            String newPassword = new String(txtProfilePassword.getPassword());
            if (!newPassword.isEmpty()) {
                // Only update the password if the user typed a new one.
                user.setPassword(newPassword);
            }

            // Persist the changes to JSON via your repository API.
            userRepository.saveOrUpdate(user);

            // Refresh the session so name and email reflect the latest values immediately.
            userSession.setFromUser(user);

            // Inform the user and close the popup window.
            info("Your profile has been updated successfully.");
            closeAncestorWindow();

        } catch (Exception ex) {
            error("An error occurred while updating your profile.\n" + ex.getMessage());
        }
    }

    // ===== Helpers (storage, UI, and small utilities) =====

    // This helper loads a user by id using a new repository instance.
    private User loadByUserId(String userId) {
        return loadByUserId(new UserRepository(), userId);
    }

    // This helper loads a user by id using an existing repository.
    private User loadByUserId(UserRepository repo, String userId) {
        List<User> allUsers = repo.findAll();
        for (User u : allUsers) {
            if (u != null && u.getUserId() != null && u.getUserId().equalsIgnoreCase(userId)) {
                return u;
            }
        }
        return null;
    }

    // This helper closes the dialog or window that contains this panel.
    private void closeAncestorWindow() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
    }

    // These helpers show friendly message dialogs with consistent titles and icons.
    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Missing Or Invalid Field", JOptionPane.WARNING_MESSAGE);
    }
    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Profile Update Error", JOptionPane.ERROR_MESSAGE);
    }
    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Profile Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // This helper prevents null text from appearing in the fields.
    private String safe(String value) {
        return value == null ? "" : value;
    }
}

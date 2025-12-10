package amc.roleModules.Universal.universalUtil;

import javax.swing.*;

/**
 * This class handles the login..
 * It validates inputs, checks credentials via a provided userLookup, and opens the correct dashboard.
 */

public class LoginController {
    private final JFrame parentWindow;

// Holds the text field where the user types the email or username.
    private final JTextField txtEmailOrUsername;

// Holds the password field where the user types the password.
    private final JPasswordField txtPassword;

// Holds the login button so we can attach the click action.
    private final JButton btnLogin;

// Holds the checkbox that shows or hides the password characters.
    private final JCheckBox cbShowPassword;

    // Holds the original echo character so we can restore the masked state after showing the password.
    private final char defaultEchoChar;
 
    // Holds a reference to the dependency that looks up users in your data source.
    private final UserLookup userLookup;

    // Holds a reference to the dependency that opens the correct dashboard after a successful login.
    private final DashboardNavigator dashboardNavigator;

    // Tracks failed attempts to prevent unlimited guessing.
    private int failedAttempts = 0;

    // Sets the maximum number of allowed failed attempts before exiting the application.
    private final int maxAttempts = 5;

    /**
     * This constructor receives your existing GUI components and lightweight dependencies.
     * It immediately wires all required event handlers.
     */
    public LoginController(JFrame parentWindow,
                           JTextField txtEmailOrUsername,
                           JPasswordField txtPassword,
                           JButton btnLogin,
                           JCheckBox cbShowPassword,
                           UserLookup userLookup,
                           DashboardNavigator dashboardNavigator) {

        this.parentWindow = parentWindow;
        this.txtEmailOrUsername = txtEmailOrUsername;
        this.txtPassword = txtPassword;
        this.btnLogin = btnLogin;
        this.cbShowPassword = cbShowPassword;
        this.userLookup = userLookup;
        this.dashboardNavigator = dashboardNavigator;

        this.defaultEchoChar = txtPassword.getEchoChar();

        wireEvents(); // This sets up all actions for login and password visibility.
    }

    /**
     * This method attaches click and Enter-key handlers, and wires the show/hide password checkbox.
     */
    private void wireEvents() {
        // This runs a login attempt when the login button is clicked.
        btnLogin.addActionListener(_ -> attemptLogin());

        // This allows pressing Enter in either field to trigger login.
        txtPassword.addActionListener(_ -> attemptLogin());
        txtEmailOrUsername.addActionListener(_ -> attemptLogin());

        // This toggles password visibility if the checkbox exists on the form.
        if (cbShowPassword != null) {
            cbShowPassword.addActionListener(_ -> {
                if (cbShowPassword.isSelected()) {
                    txtPassword.setEchoChar((char) 0); // This shows the actual characters.
                } else {
                    txtPassword.setEchoChar(defaultEchoChar); // This restores the masked characters.
                }
            });
        }
    }

    /**
     * This method validates the inputs, checks the credentials, shows messages, and navigates on success.
     */
    private void attemptLogin() {
        final String emailOrUsername = txtEmailOrUsername.getText().trim();
        final String passwordInput = new String(txtPassword.getPassword());

        // This ensures that the identifier field is not empty.
        if (emailOrUsername.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "Please enter your email or username.", "Missing Field", JOptionPane.WARNING_MESSAGE);
            txtEmailOrUsername.requestFocus();
            return;
        }

        // This ensures that the password field is not empty.
        if (passwordInput.isEmpty()) {
            JOptionPane.showMessageDialog(parentWindow, "Please enter your password.", "Missing Field", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        try {
            // This retrieves the user record using the provided lookup dependency.
            UserRecord userRecord = userLookup.findByEmailOrUsername(emailOrUsername);

            // This verifies that the user exists and the password matches exactly.
            if (userRecord != null && passwordInput.equals(userRecord.getPassword())) {
                // This informs the user of a successful login.
                JOptionPane.showMessageDialog(parentWindow, "Login successful.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // This opens the correct dashboard and closes the login window.
                dashboardNavigator.openDashboard(userRecord);
                parentWindow.dispose();
                return;
            }

            // This handles incorrect credentials and tracks remaining attempts.
            failedAttempts++;
            int remainingAttempts = Math.max(0, maxAttempts - failedAttempts);
            JOptionPane.showMessageDialog(parentWindow, "Incorrect email, username, or password.\nAttempts remaining: " + remainingAttempts, "Login Failed", JOptionPane.ERROR_MESSAGE);

            // This exits the application after too many failed attempts. Otherwise, it resets the password field for another try.
            if (failedAttempts >= maxAttempts) {
                JOptionPane.showMessageDialog(parentWindow, "Too many failed attempts. The application will close.", "Locked Out", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (Exception ex) {
            // This shows a friendly error if the data source throws an exception.
            JOptionPane.showMessageDialog(parentWindow, "An unexpected error occurred while logging in.\nDetails: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This represents the minimal user data needed for login and routing.
     * Implement this by adapting your actual User class.
     */
    public interface UserRecord {
        String getEmail();   // This should return the user's email address.
        String getUsername(); // This should return the user's username.
        String getPassword(); // This should return the user's password (plain or hashed depending on your storage).
        String getRole();     // This should return the user's role such as MANAGER, STAFF, DOCTOR, or CUSTOMER.
    }

    /**
     * This looks up a user by email or username from the existing data source.
     * Implement this to call your repository, DAO, or JSON reader.
     */
    public interface UserLookup {
        UserRecord findByEmailOrUsername(String key) throws Exception;
    }

    /**
     * This opens the correct dashboard for the given user using your existing navigation code.
     * Implement this to call your screen router or to instantiate the appropriate JFrame.
     */
    public interface DashboardNavigator {
        void openDashboard(UserRecord userRecord);
    }
}

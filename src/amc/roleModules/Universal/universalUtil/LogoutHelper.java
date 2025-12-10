package amc.roleModules.Universal.universalUtil;

import amc.userSession;
import amc.roleModules.Universal.universalScreens.LoginScreen;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class provides a single method you can call from any dashboard
 * to log out the current user and return to the login screen.
 */
public final class LogoutHelper {

    private LogoutHelper() { }

    /**
     * This method asks the user to confirm, clears the session,
     * opens the login screen, and closes the current window.
     */
    public static void performLogout(JFrame currentWindow) {
        // Step 1: Ask the user to confirm the logout action.
        int choice = JOptionPane.showConfirmDialog(
                currentWindow,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // Step 2: If the user pressed No, do nothing and return.
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        // Step 3: Clear the in-memory session so no user stays logged in.
        userSession.clear();

        // Step 4: Show the login screen again.
        LoginScreen login = new LoginScreen();
        login.setLocationRelativeTo(null); // This centers the window on the screen.
        login.setVisible(true);

        // Step 5: Close the current dashboard window so only the login remains.
        if (currentWindow != null) {
            currentWindow.dispose();
        }
    }
}

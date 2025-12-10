package amc;

import javax.swing.SwingUtilities;
import amc.roleModules.Universal.universalScreens.LoginScreen;

public class mainLauncher {

    public static void main(String[] args) {
// Run the GUI in the Event Dispatch Thread for thread safety.
        SwingUtilities.invokeLater(() -> {

// Create the login screen.
            LoginScreen loginScreen = new LoginScreen(); 
            
// Show the login screen.

            loginScreen.setLocationRelativeTo(null);
            loginScreen.setVisible(true);
        });
    }
}

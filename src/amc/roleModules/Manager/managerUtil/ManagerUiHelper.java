// This file contains a utility class for showing simple UI popups in the manager modules.
package amc.roleModules.Manager.managerUtil;

// Standard Java library import for Swing UI components.
import javax.swing.JOptionPane;

/**
 * PURPOSE: Provides a centralized helper for displaying simple user interface dialogs.
 * This class cannot be instantiated and all its methods are static.
 */
public final class ManagerUiHelper {

    /**
     * PURPOSE: A private constructor to prevent this utility class from being instantiated.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private ManagerUiHelper() {
        // STEP 1: This constructor is intentionally left empty to prevent object creation.
    }

    /**
     * PURPOSE: To display a standard information message popup to the user.
     * INPUTS: A string containing the message to be displayed.
     * OUTPUT: None. A dialog box is shown on the screen.
     */
    public static void showInfo(String message) {
        // STEP 1: Use the standard JOptionPane to show a simple dialog with the provided message.
        // The first argument is null, so the dialog is centered on the screen.
        JOptionPane.showMessageDialog(null, message);
    }
}
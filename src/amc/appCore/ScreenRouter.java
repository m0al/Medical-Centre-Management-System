package amc.appCore;

import amc.userSession;
import javax.swing.JOptionPane;

/**
 * This class decides which dashboard to open after login.
 * It uses class names so the GUI team can add screens later without changing code here.
 */
public final class ScreenRouter {

    private ScreenRouter(){}

    // Open the correct dashboard for the current role.
    public static void openDashboardForCurrentUser() {
        String role = userSession.getRole();
        String targetClass = getTargetDashboardClassName(role);
        tryOpenFrame(targetClass);
    }

    // Return the full class name of the dashboard for a role.
    public static String getTargetDashboardClassName(String role) {
        if (role == null) return null;
        switch (role) {
            case "MANAGER":  return "amc.roleModules.Manager.managerScreens.ManagerDashboard";
            case "STAFF":    return "amc.roleModules.Staff.staffScreens.mainView";
            case "DOCTOR":   return "amc.roleModules.Doctor.doctorScreens.DoctorDashboard";
            case "CUSTOMER": return "amc.roleModules.Customer.customerScreens.CustomerDashboard";
            default:         return null;
        }
    }

    // Try to create and show the dashboard window by class name.
    private static void tryOpenFrame(String fullClassName) {
        if (fullClassName == null) {
            JOptionPane.showMessageDialog(null, "No dashboard found for this role.");
            return;
        }
        try {
            Class<?> frameClass = Class.forName(fullClassName);
            Object frameObject = frameClass.getDeclaredConstructor().newInstance();
            // Assumes each dashboard extends JFrame and has setVisible(true).
            frameClass.getMethod("setVisible", boolean.class).invoke(frameObject, true);
            
        } catch (Exception ex) {
            // This is shown if the GUI class does not exist yet.
            ex.printStackTrace(); // Add this line to print the stack trace
            JOptionPane.showMessageDialog(null,
                "Dashboard is not ready: " + fullClassName + "\nPlease add this screen.\n\n" + ex.getMessage());
        }
    }
}
package amc.roleModules.Customer.customerScreens;

import javax.swing.*;

public class CustomerDashboard extends JFrame {
    public CustomerDashboard() {
        CustomerDashboardImpl real = new CustomerDashboardImpl();
        real.setVisible(true);
        dispose();
    }
}
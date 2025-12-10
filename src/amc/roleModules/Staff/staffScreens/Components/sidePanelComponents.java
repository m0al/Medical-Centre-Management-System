package amc.roleModules.Staff.staffScreens.Components;

import javax.swing.*;
import java.awt.*;

public class sidePanelComponents {
    public static JButton dashboardButton = new JButton("Dashboard");
    public static JButton customersButton = new JButton("Customers");
    public static JButton appointmentsButton = new JButton("Appointments");
    public static JButton paymentsButton= new JButton("Payment Receipts");
    public static JButton logoutButton = new JButton("Logout");

    public static JPanel sidePanel() {

        // Side Panel colors
        String borderColour = "#D6D6D6";

        // Creating the side panel
        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(30, 90, 180));
        sidePanel.setPreferredSize(new Dimension(200, 0)); // With a height set to 0, means that it will adapt to the size of the main frame itself
        sidePanel.setLayout(new BorderLayout());


        // Creating panels to contain all the buttons
        JPanel emptyPanel = new JPanel(); // panel will be at the top of the side panel but will be empty just for making the button panel lower
        emptyPanel.setPreferredSize(new Dimension(0, 100));
        emptyPanel.setBackground(new Color(30, 90, 180));

        // Making a panel dedicated to buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 1,0, 0));
        buttonPanel.setBackground(new Color(30, 90, 180));

        // Creating the buttons for the side panel

        // Setting the common settings to all of the buttons
        for (JButton btn: new JButton[]{dashboardButton, customersButton, appointmentsButton, paymentsButton, logoutButton}){
            btn.setMaximumSize(new Dimension(0, 20));
            btn.setBackground(new Color(30, 90, 180));
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode(borderColour)));

        }

        // Adding top border to only the first button
        dashboardButton.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.decode(borderColour)));

        // Adding all the buttons to the button panel
        buttonPanel.add(dashboardButton);
        buttonPanel.add(customersButton);
        buttonPanel.add(appointmentsButton);
        buttonPanel.add(paymentsButton);
        buttonPanel.add(logoutButton);

        // Adding the panels to the side panel
        sidePanel.add(emptyPanel, BorderLayout.NORTH);
        sidePanel.add(buttonPanel, BorderLayout.CENTER);

        return sidePanel;
    }

}

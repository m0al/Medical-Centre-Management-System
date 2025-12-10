package amc.roleModules.Staff.staffScreens.Components;

import javax.swing.*;
import java.awt.*;

import amc.roleModules.Staff.staffUtil.panelSwitcher;
import amc.roleModules.Staff.staffScreens.Components.sidePanelComponents.*;
import amc.roleModules.Staff.staffScreens.Components.topPanelComponents;

import amc.roleModules.Staff.staffScreens.welcomeScreen;
import amc.roleModules.Staff.staffScreens.appointmentBookingPanel;
import amc.roleModules.Staff.staffScreens.customerManagementPanel;
import amc.roleModules.Staff.staffScreens.receiptPanel;

public class mainPanelComponents {
    private JPanel customerPanel;
    public static JPanel mainPanel(){

        // Creating a main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel topPanel = topPanelComponents.topPanel();
        JPanel utilsPanel = new JPanel(new BorderLayout());

        // the panels that will be routed





        panelSwitcher switcher = new panelSwitcher(utilsPanel);
        // customizing the buttons to allow the main panel to switch on keypress
        sidePanelComponents.customersButton.addActionListener(e -> {
                customerManagementPanel custPanelClass = new customerManagementPanel();
                JPanel customerPanel = custPanelClass.customerManagement();
                switcher.showPanel(customerPanel);
            }
        );
        sidePanelComponents.appointmentsButton.addActionListener(e -> {
            appointmentBookingPanel appPanelClass = new appointmentBookingPanel();
            JPanel appointmentPanel = appPanelClass.appointmentBooking();
            switcher.showPanel(appointmentPanel);
        });
        sidePanelComponents.paymentsButton.addActionListener(e -> {
            receiptPanel recPanalClass = new receiptPanel();
             JPanel recPanel = recPanalClass.receiptPanel();
            switcher.showPanel(recPanel);
        });
        sidePanelComponents.dashboardButton.addActionListener(e -> {
            welcomeScreen welcomeScreenPanel = new welcomeScreen();
            switcher.showPanel(welcomeScreenPanel);
        });

//        utilsPanel.add(customerPanel);
//        utilsPanel.add(appointmentPanel);
//        utilsPanel.add(recPanel);
        welcomeScreen welcomeScreenPanel = new welcomeScreen();
        utilsPanel.add(welcomeScreenPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(utilsPanel, BorderLayout.CENTER);



        return mainPanel;
    }
}

package amc.roleModules.Staff.staffScreens.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import com.toedter.calendar.*;
import amc.userSession;

public class topPanelComponents {

    static UserRepository userRepo = new UserRepository();
    public static JPanel topPanel() {

        // Creating the top panel (quick access panel)
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(0, 40));
        topPanel.setLayout(new GridLayout(1, 2));
//        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#D6D6D6")));

        // Right sub-panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 7));
        buttonPanel.setOpaque(false);

        // Creating 3 quick access buttons
        JButton createCustomerButton = new JButton("+ Create Customer");
        JButton bookAppointmentButton = new JButton("+ Book Appointment");
        JButton createReceiptButton = new JButton("+ Create Receipt");
        JButton updateOwnProfileButton = new JButton("Update Own Profile");

        // Setting the common views to all of the buttons
        for (JButton btn: new JButton[]{createReceiptButton, createCustomerButton, bookAppointmentButton, updateOwnProfileButton}){
            btn.setBackground(Color.decode("#1E90FF"));
            btn.setForeground(Color.WHITE);
            btn.setSize(new Dimension(70, 10));
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setBorderPainted(true);
        }


        createCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new createNewUserPopUp().setVisible(true);
            }
        });

        bookAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new createNewAppointmentPopUp().setVisible(true);
            }
        });

        createReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new createNewReceiptPopUp().setVisible(true);
            }
        });

        updateOwnProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserRepository repo = new UserRepository();
                User currentUser = repo.findByID(userSession.getUserId()).get();
                new updateOwnProfilePopUp(currentUser).setVisible(true);
            }
        });

        buttonPanel.add(createCustomerButton);
        buttonPanel.add(bookAppointmentButton);
        buttonPanel.add(createReceiptButton);
        buttonPanel.add(updateOwnProfileButton);

        topPanel.add(buttonPanel);


        return topPanel;
    }
}

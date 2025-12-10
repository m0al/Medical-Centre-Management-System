package amc.roleModules.Staff.staffScreens.Components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import amc.roleModules.Staff.staffUtil.createNewItem;

public class createNewUserPopUp extends JFrame{

    private JTextField nameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    public JButton submitButton;

    public createNewUserPopUp() {

        setTitle("New Customer Record");
        setSize(400, 300);
        setLocationRelativeTo(null); // center the frame

        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 6));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Labels and Fields
        mainPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        mainPanel.add(nameField);

        mainPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        mainPanel.add(emailField);

        mainPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        mainPanel.add(phoneField);

        mainPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        mainPanel.add(addressField);

        mainPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        mainPanel.add(passwordField);

        // Submit Button
        submitButton = new JButton("Submit");
        mainPanel.add(submitButton);

        // Empty label to align grid
        mainPanel.add(new JLabel(""));

        submitButton.setBackground(Color.decode("#1E90FF"));
        submitButton.setForeground(Color.WHITE);
        submitButton.setSize(new Dimension(70, 10));
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(true);
        submitButton.setContentAreaFilled(true);
        submitButton.setBorderPainted(true);


        for (JTextField text: new JTextField[] {passwordField, emailField, phoneField, nameField, addressField}) {
            text.setPreferredSize(new Dimension(40, 15));
            text.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        add(mainPanel);

        // Button Action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateForm();
            }
        });
    }

    private void validateForm() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Presence Check
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email Validation
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email address!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If all good
        JOptionPane.showMessageDialog(this, "Customer record created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        createNewItem.createNewUser(name, email, phone, address, password);
        this.dispose(); // Closing the frame after user has been created!
    }

}


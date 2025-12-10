package amc.roleModules.Staff.staffScreens.Components;

import javax.swing.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import amc.dataAccess.UserRepository;
import amc.dataConstants.RoleTypes;
import amc.dataModels.User;

public class updateOwnProfilePopUp extends JFrame {
    private JTextField nameField, addressField, phoneField, emailField;
    private JPasswordField passwordField;
    private JButton saveButton, cancelButton;

    // Store original values
    private final User currentUser;

    public updateOwnProfilePopUp(User user) {
        this.currentUser = user;

        setTitle("Update Profile");
        setSize(400, 350);
        setLocationRelativeTo(null); // Center window
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Name
        mainPanel.add(new JLabel("Name:"));
        nameField = new JTextField(user.getName());
        mainPanel.add(nameField);

        // Address
        mainPanel.add(new JLabel("Address:"));
        addressField = new JTextField(user.getAddress());
        mainPanel.add(addressField);

        // Phone
        mainPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField(user.getPhone());
        mainPanel.add(phoneField);

        // Email
        mainPanel.add(new JLabel("Email:"));
        emailField = new JTextField(user.getEmail());
        mainPanel.add(emailField);

        // Password
        mainPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(user.getPassword()); // ⚠️ in real apps, don’t pre-fill raw password!
        mainPanel.add(passwordField);

        // Buttons
        saveButton = new JButton("Save Changes");
        styleBlueButton(saveButton);

        cancelButton = new JButton("Cancel");
        styleBlueButton(cancelButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        saveButton.addActionListener(this::handleSave);
        cancelButton.addActionListener(e -> dispose());
    }

    private void styleBlueButton(JButton button) {
        button.setBackground(Color.decode("#1E90FF"));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
    }

    private void handleSave(ActionEvent e) {
        String newName = nameField.getText().trim();
        String newAddress = addressField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPassword = new String(passwordField.getPassword());

        // Compare changes
        StringBuilder changes = new StringBuilder("Updated fields:\n");
        if (!newName.equals(currentUser.getName())) changes.append("Name: ").append(newName).append("\n");
        if (!newAddress.equals(currentUser.getAddress())) changes.append("Address: ").append(newAddress).append("\n");
        if (!newPhone.equals(currentUser.getPhone())) changes.append("Phone: ").append(newPhone).append("\n");
        if (!newEmail.equals(currentUser.getEmail())) changes.append("Email: ").append(newEmail).append("\n");
        if (!newPassword.equals(currentUser.getPassword())) changes.append("Password: (changed)\n");

        // TODO: call UserRepository to update DB here
        JOptionPane.showMessageDialog(this, changes.toString(), "Profile Updated", JOptionPane.INFORMATION_MESSAGE);

        // Saving the changes in the file
        UserRepository userRepo = new UserRepository();
        User newUser = new User(this.currentUser.getUserId(), RoleTypes.staff, newName, newEmail, newPhone, newAddress, newPassword);
        userRepo.saveOrUpdate(newUser);

        dispose();
    }
}

package amc.roleModules.Staff.staffScreens.Components;

import amc.dataModels.User;
import amc.dataAccess.UserRepository;
import amc.dataConstants.RoleTypes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;



public class updateUser extends JFrame {
    private JTextField nameField, addressField, emailField, phoneField;
    private JButton submitButton, cancelButton, deleteButton;

    // Store original values
    private String userID, userPassword,  originalName, originalAddress, originalEmail, originalPhone;

    public updateUser(User user) {
        setTitle("Update Customer");
        setSize(420, 320);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        userID = user.getUserId();
        userPassword = user.getPassword();

        String name = user.getName();
        String address = user.getAddress();
        String email = user.getEmail();
        String phone = user.getPhone();
        // Save original values
        this.originalName = name;
        this.originalAddress = address;
        this.originalEmail = email;
        this.originalPhone = phone;

        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 8));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Name
        mainPanel.add(new JLabel("Name:"));
        nameField = new JTextField(name);
        mainPanel.add(nameField);

        // Address
        mainPanel.add(new JLabel("Address:"));
        addressField = new JTextField(address);
        mainPanel.add(addressField);

        // Email
        mainPanel.add(new JLabel("Email:"));
        emailField = new JTextField(email);
        mainPanel.add(emailField);

        // Phone
        mainPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField(phone);
        mainPanel.add(phoneField);

        // Buttons
        submitButton = new JButton("Save Changes");
        styleBlueButton(submitButton);

        cancelButton = new JButton("Cancel");
        styleBlueButton(cancelButton);

        deleteButton = new JButton("Delete User");
        styleRedButton(deleteButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        submitButton.addActionListener(this::handleSave);
        cancelButton.addActionListener(e -> dispose());
        deleteButton.addActionListener(e -> handleDelete(user));
    }

    private void styleBlueButton(JButton button) {
        button.setBackground(Color.decode("#1E90FF"));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
    }

    private void styleRedButton(JButton button) {
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 30));
    }

    private void handleSave(ActionEvent e) {
        Map<String, String> changes = new HashMap<>();

        // Compare fields with original
        if (!nameField.getText().trim().equals(originalName)) {
            changes.put("name", nameField.getText().trim());
        }
        if (!addressField.getText().trim().equals(originalAddress)) {
            changes.put("address", addressField.getText().trim());
        }
        if (!emailField.getText().trim().equals(originalEmail)) {
            changes.put("email", emailField.getText().trim());
        }
        if (!phoneField.getText().trim().equals(originalPhone)) {
            changes.put("phone", phoneField.getText().trim());
        }

        if (changes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No changes detected.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {

            User newUser = new User(userID, RoleTypes.customer, nameField.getText().trim(), emailField.getText().trim(), phoneField.getText().trim(), addressField.getText().trim(), userPassword);

            JOptionPane.showMessageDialog(this, "Changes to apply:\n" + changes, "Updated Fields", JOptionPane.INFORMATION_MESSAGE);
            UserRepository repo = new UserRepository();
            repo.saveOrUpdate(newUser);
        }

        dispose();
    }

    private void handleDelete(User user) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: call your delete logic here
            UserRepository userRepo = new UserRepository();

            // Deleting the user
            userRepo.delete(user);

            dispose();
        }
    }

}

package amc.roleModules.Customer.customerScreens;

import amc.dataModels.User;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.regex.Pattern;


public class CustomerProfilePanel extends JPanel {

    private final CustomerService service;

    private JTextField userIdField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JPasswordField passwordField;
    private JCheckBox showPwd;

    public CustomerProfilePanel(CustomerService service) {
        this.service = service;
        initUI();
        loadProfile();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        userIdField   = new JTextField(); userIdField.setEditable(false);
        nameField     = new JTextField();
        emailField    = new JTextField();
        phoneField    = new JTextField();
        addressField  = new JTextField();
        passwordField = new JPasswordField();
        showPwd       = new JCheckBox("Show");

        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new PhoneFilter());

        JButton resetBtn = new JButton("Reset");
        JButton saveBtn  = new JButton("Save Profile");

        saveBtn.addActionListener(_ -> save());
        resetBtn.addActionListener(_ -> loadProfile());
        showPwd.addActionListener(_ ->
                passwordField.setEchoChar(showPwd.isSelected() ? (char)0 : defaultEchoChar()));

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "save");
        getActionMap().put("save", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { save(); }
        });

        int row = 0;

        addAt(new JLabel("User ID"),   0, row++);
        addAt(new JLabel("Name"),      0, row++);
        addAt(new JLabel("Email"),     0, row++);
        addAt(new JLabel("Phone"),     0, row++);
        addAt(new JLabel("Address"),   0, row++);
        addAt(new JLabel("Password"),  0, row++);


        row = 0;
        addAt(userIdField,  1, row++);
        addAt(nameField,    1, row++);
        addAt(emailField,   1, row++);
        addAt(phoneField,   1, row++);
        addAt(addressField, 1, row++);

        JPanel pwdRow = new JPanel(new BorderLayout(8, 0));
        pwdRow.add(passwordField, BorderLayout.CENTER);
        pwdRow.add(showPwd, BorderLayout.EAST);
        addAt(pwdRow, 1, row++);


        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.add(resetBtn);
        btns.add(saveBtn);
        addAt(btns, 1, row);
    }

    private void addAt(Component comp, int gridx, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = (gridx == 1) ? 1 : 0;
        c.gridx = gridx;
        c.gridy = gridy;
        add(comp, c);
    }

    private char defaultEchoChar() {
        Object v = UIManager.get("PasswordField.echoChar");
        return (v instanceof Character) ? (Character) v : '\u2022';
    }

    private void loadProfile() {
        User me = service.reloadCurrentUser();
        if (me == null) {
            setEnabled(false);
            JOptionPane.showMessageDialog(this, "No logged-in user found. Please login first.",
                    "Profile", JOptionPane.WARNING_MESSAGE);
            return;
        }
        setEnabled(true);
        userIdField.setText(nz(me.getUserId()));
        nameField.setText(nz(me.getName()));
        emailField.setText(nz(me.getEmail()));
        phoneField.setText(nz(me.getPhone()));
        addressField.setText(nz(me.getAddress()));
        passwordField.setText("");
        showPwd.setSelected(false);
        passwordField.setEchoChar(defaultEchoChar());
    }

    private void save() {
        User me = service.getCurrentUser();
        if (me == null) {
            JOptionPane.showMessageDialog(this, "No logged-in user.", "Profile", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String pwdIn   = new String(passwordField.getPassword());

        if (name.isBlank())  { warn("Name is required.",  nameField);  return; }
        if (email.isBlank()) { warn("Email is required.", emailField); return; }


        String passwordToSave = pwdIn.isBlank() ? nz(me.getPassword()) : pwdIn;

        try {
            service.updateOwnProfile(name, email, phone, address, passwordToSave);
            JOptionPane.showMessageDialog(this, "Profile saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadProfile();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save profile:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --------- Helpers ---------
    private String nz(String s) { return (s == null) ? "" : s; }

    private void warn(String msg, JComponent focus) {
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
        if (focus != null) focus.requestFocusInWindow();
    }


    private static class PhoneFilter extends DocumentFilter {
        private static final Pattern ALLOWED = Pattern.compile("[0-9 +\\-()]*");
        @Override public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str != null && ALLOWED.matcher(str).matches()) super.insertString(fb, offs, str, a);
        }
        @Override public void replace(FilterBypass fb, int offs, int len, String str, AttributeSet a)
                throws BadLocationException {
            if (str != null && ALLOWED.matcher(str).matches()) super.replace(fb, offs, len, str, a);
        }
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component comp : getComponents()) comp.setEnabled(enabled);
    }
}
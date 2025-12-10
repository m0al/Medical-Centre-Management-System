package amc.roleModules.Customer.customerScreens.Universal.universalScreens;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import amc.appCore.ScreenRouter;
import amc.roleModules.Customer.customerScreens.Universal.universalUtil.LoginController;
import amc.userSession;
import java.util.Optional;
import javax.swing.JOptionPane;

public class LoginScreen extends javax.swing.JFrame {

    public LoginScreen() {
        initComponents();

//<editor-fold defaultstate="collapsed" desc="STEP 1: Create the UserLookup dependency.">
        /*
        * This tells the controller how to find a user inside the JSON file.
         */
//</editor-fold>
        LoginController.UserLookup userLookup = key -> {
            UserRepository repository = new UserRepository();

// Try to find a user by email first.
            for (User u : repository.findAll()) {
                if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(key)) {
                    return new SimpleUserRecord(u);
                }
            }

// If not found, try to match by userID.
            for (User u : repository.findAll()) {
                if (u.getUserId() != null && u.getUserId().equalsIgnoreCase(key)) {
                    return new SimpleUserRecord(u);
                }
            }

// If neither match, return null meaning "user not found".
            return null;
        };

//<editor-fold defaultstate="collapsed" desc="STEP 2: Create the DashboardNavigator dependency.">
        /*
    * This tells the controller what to do when a user successfully logs in.
    * In our case, we set the global userSession and let ScreenRouter open the right dashboard.
         */
//</editor-fold>
        LoginController.DashboardNavigator navigator = record -> {
            try {
                UserRepository repository = new UserRepository();
                User matchedUser = null;

// Try finding the full user record by email.
                if (record.getEmail() != null) {
                    Optional<User> found = repository.findByEmail(record.getEmail());
                    if (found.isPresent()) {
                        matchedUser = found.get();
                    }
                }

// If still not found, try by userID.
                if (matchedUser == null && record.getUsername() != null) {
                    for (User u : repository.findAll()) {
                        if (u.getUserId().equalsIgnoreCase(record.getUsername())) {
                            matchedUser = u;
                            break;
                        }
                    }
                }

// If we found a user, set the session and open the dashboard.
                if (matchedUser != null) {
                    userSession.setFromUser(matchedUser);
                    ScreenRouter.openDashboardForCurrentUser();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "An error occurred while opening the dashboard.\n" + e.getMessage(),
                        "Navigation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        };

//<editor-fold defaultstate="collapsed" desc="STEP 3: Hand all of our GUI objects into the controller.">
//</editor-fold>
        new LoginController(
                this, // The parent window (this JFrame)
                txtUser, // The text field for userId or email
                txtPassword, // The password field
                btnLogin, // The login button
                cbPasswordVisibility, // The "show password" checkbox
                userLookup, // The user lookup we wrote above
                navigator // The dashboard navigator we wrote above
        );
    }

    class SimpleUserRecord implements LoginController.UserRecord {

        private final User user;

        public SimpleUserRecord(User user) {
            this.user = user;
        }

        @Override
        public String getEmail() {
            return user.getEmail();
        }

        @Override
        public String getUsername() {
            return user.getUserId();
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getRole() {
            return user.getRole();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubheading = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        cbPasswordVisibility = new javax.swing.JCheckBox();
        btnLogin = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AMC - Login");
        setName("Login Screen"); // NOI18N
        setResizable(false);

        loginPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("APU Medical Centre");

        lblSubheading.setForeground(new java.awt.Color(102, 102, 102));
        lblSubheading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubheading.setText("Please login to continue.");

        lblUser.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUser.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblUser.setText("User ID | Email: ");

        txtUser.setBackground(new java.awt.Color(204, 204, 204));
        txtUser.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        lblPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPassword.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblPassword.setText("Password: ");

        txtPassword.setBackground(new java.awt.Color(204, 204, 204));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N

        cbPasswordVisibility.setForeground(new java.awt.Color(255, 255, 255));

        btnLogin.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        btnLogin.setText("Login");

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblSubheading, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                            .addGroup(loginPanelLayout.createSequentialGroup()
                                .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                .addGap(6, 6, 6)
                                .addComponent(txtUser, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                                .addGap(158, 158, 158))
                            .addGroup(loginPanelLayout.createSequentialGroup()
                                .addComponent(lblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                                .addGap(6, 6, 6)
                                .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbPasswordVisibility, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(156, 156, 156))))
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                        .addGap(88, 88, 88)))
                .addContainerGap())
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTitle)
                .addGap(6, 6, 6)
                .addComponent(lblSubheading)
                .addGap(58, 58, 58)
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUser)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbPasswordVisibility, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JCheckBox cbPasswordVisibility;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubheading;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}

package amc.roleModules.Universal.universalScreens;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import amc.appCore.ScreenRouter;
import amc.roleModules.Universal.universalUtil.LoginController;
import amc.userSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Optional;

/**
 * This file provides the user interface for the login screen.
 * It is designed with a modern, minimalist aesthetic inspired by Apple's design
 * language, using only standard Java Swing and AWT libraries. It replaces the
 * previous NetBeans-generated GUI.
 */
public class LoginScreen extends JFrame {

    // --- UI Components ---
    private JTextField txtUser;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox cbPasswordVisibility;

    /**
     * The main constructor for the LoginScreen.
     * It initializes the user interface and then wires up the application logic.
     */
    public LoginScreen() {
        super("AMC - Login"); // Sets the window title.
        initializeUi();
        initializeLogic();
    }

    /**
     * This method sets up all the visual components of the login screen.
     * It defines the layout, colors, fonts, and custom styles for the components
     * to create a modern and clean user experience.
     */
    private void initializeUi() {
        // --- Frame Setup ---
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600); // A standard landscape aspect ratio for desktop applications.
        setLocationRelativeTo(null); // This centers the window.
        setResizable(false);

        // --- Main Panel ---
        // This panel holds all other components. We use a GridBagLayout for precise
        // control over positioning, which is essential for a clean, centered design.
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 247)); // A light, clean gray, similar to Apple's UI.
        mainPanel.setBorder(new EmptyBorder(20, 150, 40, 150)); // Provides padding around the content.
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Vertical spacing between components.
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Each component takes the full width.
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components will stretch horizontally.
        gbc.weightx = 1.0; // Allows horizontal stretching.

        // --- Fonts ---
        // We attempt to use modern, clean fonts, falling back to system defaults.
        Font titleFont = getModernFont("Segoe UI", Font.BOLD, 32);
        Font defaultFont = getModernFont("Segoe UI", Font.PLAIN, 16);
        Font smallFont = getModernFont("Segoe UI", Font.PLAIN, 14);

        // --- Title and Subheading ---
        JLabel lblTitle = new JLabel("APU Medical Centre", SwingConstants.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(Color.BLACK);
        gbc.insets = new Insets(20, 0, 5, 0); // Less space after the title.
        mainPanel.add(lblTitle, gbc);

        JLabel lblSubheading = new JLabel("Please sign in to continue.", SwingConstants.CENTER);
        lblSubheading.setFont(defaultFont);
        lblSubheading.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 0, 40, 0); // More space after the subheading.
        mainPanel.add(lblSubheading, gbc);

        // --- User Input Field ---
        gbc.insets = new Insets(10, 0, 10, 0); // Reset insets for form fields.
        txtUser = new ModernTextField("User ID or Email");
        txtUser.setFont(defaultFont);
        mainPanel.add(txtUser, gbc);

        // --- Password Input Field ---
        txtPassword = new ModernPasswordField("Password");
        txtPassword.setFont(defaultFont);
        mainPanel.add(txtPassword, gbc);

        // --- Password Visibility Checkbox ---
        cbPasswordVisibility = new JCheckBox("Show Password");
        cbPasswordVisibility.setFont(smallFont);
        cbPasswordVisibility.setForeground(Color.GRAY);
        cbPasswordVisibility.setOpaque(false); // Makes the background transparent.
        cbPasswordVisibility.setFocusable(false); // Removes the focus highlight.
        gbc.anchor = GridBagConstraints.WEST; // Aligns the checkbox to the left.
        gbc.fill = GridBagConstraints.NONE; // Prevents the checkbox from stretching.
        mainPanel.add(cbPasswordVisibility, gbc);

        // --- Login Button ---
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill for the button.
        gbc.ipady = 20; // Makes the button taller, giving it a premium feel.
        gbc.insets = new Insets(30, 0, 0, 0); // Extra space above the button.
        btnLogin = new JButton("Sign In");
        btnLogin.setFont(defaultFont.deriveFont(Font.BOLD));
        mainPanel.add(btnLogin, gbc);
    }

    /**
     * This method initializes the business logic.
     * It replicates the logic from the original constructor, creating dependencies
     * for the LoginController and then handing over control of the UI components to it.
     * This decouples the view from the controller logic.
     */
    private void initializeLogic() {
        // This dependency tells the controller how to find a user in the data source.
        // It first tries to find a user by email, and if not found, then tries by user ID.
        // The search is case-insensitive as requested.
        LoginController.UserLookup userLookup = key -> {
            UserRepository repository = new UserRepository();
            
            // First, try to find a user by matching the email in a case-insensitive manner.
            Optional<User> foundByEmail = repository.findAll().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(key))
                .findFirst();
            
            if (foundByEmail.isPresent()) {
                return new SimpleUserRecord(foundByEmail.get());
            }

            // If no email matched, try to find a user by matching the user ID, case-insensitively.
            Optional<User> foundById = repository.findAll().stream()
                .filter(u -> u.getUserId() != null && u.getUserId().equalsIgnoreCase(key))
                .findFirst();

            // Return the record if found by ID, otherwise return null.
            return foundById.map(SimpleUserRecord::new).orElse(null);
        };

        // This dependency tells the controller what to do after a successful login.
        // It sets the global user session and uses the ScreenRouter to open the
        // correct dashboard for the logged-in user's role.
        LoginController.DashboardNavigator navigator = record -> {
            try {
                UserRepository repository = new UserRepository();
                User matchedUser = null;

                // After login, we need to find the full User object again to set the session.
                // First, we try to find the user by their email.
                if (record.getEmail() != null) {
                    Optional<User> found = repository.findByEmail(record.getEmail());
                    if (found.isPresent()) {
                        matchedUser = found.get();
                    }
                }

                // If the user wasn't found by email (e.g., they logged in with a user ID),
                // we then try to find them by their user ID.
                if (matchedUser == null && record.getUsername() != null) {
                    Optional<User> found = repository.findAll().stream()
                        .filter(u -> u.getUserId().equalsIgnoreCase(record.getUsername()))
                        .findFirst();
                    if (found.isPresent()) {
                        matchedUser = found.get();
                    }
                }

                // If a user was successfully retrieved, set the session and open the dashboard.
                if (matchedUser != null) {
                    userSession.setFromUser(matchedUser);
                    ScreenRouter.openDashboardForCurrentUser();
                    // Close the login window upon successful navigation.
                    SwingUtilities.getWindowAncestor(btnLogin).dispose();
                } else {
                    // This is a fallback error in case the user disappears from the data source
                    // between login and navigation.
                    JOptionPane.showMessageDialog(this,
                        "Could not retrieve user details after login.",
                        "Navigation Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "An error occurred while opening the dashboard.\n" + e.getMessage(),
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        };

        // The controller is given all the UI components it needs to manage.
        // This wires up all the event listeners for button clicks and text field actions.
        new LoginController(
            this,
            txtUser,
            txtPassword,
            btnLogin,
            cbPasswordVisibility,
            userLookup,
            navigator
        );
    }

    /**
     * A helper method to find and create a modern-looking font.
     * It searches for a list of preferred fonts and returns the first one found
     * on the system, or a default sans-serif font if none are available.
     *
     * @param preferredFont The name of the font to try first (e.g., "Segoe UI").
     * @param style The font style (e.g., Font.BOLD).
     * @param size The font size.
     * @return The created Font object.
     */
    private Font getModernFont(String preferredFont, int style, int size) {
        String[] fallbacks = { "Inter", "SF Pro Display", "Helvetica Neue", "Arial", "SansSerif" };
        String fontName = preferredFont;

        boolean found = false;
        String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String available : availableFonts) {
            if (available.equals(fontName)) {
                found = true;
                break;
            }
        }

        if (!found) {
            for (String fallback : fallbacks) {
                for (String available : availableFonts) {
                    if (available.equals(fallback)) {
                        fontName = fallback;
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }
        return new Font(fontName, style, size);
    }

    /**
     * An adapter class that implements the UserRecord interface required by the
     * LoginController. This allows the controller to work with our User data model
     * without being tightly coupled to it.
     */
    class SimpleUserRecord implements LoginController.UserRecord {
        private final User user;

        public SimpleUserRecord(User user) {
            this.user = user;
        }

        @Override
        public String getEmail() { return user.getEmail(); }
        @Override
        public String getUsername() { return user.getUserId(); }
        @Override
        public String getPassword() { return user.getPassword(); }
        @Override
        public String getRole() { return user.getRole(); }
    }

    /**
     * The main entry point to run the LoginScreen independently for testing.
     * It ensures the UI is created on the Event Dispatch Thread for thread safety.
     */
    public static void main(String[] args) {
        // The UIManager is used to improve the look and feel of Swing components,
        // particularly for font rendering.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // All Swing applications should be started on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}

/**
 * A custom JTextField with placeholder text functionality and a modern, rounded look.
 * The placeholder is implemented by setting the text and changing the foreground color.
 */
class ModernTextField extends JTextField {
    private final String placeholder;
    private boolean isPlaceholderVisible;

    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setBackground(new Color(230, 230, 232));
        setCaretColor(Color.BLACK);

        showPlaceholder();

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderVisible) {
                    hidePlaceholder();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    showPlaceholder();
                }
            }
        });
    }

    private void showPlaceholder() {
        setText(placeholder);
        setFont(getFont().deriveFont(Font.ITALIC));
        setForeground(Color.GRAY);
        isPlaceholderVisible = true;
    }

    private void hidePlaceholder() {
        setText("");
        setFont(getFont().deriveFont(Font.PLAIN));
        setForeground(Color.BLACK);
        isPlaceholderVisible = false;
    }

    @Override
    public String getText() {
        return isPlaceholderVisible ? "" : super.getText();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
        super.paintComponent(g);
    }
}

/**
 * A custom JPasswordField that inherits the modern look of ModernTextField
 * and implements the same placeholder text functionality.
 */
class ModernPasswordField extends JPasswordField {
    private final String placeholder;
    private boolean isPlaceholderVisible;

    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setBackground(new Color(230, 230, 232));
        setCaretColor(Color.BLACK);

        showPlaceholder();

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isPlaceholderVisible) {
                    hidePlaceholder();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getPassword().length == 0) {
                    showPlaceholder();
                }
            }
        });
    }

    private void showPlaceholder() {
        // Do NOT set echoChar here. Let the LoginController manage it.
        setText(placeholder);
        setFont(getFont().deriveFont(Font.ITALIC));
        setForeground(Color.GRAY);
        isPlaceholderVisible = true;
    }

    private void hidePlaceholder() {
        setEchoChar(new JPasswordField().getEchoChar()); // Use default echo char
        setText("");
        setFont(getFont().deriveFont(Font.PLAIN));
        setForeground(Color.BLACK);
        isPlaceholderVisible = false;
    }

    @Override
    public char[] getPassword() {
        return isPlaceholderVisible ? new char[0] : super.getPassword();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
        super.paintComponent(g);
    }
}



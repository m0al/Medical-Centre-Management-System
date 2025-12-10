package amc.roleModules.Staff.staffScreens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class welcomeScreen extends JPanel {

    public welcomeScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(60, 40, 60, 40));

        // Title
        JLabel title = new JLabel("Welcome to", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        title.setForeground(new Color(70, 70, 70));

        // Subtitle (Main Heading)
        JLabel subtitle = new JLabel("APU Medical Center", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        subtitle.setForeground(new Color(30, 90, 180));

        // Staff Page Label
        JLabel staffLabel = new JLabel("Staff Page", SwingConstants.CENTER);
        staffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        staffLabel.setForeground(new Color(100, 100, 100));

        // Logo placeholder (replace with ImageIcon if you have a logo)
        JLabel logo = new JLabel("🏥", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));

        // Center container
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(title);
        centerPanel.add(subtitle);
        centerPanel.add(staffLabel);
        centerPanel.add(logo);

        add(centerPanel, BorderLayout.CENTER);
    }
}

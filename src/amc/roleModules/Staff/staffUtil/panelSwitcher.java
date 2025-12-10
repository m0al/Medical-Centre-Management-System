package amc.roleModules.Staff.staffUtil;

import javax.swing.*;
import java.awt.*;

// This class is responsible to route the correct panel to the rightPanel component
// When any of the sideBar panels' buttons have been pressed
public class panelSwitcher {

    private final JPanel mainPanel;

    public panelSwitcher(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void showPanel(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

}

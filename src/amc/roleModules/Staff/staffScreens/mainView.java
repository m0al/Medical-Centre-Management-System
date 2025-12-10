package amc.roleModules.Staff.staffScreens;

import amc.roleModules.Staff.staffScreens.Components.mainPanelComponents;
import amc.roleModules.Staff.staffScreens.Components.sidePanelComponents;
import amc.roleModules.Staff.staffScreens.Components.topPanelComponents;
import amc.roleModules.Staff.staffScreens.Components.mainPanelComponents;

import javax.swing.*;
import java.awt.*;

public class mainView {


    public mainView(){
        // Creating the frame
        JFrame frame = new JFrame();
        frame.setSize(900, 700); // Setting the size of the frame
        frame.setBackground(Color.decode("#F2F2F2"));
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel rightPanel = mainPanelComponents.mainPanel();

        // initializing the panels
        JPanel side = sidePanelComponents.sidePanel();

        // Adding all of the components to the frame
        frame.add(side, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        // making the frame visible
        frame.setVisible(true);

    }
}

package amc.roleModules.Staff.staffScreens;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import amc.dataAccess.UserRepository;
import amc.dataConstants.RoleTypes;
import amc.dataModels.User;
import amc.roleModules.Staff.staffUtil.searchFunctionalities;

import amc.roleModules.Staff.staffScreens.Components.updateUser;

// Make the table in the customer/appointments/receipts with the data from the text/json files.

public class customerManagementPanel {

    public static JTextField searchItem = new JTextField(15);
    public static JScrollPane scrollPane;
    public static String[] columnNames = {"Customer ID", "Customer Name", "Contact Number", "Email", "Address"};

    public JPanel customerManagement() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        // This panel will contain the title, the search text entry and the search button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Title
        JLabel title = new JLabel("Customers");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.GRAY);
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));

        // Search option implementation
        // Creating a panel to encapsulate the text area and the button inside
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 30));
        searchPanel.setBackground(Color.WHITE);


        // Making the text entry for the user to insert the search item
        searchItem.setPreferredSize(new Dimension(40, 30));
        searchItem.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


        // Making the search button


        // This panel will be the one containing all the list of customers
        JPanel customerPanels = new JPanel();
        customerPanels.setBackground(Color.WHITE);
        customerPanels.setLayout(new BoxLayout(customerPanels, BoxLayout.Y_AXIS));



        List<User> customerData = retrieveCustomers();

        // Wrapping the table in a scrollable view
        scrollPane = new JScrollPane(getCustomersDataTable(columnNames, customerData));

        searchItem.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                if (Objects.equals(searchItem.getText(), "")) {
                    scrollPane.setViewportView(getCustomersDataTable(columnNames, retrieveCustomers()));
                } else {
                    scrollPane.setViewportView(getCustomersDataTable(columnNames, searchFunctionalities.searchUser(searchItem.getText())));
                }

            }
        });
        customerPanels.add(scrollPane);

        searchPanel.add(searchLabel);
        searchPanel.add(searchItem);

        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(customerPanels, BorderLayout.CENTER);


        return mainPanel;
    }

    // Making it clean for creating, populating and customising the data table
    private static JTable getCustomersDataTable(String[] columnNames, List<User> customerData) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (User u: customerData) {
            Object[] rowData = {u.getUserId(), u.getName(), u.getPhone(), u.getEmail(), u.getAddress()};
            model.addRow(rowData);
        }

        // Creating & Customizing the JTable
        JTable table = new JTable(model);

        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setForeground(Color.BLACK);

        table.setDefaultEditor(Object.class, null); // Disabling the user to edit a record

        table.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {  // Double click
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow(); // Get selected row index

                    String userID = target.getValueAt(row, 0).toString();
                    UserRepository repo = new UserRepository();
                    Optional<User> clickedUser = repo.findByID(userID);


                    updateUser updatePopUp = new updateUser(clickedUser.get());
                    updatePopUp.setVisible(true);
                }
            }
        });
        return table;

    }

    // This function will let read all the data from the users' database
    private static List<User> retrieveCustomers() {

        UserRepository repo = new UserRepository();

        List<User> allCustomers = new ArrayList<>();

        for (User u: repo.findAll()) {
            if (u.getRole().equals(RoleTypes.customer)) {
                allCustomers.add(u);
            }
        }

        return allCustomers;

    }

}

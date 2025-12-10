package amc.roleModules.Staff.staffScreens;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


import amc.dataAccess.AppointmentRepository;
import amc.dataAccess.PaymentRepository;
import amc.dataAccess.UserRepository;
import amc.dataModels.Appointment;
import amc.dataModels.Payment;
import amc.dataModels.User;
import amc.roleModules.Staff.staffUtil.searchFunctionalities;

public class receiptPanel {

    public static JTextField searchItem = new JTextField(15);
    public static JScrollPane scrollPane;
    public static String[] columnNames = {"Payment ID", "Appointment ID", "Customer ID", "Customer Name", "Amount(RM)", "Method of Payment", "Time"};

    public JPanel receiptPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout());


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Title
        JLabel title = new JLabel("Payment Receipts");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.GRAY);
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Search option implementation
        // Creating a panel to encapsulate the text area and the button inside
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 30));
        searchPanel.setBackground(Color.WHITE);


        // Making the text entry for the user to insert the search item
        searchItem.setPreferredSize(new Dimension(40, 30));
        searchItem.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


        // Making the search button
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));


        JPanel receiptPanel = new JPanel();
        receiptPanel.setBackground(Color.WHITE);
        receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));

        // Making the table that will display all the previously generated receipts

        List<Payment> paymentData = retrievePayments();

        scrollPane = new JScrollPane(getReceiptDataTable(columnNames, paymentData));
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
                    scrollPane.setViewportView(getReceiptDataTable(columnNames, retrievePayments()));
                } else {
                    scrollPane.setViewportView(getReceiptDataTable(columnNames, searchFunctionalities.searchPayment(searchItem.getText())));
                }

            }
        });
        receiptPanel.add(scrollPane);

        searchPanel.add(searchLabel);
        searchPanel.add(searchItem);

        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(receiptPanel, BorderLayout.CENTER);


        return mainPanel;

    }

    private static  JTable getReceiptDataTable(String[] columnNames, List<Payment> paymentData) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        Optional<User> customerName;

        Optional<Appointment> currentAppointment;
        Optional<User> currentUser;

        for (Payment p: paymentData) {
            // getting information about the current appointment and current user
            AppointmentRepository repoAppointment = new AppointmentRepository();
            currentAppointment = repoAppointment.findByID(p.getAppointmentId());
            UserRepository repo = new UserRepository();
            currentUser = repo.findByID(currentAppointment.get().getCustomerId());


            Object[] rowData = {
                    p.getPaymentId(),
                    p.getAppointmentId(),
                    currentAppointment.get().getCustomerId(),
                    currentUser.get().getName(),
                    p.getAmount(),
                    p.getMethod(),
                    p.getTimestampIso()
            };

            model.addRow(rowData);
        }

        JTable table = new JTable(model);

        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setForeground(Color.BLACK);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setForeground(Color.BLACK);

        table.setDefaultEditor(Object.class, null); // Disabling the user to edit a record

        return table;
    }


    private static List<Payment> retrievePayments(){
        return new PaymentRepository().listAll();
    }
}

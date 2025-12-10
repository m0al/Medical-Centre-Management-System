package amc.roleModules.Staff.staffScreens;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
//import com.google.gson.reflect....

import amc.dataAccess.AppointmentRepository;
import amc.roleModules.Staff.staffScreens.Components.updateAppointment;
import amc.roleModules.Staff.staffUtil.searchFunctionalities;
import amc.dataAccess.UserRepository;
import amc.dataModels.Appointment;
import amc.dataModels.User;

public class appointmentBookingPanel {


    // Making the search text field public to access be able to access it in the backend folder
    public static JTextField searchItem = new JTextField(15);
    public static JScrollPane scrollPane;
    public static String[] columnNames = {"Appointment ID", "Customer Name", "Doctor Name", "Date", "Status"};

    public JPanel appointmentBooking() {

        JPanel mainPanel = new JPanel(new BorderLayout());


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        // Title
        JLabel title = new JLabel("Appointments");
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

        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(80, 30));
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        searchButton.setFocusPainted(false);



        // This panel will be the one containing all the list of customers
        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setBackground(Color.WHITE);
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));



        // Creating a table that will display all the appointments

        List<Appointment> appointmentsData = retrieveAppointments();
        // Putting it in a scrollpane to allow scrolling if there are too many for 1 screen to render
        scrollPane = new JScrollPane(getAppointmentDataTable(columnNames, appointmentsData));


        // Handling change in the table as search is being carried out
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
                    scrollPane.setViewportView(getAppointmentDataTable(columnNames, retrieveAppointments()));
                } else {
                    scrollPane.setViewportView(getAppointmentDataTable(columnNames, searchFunctionalities.searchAppointmetns(searchItem.getText())));
                }

            }
        });



        // Adding Customer Panel cards

        appointmentsPanel.add(scrollPane);

        searchPanel.add(searchLabel);
        searchPanel.add(searchItem);

        headerPanel.add(title, BorderLayout.CENTER);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(appointmentsPanel, BorderLayout.CENTER);


        return mainPanel;
    }

    // Function that will read and place all data in a table
    private static  JTable getAppointmentDataTable(String[] columnNames, List<Appointment> appointmentsData) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        Optional<User> customerUser, doctorUser;


        for (Appointment a: appointmentsData) {
            UserRepository userRepo = new UserRepository();
            customerUser = userRepo.findByID(a.getCustomerId());
            doctorUser = userRepo.findByID(a.getDoctorId());

            Object[] rowData = {a.getAppointmentId(), customerUser.get().getName(), doctorUser.get().getName(), a.getDateTimeIso(), a.getStatus()};
            model.addRow(rowData);
        }

        // Creating & customizing the data table
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

                    String appointmentID = target.getValueAt(row, 0).toString();
                    AppointmentRepository repo = new AppointmentRepository();
                    Optional<Appointment> clickedApp = repo.findByID(appointmentID);

                    try {
                        updateAppointment updatePopUp = new updateAppointment(clickedApp.get());
                        updatePopUp.setVisible(true);

                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        return table;
    }


    private static List<Appointment>  retrieveAppointments() {
        return new AppointmentRepository().listAll();

    }


}

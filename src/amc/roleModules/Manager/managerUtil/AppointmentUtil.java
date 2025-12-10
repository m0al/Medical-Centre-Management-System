// This file provides static utility methods for handling appointment-related data and logic.
package amc.roleModules.Manager.managerUtil;

// Third-party library for JSON processing.
import com.google.gson.reflect.TypeToken;

// Standard Java library imports.
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Project-specific imports.
import amc.dataModels.Appointment;
import amc.dataModels.User;
import amc.dataAccess.UserRepository;
import amc.helperUtils.JsonStore;
import amc.helperUtils.DataPaths;
import amc.dataConstants.RoleTypes;

/**
 * PURPOSE: To provide a set of helper functions for managing appointment data.
 * This includes reading, filtering, and retrieving appointments, as well as
 * loading related user data. This is a utility class and cannot be instantiated.
 */
public class AppointmentUtil {

    /**
     * PURPOSE: A private constructor to prevent this utility class from being instantiated.
     * INPUTS: None.
     * OUTPUT: None.
     */
    private AppointmentUtil() {
        // This constructor is intentionally left empty.
    }

    /**
     * PURPOSE: To read all appointment records from the main appointment data file.
     * INPUTS: None.
     * OUTPUT: A list of all appointments. Returns an empty list if reading fails or the file is empty.
     */
    public static List<Appointment> readAppointments() {
        // STEP 1: Define the specific type for a list of Appointment objects for the JSON parser.
        Type appointmentListType = new TypeToken<List<Appointment>>() {}.getType();
        
        // STEP 2: Read the list from the JSON file using the JsonStore helper.
        List<Appointment> allAppointments = JsonStore.readList(DataPaths.appointmentDataPath, appointmentListType);
        
        // STEP 3: If the result is null (e.g., file not found), return a new empty list to prevent errors.
        return allAppointments != null ? allAppointments : new ArrayList<>();
    }

    /**
     * PURPOSE: To filter a list of appointments based on multiple criteria like search text, date, status, and doctor.
     * INPUTS: A list of all appointments, a search query, start and end dates, a status filter, and a doctor ID filter.
     * OUTPUT: A new list of appointments that match all the given criteria, sorted by date.
     */
    public static List<Appointment> filterAppointments(
            List<Appointment> allAppointments,
            String searchQuery,
            LocalDate startDate,
            LocalDate endDate,
            String statusFilter,
            String doctorIdFilter) {

        // STEP 1: Load maps of user IDs to names to make searching by name possible.
        Map<String, String> doctorIdToNameMap = loadDoctorIdToName();
        Map<String, String> patientIdToNameMap = loadPatientIdToName();

        // STEP 2: Start filtering the list of all appointments.
        List<Appointment> filteredAppointments = allAppointments.stream()
            .filter(appointment -> {
                // STEP 2A: Filter by the text search query.
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    String lowerCaseQuery = searchQuery.trim().toLowerCase();
                    String patientName = patientIdToNameMap.getOrDefault(appointment.getCustomerId(), "").toLowerCase();
                    String doctorName = doctorIdToNameMap.getOrDefault(appointment.getDoctorId(), "").toLowerCase();
                    String appointmentId = appointment.getAppointmentId().toLowerCase();

                    // The appointment is kept only if the query matches the patient, doctor, or ID.
                    if (!patientName.contains(lowerCaseQuery) &&
                        !doctorName.contains(lowerCaseQuery) &&
                        !appointmentId.contains(lowerCaseQuery)) {
                        return false; // Exclude if no match is found.
                    }
                }

                // STEP 2B: Filter by the date range.
                if (appointment.getDateTimeIso() != null) {
                    LocalDateTime appointmentDateTime = LocalDateTime.parse(appointment.getDateTimeIso(), DateTimeFormatter.ISO_DATE_TIME);
                    LocalDate appointmentDate = appointmentDateTime.toLocalDate();

                    // Exclude if the appointment date is before the start date.
                    if (startDate != null && appointmentDate.isBefore(startDate)) {
                        return false;
                    }
                    // Exclude if the appointment date is after the end date.
                    if (endDate != null && appointmentDate.isAfter(endDate)) {
                        return false;
                    }
                } else {
                    // If an appointment has no date, it cannot match a date filter.
                    if (startDate != null || endDate != null) {
                        return false;
                    }
                }

                // STEP 2C: Filter by appointment status.
                if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equalsIgnoreCase("All")) {
                    if (appointment.getStatus() == null || !appointment.getStatus().equalsIgnoreCase(statusFilter)) {
                        return false; // Exclude if status does not match.
                    }
                }

                // STEP 2D: Filter by the selected doctor.
                if (doctorIdFilter != null && !doctorIdFilter.trim().isEmpty() && !doctorIdFilter.equalsIgnoreCase("All")) {
                    if (appointment.getDoctorId() == null || !appointment.getDoctorId().equalsIgnoreCase(doctorIdFilter)) {
                        return false; // Exclude if doctor ID does not match.
                    }
                }

                // STEP 2E: If the appointment passed all filters, keep it.
                return true;
            })
            .collect(Collectors.toList());

        // STEP 3: Sort the final filtered list by date and time, from earliest to latest.
        Collections.sort(filteredAppointments, Comparator.comparing(
            appointment -> LocalDateTime.parse(appointment.getDateTimeIso(), DateTimeFormatter.ISO_DATE_TIME)
        ));

        // STEP 4: Return the filtered and sorted list.
        return filteredAppointments;
    }

    /**
     * PURPOSE: To find and retrieve a single appointment by its unique ID.
     * INPUTS: The appointmentId string to search for.
     * OUTPUT: The matching Appointment object, or null if it's not found.
     */
    public static Appointment getAppointmentById(String appointmentId) {
        // STEP 1: Get all appointments from the data source.
        List<Appointment> allAppointments = readAppointments();
        
        // STEP 2: Search through the list to find the first appointment with a matching ID (ignoring case).
        return allAppointments.stream()
                .filter(appointment -> appointment.getAppointmentId().equalsIgnoreCase(appointmentId))
                .findFirst()
                .orElse(null); // Return null if no match is found.
    }

    /**
     * PURPOSE: To print the details of an appointment to the console.
     * INPUTS: The Appointment object to be printed.
     * OUTPUT: None. Prints text to the standard output.
     */
    public static void printAppointment(Appointment appointmentToPrint) {
        // STEP 1: Check if the provided appointment is null to avoid errors.
        if (appointmentToPrint == null) {
            System.out.println("No appointment details to print.");
            return;
        }
        // STEP 2: Print each detail of the appointment to the console.
        System.out.println("--- Appointment Details ---");
        System.out.println("Appointment ID: " + appointmentToPrint.getAppointmentId());
        System.out.println("Date & Time: " + appointmentToPrint.getDateTimeIso());
        System.out.println("Patient ID: " + appointmentToPrint.getCustomerId());
        System.out.println("Doctor ID: " + appointmentToPrint.getDoctorId());
        System.out.println("Status: " + appointmentToPrint.getStatus());
        System.out.println("Created By: " + appointmentToPrint.getCreatedBy());
        System.out.println("Notes: " + appointmentToPrint.getNotes());
        System.out.println("Charge: " + appointmentToPrint.getCharge());
        System.out.println("--------------------------");
    }

    /**
     * PURPOSE: To load all users with the 'Doctor' role into a map of ID to name.
     * INPUTS: None.
     * OUTPUT: A map where each key is a doctor's ID and the value is their name.
     */
    public static Map<String, String> loadDoctorIdToName() {
        // STEP 1: Create a repository to access user data.
        UserRepository userRepository = new UserRepository();
        
        // STEP 2: Get all users from the repository.
        List<User> allUsers = userRepository.findAll();
        
        // STEP 3: Filter for users who are doctors and collect them into a map of ID to name.
        return allUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(RoleTypes.doctor))
                .collect(Collectors.toMap(User::getUserId, User::getName));
    }

    /**
     * PURPOSE: To load all users with the 'Customer' role into a map of ID to name.
     * INPUTS: None.
     * OUTPUT: A map where each key is a patient's ID and the value is their name.
     */
    public static Map<String, String> loadPatientIdToName() {
        // STEP 1: Create a repository to access user data.
        UserRepository userRepository = new UserRepository();
        
        // STEP 2: Get all users from the repository.
        List<User> allUsers = userRepository.findAll();
        
        // STEP 3: Filter for users who are customers and collect them into a map of ID to name.
        return allUsers.stream()
                .filter(user -> user.getRole().equalsIgnoreCase(RoleTypes.customer))
                .collect(Collectors.toMap(User::getUserId, User::getName));
    }
}
// This file provides utility functions for the manager's report viewing screen.
package amc.roleModules.Manager.managerUtil;

// Project-specific imports for data models, data access, helpers, and user session.
import amc.dataModels.Appointment;
import amc.dataModels.Report;
import amc.dataAccess.AppointmentRepository;
import amc.dataAccess.ReportRepository;
import amc.helperUtils.idGenerator;
import amc.userSession;

// Standard Java library imports for date/time, collections, and streams.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PURPOSE: Handles the business logic for generating and retrieving financial and operational reports.
 * This class interacts with appointment and report data to provide insights to the manager.
 */
public class ViewReportsUtil {

    // STEP 1: Declare repositories for database access.
    private final ReportRepository reportRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * PURPOSE: Initializes the utility by creating instances of the required repositories.
     * INPUTS: None.
     * OUTPUT: A new instance of ViewReportsUtil.
     */
    public ViewReportsUtil() {
        // STEP 1: Instantiate the repository for accessing report data.
        this.reportRepository = new ReportRepository();
        // STEP 2: Instantiate the repository for accessing appointment data.
        this.appointmentRepository = new AppointmentRepository();
    }

    /**
     * PURPOSE: To generate a new daily report summarizing today's appointments and revenue.
     * INPUTS: None.
     * OUTPUT: The newly created Report object.
     */
    public Report generateTodaysReport() {
        // STEP 1: Get the current date and create a formatter to get a "yyyy-MM-dd" string.
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDateString = currentDate.format(shortDateFormatter);

        // STEP 2: Filter all appointments to get only the ones that occurred today.
        List<Appointment> appointmentsForToday = appointmentRepository.listAll().stream()
            .filter(appointment -> appointment.getDateTimeIso() != null && appointment.getDateTimeIso().startsWith(todayDateString))
            .collect(Collectors.toList());

        // STEP 3: Calculate the total number of appointments and the total revenue from them.
        int totalAppointments = appointmentsForToday.size();
        double totalRevenue = appointmentsForToday.stream()
            .mapToDouble(Appointment::getCharge)
            .sum();

        // STEP 4: Create and populate a new Report object with the calculated data.
        Report newReport = new Report();
        newReport.setReportId(idGenerator.nextId("R"));
        newReport.setTitle("Daily Report - " + todayDateString);
        newReport.setGeneratedByUserId(userSession.getUserId());
        newReport.setGeneratedAtIso(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        newReport.setTotalAppointments(totalAppointments);
        newReport.setTotalRevenue(totalRevenue);

        // STEP 5: Save the new report to the database and return it.
        reportRepository.create(newReport);
        return newReport;
    }

    /**
     * PURPOSE: To check if a daily report has already been generated for the current day.
     * INPUTS: None.
     * OUTPUT: True if a report for today exists, otherwise false.
     */
    public boolean doesReportExistForToday() {
        // STEP 1: Get the current date and format it as a "yyyy-MM-dd" string.
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDateString = currentDate.format(shortDateFormatter);

        // STEP 2: Check if any report in the database has a title containing today's date string.
        return reportRepository.listAll().stream()
            .anyMatch(report -> report.getTitle().contains(todayDateString));
    }

    /**
     * PURPOSE: To calculate statistics for the last 30 days of activity.
     * INPUTS: None.
     * OUTPUT: A map containing the total number of appointments and total revenue for the period.
     */
    public Map<String, Number> getMonthlyStatistics() {
        // STEP 1: Get the date range for the last 30 days.
        LocalDate currentDate = LocalDate.now();
        LocalDate thirtyDaysAgoDate = currentDate.minusDays(30);

        // STEP 2: Filter all appointments to get only those within the last 30 days.
        List<Appointment> recentAppointments = appointmentRepository.listAll().stream()
            .filter(appointment -> {
                // STEP 2A: Skip appointments that have no timestamp.
                if (appointment.getDateTimeIso() == null) return false;
                // STEP 2B: Safely parse the appointment date and check if it's within the date range.
                try {
                    LocalDate appointmentDate = LocalDateTime.parse(appointment.getDateTimeIso(), DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
                    return !appointmentDate.isBefore(thirtyDaysAgoDate) && !appointmentDate.isAfter(currentDate);
                } catch (Exception exception) {
                    return false; // Ignore appointments with malformed dates.
                }
            })
            .collect(Collectors.toList());

        // STEP 3: Calculate the total count and revenue from the filtered appointments.
        long totalAppointments = recentAppointments.size();
        double totalRevenue = recentAppointments.stream().mapToDouble(Appointment::getCharge).sum();

        // STEP 4: Create a map to hold the statistics and return it.
        Map<String, Number> statisticsMap = new HashMap<>();
        statisticsMap.put("totalAppointments", totalAppointments);
        statisticsMap.put("totalRevenue", totalRevenue);

        return statisticsMap;
    }
    
    /**
     * PURPOSE: To get a list of all historical reports, sorted with the newest one first.
     * INPUTS: None.
     * OUTPUT: A sorted list of all Report objects.
     */
    public List<Report> getAllReportsSorted() {
        // STEP 1: Retrieve all reports from the repository.
        List<Report> allReports = reportRepository.listAll();
        // STEP 2: Sort the list in descending order based on when they were generated.
        allReports.sort((report1, report2) -> report2.getGeneratedAtIso().compareTo(report1.getGeneratedAtIso()));
        // STEP 3: Return the sorted list.
        return allReports;
    }

    /**
     * PURPOSE: To find a specific daily report by its date.
     * INPUTS: A LocalDate object for the desired report date.
     * OUTPUT: The Report object if found, otherwise null.
     */
    public Report getReportByDate(LocalDate targetDate) {
        // STEP 1: Format the target date into a "yyyy-MM-dd" string to match report titles.
        String targetDateString = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // STEP 2: Search all reports to find one whose title contains the target date string.
        return reportRepository.listAll().stream()
            .filter(report -> report.getTitle().contains(targetDateString))
            .findFirst()
            .orElse(null); // Return null if no matching report is found.
    }

    /**
     * PURPOSE: To find a specific report by its unique ID.
     * INPUTS: The reportId string of the report to find.
     * OUTPUT: The Report object if found, otherwise null.
     */
     public Report getReportById(String reportId) {
        // STEP 1: Search all reports to find one with a matching ID.
        return reportRepository.listAll().stream()
            .filter(report -> report.getReportId().equals(reportId))
            .findFirst()
            .orElse(null); // Return null if no report with that ID is found.
    }
}
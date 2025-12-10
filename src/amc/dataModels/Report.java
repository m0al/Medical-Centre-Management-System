package amc.dataModels;

/** This class represents one report record stored in reportData.json. */
public class Report {

    // Unique id like R001. It identifies the report.
    private String reportId;

    // Short title for the report.
    private String title;

    // User id of the person who generated this report.
    private String generatedByUserId;

    // Date and time in ISO format, for example 2025-08-21T18:00.
    private String generatedAtIso;

    // Total number of appointments in this period.
    private int totalAppointments;

    // Total revenue in this period.
    private double totalRevenue;

    /** Empty constructor for JSON loading and general use. */
    public Report() {}

    /** Convenience constructor for quick seeding or tests. */
    public Report(String reportId, String title, String generatedByUserId,
                  String generatedAtIso, int totalAppointments, double totalRevenue) {
        this.reportId = reportId;
        this.title = title;
        this.generatedByUserId = generatedByUserId;
        this.generatedAtIso = generatedAtIso;
        this.totalAppointments = totalAppointments;
        this.totalRevenue = totalRevenue;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGeneratedByUserId() { return generatedByUserId; }
    public void setGeneratedByUserId(String generatedByUserId) { this.generatedByUserId = generatedByUserId; }

    public String getGeneratedAtIso() { return generatedAtIso; }
    public void setGeneratedAtIso(String generatedAtIso) { this.generatedAtIso = generatedAtIso; }

    public int getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
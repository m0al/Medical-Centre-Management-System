package amc.dataModels;

/** This class represents one appointment record stored in appointmentData.json. */
public class Appointment {

    // Unique id like A001. It identifies the appointment.
    private String appointmentId;

    // Customer user id like U300. It links the appointment to a customer.
    private String customerId;

    // Doctor user id like U200. It links the appointment to a doctor.
    private String doctorId;

    // Date and time in ISO format, for example 2025-08-20T14:30.
    private String dateTimeIso;

    // Optional notes for the appointment.


    // Status string: PENDING, CONFIRMED, COMPLETED, or CANCELLED.
    private String status;

    // Total charge for this appointment. Use 0.0 if not set yet.
    private double charge;

    // User ID of the person who created this appointment.
    private String createdBy;

    /** Empty constructor for JSON loading and general use. */
    public Appointment() {}

   private String notes; 

   public String getNote() { return notes; }
public void setNote(String notes) { this.notes = notes; }
    /** Convenience constructor for quick seeding or tests. */
    public Appointment(String appointmentId, String customerId, String doctorId,
                       String dateTimeIso, String notes, String status, double charge, String createdBy) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.doctorId = doctorId;
        this.dateTimeIso = dateTimeIso;
        this.notes = notes;
        this.status = status;
        this.charge = charge;
        this.createdBy = createdBy;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDateTimeIso() { return dateTimeIso; }
    public void setDateTimeIso(String dateTimeIso) { this.dateTimeIso = dateTimeIso; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCharge() { return charge; }
    public void setCharge(double charge) { this.charge = charge; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
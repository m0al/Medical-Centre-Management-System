package amc.dataModels;

/** This class represents one payment record stored in paymentData.json. */
public class Payment {

    // Unique id like P001. It identifies the payment.
    private String paymentId;

    // Appointment id like A001. It links the payment to an appointment.
    private String appointmentId;

    // Total amount paid. Use 0.0 if not set yet.
    private double amount;

    // Method string: CASH, CARD, or EWALLET.
    private String method;

    // Date and time in ISO format, for example 2025-08-20T15:10.
    private String timestampIso;

    /** Empty constructor for JSON loading and general use. */
    public Payment() {}

    /** Convenience constructor for quick seeding or tests. */
    public Payment(String paymentId, String appointmentId, double amount,
                   String method, String timestampIso) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.amount = amount;
        this.method = method;
        this.timestampIso = timestampIso;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getTimestampIso() { return timestampIso; }
    public void setTimestampIso(String timestampIso) { this.timestampIso = timestampIso; }
}
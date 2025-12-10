package amc.dataConstants;

/** This class holds appointment status strings. It should not be instantiated. */
public final class AppointmentStatusTypes {
    private AppointmentStatusTypes() {} // Prevents creating this utility class.

    public static final String pending   = "PENDING";
    public static final String confirmed = "CONFIRMED";
    public static final String completed = "COMPLETED";
    public static final String cancelled = "CANCELLED";
}
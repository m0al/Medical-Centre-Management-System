package amc.helperUtils;

// This class holds file path constants. It should not be instantiated.
public final class DataPaths {
    private DataPaths() {} // Private constructor prevents instantiation.

    public static final String userDataPath        = "textFiles/userData.json";
    public static final String appointmentDataPath = "textFiles/appointmentData.json";
    public static final String paymentDataPath     = "textFiles/paymentData.json";
    public static final String feedbackDataPath    = "textFiles/feedbackData.json";
    public static final String reportDataPath      = "textFiles/reportData.json";
    public static final String temporaryIdFilePath = "textFiles/temporaryId.json";
}
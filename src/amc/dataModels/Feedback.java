package amc.dataModels;

/** This class represents one feedback record stored in feedbackData.json. */
public class Feedback {

    // Unique id like F001. It identifies the feedback.
    private String feedbackId;

    // The user who writes the feedback (usually a customer).
    private String fromUserId;

    // The user who receives the feedback (usually a doctor).
    private String toUserId;

    // The related appointment id like A001.
    private String appointmentId;

    // Rating value from 1 to 5.
    private int rating;

    // Short comment text about the visit.
    private String comment;

    // Date and time in ISO format, for example 2025-08-21T10:00.
    private String timestampIso;

    public Feedback() {}

    public Feedback(String feedbackId, String fromUserId, String toUserId,
                    String appointmentId, int rating, String comment, String timestampIso) {
        this.feedbackId = feedbackId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
        this.timestampIso = timestampIso;
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getTimestampIso() { return timestampIso; }
    public void setTimestampIso(String timestampIso) { this.timestampIso = timestampIso; }
}
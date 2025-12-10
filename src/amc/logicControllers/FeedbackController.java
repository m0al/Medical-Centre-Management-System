package amc.logicControllers;

import amc.dataAccess.FeedbackRepository;
import amc.dataModels.Feedback;
import amc.helperUtils.DateTimeUtil;
import amc.helperUtils.idGenerator;
import amc.helperUtils.InputValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** This controller creates and lists feedback with basic checks. */
public class FeedbackController {

    private final FeedbackRepository feedbackRepository = new FeedbackRepository();

    /** Creates one feedback entry and returns it. Returns null on bad input. */
    public Feedback create(String fromUserId, String toUserId, String appointmentId,
                           int rating, String comment) {

        if (!InputValidator.notEmpty(fromUserId)) return null;
        if (!InputValidator.notEmpty(toUserId)) return null;
        if (!InputValidator.notEmpty(appointmentId)) return null;
        if (rating < 1 || rating > 5) return null;

        String text = (comment == null) ? "" : comment.trim();
        if (!InputValidator.minLength(text, 1)) return null;

        List<Feedback> all = feedbackRepository.listAll();

        // Build the next id like F001.
        List<String> ids = new ArrayList<String>();
        for (Feedback f : all) { ids.add(f.getFeedbackId()); }
        String newId = idGenerator.nextId("F", ids);

        // Build the record.
        Feedback fb = new Feedback(
                newId,
                fromUserId,
                toUserId,
                appointmentId,
                rating,
                text,
                DateTimeUtil.nowIso()
        );

        // Save to file.
        feedbackRepository.create(fb);
        return fb;
    }

    /** Returns feedback for one doctor. */
    public List<Feedback> listForDoctor(String doctorUserId) {
        if (!InputValidator.notEmpty(doctorUserId)) return Collections.emptyList();
        return feedbackRepository.listForDoctor(doctorUserId);
    }

    /** Returns feedback written by one customer. */
    public List<Feedback> listForCustomer(String customerUserId) {
        if (!InputValidator.notEmpty(customerUserId)) return Collections.emptyList();
        return feedbackRepository.listForCustomer(customerUserId);
    }

    /** Returns all feedback. Useful for manager reports. */
    public List<Feedback> listAll() {
        return feedbackRepository.listAll();
    }
}
package amc.dataAccess;

import amc.dataModels.Feedback;
import amc.helperUtils.DataPaths;
import amc.helperUtils.JsonStore;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** This class loads feedback records and provides simple queries. */
public class FeedbackRepository {

    // This tells Gson we are reading and writing List<Feedback>.
    private static final Type listType = new TypeToken<List<Feedback>>(){}.getType();

    /** Returns all feedback from the file. */
    public List<Feedback> listAll() {
        return JsonStore.readList(DataPaths.feedbackDataPath, listType);
    }

    /** Returns feedback addressed to one doctor. */
    public List<Feedback> listForDoctor(String doctorUserId) {
        List<Feedback> all = JsonStore.readList(DataPaths.feedbackDataPath, listType);
        List<Feedback> result = new ArrayList<Feedback>();
        for (Feedback f : all) {
            if (f.getToUserId() != null && f.getToUserId().equals(doctorUserId)) {
                result.add(f);
            }
        }
        return result;
    }

    /** Returns feedback written by one customer. */
    public List<Feedback> listForCustomer(String customerUserId) {
        List<Feedback> all = JsonStore.readList(DataPaths.feedbackDataPath, listType);
        List<Feedback> result = new ArrayList<Feedback>();
        for (Feedback f : all) {
            if (f.getFromUserId() != null && f.getFromUserId().equals(customerUserId)) {
                result.add(f);
            }
        }
        return result;
    }

    /** Adds a new feedback record and saves the file. */
    public void create(Feedback newFeedback) {
        List<Feedback> all = JsonStore.readList(DataPaths.feedbackDataPath, listType);
        all.add(newFeedback);
        JsonStore.writeList(DataPaths.feedbackDataPath, all, listType);
    }
}
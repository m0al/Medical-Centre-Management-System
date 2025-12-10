package amc.dataAccess;

import amc.dataModels.Report;
import amc.helperUtils.DataPaths;
import amc.helperUtils.JsonStore;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/** This class loads report records and provides simple read access. */
public class ReportRepository {

    // This tells Gson we are reading and writing List<Report>.
    private static final Type listType = new TypeToken<List<Report>>(){}.getType();

    /** Returns all reports from the file. */
    public List<Report> listAll() {
        return JsonStore.readList(DataPaths.reportDataPath, listType);
    }

    /** Adds a new report and saves the file. */
    public void create(Report newReport) {
        List<Report> all = JsonStore.readList(DataPaths.reportDataPath, listType);
        all.add(newReport);
        JsonStore.writeList(DataPaths.reportDataPath, all, listType);
    }
}
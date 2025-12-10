package amc.logicControllers;

import amc.dataAccess.ReportRepository;
import amc.dataModels.Report;

import java.util.List;

/** This controller exposes a simple list method for reports. */
public class ReportController {

    private final ReportRepository reportRepository = new ReportRepository();

    /** Returns all saved reports for display. */
    public List<Report> listAll() {
        return reportRepository.listAll();
    }
}
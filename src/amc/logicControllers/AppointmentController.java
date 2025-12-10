package amc.logicControllers;

import amc.dataAccess.AppointmentRepository;
import amc.dataModels.Appointment;
import amc.helperUtils.InputValidator;

import java.util.Collections;
import java.util.List;

/** This controller exposes simple read methods for appointments. */
public class AppointmentController {

    private final AppointmentRepository appointmentRepository = new AppointmentRepository();

    /** Returns appointments for one doctor. Returns an empty list on bad input. */
    public List<Appointment> listByDoctor(String doctorId) {
        if (!InputValidator.notEmpty(doctorId)) return Collections.emptyList();
        return appointmentRepository.listByDoctor(doctorId);
    }

    /** Returns appointments for one customer. Returns an empty list on bad input. */
    public List<Appointment> listByCustomer(String customerId) {
        if (!InputValidator.notEmpty(customerId)) return Collections.emptyList();
        return appointmentRepository.listByCustomer(customerId);
    }

    /** Returns all appointments. Useful for the manager dashboard. */
    public List<Appointment> listAll() {
        return appointmentRepository.listAll();
    }
}
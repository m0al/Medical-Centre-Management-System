package amc.logicControllers;

import amc.dataAccess.PaymentRepository;
import amc.dataModels.Payment;
import amc.helperUtils.InputValidator;

import java.util.Collections;
import java.util.List;

/** This controller exposes simple read methods for payments. */
public class PaymentController {

    private final PaymentRepository paymentRepository = new PaymentRepository();

    /** Returns payments for one appointment. Returns an empty list on bad input. */
    public List<Payment> listByAppointment(String appointmentId) {
        if (!InputValidator.notEmpty(appointmentId)) return Collections.emptyList();
        return paymentRepository.listByAppointment(appointmentId);
    }

    /** Returns all payments. Useful for reports. */
    public List<Payment> listAll() {
        return paymentRepository.listAll();
    }
}
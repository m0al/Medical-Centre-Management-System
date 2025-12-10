package amc.dataAccess;

import amc.dataModels.Payment;
import amc.helperUtils.DataPaths;
import amc.helperUtils.JsonStore;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** This class loads payment records and provides simple read queries. */
public class PaymentRepository {

    // This tells Gson we are reading and writing List<Payment>.
    private static final Type listType = new TypeToken<List<Payment>>(){}.getType();

    /** Returns all payments from the file. */
    public List<Payment> listAll() {
        return JsonStore.readList(DataPaths.paymentDataPath, listType);
    }

    /** Returns payments linked to one appointment. */
    public List<Payment> listByAppointment(String appointmentId) {
        List<Payment> all = JsonStore.readList(DataPaths.paymentDataPath, listType);
        List<Payment> result = new ArrayList<Payment>();
        for (Payment p : all) {
            if (p.getAppointmentId() != null && p.getAppointmentId().equals(appointmentId)) {
                result.add(p);
            }
        }
        return result;
    }

    public void saveOrUpdate(Payment newPayment) {
        List<Payment> payment = JsonStore.readList(DataPaths.paymentDataPath, listType);
        List<Payment> updatedPayment = new ArrayList<>();

        boolean replaced = false;

        for (Payment existingPayment: payment) {
            if (existingPayment.getPaymentId() != null && existingPayment.getPaymentId().equals(newPayment.getPaymentId())) {
                updatedPayment.add(newPayment);
                replaced = true;
            } else {
                updatedPayment.add(existingPayment);
            }


            if (!replaced) {
                updatedPayment.add(newPayment);
            }
        }

        JsonStore.writeList(DataPaths.paymentDataPath, updatedPayment, listType);
    }
}
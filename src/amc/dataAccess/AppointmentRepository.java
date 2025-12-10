package amc.dataAccess;

import amc.dataModels.Appointment;
import amc.helperUtils.DataPaths;
import amc.helperUtils.JsonStore;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** This class loads appointment records and provides simple read queries. */
public class AppointmentRepository {

    // This tells Gson we are reading and writing List<Appointment>.
    private static final Type listType = new TypeToken<List<Appointment>>(){}.getType();

    /** Returns all appointments from the file. */
    public List<Appointment> listAll() {
        return JsonStore.readList(DataPaths.appointmentDataPath, listType);
    }

    /** Returns appointments for one doctor. */
    public List<Appointment> listByDoctor(String doctorId) {
        List<Appointment> all = JsonStore.readList(DataPaths.appointmentDataPath, listType);
        List<Appointment> result = new ArrayList<Appointment>();
        for (Appointment a : all) {
            if (a.getDoctorId() != null && a.getDoctorId().equals(doctorId)) {
                result.add(a);
            }
        }
        return result;
    }

    /** Returns appointments for one customer. */
    public List<Appointment> listByCustomer(String customerId) {
        List<Appointment> all = JsonStore.readList(DataPaths.appointmentDataPath, listType);
        List<Appointment> result = new ArrayList<Appointment>();
        for (Appointment a : all) {
            if (a.getCustomerId() != null && a.getCustomerId().equals(customerId)) {
                result.add(a);
            }
        }
        return result;
    }

    // Search an entire appointment record by its ID
    public Optional<Appointment> findByID(String appointmentID) {
        List<Appointment> app = JsonStore.readList(DataPaths.appointmentDataPath, listType);

        for (Appointment a: app) {
            if (a.getAppointmentId() != null && a.getAppointmentId().equalsIgnoreCase(appointmentID)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    public void saveOrUpdate(Appointment newAppointment) {
        List<Appointment> app = JsonStore.readList(DataPaths.appointmentDataPath, listType);
        List<Appointment> updatedAppointment = new ArrayList<>();

        boolean replaced = false;

        for (Appointment existingAppointment: app) {
            if (existingAppointment.getAppointmentId() != null && existingAppointment.getAppointmentId().equals(newAppointment.getAppointmentId())) {
                updatedAppointment.add(newAppointment); // Replace the existing one
                replaced = true;
            } else {
                updatedAppointment.add(existingAppointment); // Keep the others
            }
        }

        if (!replaced) {
            updatedAppointment.add(newAppointment);
        }

        JsonStore.writeList(DataPaths.appointmentDataPath, updatedAppointment, listType);
    }
}
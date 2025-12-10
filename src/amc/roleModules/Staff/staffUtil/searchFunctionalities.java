package amc.roleModules.Staff.staffUtil;

import amc.dataAccess.AppointmentRepository;
import amc.dataAccess.UserRepository;
import amc.helperUtils.DataPaths;
import amc.helperUtils.JsonStore;
import amc.logicControllers.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import amc.dataModels.*;
import com.google.gson.reflect.TypeToken;


public class searchFunctionalities {


    private static final Type listType = new TypeToken<List<Appointment>>(){}.getType();
    private static final Type userListType = new TypeToken<List<User>>(){}.getType();
    private static final Type paymentListType = new TypeToken<List<Payment>>(){}.getType();


    public Optional<User> customerSearch(String searchCriteria) {
        return new UserController().findById(searchCriteria);
    }

    public static List<Payment> searchPayment(String query) {

        List<Payment> all = JsonStore.readList(DataPaths.paymentDataPath, paymentListType);
        List<Payment> result = new ArrayList<>();

        String search = query.trim().toLowerCase();
        Optional<Appointment> currentAppointment;
        Optional<User> currentUser;

        for (Payment p: all) {

            AppointmentRepository repoAppointment = new AppointmentRepository();
            currentAppointment = repoAppointment.findByID(p.getAppointmentId());
            UserRepository repo = new UserRepository();
            currentUser = repo.findByID(currentAppointment.get().getCustomerId());

            if (currentUser.get().getName() != null && currentUser.get().getName().toLowerCase().contains(search)) {
                result.add(p);
            }
        }

        return result;
    }

    public static List<User> searchUser(String query) {

        List<User> all = JsonStore.readList(DataPaths.userDataPath, userListType);
        List<User> result = new ArrayList<>();

        String search = query.trim().toLowerCase();

        for (User u: all) {
            if (u.getName() != null && u.getName().toLowerCase().contains(search) ) {
                result.add(u);
            }
        }

        return result;
    }

    public static List<Appointment> searchAppointmetns(String query) {
        List<Appointment> all = JsonStore.readList(DataPaths.appointmentDataPath, listType);
        List<Appointment> result = new ArrayList<>();

        // normalize query (ignore case, trim spaces, etc.)
        String search = query.trim().toLowerCase();

        Optional<User> customerName;

        for (Appointment a : all) {
            UserRepository repo = new UserRepository();
            customerName = repo.findByID(a.getCustomerId());

            if ((customerName.get().getName() != null && customerName.get().getName().toLowerCase().contains(search)))
            {
                result.add(a);
            }

            if (a.getCustomerId().toLowerCase().contains(search)) {
                result.add(a);
            }
        }

        return result;
    }

}
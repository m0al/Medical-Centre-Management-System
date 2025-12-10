package amc.roleModules.Staff.staffUtil;


import amc.dataConstants.AppointmentStatusTypes;
import amc.dataModels.Appointment;
import amc.helperUtils.idGenerator;
import amc.dataConstants.RoleTypes;
import amc.dataAccess.*;

import amc.dataModels.User;
import amc.dataModels.Payment;

import java.util.Date;

public class createNewItem {
    public static void createNewUser(String name, String email, String phone, String address, String password) {

        String newID = idGenerator.nextId("U");
        User newUser = new User(newID, RoleTypes.customer, name, email, phone, address, password);

        UserRepository repo = new UserRepository();
        repo.saveOrUpdate(newUser);

    }

    // Creating a new appointment

    public static void createNewAppointment(String customerID, String doctorID, String time, String notes, String status, double charge, String createdBy){
        String newID = idGenerator.nextId("A");

        Appointment newAppointment = new Appointment(newID, customerID, doctorID, time, notes, status, charge, createdBy);

        AppointmentRepository repo = new AppointmentRepository();
        // createNewItem.createNewAppointment(customerId, doctorId, isoDateTime, note, AppointmentStatusTypes.confirmed, charge, "X");
        repo.saveOrUpdate(newAppointment);

    }


    public static void createNewReceipt(String appointmentID, double amount, String paymentMethod, String time) {
        String newID = idGenerator.nextId("P");

        Payment newPayment = new Payment(newID, appointmentID, amount, paymentMethod, time);

        PaymentRepository repo = new PaymentRepository();
        repo.saveOrUpdate(newPayment);

    }
}

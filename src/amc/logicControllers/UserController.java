package amc.logicControllers;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import amc.helperUtils.InputValidator;  // <— updated

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** This controller exposes simple user operations that all roles can use. */
public class UserController {

    private final UserRepository userRepository = new UserRepository();

    /** Returns all users that match the role string (case-insensitive). */
    public List<User> listByRole(String role) {
        List<User> allUsers = userRepository.findAll();
        List<User> result = new ArrayList<User>();
        for (User user : allUsers) {
            if (user.getRole() != null && user.getRole().equalsIgnoreCase(role)) {
                result.add(user);
            }
        }
        return result;
    }

    /** Returns one user by id if found. */
    public Optional<User> findById(String userId) {
        if (!InputValidator.notEmpty(userId)) return Optional.empty();  // <— updated
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (userId.equals(user.getUserId())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /** Updates the current user's own profile with basic fields only. */
    public Optional<User> updateOwnProfile(String userId, User newValues) {
        if (!InputValidator.notEmpty(userId) || newValues == null) return Optional.empty();  // <— updated

        List<User> allUsers = userRepository.findAll();

        // Ensure the email is unique if it changes.
        String newEmail = newValues.getEmail();
        if (InputValidator.notEmpty(newEmail)) {  // <— updated
            for (User other : allUsers) {
                boolean isSameUser = userId.equals(other.getUserId());
                boolean emailClash = newEmail.equalsIgnoreCase(other.getEmail());
                if (!isSameUser && emailClash) {
                    return Optional.empty(); // Email is already taken.
                }
            }
        }

        // Apply changes and save.
        User updated = null;
        for (int i = 0; i < allUsers.size(); i++) {
            User existing = allUsers.get(i);
            if (userId.equals(existing.getUserId())) {
                if (InputValidator.notEmpty(newValues.getName()))    existing.setName(newValues.getName());
                if (InputValidator.notEmpty(newValues.getEmail()) && InputValidator.isEmail(newValues.getEmail()))
                    existing.setEmail(newValues.getEmail());
                if (InputValidator.notEmpty(newValues.getPhone()))   existing.setPhone(newValues.getPhone());
                if (InputValidator.notEmpty(newValues.getAddress())) existing.setAddress(newValues.getAddress());
                if (InputValidator.notEmpty(newValues.getPassword())) existing.setPassword(newValues.getPassword());

                userRepository.saveOrUpdate(existing);
                updated = existing;
                break;
            }
        }
        return Optional.ofNullable(updated);
    }
}
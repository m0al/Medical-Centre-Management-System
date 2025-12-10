// This file provides the business logic for managing user data from the manager's perspective.
package amc.roleModules.Manager.managerUtil;

// Project-specific imports for data access, data models, constants, and helpers.
import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import amc.dataConstants.RoleTypes;
import amc.helperUtils.InputValidator;
import amc.helperUtils.idGenerator;

// Standard Java library imports for collections and functional programming.
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PURPOSE: To handle all logic related to user management, including creating, reading,
 * updating, deleting, and searching for users. It serves as a bridge between the UI
 * and the data layer.
 */
public class ManageUsersUtil {

    // STEP 1: Declare the repository that will handle database operations for users.
    private final UserRepository userRepository;

    /**
     * PURPOSE: Initializes the user management utility.
     * INPUTS: None.
     * OUTPUT: A new instance of ManageUsersUtil with a connected user repository.
     */
    public ManageUsersUtil() {
        // STEP 1: Create a new instance of the UserRepository to interact with the user data file.
        this.userRepository = new UserRepository();
    }

    /**
     * PURPOSE: To retrieve a complete list of all users currently in the system.
     * INPUTS: None.
     * OUTPUT: A list containing all User objects.
     */
    public List<User> getAllUsers() {
        // STEP 1: Call the repository's findAll method to get every user record.
        return userRepository.findAll();
    }

    /**
     * PURPOSE: To find a single user by their unique identifier.
     * INPUTS: The userId string of the user to find.
     * OUTPUT: An Optional containing the User if they are found, or an empty Optional if not.
     */
    public Optional<User> getUserById(String userId) {
        // STEP 1: Check if the provided userId is null or just empty spaces. If so, return nothing.
        if (userId == null || userId.trim().isEmpty()) {
            return Optional.empty();
        }
        // STEP 2: Search through all users to find one with a matching ID, ignoring case.
        return userRepository.findAll().stream()
                .filter(user -> user.getUserId() != null && user.getUserId().equalsIgnoreCase(userId.trim()))
                .findFirst();
    }

    /**
     * PURPOSE: To add a new user to the system after checking their data for correctness.
     * INPUTS: A User object containing the new user's information.
     * OUTPUT: An Optional containing an error message if anything goes wrong, or an empty Optional on success.
     */
    public Optional<String> addUser(User user) {
        // STEP 1: Validate the user's data. The 'true' flag indicates it's a new user.
        Optional<String> validationError = validateUser(user, true);
        if (validationError.isPresent()) {
            return validationError;
        }

        // STEP 2: Check if another user with the same email already exists to prevent duplicates.
        if (userRepository.findAll().stream().anyMatch(existingUser -> existingUser.getEmail() != null && existingUser.getEmail().equalsIgnoreCase(user.getEmail()))) {
            return Optional.of("User with email '" + user.getEmail() + "' already exists.");
        }

        // STEP 3: Generate a new, unique ID for the user.
        String newId = idGenerator.nextId("U");
        user.setUserId(newId);

        // STEP 4: Try to save the new user to the database.
        try {
            userRepository.saveOrUpdate(user);
            return Optional.empty(); // Return empty to indicate success.
        } catch (Exception exception) {
            // STEP 5: If saving fails, return an error message with the reason.
            return Optional.of("Failed to add user: " + exception.getMessage());
        }
    }

    /**
     * PURPOSE: To update the information of an existing user.
     * INPUTS: A User object with the updated details. The user's ID must be correct.
     * OUTPUT: An Optional with an error message on failure, or an empty Optional on success.
     */
    public Optional<String> updateUser(User user) {
        // STEP 1: Validate the user's data. The 'false' flag indicates it's an existing user.
        Optional<String> validationError = validateUser(user, false);
        if (validationError.isPresent()) {
            return validationError;
        }

        // STEP 2: Make sure the user we are trying to update actually exists in the database.
        Optional<User> existingUser = getUserById(user.getUserId());
        if (existingUser.isEmpty()) {
            return Optional.of("User with ID '" + user.getUserId() + "' not found for update.");
        }

        // STEP 3: Check if the new email is already being used by a *different* user.
        if (userRepository.findAll().stream()
                .filter(otherUser -> !otherUser.getUserId().equalsIgnoreCase(user.getUserId()))
                .anyMatch(otherUser -> otherUser.getEmail() != null && otherUser.getEmail().equalsIgnoreCase(user.getEmail()))) {
            return Optional.of("Another user with email '" + user.getEmail() + "' already exists.");
        }

        // STEP 4: Try to save the updated user information.
        try {
            userRepository.saveOrUpdate(user);
            return Optional.empty(); // Return empty for success.
        } catch (Exception exception) {
            // STEP 5: If saving fails, return an error message.
            return Optional.of("Failed to update user: " + exception.getMessage());
        }
    }

    /**
     * PURPOSE: To remove a user from the system permanently.
     * INPUTS: The userId string of the user to be deleted.
     * OUTPUT: An Optional with an error message on failure, or an empty Optional on success.
     */
    public Optional<String> deleteUser(String userId) {
        // STEP 1: Find the user that needs to be deleted.
        Optional<User> userToDelete = getUserById(userId);
        if (userToDelete.isEmpty()) {
            // STEP 2: If the user doesn't exist, return an error message.
            return Optional.of("User with ID '" + userId + "' not found for deletion.");
        }

        // STEP 3: Try to delete the user from the repository.
        try {
            userRepository.delete(userToDelete.get());
            return Optional.empty(); // Return empty for success.
        } catch (Exception exception) {
            // STEP 4: If deletion fails, return an error message.
            return Optional.of("Failed to delete user: " + exception.getMessage());
        }
    }

    /**
     * PURPOSE: To search for users based on a query string.
     * INPUTS: A query string to search for.
     * OUTPUT: A list of users that match the query in their name, email, or ID.
     */
    public List<User> searchUsers(String query) {
        // STEP 1: If the search query is empty, return all users without filtering.
        if (query == null || query.trim().isEmpty()) {
            return getAllUsers();
        }
        // STEP 2: Convert the query to lowercase for case-insensitive searching.
        String lowerCaseQuery = query.trim().toLowerCase();

        // STEP 3: Filter all users, keeping only those whose name, email, or ID contains the query.
        return userRepository.findAll().stream()
                .filter(user -> (user.getName() != null && user.getName().toLowerCase().contains(lowerCaseQuery)) ||
                                 (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseQuery)) ||
                                 (user.getUserId() != null && user.getUserId().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }

    /**
     * PURPOSE: A private helper to check if a user's data is valid and complete.
     * INPUTS: The User object to validate, and a boolean 'isNewUser' to adjust password rules.
     * OUTPUT: An Optional containing the first validation error found, or empty if all checks pass.
     */
    private Optional<String> validateUser(User user, boolean isNewUser) {
        // STEP 1: Ensure the user object itself isn't null.
        if (user == null) {
            return Optional.of("User object cannot be null.");
        }
        // STEP 2: Check for a non-empty name.
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return Optional.of("Name is required.");
        }
        // STEP 3: Check for a non-empty email.
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Optional.of("Email is required.");
        }
        // STEP 4: Check if the email has a valid format.
        if (!InputValidator.isEmail(user.getEmail())) {
            return Optional.of("Invalid email format.");
        }
        // STEP 5: For new users, a password is required.
        if (isNewUser && (user.getPassword() == null || user.getPassword().isEmpty())) {
            return Optional.of("Password is required for new users.");
        }
        // STEP 6: If a password exists, it must meet the minimum length requirement.
        if (user.getPassword() != null && user.getPassword().length() < 6) {
            return Optional.of("Password must be at least 6 characters long.");
        }
        // STEP 7: Check for a non-empty role.
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            return Optional.of("Role is required.");
        }
        // STEP 8: Check if the role is one of the officially recognized roles.
        if (!RoleTypes.isValidRole(user.getRole())) {
            return Optional.of("Invalid user role: " + user.getRole());
        }
        // STEP 9: If all checks pass, return an empty Optional to indicate success.
        return Optional.empty();
    }
}

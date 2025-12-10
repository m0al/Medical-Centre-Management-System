package amc.dataAccess;

import amc.dataModels.User;
import amc.helperUtils.DataPaths;
import amc.helperUtils.JsonStore;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// User data access. Loads and saves users from userData.json.
public class UserRepository {
    private static final Type userListType = new TypeToken<List<User>>(){}.getType();


    public List<User> findAll() {
        return JsonStore.readList(DataPaths.userDataPath, userListType);
    }

    public Optional<User> findByEmail(String emailAddress) {
        List<User> users = JsonStore.readList(DataPaths.userDataPath, userListType);
        for (User user : users) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(emailAddress)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }


    public Optional<User> findByName(String userName) {
        List<User> users = JsonStore.readList(DataPaths.userDataPath, userListType);
        for (User user: users) {
            if (user.getName() != null && user.getName().equals(userName)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public  Optional<User> findByID(String userID) {
        List<User> users = JsonStore.readList(DataPaths.userDataPath, userListType);
        for (User user: users) {
            if (user.getUserId() != null && user.getUserId().equalsIgnoreCase(userID)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public void saveOrUpdate(User newUser) {
        List<User> users = JsonStore.readList(DataPaths.userDataPath, userListType);
        List<User> updatedUsers = new ArrayList<>();
        boolean replaced = false;

        for (User existingUser : users) {
            if (existingUser.getUserId() != null && existingUser.getUserId().equals(newUser.getUserId())) {
                updatedUsers.add(newUser); // Replace the matching user.
                replaced = true;
            } else {
                updatedUsers.add(existingUser); // Keep others.
            }
        }
        if (!replaced) {
            updatedUsers.add(newUser); // Add as new if not found.
        }
        JsonStore.writeList(DataPaths.userDataPath, updatedUsers, userListType);
    }

    /**
     * Deletes a user from the repository.
     *
     * @param userToDelete The User object to delete.
     */
    public void delete(User userToDelete) {
        List<User> users = JsonStore.readList(DataPaths.userDataPath, userListType);
        // Remove the user with matching userId.
        users.removeIf(user -> user.getUserId() != null && user.getUserId().equals(userToDelete.getUserId()));
        JsonStore.writeList(DataPaths.userDataPath, users, userListType);
    }
}
package amc.logicControllers;

import amc.dataAccess.UserRepository;
import amc.dataModels.User;
import amc.userSession;
import java.util.Optional;

// This controller handles login.
public class AuthController {
    private final UserRepository userRepository = new UserRepository();

    // Check email and password. Set the session on success.
    public Optional<User> login(String emailAddress, String plainPassword) {
        Optional<User> foundUser = userRepository.findByEmail(emailAddress);
        if (!foundUser.isPresent()) return Optional.empty();

        User user = foundUser.get();
        if (user.getPassword() != null && user.getPassword().equals(plainPassword)) {
            userSession.setFromUser(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
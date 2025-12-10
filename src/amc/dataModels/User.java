package amc.dataModels;

/**
 * This class represents one user record stored in userData.json.
 * It is a simple data holder with getters and setters.
 */
public class User {

    // Unique id like U001. It identifies the user.
    private String userId;

    // Role string like MANAGER, STAFF, DOCTOR, or CUSTOMER.
    private String role;

    // Full name of the user.
    private String name;

    // Email address used for login.
    private String email;

    // Contact phone number.
    private String phone;

    // Postal address or simple location text.
    private String address;

    // Plain text password for coursework only.
    private String password;

    // Empty constructor for Gson and general use.
    public User() {}

    // Convenience constructor for quick seeding.
    public User(String userId, String role, String name, String email,
                String phone, String address, String password) {
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    // Getters and setters. They expose fields in a controlled way.

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
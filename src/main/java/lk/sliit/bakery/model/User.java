package lk.sliit.bakery.model;

public abstract class User {
    private final String id;
    private String name;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private String phone;
    private String address;

    protected User(String id, String name, String email, String passwordHash,
                   Role role, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    // Each subtype supplies its own dashboard wording: this is a small example of polymorphism.
    public abstract String getDashboardTitle();
}

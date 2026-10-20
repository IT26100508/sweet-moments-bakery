package lk.sliit.bakery.model;

public class Admin extends User {
    public Admin(String id, String name, String email, String passwordHash,
                 String phone, String address) {
        super(id, name, email, passwordHash, Role.ADMIN, phone, address);
    }

    @Override
    public String getDashboardTitle() {
        return "Bakery Administration";
    }
}

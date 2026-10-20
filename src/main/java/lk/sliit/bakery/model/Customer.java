package lk.sliit.bakery.model;

public class Customer extends User {
    public Customer(String id, String name, String email, String passwordHash,
                    String phone, String address) {
        super(id, name, email, passwordHash, Role.CUSTOMER, phone, address);
    }

    @Override
    public String getDashboardTitle() {
        return "Your Bakery Dashboard";
    }
}

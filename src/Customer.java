public class Customer {
    String name;
    String address;
    String email;
    String phoneNumber;

    Customer(String name, String address, String email, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {

        return "Name: " + name +
                "\nAddress: " + address +
                "\nEmail: " + email +
                "\nPhone Number: " + phoneNumber;

    }
}
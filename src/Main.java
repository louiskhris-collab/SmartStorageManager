import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseManager.getConnection();

        StorageManager manager = new StorageManager();
        Scanner input = new Scanner(System.in);

        int option = 0;

        // The program continues until the user selects option 9.
        while (option != 8) {

            System.out.println("\n==========================");
            System.out.println("   STORAGE MANAGER MENU");
            System.out.println("==========================");
            System.out.println("1. Rent Unit");
            System.out.println("2. View Unit");
            System.out.println("3. Show All Units");
            System.out.println("4. Filter Units");
            System.out.println("5. Storage Dashboard");
            System.out.println("6. Update Rental Rate");
            System.out.println("7. Customer Management");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            option = input.nextInt();

            switch (option) {

                case 1:
                    System.out.print("Enter a unit to rent: ");
                    int chooseUnitNumber = input.nextInt();

                    // Clears the newline left by nextInt().
                    input.nextLine();

                    if (!manager.isUnitAvailableDatabase(chooseUnitNumber)) {
                        System.out.println("Unit is occupied or unavailable.");
                        break;
                    }

                    System.out.print("Enter customer name: ");
                    String name = input.nextLine();

                    if (!manager.isValidName(name)) {
                        System.out.println("Invalid name.");
                        break;
                    }

                    System.out.print("Enter address: ");
                    String address = input.nextLine().trim();

                    if (!manager.isValidAddress(address)) {
                        System.out.println("Invalid address.");
                        break;
                    }

                    System.out.print("Enter email: ");
                    String email = input.nextLine();

                    if (!manager.isValidEmail(email)) {
                        System.out.println("Invalid email address.");
                        break;
                    }

                    System.out.print("Enter phone: ");
                    String phone = input.nextLine();

                    if (!manager.isValidPhoneNumber(phone)) {
                        System.out.println("Invalid phone number.");
                        break;
                    }

                    int customerId = manager.addCustomerDatabase(name, address, email, phone);

                    if (customerId != -1) {
                        manager.assignCustomerToUnitDatabase(customerId, chooseUnitNumber);
                    }

                    break;

                case 2:
                    System.out.println("\nView Storage Unit");

                    System.out.print("Enter unit number: ");
                    int searchNumber = input.nextInt();

                    manager.viewUnitByNumberDatabase(searchNumber);
                    break;

                case 3:
                    System.out.println("\nAll Storage Units");
                    manager.showAllUnitsDatabase();
                    break;

                case 4:
                    int filter = 0;

                    while (filter != 4) {

                        System.out.println("\n==========================");
                        System.out.println("     SEARCH BY FILTER");
                        System.out.println("==========================");
                        System.out.println("1. Vacant Units");
                        System.out.println("2. Occupied Units");
                        System.out.println("3. Units By Size");
                        System.out.println("4. Back");
                        System.out.print("Enter choice: ");

                        filter = input.nextInt();

                        switch (filter) {

                            case 1:
                                manager.showVacantUnitsDatabase();
                                break;

                            case 2:
                                manager.showOccupiedUnitsDatabase();
                                break;

                            case 3:
                                // Clears the newline left by nextInt().
                                input.nextLine();

                                System.out.print("Enter unit size: ");
                                String sizeInput = input.nextLine();

                                manager.showUnitsBySizeDatabase(sizeInput);
                                break;

                            case 4:
                                System.out.println("Returning to the main menu.");
                                break;

                            default:
                                System.out.println("Invalid filter option.");
                        }
                    }

                    break;

                case 5:
                    manager.showStorageDashboardDatabase();
                    break;

                case 6:
                    // Clears the newline left by nextInt().
                    input.nextLine();

                    System.out.print("Enter unit size: ");
                    String sizeToUpdate = input.nextLine();

                    System.out.print("Enter new rental rate: ");
                    double newRate = input.nextDouble();

                    manager.updateRentalRateDatabase(sizeToUpdate, newRate);
                    break;

                case 7:
                    customerManagementMenu(manager, input);
                    break;

                case 8:
                    System.out.println("Exiting program...");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 through 9.");
            }
        }

        input.close();
    }


     // Displays the main Customer Management menu.
     // This method handles only the menu and user input.

    //1 First menu
    public static void customerManagementMenu(StorageManager manager, Scanner input) {

        int choice = 0;

        while (choice != 2) {

            System.out.println("\n==========================");
            System.out.println("   CUSTOMER MANAGEMENT");
            System.out.println("==========================");
            System.out.println("1. Search Customer");
            System.out.println("2. Back");
            System.out.print("Choose an option: ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {

                case 1:
                    customerSearchMenu(manager, input);
                    break;

                case 2:
                    System.out.println("Returning to the main menu.");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    //2 Second menu to look up customer via info
    public static void customerSearchMenu(StorageManager manager, Scanner input) {

        int choice = 0;

        while (choice != 5) {

            System.out.println("\n==========================");
            System.out.println("      SEARCH CUSTOMER");
            System.out.println("==========================");
            System.out.println("1. Search by Phone Number");
            System.out.println("2. Search by Customer Number");
            System.out.println("3. Search by Name");
            System.out.println("4. Search by Unit Number");
            System.out.println("5. Back");
            System.out.print("Choose an option: ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {

                case 1: {
                    System.out.print("Enter phone number: ");
                    String searchedPhone = input.nextLine();

                    ArrayList<Integer> customerIds = manager.searchCustomerByPhoneDatabase(searchedPhone);

                   // int customerId = manager.searchCustomerByPhoneDatabase(phone);
                    if (!customerIds.isEmpty()) {
                        System.out.println("Choose a customer (1-" + customerIds.size() + "): ");
                    }

                    int selectedCustomer = input.nextInt();
                    input.nextLine();
                    if (selectedCustomer >= 1 && selectedCustomer <= customerIds.size()) {
                        int customerId = customerIds.get(selectedCustomer - 1);
                        customerActionMenu(manager, input, customerId);
                    } else {
                        System.out.println("Invalid customer selection");
                    }
                }
                    break;

                case 2: {
                    System.out.println("Enter Customer Number: ");
                    String customerNumber = input.nextLine();

                    int customerId = manager.searchCustomerByCustomerNumberDatabase(customerNumber);

                    if (customerId != -1) {
                        customerActionMenu(manager, input, customerId);
                    }
                }
                    break;

                case 3: {
                    System.out.println("Enter Customer Name: ");
                    String searchedName = input.nextLine().trim();

                    ArrayList<Integer> customerIds = manager.searchCustomerByCustomerNameDatabase(searchedName);

                    if (!customerIds.isEmpty()) {
                        System.out.println("Choose a customer (1-" + customerIds.size() + "): ");
                    }

                    int selectedCustomer = input.nextInt();
                    input.nextLine();
                    if (selectedCustomer >= 1 && selectedCustomer <= customerIds.size()) {
                        int customerId = customerIds.get(selectedCustomer - 1);
                        customerActionMenu(manager, input, customerId);
                    } else {
                        System.out.println("Invalid customer selection");
                    }
                }
                    break;

                case 4: {
                    System.out.println("Enter customer unit number. ");
                    int searchUnitNumber = input.nextInt();

                    int customerId = manager.searchCustomerByUnitNumberDatabase(searchUnitNumber);

                    if (customerId != -1) {
                        customerActionMenu(manager, input, customerId);
                    }
                }
                    break;

                case 5:
                    System.out.println("Returning to Customer Management.");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    //3 Third Once customer is found. Dictate action
    public static void customerActionMenu(StorageManager manager, Scanner input, int customerId) {

        int choice;

        do {
            System.out.println("\n=== Customer Actions ===");
            System.out.println("1. Update Customer");
            System.out.println("2. Move Out Customer");
            System.out.println("3. Transfer Customer");
            System.out.println("4. View Rental History");
            System.out.println("5. Return");

            System.out.print("Choose an option: ");
            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1:
                    updateCustomerMenu(manager, input, customerId);
                    break;

                case 2:
                    System.out.println("Move out customer " + customerId);
                    moveOutCustomerMenu(manager, input, customerId);
                    break;

                case 3:
                    transferCustomerMenu(manager, input, customerId);
                    break;

                case 4:
                    manager.showRentalHistoryDatabase(customerId);
                    break;

                case 5:
                    System.out.println("Returning...");
                    break;

                    default:
                    System.out.println("Invalid option.");
            }

        } while (choice != 5);
    }

    //Update customer info menu
    public static void updateCustomerMenu(StorageManager manager, Scanner input, int customerId) {

        int choice = 0;

        while (choice != 5) {

            System.out.println("\n==========================");
            System.out.println("    UPDATE CUSTOMER");
            System.out.println("===========================");
            System.out.println("1. Update Name");
            System.out.println("2. Update Address");
            System.out.println("3. Update Email");
            System.out.println("4. Update Phone Number");
            System.out.println("5. Return");
            System.out.print("Choose an option: ");

            choice = input.nextInt();
            input.nextLine();

            switch (choice) {

                case 1:
                    System.out.println("Update customer name: ");
                    String newName = input.nextLine();

                    manager.updateCustomerName(customerId, newName);
                    break;

                case 2:
                    System.out.println("Update Address selected.");
                    String newAddress = input.nextLine();

                    manager.updateCustomerAddress(customerId, newAddress);
                    break;

                case 3:
                    System.out.println("Update Email selected.");
                    String newEmail = input.nextLine();

                    manager.updateCustomerEmail(customerId, newEmail);
                    break;

                case 4:
                    System.out.println("Update Phone selected.");
                    String newPhone = input.nextLine();

                    manager.updateCustomerPhone(customerId, newPhone);
                    break;

                case 5:
                    System.out.println("Returning...");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    //Move out customer menu
    public static void moveOutCustomerMenu(StorageManager manager, Scanner input, int customerId) {

        System.out.println("\n==========================");
        System.out.println("    MOVE OUT CUSTOMER");
        System.out.println("==========================");
        System.out.println("This will remove the customer's assigned unit.");
        System.out.print("Are you sure you want to continue? (Y/N): ");

        String confirmation = input.nextLine().trim();

        if (confirmation.equalsIgnoreCase("Y")) {
            manager.moveOutCustomerDatabase(customerId);
        } else if (confirmation.equalsIgnoreCase("N")) {
            System.out.println("Move out canceled.");
        } else {
            System.out.println("Invalid selection. Move out canceled.");
        }
    }

    //Transfer customer menu
    public static void transferCustomerMenu(StorageManager manager, Scanner input, int customerId) {
        System.out.println("+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+");
        System.out.println("        TRANSFER CUSTOMER           ");
        System.out.println("+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_");

        System.out.println("Enter new unit number: ");
        int newUnitNumber = input.nextInt();
        input.nextLine(); //Buffer input before next input

        if (!manager.isUnitAvailableDatabase(newUnitNumber)) {
            System.out.println("The unit you have chosen does not exist");
            return;
        }

        System.out.println("Confirmation transfer to new unit " + newUnitNumber + "? (Y/N): ");

        String cornfirmation = input.nextLine().trim();
        if (cornfirmation.equalsIgnoreCase("Y")) {
            manager.transferCustomerDatabase(customerId,newUnitNumber);
        } else {
            System.out.println("Transfer canceled. ");
        }


    }

}
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseManager.getConnection();

        StorageManager manager = new StorageManager();
        Scanner input = new Scanner(System.in);

        int option = 0;

        // The program continues until the user selects option 9.
        while (option != 9) {

            System.out.println("\n==========================");
            System.out.println("   STORAGE MANAGER MENU");
            System.out.println("==========================");
            System.out.println("1. Rent Unit");
            System.out.println("2. View Unit");
            System.out.println("3. Show All Units");
            System.out.println("4. Filter Units");
            System.out.println("5. Move Out Unit");
            System.out.println("6. Financial Report");
            System.out.println("7. Update Rental Rate");
            System.out.println("8. Customer Management");
            System.out.println("9. Exit");
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
                    String address = input.nextLine();

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
                    System.out.print("Enter the unit number to vacate: ");

                    int vacateUnit = input.nextInt();

                    manager.moveOutUnitDatabase(vacateUnit);
                    break;

                case 6:
                    manager.showFinancialReportDatabase();
                    break;

                case 7:
                    // Clears the newline left by nextInt().
                    input.nextLine();

                    System.out.print("Enter unit size: ");
                    String sizeToUpdate = input.nextLine();

                    System.out.print("Enter new rental rate: ");
                    double newRate = input.nextDouble();

                    manager.updateRentalRateDatabase(sizeToUpdate, newRate);
                    break;

                case 8:
                    customerManagementMenu(manager, input);
                    break;

                case 9:
                    System.out.println("Exiting program...");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 through 9.");
            }
        }

        input.close();
    }

    /*
     * Displays the main Customer Management menu.
     *
     * This method handles only the menu and user input.
     * It does not contain database code.
     */
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

    /*
     * Allows the employee to choose how to search
     * for a customer.
     */
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

                case 1:
                    System.out.print("Enter phone number: ");
                    String phone = input.nextLine();

                    manager.searchCustomerByPhoneDatabase(phone);
                    break;

                case 2:
                    System.out.println("Customer-number search will be added next.");
                    break;

                case 3:
                    System.out.println("Customer-name search will be added later.");
                    break;

                case 4:
                    System.out.println("Unit-number search will be added later.");
                    break;

                case 5:
                    System.out.println("Returning to Customer Management.");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
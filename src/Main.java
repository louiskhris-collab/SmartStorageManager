//import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DatabaseManager.getConnection();

        StorageManager manager = new StorageManager();

        Scanner input = new Scanner(System.in);

        int option = 0;

        while(option != 10){
            System.out.println("=====================");
            System.out.println("==Storage Manager Menu==");
            System.out.println("1. Register new customer to unit");
            System.out.println("2. View Storage Unit");
            System.out.println("3. Show All Units");
            System.out.println("4. Search Unit By Filter");
            System.out.println("5. Vacate Unit");
            System.out.println("6. Show Financial Report");
            System.out.println("7. Update Unit Rate ");
            System.out.println("8. Update Tenant name");
            System.out.println("9. Save and Exit ");

            System.out.println("Enter choice: ");
            System.out.println("=====================");

            option = input.nextInt();// when pressing a number it also adds a \n. So it will skip next input with a " "

            switch (option){
                case 1:

                    System.out.println("Enter a unit to rent. ");
                    int chooseUnitNumber = input.nextInt();

                    input.nextLine(); //break

                    if (!manager.isUnitAvailableDatabase(chooseUnitNumber)) {
                        System.out.println("Unit is occupied: ");
                        break;
                    }

                    System.out.print("Enter customer name: ");
                    String name = input.nextLine();
                         if (!manager.isValidName(name)){
                            System.out.println("Invalid name");
                            break;
                    }

                    System.out.print("Enter address: ");
                    String address = input.nextLine();
                        if (!manager.isValidAddress(address)) {
                            System.out.println("Invalid Address");
                            break;
                        }

                    System.out.print("Enter email: ");
                    String email = input.nextLine();
                        if (!manager.isValidEmail(email)){
                            System.out.printf("Invalid email address");
                            break;
                        }

                    System.out.print("Enter phone: ");
                    String phone = input.nextLine();
                        if (!manager.isValidPhoneNumber(phone)){
                            System.out.println("invalid phone number");
                            break;
                        }

                   int customerId = manager.addCustomerDatabase(name, address, email, phone);

                    if (customerId != -1) {
                        manager.assignCustomerToUnitDatabase(customerId, chooseUnitNumber);
                    }

                    break;


                case 2:
                    System.out.println("View Storage Unit Selected");

                    System.out.print("Enter unit number: ");
                    int searchNumber = input.nextInt();

                    manager.viewUnitByNumberDatabase(searchNumber);

                    //manager.viewUnitByNumber(searchNumber);

                    break;

                case 3:
                    System.out.println("Show All Units Selected");
                    manager.showAllUnitsDatabase();
                    break;

                case 4:
                    System.out.println("Search Unit By Filter Selected");

                    int filter = 0;

                    while (filter != 4){
                        System.out.println("=====================");
                        System.out.println("==Search By Filter==");
                        System.out.println("1. Vacant Units ");
                        System.out.println("2. Occupied Units");
                        System.out.println("3. Unit By Size");
                        System.out.println("4. Exit");

                        System.out.println("Enter choice: ");
                        System.out.println("=====================");

                        filter = input.nextInt();

                        switch (filter){
                            case 1:
                                manager.showVacantUnitsDatabase();
                                 break;

                            case 2:
                                manager.showOccupiedUnitsDatabase();
                                break;

                            case 3:
                                System.out.println("Unit By Size");
                                input.nextLine(); //to buffer out previous scanner input for ' ' taken

                                System.out.println("Type in size");
                                String sizeInput = input.nextLine();
                                manager.showUnitsBySizeDatabase(sizeInput);
                                break;

                            case 4:
                                System.out.println("Back to Home Menu");
                                break;
                        }
                    }
                    break;

                case 5:
                    System.out.println("Enter a unit number to vacate");
                    int vacateUnit = input.nextInt();

                    manager.moveOutUnitDatabase(vacateUnit);
                    break;

                case 6:
                    manager.showFinancialReportDatabase();
                    System.out.println("Exiting to financial reports");
                    break;

                case 7:
                    System.out.print("Enter unit size: ");
                    input.nextLine(); // buffer input
                    String sizeToUpdate = input.nextLine();

                    System.out.print("Enter new rental rate: ");
                    double newRate = input.nextDouble();

                    manager.updateRentalRateDatabase(sizeToUpdate, newRate);
                    break;

                case 8:
                    input.nextLine(); // clears newline left by nextInt()

                    System.out.print("Enter customer number: ");
                    String customerNumber = input.nextLine();

                    System.out.print("Enter new customer name: ");
                    String newName = input.nextLine();

                    manager.updateCustomerNameDatabase(customerNumber, newName);
                    break;


                case 9:
                    System.out.println("Exiting Program...");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }
        }

        input.close();
            }
        }

        //StorageUnit unit1 = new StorageUnit(1001,"10x15", false, 175);
       // System.out.println(unit1.toString());


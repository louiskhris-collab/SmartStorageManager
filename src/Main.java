//import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


        //ArrayList<StorageUnit> units = new ArrayList<>();
        //ArrayList<Customer> credentials = new ArrayList<>();

        StorageManager manager = new StorageManager();
        manager.loadSavedUnits();

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
            System.out.println("6. Delete Storage Unit");
            System.out.println("7. Show Financial Report");
            System.out.println("8. Update Unit Rate ");
            System.out.println("9. Update Tenant name");
            System.out.println("10. Save and Exit ");

            System.out.println("Enter choice: ");
            System.out.println("=====================");

            option = input.nextInt();// when pressing a number it also adds a \n. So it will skip next input with a " "

            switch (option){
                case 1:
                    System.out.println("Register new customer Selected");

                    //Note to self how Java Buffer works in Scanner method
                    input.nextLine(); // eats leftover Enter or the \n from option = input.nextInt();

                    //--------------------------------------------------------------------------------------------------------------------------
                    //Enter Name
                    System.out.print("Enter Your Name: ");
                    String name = input.nextLine();

                    //----------------------------------------------------------------------------------------------------------------------------
                    //Enter Address Info
                    System.out.println("Enter Your Address: ");
                    String Addr = input.nextLine();
                    //----------------------------------------------------------------------------------------------------------------------------
                    //Enter Email Info
                    System.out.println("Enter your email address: ");
                    String EmAddr = input.nextLine();
                    //----------------------------------------------------------------------------------------------------------------------------
                    //Enter Phone Info
                    System.out.println("Enter your phone number: ");
                    String PhNum = input.nextLine();

                    Customer customer = new Customer(name, Addr, EmAddr, PhNum); //store inputs in customer object

                    //----------------------------------------------------------------------------------------------------------------------------


                    System.out.println("Choose Unit Size:");
                    System.out.println("1. 5x3  - $40");
                    System.out.println("2. 5x5  - $60");
                    System.out.println("3. 5x10 - $90");
                    System.out.println("4. 10x10 - $120");
                    System.out.println("5. 10x15 - $175");
                    System.out.println("6. 10x20 - $220");

                    System.out.println("Choose what Unit size you want: ");
                    int choseSize = input.nextInt();

                    String size = manager.getSizeByChoice(choseSize);
                    double rentRate = manager.getRentRateByChoice(choseSize);


                    if (size.equals("") || rentRate == 0){
                        System.out.println("Invalid Entry! ");
                        break;
                    }


                    //----------------------------------------------------------------------------------------------------------------------------
                    //Enter unit info
                    System.out.print("Enter Unit Number: ");
                    int number = input.nextInt();

                    if (manager.unitExists(number)) {
                        System.out.println("Unit Number Already Taken.");
                    } else {
                        StorageUnit unit = new StorageUnit(number, size, true,rentRate, name);
                        manager.addCustomerAndUnit(customer, unit);

                        System.out.println("Storage unit added successfully!");
                    }

                    break;


                case 2:
                    System.out.println("View Storage Unit Selected");

                    System.out.print("Enter unit number: ");
                    int searchNumber = input.nextInt();

                    manager.viewUnitByNumber(searchNumber);

                    break;

                case 3:
                    System.out.println("Show All Units Selected");

                    manager.showAllUnits();

                    //for each storage unit object inside the units array list -> temp variable as u
                    //to print out all storage unit objects in units array list.
                    /*for (StorageUnit u: units){
                        System.out.println(u);
                        System.out.println("--------------");
                    }*/

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
                                manager.showVacantUnits();
                                 break;

                            case 2:
                                manager.showOccupiedUnits();
                                break;

                            case 3:
                                System.out.println("Unit By Size");
                                input.nextLine(); //to buffer out previous scanner input for ' ' taken

                                System.out.println("Type in size");
                                String sizeInput = input.nextLine();
                                manager.showUnitsBySize(sizeInput);
                                break;

                            case 4:
                                System.out.println("Back to Home Menu");
                                break;
                        }
                    }
                    break;

                case 5:
                    System.out.println("Enter unit to vacate");
                    int vacateUnit = input.nextInt();

                    manager.moveOutUnit(vacateUnit);
                    break;

                case 6:
                    System.out.println("To remove enter unit number. ");
                    int removeUnit = input.nextInt();

                    manager.deleteUnitByNumber(removeUnit);
                    break;

                case 7:
                    manager.showFinancialReport();
                    System.out.println("Exiting to financial reports");
                    break;

                case 8:
                    System.out.println("Update Rental Rate: ");
                    System.out.println("Choose a current Occupied Unit");

                    int updateUnit = input.nextInt();

                    System.out.println("Update monthly rate ");
                    double updateRate = input.nextDouble();

                    manager.updateRentalRate(updateUnit, updateRate);
                    break;

                case 9:
                    System.out.println("Update Tenant name: ");
                    System.out.println("Choose a current Occupied Unit");

                    int tenantUnitSearch = input.nextInt();
                    input.nextLine(); // takes in leftover Enter

                    System.out.println("Update tenant Name:");
                    String updateTenantName = input.nextLine();

                    manager.updateTenantName(tenantUnitSearch, updateTenantName);
                    break;


                case 10:
                    manager.saveUnitInfo();
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


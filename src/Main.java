import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        ArrayList<StorageUnit> units = new ArrayList<>();
        ArrayList<Customer> credentials = new ArrayList<>();

        Scanner input = new Scanner(System.in);

        int option = 0;

        while(option != 5){
            System.out.println("=====================");
            System.out.println("==Storage Manager Menu==");
            System.out.println("1. Register new customer to unit");
            System.out.println("2. View Storage Unit");
            System.out.println("3. Show All Units");
            System.out.println("4. Search Unit By Filter");
            System.out.println("5. Exit");

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
                    String id = input.nextLine();

                    Customer personel = new Customer(id, "901 Madison Avenue", "abc@gmail.com", "987-654-3210");
                    credentials.add(personel);

                    //----------------------------------------------------------------------------------------------------------------------------
                    //Enter unit number
                    System.out.print("Enter Unit Number: ");
                    int number = input.nextInt();

                    StorageUnit unit = new StorageUnit(number, "10x10", true, 120, id);
                    units.add(unit);

                    System.out.println("Storage unit added successfully!");
                    break;

                case 2:
                    System.out.println("View Storage Unit Selected");

                    boolean found = false;

                    int searchNumber = input.nextInt();
                    for (StorageUnit u: units){
                        if (u.unitNumber == searchNumber){
                            System.out.println(u);
                            found= true;
                        }
                    }

                    if (!found){
                        System.out.println("Unit Can't be found");
                    }
                    break;

                case 3:
                    System.out.println("Show All Units Selected");

                    //for each storage unit object inside the units array list -> temp variable as u
                    //to print out all storage unit objects in units array list.
                    for (StorageUnit u: units){
                        System.out.println(u);
                        System.out.println("--------------");
                    }

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
                            System.out.println("Vacant Units");

                            boolean foundfilter = false;

                            for (StorageUnit u: units){
                                if (!u.occupied){
                                    System.out.println(u);
                                    foundfilter = true;
                                }

                            }
                            if (!foundfilter){
                                System.out.println("All units occupied");
                            }
                            break;

                            case 2:
                                System.out.println("Occupied Units");

                                boolean OccFilter = false;

                                for (StorageUnit u: units){
                                    if (u.occupied){
                                        System.out.println(u);
                                        OccFilter = true;
                                    }
                                }
                                if (!OccFilter){
                                    System.out.println("No occupied Units");
                                }
                                break;

                            case 3:
                                System.out.println("Unit By Size");
                                input.nextLine(); //to buffer out previous scanner input for ' ' taken

                                boolean sizeMatch = false;

                                System.out.println("Type in size");
                                String SearchSize = input.nextLine();
                                for (StorageUnit u: units){
                                    if (u.size.equalsIgnoreCase(SearchSize)){
                                        System.out.println(u);
                                        sizeMatch = true;
                                    }
                                }
                                if (!sizeMatch){
                                    System.out.println("No units found with that size!");
                                }
                                break;

                            case 4:
                                System.out.println("Back to Home Menu");
                                break;
                        }
                    }
                    break;




                case 5:
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


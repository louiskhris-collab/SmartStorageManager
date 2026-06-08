import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StorageManager {

    ArrayList<StorageUnit> units = new ArrayList<>();
    ArrayList<Customer> customers = new ArrayList<>();

    public void addCustomerAndUnit(Customer customer, StorageUnit unit) {
        customers.add(customer);
        units.add(unit);
    }

    public boolean unitExists(int unitNumber) {
        for (StorageUnit unit : units) {
            if (unit.unitNumber == unitNumber) {
                return true;
            }
        }
        return false;
    }

    public void showAllUnits() {
        for (StorageUnit unit : units) {
            System.out.println(unit);
        }
    }

    public void viewUnitByNumber(int unitNumber) {
        boolean found = false;

        for (StorageUnit unit : units) {
            if (unit.unitNumber == unitNumber) {
                System.out.println(unit);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Unit can't be found.");
        }
    }

    public void showVacantUnits (){
        boolean foundVac = false;

        for (StorageUnit unit: units) {
            if (!unit.occupied) {
                System.out.println(unit);
                foundVac = true;
            }

        }
        if (!foundVac) {
            System.out.println("All units occupied");
        }
    }

    public void showOccupiedUnits (){
        boolean foundOcc = false;

        for (StorageUnit unit: units) {
            if (unit.occupied) {
                System.out.println(unit);
                foundOcc = true;
            }

        }
        if (!foundOcc) {
            System.out.println("No occupied units found ");
        }
    }

    public void showUnitsBySize(String size) {
        //System.out.println("Unit By Size");
        //input.nextLine(); //to buffer out previous scanner input for ' ' taken
        boolean sizeMatch = false;

        //System.out.println("Type in size");
        //String SearchSize = input.nextLine();
        for (StorageUnit unit: units){
            if (unit.size.equalsIgnoreCase(size)){
                System.out.println(unit);
                sizeMatch = true;
            }
        }
        if (!sizeMatch){
            System.out.println("No units found with that size!");
        }
    }

    public double getRentRateByChoice(int choice){
        switch(choice){
                case 1:
                    return 40;
                case 2:
                    return 60;
                case 3:
                    return 90;
                case 4:
                    return 120;
                case 5:
                    return 175;
                case 6:
                    return 220;
                default:
                    return 0;

        }
    }

    public String getSizeByChoice(int choice){
        switch (choice){
            case 1:
                return "5x3";
            case 2:
                return "5x5";
            case 3:
                return "5x10";
            case 4:
                return "10x10";
            case 5:
                return "10x15";
            case 6:
                return "10x20";
            default:
                return "";
        }
    }

    public void saveUnitInfo(){
        try {
            PrintWriter saver = new PrintWriter("units.txt");

            for (StorageUnit unit: units){
                saver.println(
                        unit.unitNumber + "," +
                                unit.size + "," +
                                unit.occupied + "," +
                                unit.monthlyRate + "," +
                                unit.tenant
                );
            }
            saver.close();
            System.out.println("Units saved. ");
        }
        catch (FileNotFoundException e) {
            System.out.println("Error saving file.");
        }

    }

}

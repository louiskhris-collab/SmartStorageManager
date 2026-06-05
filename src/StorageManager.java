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

}

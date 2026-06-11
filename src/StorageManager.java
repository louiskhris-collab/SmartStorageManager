import com.sun.source.tree.WhileLoopTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

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

    public void loadSavedUnits(){
        try {
            File file = new File("units.txt");
            Scanner fileReader = new Scanner(file);

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();

                String[] parts = line.split(",");

                int unitNumber = Integer.parseInt(parts[0]);
                String size = parts[1];
                boolean occupied = Boolean.parseBoolean(parts[2]);
                double monthlyRate = Double.parseDouble(parts[3]);
                String tenant = parts[4];

                StorageUnit unit = new StorageUnit(unitNumber,size,occupied,monthlyRate,tenant);

                units.add(unit);
            }
            fileReader.close();
            System.out.println("Loaded saved units successfully. ");

        }
        catch (FileNotFoundException e) {
            System.out.println("Saved units not found.");
        }
    }

    public void deleteUnitByNumber(int unitNumber) {
        boolean removed = false;

        for (int i = 0 ; i < units.size(); i++){
            if (units.get(i).unitNumber == unitNumber) {
                units.remove(i);
                removed = true;
                System.out.println("Unit deleted Successfully.");
                break;
            }
        }

        if (!removed) {
            System.out.println("Unit not found. ");
        }
    }

    public void moveOutUnit(int unitNumber) {
        //boolean found = false;

        for (StorageUnit unit: units){
            if (unit.unitNumber == unitNumber) {
                //found = true;
                unit.occupied = false;
                unit.tenant = "Vacant";
                System.out.println("Unit vacated");
                return;
            }
        }

            System.out.println("Unit not found. ");

    }

    public void showFinancialReport() {
        int totalUnits = units.size(); //size is array class method
        int occupiedUnits = 0;
        int vacantUnits = 0;
        double monthlyRevenue = 0;

        double revenueFor5x3 = 0;
        double revenueFor5x5 = 0;
        double revenueFor5x10 = 0;
        double revenueFor10x10 = 0;
        double revenueFor10x15 = 0;
        double revenueFor10x20 = 0;

        for (StorageUnit unit : units) {
            if (unit.occupied) {
                occupiedUnits++;
                monthlyRevenue += unit.monthlyRate;

                switch (unit.size) { //switch case doesn't need equalsIgnoreCase method because size String predermined in getSizeByChoice
                    case "5x3":
                        revenueFor5x3 += unit.monthlyRate;
                        break;

                    case "5x5":
                        revenueFor5x5 += unit.monthlyRate;
                        break;

                    case "5x10":
                        revenueFor5x10 += unit.monthlyRate;
                        break;

                    case "10x10":
                        revenueFor10x10 += unit.monthlyRate;
                        break;

                    case "10x15":
                        revenueFor10x15 += unit.monthlyRate;
                        break;

                    case "10x20":
                        revenueFor10x20 += unit.monthlyRate;
                        break;

                }
               /* if (unit.size.equalsIgnoreCase("5x3")) {
                    revenueFor5x3 += unit.monthlyRate;
                } else if (unit.size.equalsIgnoreCase("5x5")) {
                    revenueFor5x5 += unit.monthlyRate;
                } else if (unit.size.equalsIgnoreCase("5x10")) {
                    revenueFor5x10 += unit.monthlyRate;
                } else if (unit.size.equalsIgnoreCase("10x10")) {
                    revenueFor10x10 += unit.monthlyRate;
                } else if (unit.size.equalsIgnoreCase("10x15")) {
                    revenueFor10x15 += unit.monthlyRate;
                } else if (unit.size.equalsIgnoreCase("10x20")) {
                    revenueFor10x20 += unit.monthlyRate;
                }*/
            } else {
                vacantUnits++;
            }
        }

        double occupancyRate = 0;

        if (totalUnits > 0) {
            occupancyRate = ((double) occupiedUnits / totalUnits) * 100;
        }

        System.out.println("===== Storage Report =====");
        System.out.println("Total Units: " + totalUnits);
        System.out.println("Occupied Units: " + occupiedUnits);
        System.out.println("Vacant Units: " + vacantUnits);
        System.out.println("Occupancy Rate: " + occupancyRate + "%");
        System.out.println("Monthly Revenue: $" + monthlyRevenue);
        System.out.println("\n");
        System.out.println("Revenue Breakdown By Unit Size:");
        System.out.println("Revenue for 5x3: $" + revenueFor5x3);
        System.out.println("Revenue for 5x5: $" + revenueFor5x5);
        System.out.println("Revenue for 5x10: $" + revenueFor5x10);
        System.out.println("Revenue for 10x10: $" + revenueFor10x10);
        System.out.println("Revenue for 10x15: $" + revenueFor10x15);
        System.out.println("Revenue for 10x20: $" + revenueFor10x20);

    }

}

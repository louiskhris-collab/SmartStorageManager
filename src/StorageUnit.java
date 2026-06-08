import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class StorageUnit {
    int unitNumber;
    String size;
    boolean occupied;
    double monthlyRate;
    String tenant;

    // constructor for storage unit along parameters
    StorageUnit(int unitNumber, String size, boolean occupied, double monthlyRate, String tenant){
        this.unitNumber = unitNumber;
        this.size = size;
        this.occupied = occupied;
        this.monthlyRate = monthlyRate;
        this.tenant = tenant;
    }



    @Override //ensures that toString method is called from String object.
    //if mistake where made. For example if lowercase tostring; @override will make sure its correct.
    public String toString() {
        return "Unit Number: " + unitNumber +
                "\nSize: " + size +
                "\nOccupied: " + occupied +
                "\nMonthly Rate: $" + monthlyRate +
                "\nTenant: " + tenant;
    }

}

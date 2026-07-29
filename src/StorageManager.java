import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StorageManager {

    //Generates customer number for customers.
    public String generateCustomerNumber() {
        Random rand = new Random();
        String fullCustomerNumber;
        do {
            int num = rand.nextInt(900000) + 100000;
            fullCustomerNumber = "CUS-" + num;

        }
        while (checkCustomerNumberExist(fullCustomerNumber));

        return fullCustomerNumber;
    }

    //Check if generated number already exist
    public boolean checkCustomerNumberExist(String customerNumber) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
                        SELECT customer_number
                        FROM customers
                        WHERE customer_number = ?;
                """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setString(1, customerNumber);

            ResultSet results = pS.executeQuery();

            if (results.next()){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Customer number already exist.");
            System.out.println(e.getMessage());
        }
       return false;
    }

    //Creating new customer and adding to customer table in database.
    public int addCustomerDatabase(String name, String address, String email, String phone) {
        Connection connection = DatabaseManager.getConnection();

        String customerNumber = generateCustomerNumber();

        String sql = """
            INSERT INTO customers (customer_number, name, address, email, phone)
            VALUES (?, ?, ?, ?, ?);
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pS.setString(1, customerNumber);
            pS.setString(2, name);
            pS.setString(3, address);
            pS.setString(4, email);
            pS.setString(5, phone);

            pS.executeUpdate();

            ResultSet generatedKeys = pS.getGeneratedKeys();

            if (generatedKeys.next()) {
                int customerId = generatedKeys.getInt(1);

                System.out.println("Customer added successfully.");
                System.out.println("Customer Number: " + customerNumber);
                System.out.println("Customer ID: " + customerId);

                return customerId;
            }

        } catch (SQLException e) {
            System.out.println("Unable to add customer.");
            System.out.println(e.getMessage());
        }

        return -1;
    }

    public void assignCustomerToUnitDatabase(int customerId, int unitNumber) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
                UPDATE storage_units
                            SET occupied = TRUE,
                                customer_id = ?
                            WHERE unit_number = ?;
                """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setInt(1, customerId);
            pS.setInt(2, unitNumber);

            int updatedRows = pS.executeUpdate();

            if (updatedRows > 0) {
                System.out.println("Customer assigned to unit successfully.");
            } else {
                System.out.println("Unit not found");
            }

        } catch (SQLException e) {
            System.out.println("Unable to connect customer to unit.");
            System.out.println(e.getMessage());
        }
    }

    //Check if unit is avalaible
    public boolean isUnitAvailableDatabase(int unitNumber) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
            SELECT occupied
            FROM storage_units
            WHERE unit_number = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);
            pS.setInt(1, unitNumber);

            ResultSet results = pS.executeQuery();

            if (results.next()) {
                boolean occupied = results.getBoolean("occupied");
                return !occupied;
            }

        } catch (SQLException e) {
            System.out.println("Unable to check unit availability.");
            System.out.println(e.getMessage());
        }

        return false;
    }

    public boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    public boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{3}-\\d{3}-\\d{4}");
    }

    public boolean isValidAddress(String address) {
        return address.length() >= 5;
    }


    // prints all units saved in StorageUnit DATABASE VERSION
    public void showAllUnitsDatabase() {

        Connection connection = DatabaseManager.getConnection();

        String sql = """
            SELECT
                su.unit_number,
                ut.size,
                ut.base_rate,
                su.occupied,
                su.customer_id
            FROM storage_units su
            JOIN unit_types ut
            ON su.type_id = ut.type_id;
            """;

        try {

            Statement statement = connection.createStatement();

            ResultSet results = statement.executeQuery(sql);

            while (results.next()) {
                int unitNumber = results.getInt("unit_number");
                String size = results.getString("size");
                double rate = results.getDouble("base_rate");
                boolean occupied = results.getBoolean("occupied");

                System.out.println(unitNumber + " | " + size + " | $" + rate + " | Occupied: " + occupied);
            }

        } catch (SQLException e) {
            System.out.println("Unable to create statement.");
            System.out.println(e.getMessage());
        }
    }

    // Select a unit number and print unit info from StorageUnit DATABASE VERSION
    public void viewUnitByNumberDatabase(int unitNumber) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
        SELECT
            su.unit_number,
            ut.size,
            ut.base_rate,
            su.occupied,
            su.customer_id
        FROM storage_units su
        JOIN unit_types ut
            ON su.type_id = ut.type_id
        WHERE su.unit_number = ?;
        """;

        try {

            PreparedStatement pS = connection.prepareStatement(sql);
            pS.setInt(1, unitNumber);

            ResultSet results = pS.executeQuery();


            if (results.next()) {

                int foundunitNumber = results.getInt("unit_number");
                String size = results.getString("size");
                double rate = results.getDouble("base_rate");
                boolean occupied = results.getBoolean("occupied");

                System.out.println(foundunitNumber + " | " + size + " | $" + rate + " | Occupied: " + occupied);

            } else{
                System.out.println("Unit not found !");
            }

        } catch (SQLException e) {
            System.out.println("Unable to create statement.");
            System.out.println(e.getMessage());
        }


    }

    // Displays units that are vacant
    public void showVacantUnitsDatabase (){
       Connection connection = DatabaseManager.getConnection();

       String sql = """
                          SELECT
                               su.unit_number,
                               ut.size,
                               ut.base_rate
                           FROM storage_units su
                           JOIN unit_types ut
                               ON su.type_id = ut.type_id
                           WHERE su.occupied = FALSE;
               """;

       try {
           PreparedStatement pS = connection.prepareStatement(sql);
           ResultSet results = pS.executeQuery();

           boolean found = false;

                   while(results.next()) {
                       found = true;

                       int unitNumber = results.getInt("unit_number");
                       String size = results.getString("size");
                       double rate = results.getDouble("base_rate");

                       System.out.println(unitNumber + " | " + size + " | $" + rate);
                   }

                   if (!found) {
                       System.out.println("No vacant units available.");
                   }
       } catch (SQLException e) {
           System.out.println("Unable to display vacant units.");
           System.out.println(e.getMessage());
       }
    }

    // Displays units that are occupied
    public void showOccupiedUnitsDatabase (){
        Connection connection = DatabaseManager.getConnection();

        String sql = """
                SELECT
                    su.unit_number,
                    ut.size,
                    ut.base_rate,
                    su.customer_id,
                    c.customer_number,
                    c.name
                FROM storage_units su
                JOIN unit_types ut
                    ON su.type_id = ut.type_id
                JOIN customers c
                    ON su.customer_id = c.customer_id
                WHERE su.occupied = TRUE;
               """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);
            ResultSet results = pS.executeQuery();

            boolean found = false;

            while(results.next()) {
                found = true;

                int unitNumber = results.getInt("unit_number");
                String size = results.getString("size");
                double rate = results.getDouble("base_rate");
                int customerId = results.getInt("customer_id");
                String customerNumber = results.getString("customer_number");
                String customerName = results.getString("name");


                System.out.println(unitNumber + " | " + size + " | $" + rate + " | Customer ID: " + customerId + " | " + customerNumber + " | " + customerName);
            }

            if (!found) {
                System.out.println("No occupied units available.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to display occupied units.");
            System.out.println(e.getMessage());
        }
    }

    // Search up unit size and it will display units that are matched in size
    public void showUnitsBySizeDatabase(String size) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
                          SELECT
                               su.unit_number,
                               ut.size,
                               ut.base_rate
                           FROM storage_units su
                           JOIN unit_types ut
                               ON su.type_id = ut.type_id
                           WHERE ut.size = ?;
               """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);
            pS.setString(1, size);
            ResultSet results = pS.executeQuery();

            boolean found = false;

            while(results.next()) {
                found = true;

                int unitNumber = results.getInt("unit_number");
                String unitSize = results.getString("size");
                double rate = results.getDouble("base_rate");

                System.out.println(unitNumber + " | " + unitSize + " | $" + rate);
            }

            if (!found) {
                System.out.println("No units found with size + " + size);
            }
        } catch (SQLException e) {
            System.out.println("Unable to display units of this size " + size);
            System.out.println(e.getMessage());
        }
    }

    // Rent will be chosen
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

    // Unit size chosen
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

    // Seach Customer by phone
    public int searchCustomerByPhoneDatabase(String phone) {
        //Connection connection = DatabaseManager.getConnection();

        String sql =
                "SELECT c.customer_id, c.customer_number, c.name, c.address, " +
                        "c.email, c.phone, su.unit_number, " +
                        "ut.size, ut.base_rate " +
                          "FROM customers c " +
                        "LEFT JOIN storage_units su " +
                        "ON c.customer_id = su.customer_id " +
                        "LEFT JOIN unit_types ut " +
                        "ON su.type_id = ut.type_id " +
                        "WHERE c.phone = ?";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, phone);

            ResultSet results = statement.executeQuery();

            if (results.next()) {

                System.out.println("\n==============================");
                System.out.println("       CUSTOMER FOUND");
                System.out.println("==============================");
                System.out.println("Customer Number: " + results.getString("customer_number"));
                System.out.println("Name: " + results.getString("name"));
                System.out.println("Address: " + results.getString("address"));
                System.out.println("Email: " + results.getString("email"));
                System.out.println("Phone: " + results.getString("phone"));

                int customerId = results.getInt("customer_id");
                int unitNumber = results.getInt("unit_number");

                if (results.wasNull()) {
                    System.out.println("Current Unit: None");
                } else {
                    System.out.println("Current Unit: " + unitNumber);
                    System.out.println("Unit Size: " + results.getString("size"));
                    System.out.printf("Monthly Rate: $%.2f%n", results.getDouble("base_rate"));
                }
                System.out.println("==============================");
                return customerId;

            } else {
                System.out.println("No customer found with that phone number.");
                return -1;
            }

        } catch (SQLException exception) {
            System.out.println("Unable to search for customer.");
            exception.printStackTrace();
            return -1;
        }
    }

    //Search Customer by customer number
    public int searchCustomerByCustomerNumberDatabase(String customerNumber) {
        //Connection connection = DatabaseManager.getConnection();

        String sql =
                "SELECT c.customer_id, c.customer_number, c.name, c.address, " +
                        "c.email, c.phone, su.unit_number, " +
                        "ut.size, ut.base_rate " +
                        "FROM customers c " +
                        "LEFT JOIN storage_units su " +
                        "ON c.customer_id = su.customer_id " +
                        "LEFT JOIN unit_types ut " +
                        "ON su.type_id = ut.type_id " +
                        "WHERE c.customer_number = ?";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customerNumber);

            ResultSet results = statement.executeQuery();

            if (results.next()) {

                System.out.println("\n==============================");
                System.out.println("       CUSTOMER FOUND");
                System.out.println("==============================");
                System.out.println("Customer Number: " + results.getString("customer_number"));
                System.out.println("Name: " + results.getString("name"));
                System.out.println("Address: " + results.getString("address"));
                System.out.println("Email: " + results.getString("email"));
                System.out.println("Phone: " + results.getString("phone"));

                int customerId = results.getInt("customer_id");
                int unitNumber = results.getInt("unit_number");

                if (results.wasNull()) {
                    System.out.println("Current Unit: None");
                } else {
                    System.out.println("Current Unit: " + unitNumber);
                    System.out.println("Unit Size: " + results.getString("size"));
                    System.out.printf("Monthly Rate: $%.2f%n", results.getDouble("base_rate"));
                }
                System.out.println("==============================");
                return customerId;

            } else {
                System.out.println("No customer found with that customer number.");
                return -1;
            }

        } catch (SQLException exception) {
            System.out.println("Unable to search for customer.");
            exception.printStackTrace();
            return -1;
        }
    }

    public int searchCustomerByCustomerNameDatabase(String searchedName) {
        //Connection connection = DatabaseManager.getConnection();

        String sql =
                "SELECT c.customer_id, c.customer_number, c.name, c.address, " +
                        "c.email, c.phone, su.unit_number, " +
                        "ut.size, ut.base_rate " +
                        "FROM customers c " +
                        "LEFT JOIN storage_units su " +
                        "ON c.customer_id = su.customer_id " +
                        "LEFT JOIN unit_types ut " +
                        "ON su.type_id = ut.type_id " +
                        "WHERE c.name = ?";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, searchedName);

            ResultSet results = statement.executeQuery();

            if (results.next()) {

                System.out.println("\n==============================");
                System.out.println("       CUSTOMER FOUND");
                System.out.println("==============================");
                System.out.println("Customer Number: " + results.getString("customer_number"));
                System.out.println("Name: " + results.getString("name"));
                System.out.println("Address: " + results.getString("address"));
                System.out.println("Email: " + results.getString("email"));
                System.out.println("Phone: " + results.getString("phone"));

                int customerId = results.getInt("customer_id");
                int unitNumber = results.getInt("unit_number");

                if (results.wasNull()) {
                    System.out.println("Current Unit: None");
                } else {
                    System.out.println("Current Unit: " + unitNumber);
                    System.out.println("Unit Size: " + results.getString("size"));
                    System.out.printf("Monthly Rate: $%.2f%n", results.getDouble("base_rate"));
                }
                System.out.println("==============================");
                return customerId;

            } else {
                System.out.println("No customer found with that name. ");
                return -1;
            }

        } catch (SQLException exception) {
            System.out.println("Unable to search for customer.");
            exception.printStackTrace();
            return -1;
        }
    }

    public int searchCustomerByUnitNumberDatabase(int searchedUnitNumber) {
        //Connection connection = DatabaseManager.getConnection();

        String sql =
                "SELECT c.customer_id, c.customer_number, c.name, c.address, " +
                        "c.email, c.phone, su.unit_number, " +
                        "ut.size, ut.base_rate " +
                        "FROM customers c " +
                        "LEFT JOIN storage_units su " +
                        "ON c.customer_id = su.customer_id " +
                        "LEFT JOIN unit_types ut " +
                        "ON su.type_id = ut.type_id " +
                        "WHERE su.unit_number = ?";

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, searchedUnitNumber);

            ResultSet results = statement.executeQuery();

            if (results.next()) {

                System.out.println("\n==============================");
                System.out.println("       CUSTOMER FOUND");
                System.out.println("==============================");
                System.out.println("Customer Number: " + results.getString("customer_number"));
                System.out.println("Name: " + results.getString("name"));
                System.out.println("Address: " + results.getString("address"));
                System.out.println("Email: " + results.getString("email"));
                System.out.println("Phone: " + results.getString("phone"));

                int customerId = results.getInt("customer_id");
                int unitNumber = results.getInt("unit_number");

                if (results.wasNull()) {
                    System.out.println("Current Unit: None");
                } else {
                    System.out.println("Current Unit: " + unitNumber);
                    System.out.println("Unit Size: " + results.getString("size"));
                    System.out.printf("Monthly Rate: $%.2f%n", results.getDouble("base_rate"));
                }
                System.out.println("==============================");
                return customerId;

            } else {
                System.out.println("No customer found with that unit number.");
                return -1;
            }

        } catch (SQLException exception) {
            System.out.println("Unable to search for customer.");
            exception.printStackTrace();
            return -1;
        }
    }

    // Searches StorageUnit and updates unit monthly rate
    public void updateRentalRateDatabase(String size, double newRate) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
            UPDATE unit_types
            SET base_rate = ?
            WHERE size = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setDouble(1, newRate);
            pS.setString(2, size);

            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Rental rate for " + size + " updated to $" + newRate
                );
            } else {
                System.out.println("Unit size not found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to update rental rate.");
            System.out.println(e.getMessage());
        }
    }

    // Update customer Name
    public void updateCustomerName(int customerId, String name) {
        Connection connection = DatabaseManager.getConnection();

        if (!isValidName(name)) {
            System.out.println("Invalid name format: ");
            //System.out.println("Reference for correct format: XXX-XXX-XXXX ");
            return;
        }

        String sql = """
            UPDATE customers
            SET name = ?
            WHERE customer_id = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setString(1, name);
            pS.setInt(2, customerId);

            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
            System.out.println("Customer name updated successfully.");
            } else {
                System.out.println("Customer number not found.");
            }

        } catch (SQLException exception) {
            System.out.println("Unable to update customer name.");
            exception.printStackTrace();
        }
    }

    // Update Customer Address
    public void updateCustomerAddress(int customerId, String address) {
        Connection connection = DatabaseManager.getConnection();

        if (!isValidAddress(address)) {
            System.out.println("Invalid address format: ");
            //System.out.println("Reference for correct format: XXX-XXX-XXXX ");
            return;
        }

        String sql = """
            UPDATE customers
            SET address = ?
            WHERE customer_id = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setString(1, address);
            pS.setInt(2, customerId);

            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Customer address updated successfully.");
            } else {
                System.out.println("Customer number not found.");
            }

        } catch (SQLException exception) {
            System.out.println("Unable to update customer Address.");
            exception.printStackTrace();
        }
    }

    //Update customer email
    public void updateCustomerEmail(int customerId, String email) {
        Connection connection = DatabaseManager.getConnection();

        if (!isValidEmail(email)) {
            System.out.println("Invalid email format: ");
            //System.out.println("Reference for correct format: XXX-XXX-XXXX ");
            return;
        }

        String sql = """
            UPDATE customers
            SET email = ?
            WHERE customer_id = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setString(1, email);
            pS.setInt(2, customerId);

            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Customer email updated successfully.");
            } else {
                System.out.println("Customer number not found.");
            }

        } catch (SQLException exception) {
            System.out.println("Unable to update customer email.");
            exception.printStackTrace();
        }
    }

    //Update customer phone #
    public void updateCustomerPhone(int customerId, String phone) {
        Connection connection = DatabaseManager.getConnection();

        if (!isValidPhoneNumber(phone)) {
            System.out.println("Invalid Phone Number format: ");
            System.out.println("Reference for correct format: XXX-XXX-XXXX ");
            return;
        }

        String sql = """
            UPDATE customers
            SET phone = ?
            WHERE customer_id = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);


            pS.setString(1, phone);
            pS.setInt(2, customerId);

            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Customer phone number updated successfully.");
            } else {
                System.out.println("Customer number not found.");
            }

        } catch (SQLException exception) {
            System.out.println("Unable to update customer phone number.");
            exception.printStackTrace();
        }
    }

    // Moves out tenant and marks storage as vacant
    //UNUSED
    public void moveOutUnitDatabase(int unitNumber) {
        Connection connection = DatabaseManager.getConnection();

        String sql = """
                UPDATE storage_units
                SET occupied = FALSE,
                    customer_id = NULL
                WHERE unit_number = ?;
                """;

        try{
            PreparedStatement pS = connection.prepareStatement(sql);
            pS.setInt(1, unitNumber);
            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Unit " + unitNumber + " has been vacated.");
            } else {
                System.out.println("Unit not found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to vacate unit.");
            System.out.println(e.getMessage());
        }
    }

    //Moves out customer via customerId
    public void moveOutCustomerDatabase(int customerId) {

        Connection connection = DatabaseManager.getConnection();

        String sql = """
            UPDATE storage_units
            SET occupied = FALSE,
                customer_id = NULL
            WHERE customer_id = ?;
            """;

        try {
            PreparedStatement pS = connection.prepareStatement(sql);

            pS.setInt(1, customerId);

            int rowsUpdated = pS.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Customer moved out successfully.");
                System.out.println("The storage unit is now vacant.");
            } else {
                System.out.println("This customer does not have an assigned unit.");
            }

        } catch (SQLException exception) {
            System.out.println("Unable to move out customer.");
            exception.printStackTrace();
        }
    }

    // Prints fincancial report
    public void showFinancialReportDatabase() {
        Connection connection = DatabaseManager.getConnection();

        String totalsSql = """
            SELECT
                COUNT(*) AS total_units,
                SUM(CASE WHEN su.occupied = TRUE THEN 1 ELSE 0 END) AS occupied_units,
                SUM(CASE WHEN su.occupied = FALSE THEN 1 ELSE 0 END) AS vacant_units,
                COALESCE(
                    SUM(CASE
                        WHEN su.occupied = TRUE THEN ut.base_rate
                        ELSE 0
                    END),
                    0
                ) AS monthly_revenue
            FROM storage_units su
            JOIN unit_types ut
                ON su.type_id = ut.type_id;
            """;

        String breakdownSql = """
            SELECT
                ut.size,
                COUNT(*) AS occupied_units,
                SUM(ut.base_rate) AS revenue_by_size
            FROM storage_units su
            JOIN unit_types ut
                ON su.type_id = ut.type_id
            WHERE su.occupied = TRUE
            GROUP BY ut.size
            ORDER BY ut.size;
            """;

        try {
            PreparedStatement totalStatement =
                    connection.prepareStatement(totalsSql);

            ResultSet resultsOverallReport =
                    totalStatement.executeQuery();

            if (resultsOverallReport.next()) {
                int totalUnits = resultsOverallReport.getInt("total_units");

                int occupiedUnits = resultsOverallReport.getInt("occupied_units");

                int vacantUnits = resultsOverallReport.getInt("vacant_units");

                double monthlyRevenue = resultsOverallReport.getDouble("monthly_revenue");

                double occupancyRate = 0;

                if (totalUnits > 0) {
                    occupancyRate =
                            ((double) occupiedUnits / totalUnits) * 100;
                }

                System.out.println("===== Storage Financial Report =====");
                System.out.println("Total Units: " + totalUnits);
                System.out.println("Occupied Units: " + occupiedUnits);
                System.out.println("Vacant Units: " + vacantUnits);

                System.out.printf(
                        "Occupancy Rate: %.2f%%%n", occupancyRate
                );

                System.out.printf(
                        "Monthly Revenue: $%.2f%n", monthlyRevenue
                );
            }

            PreparedStatement breakdownStatement = connection.prepareStatement(breakdownSql);

            ResultSet resultsRevenueBySize = breakdownStatement.executeQuery();

            boolean found = false;

            System.out.println("\n===== Revenue by Size =====");

            while (resultsRevenueBySize.next()) {
                found = true;

                String size = resultsRevenueBySize.getString("size");

                int occupied = resultsRevenueBySize.getInt("occupied_units");

                double revenue = resultsRevenueBySize.getDouble("revenue_by_size");

                System.out.printf(
                        "%s | Occupied: %d | Revenue: $%.2f%n",
                        size,
                        occupied,
                        revenue
                );
            }

            if (!found) {
                System.out.println("No occupied units generating revenue.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to load financial report.");
            System.out.println(e.getMessage());
        }
    }

}

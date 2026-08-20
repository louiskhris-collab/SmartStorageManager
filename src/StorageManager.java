import java.sql.*;
import java.util.ArrayList;
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

        String assignUnitSql = """
                UPDATE storage_units
                            SET occupied = TRUE,
                                customer_id = ?
                            WHERE unit_number = ?;
                """;

        String retrieveUnitRateSql = """
                SELECT ut.base_rate
                FROM storage_units su
                JOIN unit_types ut
                    ON su.type_id = ut.type_id
                WHERE su.unit_number = ?;
                """;

        try {
            connection.setAutoCommit(false);

            PreparedStatement assignUnit = connection.prepareStatement(assignUnitSql);

            assignUnit.setInt(1, customerId);
            assignUnit.setInt(2, unitNumber);

            int updatedRows = assignUnit.executeUpdate();

            if (updatedRows == 0) {
                System.out.println("Unit not found. ");
                connection.rollback();
            }

            PreparedStatement findUnitRentalRateSql = connection.prepareStatement(retrieveUnitRateSql);

            findUnitRentalRateSql.setInt(1, unitNumber);

            ResultSet rateResults = findUnitRentalRateSql.executeQuery();

            if (rateResults.next()) {
                double monthlyrate = rateResults.getDouble("base_rate");

                recordRentalHistory(connection, customerId, unitNumber,"MOVE_IN", monthlyrate, "Customer assigned to unit");
            } else {
                System.out.println("Unable to find unit rental rate.");
                connection.rollback();
                return;
            }

            connection.commit();
            System.out.println("Customer assigned to unit successfully. ");

        } catch (SQLException e) {

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println("Unable to connect customer to unit.");
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        return  address.matches("[A-Za-z0-9 .,'#/-]+");
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

    // Seach Customer by phone from customer search menu
    public ArrayList<Integer> searchCustomerByPhoneDatabase(String phone) {

        ArrayList<Integer> customerIds = new ArrayList<>();

        String sql ="""
                SELECT c.customer_id, c.customer_number, c.name, c.phone, su.unit_number
                        FROM customers c
                        LEFT JOIN storage_units su
                        ON c.customer_id = su.customer_id
                        WHERE c.phone = ?
                        """;

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, phone);
            ResultSet results = statement.executeQuery();

            int optionNumber = 1;

            while (results.next()) {
                int customerId = results.getInt("customer_id");
                customerIds.add(customerId);

                System.out.println("\n" + optionNumber + ".");
                System.out.println("Customer Number: " + results.getString("customer_number"));
                System.out.println("Name: " + results.getString("name"));
                System.out.println("Phone: " + results.getString("phone"));
                int unitNumber = results.getInt("unit_number");

                if (results.wasNull()) {
                    System.out.println("Current Unit: None");
                } else {
                    System.out.println("Current Unit: " + unitNumber);
                }
                optionNumber++;
            }

            if (customerIds.isEmpty()) {
                System.out.println("No customers found with that phone number.");
            }
        } catch (SQLException exception) {
            System.out.println("Unable to search for customer.");
            exception.printStackTrace();
        }
        return customerIds;
    }

    //Search Customer by customer number
    public int searchCustomerByCustomerNumberDatabase(String customerNumber) {

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
                return displayCustomerSearchResults(results);
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

    // switched to array list in case searched customer returns more than 1 result
    public ArrayList<Integer> searchCustomerByCustomerNameDatabase(String searchedName) {

        ArrayList<Integer> customerIds = new ArrayList<>();

        String sql = """
                SELECT c.customer_id, c.customer_number, c.name,
                        c.phone, su.unit_number
                        FROM customers c 
                        LEFT JOIN storage_units su
                        ON c.customer_id = su.customer_id
                        WHERE c.name = ?
                        ORDER BY c.customer_number;
                        """;

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, searchedName.trim());
            ResultSet results = statement.executeQuery();

            int optionNumber = 1;

            while (results.next()) {
                int customerId = results.getInt("customer_id");
                customerIds.add(customerId);

                System.out.println("\n" + optionNumber + ".");
                System.out.println("Customer Number: " + results.getString("customer_number"));
                System.out.println("Name: " + results.getString("name"));
                System.out.println("Phone: " + results.getString("phone"));
                int unitNumber = results.getInt("unit_number");

                if (results.wasNull()) {
                    System.out.println("Current Unit: None");
                } else {
                    System.out.println("Current Unit: " + unitNumber);
                }
                optionNumber++;
            }

            if (customerIds.isEmpty()) {
                System.out.println("No customers found with searched name.");
            }
        } catch (SQLException exception) {
            System.out.println("Unable to search for customer.");
            exception.printStackTrace();
        }
        return customerIds;
    }

    //Search customer by unit Number from customer Search menu
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

    //After Customer is searched by Phone/Name/Email/Unit #
    private int displayCustomerSearchResults(ResultSet results)
        throws SQLException {
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

            pS.setString(1, name.trim());
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

    //Moves out customer via customerId
    public void moveOutCustomerDatabase(int customerId) {

        Connection connection = DatabaseManager.getConnection();

        String findCurrentUnitSql = """
               SELECT su.unit_number, ut.base_rate
                FROM storage_units su
                JOIN unit_types ut
                    ON su.type_id = ut.type_id
                WHERE su.customer_id = ?
                    AND su.occupied = TRUE;
            """;

        String vacateUnitSql = """
                UPDATE storage_units
                SET occupied = FALSE,
                    customer_id = NULL
                WHERE unit_number = ?
                    AND customer_id = ?;
                """;

        try {
            connection.setAutoCommit(false);

            PreparedStatement findCurrentUnit = connection.prepareStatement(findCurrentUnitSql);
            findCurrentUnit.setInt(1, customerId);

            ResultSet results = findCurrentUnit.executeQuery();

            if (!results.next()) {
                System.out.println("Unit does not exist or is already vacant.");
                connection.rollback();
                return;
            }

            int unitNumber = results.getInt("unit_number");
            double monthlyRate = results.getDouble("base_rate");

            PreparedStatement vacateUnit = connection.prepareStatement(vacateUnitSql);
            vacateUnit.setInt(1, unitNumber);
            vacateUnit.setInt(2, customerId);

            int rowsUpdated = vacateUnit.executeUpdate();
            if (rowsUpdated == 0) {
                connection.rollback();
                System.out.println("Unable to vacate unit.");
                return;
            }

            recordRentalHistory(connection, customerId, unitNumber, "MOVE_OUT", monthlyRate, "Customer moved out of unit.");

            connection.commit();
            System.out.println("Unit " + unitNumber + " has been vacated.");

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println("Unable to vacate unit.");
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Prints fincancial report
    public void showStorageDashboardDatabase() {
        Connection connection = DatabaseManager.getConnection();

        String totalsSql = """
    SELECT
        COUNT(*) AS total_units,

        SUM(CASE WHEN su.occupied = TRUE THEN 1 ELSE 0 END) AS occupied_units,

        SUM(CASE WHEN su.occupied = FALSE THEN 1 ELSE 0 END) AS vacant_units,

        COALESCE(
            SUM(CASE WHEN su.occupied = TRUE THEN ut.base_rate ELSE 0 END),
            0
        ) AS monthly_revenue,

        COALESCE(
            SUM(ut.base_rate),
            0
        ) AS potential_monthly_revenue,

        COALESCE(
            AVG(CASE WHEN su.occupied = TRUE THEN ut.base_rate ELSE NULL END),
            0
        ) AS average_occupied_rate

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
            PreparedStatement totalStatement = connection.prepareStatement(totalsSql);

            ResultSet resultsOverallReport = totalStatement.executeQuery();

            if (resultsOverallReport.next()) {

                int totalUnits = resultsOverallReport.getInt("total_units");

                int occupiedUnits = resultsOverallReport.getInt("occupied_units");

                int vacantUnits = resultsOverallReport.getInt("vacant_units");

                double monthlyRevenue = resultsOverallReport.getDouble("monthly_revenue");

                double potentialMonthlyRevenue = resultsOverallReport.getDouble("potential_monthly_revenue");

                double averageOccupiedRate = resultsOverallReport.getDouble("average_occupied_rate");

                double lostRevenue = potentialMonthlyRevenue - monthlyRevenue;

                double occupancyRate = 0;

                if (totalUnits > 0) {
                    occupancyRate = ((double) occupiedUnits / totalUnits) * 100;
                }
                System.out.println("========================================");
                System.out.println("===== SMART STORAGE DASHBOARD =====");
                System.out.println("========================================");

                System.out.println("Total Units: " + totalUnits);
                System.out.println("Occupied Units: " + occupiedUnits);
                System.out.println("Vacant Units: " + vacantUnits);

                System.out.printf("Occupancy Rate: %.2f%%%n", occupancyRate);

                System.out.println("----------------------------------------------------");

                System.out.printf("Monthly Revenue: $%.2f%n", monthlyRevenue);
                System.out.printf("Potential Monthly Revenue: $%.2f%n", potentialMonthlyRevenue);
                System.out.printf("Lost Revenue: $%.2f%n", lostRevenue);
                System.out.printf("Average Occupied Rate: $%.2f%n", averageOccupiedRate);

                System.out.println("========================================================");
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

                System.out.printf("%s | Occupied: %d | Revenue: $%.2f%n", size, occupied, revenue);
            }

            if (!found) {
                System.out.println("No occupied units generating revenue.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to load financial report.");
            System.out.println(e.getMessage());
        }
    }

    public void transferCustomerDatabase(int customerId, int newUnitNumber) {
        Connection connection = DatabaseManager.getConnection();

        String findOldUnitSql = """
                SELECT su.unit_number, ut.base_rate
                FROM storage_units su
                JOIN unit_types ut
                    ON su.type_id = ut.type_id
                WHERE su.customer_id = ?;
                """;

        String vacateOldUnitSql = """
                UPDATE storage_units
                SET occupied = FALSE,
                    customer_id = NULL
                WHERE customer_id = ?
                    AND unit_number = ?;
                """;

        String assignNewUnitSql = """
                UPDATE storage_units
                SET occupied = TRUE,
                    customer_id = ?
                WHERE unit_number = ?
                    AND occupied = FALSE;
                """;

        String findNewUnitSql = """
                SELECT ut.base_rate
                FROM storage_units su
                JOIN unit_types ut
                    ON su.type_id = ut.type_id
                WHERE su.unit_number = ?
                    AND su.occupied = FALSE;
                """;

        try {
            connection.setAutoCommit(false);

            PreparedStatement findOldUnitStatement = connection.prepareStatement(findOldUnitSql);
            findOldUnitStatement.setInt(1, customerId);

            ResultSet findOldUnitResults = findOldUnitStatement.executeQuery();
            if (!findOldUnitResults.next()) {
                System.out.println("Customer does not own any units currently.");
                connection.rollback();
                return;
            }

            int oldUnitNumber = findOldUnitResults.getInt("unit_number");
            double oldMonthlyRate = findOldUnitResults.getDouble("base_rate");

            PreparedStatement vacateStatement = connection.prepareStatement(vacateOldUnitSql);
            vacateStatement.setInt(1, customerId);
            vacateStatement.setInt(2, oldUnitNumber);

            PreparedStatement findNewUnitStatement = connection.prepareStatement(findNewUnitSql);
            findNewUnitStatement.setInt(1, newUnitNumber);

            ResultSet findNewUnitResults = findNewUnitStatement.executeQuery();
            if (!findNewUnitResults.next()) {
                System.out.println("The new unit does not exist or isn't available.");
                connection.rollback();
                return;
            }

            double newMonthlyRate = findNewUnitResults.getDouble("base_rate");

            int oldUnitRows = vacateStatement.executeUpdate();
            if (oldUnitRows == 0) {
                System.out.println("Customer does not own any units currently.");
                connection.rollback();
                return;
            }

            PreparedStatement assignUnitStatement = connection.prepareStatement(assignNewUnitSql);

            assignUnitStatement.setInt(1, customerId);
            assignUnitStatement.setInt(2,newUnitNumber);

            int newUnitRows = assignUnitStatement.executeUpdate();

            if (newUnitRows == 0 ) {
                System.out.println("The new unit does not exist or no longer available.");
                connection.rollback();
                return;
            }

            // Transfer out of old unit
            recordRentalHistory(connection, customerId, oldUnitNumber, "TRANSFER_OUT", oldMonthlyRate, "Transferred to unit " + newUnitNumber);

            //Transfered to new unit
            recordRentalHistory(connection, customerId, newUnitNumber, "TRANSFER_IN", newMonthlyRate, "Transferred from Unit " + oldUnitNumber);


            connection.commit();
            //Success
            System.out.println("Customer transferred to unit " + newUnitNumber + " successfully.");

        } catch (SQLException exception) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println("Unable to transfer customer.");
            exception.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void recordRentalHistory(Connection connection, int customerId, int unitNumber, String actionType, double monthlyRate, String notes)
        throws SQLException {
            String sql = """
                    INSERT INTO rental_history(customer_id, unit_number, action_type, monthly_rate, notes)
                    VALUES (?,?,?,?,?)
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, customerId);
            statement.setInt(2, unitNumber);
            statement.setString(3, actionType);
            statement.setDouble(4, monthlyRate);
            statement.setString(5, notes);

            statement.executeUpdate();
        };

    public void showRentalHistoryDatabase(int customerId){
        Connection connection = DatabaseManager.getConnection();

        String sql = """
                SELECT unit_number, action_type, action_date, monthly_rate, notes
                FROM rental_history
                WHERE customer_id = ?
                ORDER BY action_date ASC;
                """;

        try {
            PreparedStatement DisplayHistory = connection.prepareStatement(sql);

            DisplayHistory.setInt(1, customerId);

            ResultSet results = DisplayHistory.executeQuery();

            boolean found = false;

            System.out.println("\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");
            System.out.println("         RENTAL HISTORY         ");
            System.out.println("+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+");

            while (results.next()) {
                found = true;

                int unitNumber = results.getInt("unit_number");
                String actionType = results.getString("action_type");
                String actionDate = results.getString("action_date");
                double monthlyRate = results.getDouble("monthly_rate");
                String notes = results.getString("notes");

                System.out.println("Action: " + actionType);
                System.out.println("Unit: " + unitNumber);
                System.out.println("Date: " + actionDate);
                System.out.printf("Rate: $%.2f%n", monthlyRate);
                System.out.println("Notes: " + notes);
                System.out.println("-----------------------------------------");
            }
            if (!found) {
                System.out.println("Customer has no rental history");
            }
        } catch (SQLException exception) {
            System.out.println("Unable to load rental History");
            exception.printStackTrace();
        }
    }
}


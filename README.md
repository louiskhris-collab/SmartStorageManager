SmartStorageManager Project 

Overview:
A storage management system to learn Java fundamentals, such as OOP, Scanner class, Arrays, Loops, Read Lines in files, Loading / Saving info into txt files, 


Features:
1: Rent to new customers
2: View individual storage units
3: View all storage units
4: Filter units by availability and size
5: Storage Dashboard 
6: Update rental rates
7. Customer Management

------Version #2------
New features:
**Customer Management**
- Search customers by:
  - Phone number
  - Customer number
  - Name
  - Unit number
- Handle multiple customers with matching names or phone numbers
- Update customer information
- Move customers out
- Transfer customers between storage units

  **Rental History**
  Rental activity is automatically recorded whenever a customer:
  - Moves into a unit
  - Moves out of a unit
  - Transfers out of a unit
  - Transfers into a new unit

  Each History records stores information such as:
  - Customer
  - Unit number
  - Action Type
  - Date
  - Monthly rental rate at the tie of the action
  - Notes describing the event
 
  **Storage Dashboard**
Storage dashboard provides an overview of facility performance, regarding unit data and financial information
- Total storage units
- Occupied units
- Vacant units
- Occupancy rate
- Current monthly revenue
- Potential revenue
- Revenue breakdowns

**Database Design Changes**
Major Tables include:
- customers
- storage_units
- unit_types
- rental_history

**Version 2 Overall Update**
- MySQL database integration
- JDBC
- Customer management system/menu
- Updated customer search
- Multi-result customer searches
- Customer transfer
- Rental history tracking
- SQL transactions and rollback protection
- Storage Dashboard modifications
- improved user input validation
- improved menu organization

----------------------------------------------------------------

**Future Improvements**
- Spring Boot backend
- Web-based frontend
- Authentication and employee accounts
- Improved reporting
- Customer payment tracking
- REST API
- Cloud database deployment

**Resources**
InteliJ IDE
Java
JDBC
SQL
Git/Github
Textbook: Starting Out with Java: Early Objects (6th Edition)

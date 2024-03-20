package users;

import services.Appointments;
import services.Service;

import java.sql.*;
import java.util.Scanner;

public final class Client extends User{
    private String CCNum;
    private String CCExp;

    public Client(String email, String password, String name, String phone, String address, String CCNum, String CCExp) {
        super(email, password, name, phone, address);
        this.CCNum = CCNum;
        this.CCExp = CCExp;
    }

    public Client(User user, String CCNum, String CCExp){
        super(user.email, user.password, user.name, user.phone, user.address);
        this.CCNum = CCNum;
        this.CCExp = CCExp;
    }

    //method to get info that exists already in db
    public Client(User user, Connection conn){
        super(user.email, user.password, user.name, user.phone, user.address);
        String userID = user.getUserID(conn);
        String sql = "SELECT CCNum, CCExp FROM Clients WHERE userId = ?;";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            this.CCNum = resultSet.getString("CCNum");
            this.CCExp = resultSet.getString("CCExp");
        }
        catch (SQLTimeoutException te){
            System.out.println("Failed to connect to the database. Please verify your internet connection");
        }
        catch (SQLException se){
            //todo
            System.out.println(se);
        }
    }

    static Client createClient(User user, Scanner scanner){
        System.out.println("You will need a credit card to continue");
        System.out.print("Please provide us with your credit card number (XXXX XXXX XXXX XXXX) -> ");
        scanner.next();
        String ccnum = scanner.nextLine();
        System.out.print("Please enter the expiration date (YYYY-MM-DD) -> ");
        String ccexp = scanner.nextLine();
        return new Client(user, ccnum, ccexp);
    }

    @Override
    public void storeNewUserInDb(Connection connection){
        super.storeNewUserInDb(connection);
        String sqlStatement = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
        }
        catch (SQLException se){
            //todo
        }
    }

    public void clientFromUserToDb(Connection connection){
        String sql = "INSERT INTO Clients (userID, CCNum, CCExp) VALUES (?, ?, ?);";
        String userID = this.getUserID(connection);
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, this.CCNum);
            //MUST BE A DATE
            preparedStatement.setString(3, this.CCExp);
            preparedStatement.execute();
            System.out.println("Successfully saved client info into database");
        }
        catch (SQLException se){
            System.out.println("An error occured trying to save client info into database");
            System.out.println(se);
        }
    }

    public static void clientMenu(Client user, Connection conn, Scanner scanner){
        String clientID = user.getUserID(conn);
        boolean running = true;
        System.out.println("\t\t\tWelcome " + user.getName());
        System.out.println("----------------------------------------");
        while (running){
            System.out.println("Please select one of the following options:");
            System.out.println("1. Change user profile");
            System.out.println("2. Check user profile");
            System.out.println("3. Book an appointment for a service");
            System.out.println("4. Leave a Review for a service");
            System.out.println("5. Browse available services");
            System.out.println("6. View Invoices and Review Services");
            System.out.println("7. Become a service provider");
            System.out.println("8. Logout");
            System.out.print("-> ");
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    user.changeUserProfile(conn, scanner);
                    break;
                case 2:
                    System.out.println(user);
                    break;
                case 3:
                    //todo in appmnts package
                    Appointments.newAppointmentMenu(user, conn, scanner);
                    break;
                case 4:
                    break;
                case 5:
                    Service.browseServices(conn, scanner);
                    break;
                case 6:
                    String userID = user.getUserID(conn);

                    String clientInvoicesSQL = "SELECT issueDate, amount, serviceID, clientID FROM Invoices " +
                            "WHERE clientID = ? ORDER BY issueDate;";

                    try{
                        PreparedStatement clientInvoicesStatement = conn.prepareStatement(clientInvoicesSQL);
                        clientInvoicesStatement.setString(1, userID);

                        ResultSet invoiceResult = clientInvoicesStatement.executeQuery();

                        System.out.println("Account Invoices:");
                        System.out.println("------------------");
                        System.out.println("Issue Date | Invoice Amount | Service ID");
                        while (invoiceResult.next()){
                            System.out.println(matchLength(invoiceResult.getString(1), 11) + "  "
                                    + matchLength(invoiceResult.getString(2), 15) + "  "
                                    + matchLength(invoiceResult.getString(3), 10));
                        }
                    } catch (SQLException e) {
                        System.out.println("Error while fetching invoice list");
                    }
                    break;
                case 7:
                    //todo service
                    //serviceprovider creation menu instead
                    running = false;
                    break;
                case 8:
                    running = false;
                    //TODO THIS IS A VERY UNSECURE WAY OF LOGGING OUT, THE OBJECTS STILL EXIST IN MEMORY?
                    System.out.println("Successfully logged out");
                    break;
            }
        }
    }

    @Override
    protected void changeUserProfile(Connection conn, Scanner scanner){
        System.out.println("\t\t\tProfile Settings");
        System.out.println("------------------------------------------------");
        String userId = this.getUserID(conn);
        boolean running = true;
        while (running){
            System.out.println("Please select one of the following options:");
            System.out.println("1. Change email address");
            System.out.println("2. Update password");
            System.out.println("3. Change phone number");
            System.out.println("4. Change address");
            //changes both date and number, must check if the date is newer
            //refuse for dates after today
            System.out.println("5. Update credit card");
            System.out.println("6. Leave this menu");
            System.out.print("-> ");
            int choice = scanner.nextInt();
            //clear buffer
            scanner.nextLine();
            switch (choice){
                case 1:
                    //change the email of the object
                    String newEmail = this.changeEmail(conn, scanner);
                    if (newEmail != null) {
                        //set the new email address in the database
                        this.updateUserInDb(newEmail, userId, conn, changedType.EMAIL);
                        System.out.println("Successfully updated your email address.");
                        this.email = newEmail;
                    }
                    else{
                        //for debugging
                        System.out.println("No db update for the same email.\n");
                    }
                    break;
                case 2:
                    String newPass = this.changePassword(scanner);
                    this.updateUserInDb(newPass, userId, conn, changedType.PASSWORD);
                    this.password = newPass;
                    break;
                case 3:
                    String newPhoneNumber = this.changePhoneNumber(scanner);
                    this.updateUserInDb(newPhoneNumber, userId, conn, changedType.PHONE);
                    this.phone = newPhoneNumber;
                    break;
                case 4:
                    String newAddress = this.changeAddress(scanner);
                    this.updateUserInDb(newAddress, userId, conn, changedType.ADDRESS);
                    this.address = newAddress;
                    break;
                case 5:
                    //todo change CC info
                case 6:
                    running = false;
                    System.out.println("Returning to account menu.");
                    break;
            }
        }
    }

    @Override
    public String toString(){
        String part = super.toString();
        return part +
                "\nCCNum: " + this.CCNum +
                "\nCCExp: " + this.CCExp;
    }

    private static String matchLength(String str, int len){
        if(str.length() > len)
            return str.substring(0, len - 3) + "...";
        else if(str.length() == len) return str;
        else{
            int spaceCount = len - str.length();
            return str + " ".repeat(spaceCount);
        }
    }
}

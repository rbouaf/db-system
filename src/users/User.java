package users;

import services.Service;

import java.sql.*;
import java.util.Scanner;

public class User {
    protected String email;
    //TODO storage of a password as a string... NOT SAFE
    protected String password;
    protected String name;
    protected String phone;
    protected String address;

    //basic user object that IS VERIFIED to be added to database
    public User(String email, String password, String name, String phone, String address){
        //TODO need at least email and password
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public static User makeNewUser(Connection conn, Scanner scanner){
        System.out.print("Please enter your email -> ");
        //static helper
        String email = setEmail(null, conn, scanner);
        //passwords will be stored in plain text for now...
        //TODO secure way of storing passwords
        String passwd;
        do {
            System.out.print("Please enter a secured password (at least 8 chars) -> ");
            passwd = scanner.nextLine();
            if (passwd.length() < 8)
                System.out.println("Your password is too small!");
            else if (passwd.contains(" "))
                System.out.println("Please do not use space characters");
            else
                break;
        }while(true);

        System.out.println("Perfect! We will now need some personal information to complete the account creation.");
        System.out.print("Please enter your name -> ");
        String name = scanner.nextLine();
        System.out.print("PLease enter your telephone number -> ");
        String phoneNum = scanner.nextLine();
        System.out.print("Please enter your address -> ");
        String address = scanner.nextLine();
        return new User(email, passwd, name, phoneNum, address);
    }

    public static void getUserInfo(Connection connection, String email) throws SQLException {
        //TODO
        String sql = "SELECT name, phone, address FROM Users WHERE email = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();
        assert resultSet != null;
        while (resultSet.next()){
            System.out.println(
                    "Name: " + resultSet.getString(1) + "\n" +
                    "Phone: " + resultSet.getString(2) + "\n" +
                    "Address: " + resultSet.getString(3));
        }
        System.out.println(resultSet);
    }

    public void storeNewUserInDb(Connection connection){
        String sqlStatement = "INSERT INTO Users (name,email, phone, address, password) VALUES (?, ?, ?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, this.name);
            preparedStatement.setString(2, this.email);
            preparedStatement.setString(3, this.phone);
            preparedStatement.setString(4, this.address);
            preparedStatement.setString(5, this.password);

            preparedStatement.execute();
            System.out.println("Successfully added a new user");
        }
        catch (SQLException se){
            //TODO
            System.out.println("An error occured trying to insert a new user into the database");
            System.out.println(se.getSQLState() + "\t" + se.getErrorCode());
            System.out.println(se.toString());
        }
    }

    protected void updateUserInDb(String newData, String userID, Connection connection, changedType type){
        String sqlStatement = null;
        switch (type){
            case EMAIL -> sqlStatement = "UPDATE Users SET email = ? WHERE userID = ?;";
            case PASSWORD -> sqlStatement = "UPDATE Users SET password = ? WHERE userID = ?;";
            case PHONE -> sqlStatement = "UPDATE Users SET phone = ? WHERE userID = ?;";
            case ADDRESS -> sqlStatement = "UPDATE Users SET address = ? WHERE userID = ?;";
        }
        //todo below statement is for debugging
        assert sqlStatement != null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, newData);
            preparedStatement.setString(2, userID);
            preparedStatement.execute();
            System.out.println("Successfully updated user info in the database");
        }
        catch (SQLException e) {
            //todo
            System.out.println("An error occured trying to update user profile...");
        }
    }

    public static void userMenu(User user, Connection conn, Scanner scanner){
        //todo
        //browse available services -> if want to book, must become a client
        boolean running = true;
        System.out.println("\t\t\tWelcome " + user.getName());
        System.out.println("---------------------------------------------");
        while (running){
            System.out.println("Please select one of the following options:");
            System.out.println("1. Change user profile");
            System.out.println("2. Check user profile");
            System.out.println("3. Browse available services");
            System.out.println("4. Become a Client");
            System.out.println("5. Become a Service Provider");
            System.out.println("6. Logout");
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
                    Service.browseServices(conn, scanner);
                    break;
                case 4:
                    Client client = Client.createClient(user, scanner);
                    //add to database
                    System.out.println(client);
                    client.clientFromUserToDb(conn);
                    //dangerous operation if we keep this part in the stack
                    //but we'll need to ignore that for now
                    Client.clientMenu(client, conn, scanner);
                    running = false;
                    break;
                case 5:
                    ServiceProvider serviceProvider = ServiceProvider.newServiceProviderMenu(user, conn, scanner);
                    ServiceProvider.serviceProviderMenu(serviceProvider, conn, scanner);
                    break;
                case 6:
                    running = false;
                    System.out.println("Successfully logged out");
                    break;
                default:
                    System.out.println("Invalid option!");
                    break;
            }
        }
    }

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
            System.out.println("5. Leave this menu");
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
                    running = false;
                    System.out.println("Returning to account menu.");
                    break;
            }
        }
    }

    protected String changeEmail(Connection conn, Scanner scanner){
        System.out.println("You chose to change your email address.");
        System.out.print("Please write your new email address -> ");
        String newEmail = null;
        boolean notProvided = true;
        while (notProvided){
            newEmail = setEmail(this, conn, scanner);
            //if null then it must be equal to itself
            if (newEmail == null){
                //perhaps do an error message, but do not want to get stuck in this loop if user wants to leave
                //System.out.println("The email you provided is the same as your current email.");
                return newEmail;
            }
            notProvided = false;
        }
        return newEmail;
    }

    protected String changePassword(Scanner scanner){
        //make a double check
        System.out.println("You chose to change your password");
        String newPass = null;
        boolean running = true;
        while (running) {
            System.out.print("Please enter a new secure password (at least 8 chars) -> ");
            newPass = scanner.nextLine();
            if (newPass.length() >= 8){
                boolean running2 = true;
                while (running2){
                    System.out.print("Please retype your password -> ");
                    String retype = scanner.nextLine();
                    if (retype.equals(newPass)) {
                        running2 = false;
                        running = false;
                    }
                    else{
                        System.out.println("The passwords do not match!");
                    }
                }
            }
            else{
                System.out.println("The new password is too short!");
            }
        }
        return newPass;
    }
    protected String changePhoneNumber(Scanner scanner){
        System.out.println("You chose to change your phone number");
        System.out.print("Please write your new phone number -> ");
        return scanner.nextLine();
    }
    protected String changeAddress(Scanner scanner){
        System.out.println("You chose to change your address");
        System.out.print("Please write your new address -> ");
        return scanner.nextLine();
    }

    //user may be null
    private static String setEmail(User user, Connection conn, Scanner scanner){
        String email;
        boolean notProvided = true;
        do {
            //could do a better job but for simplicity let's keep it this way for now
            email = scanner.nextLine();
            if (!email.contains("@") || email.contains(" ")) {
                System.out.println("You did not provide a valid email address!");
                System.out.print("Please enter an email address of the form example@domain.com -> ");
            }
            else {
                //check if that email is already used in the database
                //todo
                String sql = "SELECT email FROM Users WHERE email = ?";
                try {
                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, email);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    try {
                        resultSet.next();
                        String potentialEmail = resultSet.getString(1);
                        //check if the new email is equal to self email or to other email
                        //simply display message and leave if it is self email
                        if (user != null) {
                            if (potentialEmail.equals(user.email)){
                                System.out.println("You are already using that email.");
                                System.out.println("Cancelling process...\n");
                                return null;
                            }
                        }
                        System.out.println("This email is already used by somebody else.");
                        System.out.print("Please use another email to create a new account -> ");
                    }
                    catch (SQLException se){
                        //if exception occurs when checking prepared.next(), it means that nobody used the
                        //email already, so we are good to go.
                        notProvided = false;
                    }
                }
                catch (SQLException se) {
                    System.out.println("An error occurred during creation of a prepared statement when verifying if email already used for user registration");
                    break;
                }
            }
            //static helper
        } while(notProvided);
        return email;
    }

    public String getUserID(Connection conn){
        String sql = "SELECT userID FROM Users WHERE email = ?";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, this.email);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        }
        catch (SQLException se){
            System.out.println("Error occured trying to get the userID of" + this.email);
            return null;
        }
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return  "\nEmail: " + this.email +
                "\t\t\tPassword: " + this.password +
                "\nName: " + this.name +
                "\t\t\tAddress: " + this.address +
                "\nPhone: " + this.phone + "\n";
    }

    protected enum changedType{
        EMAIL, PASSWORD, PHONE, ADDRESS
    }
}
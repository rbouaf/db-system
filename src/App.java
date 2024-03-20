import users.Business;
import users.Client;
import users.IndependentWorker;
import users.User;

import java.sql.*;
import java.util.Scanner;

public class App {
    private static final String JDBC_DRIVER = "com.ibm.db2.jcc.DB2Driver";
    private static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/comp421";
    private static final String USER = "cs421g104"; //TODO: this will be changed before submitting
    private static final String PASS = "comp421&&"; //TODO: this will be changed before submitting

    public static void main(String[] args) {
        try {
            Class.forName(JDBC_DRIVER);
        }
        catch (ClassNotFoundException ce){
            System.out.println("JDBC Driver not found.");
        }
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Quit the application");
            System.out.print("Please select an option -> ");
            int option = scanner.nextInt();
            //clear the buffer
            scanner.nextLine();
            switch (option) {
                case 1:
                    try {
                        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
                        User user = registerNewUser(connection, scanner);
                        User.userMenu(user, connection, scanner);
                    }
                    catch (SQLTimeoutException te){
                        System.out.println("Connection failed to database, because of timeout");
                        System.out.println("Please check your internet connection");
                        running = false;
                    }
                    catch (SQLException e) {
                        //TODO
                        System.out.println(e);
                    }
                    break;
                case 2:
                    loginMenu(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for doing business with group 104");
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static User registerNewUser(Connection conn, Scanner scanner) {
        //TODO: we could record how many users register by month
        User user = User.makeNewUser(conn, scanner);
        addToDb(conn, user);
        System.out.println(user);
        return user;
    }

    private static void addToDb(Connection conn, User user){
        if (user instanceof Client client){
            client.storeNewUserInDb(conn);
        }
        else if (user instanceof IndependentWorker independentWorker){
            independentWorker.storeNewUserInDb(conn);
        }
        else if (user instanceof Business business){
            business.storeNewUserInDb(conn);
        }
        else{
            user.storeNewUserInDb(conn);
        }
    }

    private static void loginMenu(Scanner scanner){
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            ResultSet userRes = login(connection, scanner);
            if (userRes == null){
                System.out.println("Wrong username or password provided!");
                return;
            }
            System.out.println("Successfully logged in");
            String userId = userRes.getString(1);
            String userName = userRes.getString(2);
            String userEmail = userRes.getString(3);
            String userPhone = userRes.getString(4);
            String userAddress = userRes.getString(5);
            String userPasswd = userRes.getString(6);
            User user = new User(userEmail, userPasswd, userName, userPhone, userAddress);
            UserType userType = checkUserType(connection, userId);
            //System.out.println(user);
            switch (userType){
                //TODO WHAT IF THE USER IS BOTH A CLIENT AND A SERVICE PROVIDER
                case Both:
                    //todo
                    bothMenu(userId, user, scanner);
                    break;
                case Client:
                    Client client = new Client(user, connection);
                    Client.clientMenu(client, connection, scanner);
                    break;
                case ServiceProvider:
                    //todo
                    break;
                case UserOnly:
                    User.userMenu(user, connection, scanner);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        catch (SQLException se){
            //todo
        }
        catch (IllegalArgumentException ie){
            //todo something else, may need to change if users allowed to be
            //only users and not clients or service providers
            System.out.println("Invalid user type!!");
        }
    }
    public static ResultSet login(Connection connection, Scanner scanner) {
        System.out.print("Please enter your email -> ");
        String email = scanner.nextLine();
        System.out.print("Please enter your password -> ");
        String passwd = scanner.nextLine();
        //process email address first, or should we do both at the same time (harder to detect?)
        String sql = "SELECT userID, name, email, phone, address, password " +
                    "FROM Users WHERE email = ? AND password = ?;";
        //String id = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            try {
                resultSet.next();
                resultSet.getString(1);
                return resultSet;
            }
            catch (SQLException s){
                return null;
            }
        }
        catch (SQLException se){
            System.out.println("An error occurred when trying to login... please check your internet connection");
            //or an error occurred for
        }
        return null;
    }

    public static void bothMenu(String userId, User user, Scanner scanner){
        //todo
        //want to display all possible options, i.e. the ones for client AND service provider
        boolean running = true;
        System.out.println("Welcome " + user.getName());
        while (running){
            System.out.println("Please select one of the following options:");
            System.out.println("1. Change user profile");
            System.out.println("2. Book an appointment for a service");
            System.out.println("3. Leave a Review for a service");
            System.out.println("4. Browse available services");
            System.out.println("5. Post a new service");
            //the below option is for how many clients booked, how much earned
            //how much earned by service, etc.
            System.out.println("6. Update a service");
            System.out.println("7. Check useful analytics");
            System.out.println("8. Logout");
            System.out.print("-> ");
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    break;
                case 2:
                    //TODO
                    //bookAnAppointment()
                    break;
                case 3:
                    //TODO
                    //leaveAReview()
                    break;
                case 4:
                    //TODO
                    //browse
                    break;
                case 5:
                    //TODO
                    //poseNewService
                    break;
                case 6:
                    //TODO
                    //updateService
                    break;
                case 7:
                    //TODO
                    //checkAnalytics
                    break;
                case 8:
                    running = false;
                    System.out.println("Successfully logged out");
                    break;
            }
        }
    }

    private static UserType checkUserType(Connection connection, String userID) throws SQLException {
        boolean type1;
        boolean type2;
        try {
            //cannot be in the same block because type2 depends on type1 = true
            String sql = "SELECT userID FROM Clients WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            resultSet.getString(1);
            type1 = true;
            //return UserType.Client;
        }
        catch (SQLException se){
            type1 = false;
        }
        try{
            String sql = "SELECT userID FROM ServiceProviders WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            resultSet.getString(1);
            type2 = true;
        }
        catch (SQLException se){
            type2 = false;
        }
        return (type1 && type2) ? UserType.Both :
                (type1) ? UserType.Client :
                        (type2) ? UserType.ServiceProvider : UserType.UserOnly;
    }
    private enum UserType{
        Client, ServiceProvider, Both, UserOnly
    }
}

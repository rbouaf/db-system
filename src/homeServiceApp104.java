import java.sql.*;
import java.util.Scanner;

import users.Business;
import users.Client;
import users.IndependentWorker;
import users.User;
import users.tests.Tests;

public class homeServiceApp104 {
    private static final String JDBC_DRIVER = "com.ibm.db2.jcc.DB2Driver";
    private static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/comp421";
    private static final String USER = "cs421g104"; //TODO: this will be changed before submitting
    private static final String PASS = "comp421&&"; //TODO: this will be changed before submitting
    //AS AN ALTERNATIVE, we can just set the password in the shell environment in the Unix (as shown below) and read it from there.
    //$  export SOCSPASSWD=comp421&&
    public static void main(String[] args) throws SQLException {
        int sqlCode=0;      // Variable to hold SQLCODE
        String sqlState="00000";  // Variable to hold SQLSTATE
        // Register the driver.  You must register the driver before you can use it.
        try (Scanner scanner = new Scanner(System.in);
             Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                Class.forName(JDBC_DRIVER);
                boolean running = true;
                while (running) {
                    System.out.println("\nMain Menu:");
                    System.out.println("1. Register as New User");
                    System.out.println("2. Book an Appointment for a Service");
                    System.out.println("3. Leave a Review for a Service");
                    System.out.println("4. Browse Available Services");
                    System.out.println("5. View Upcoming Appointments for a Client");
                    System.out.println("6. Quit");
                    //to remove
                    System.out.println("7. Debugging");
                    System.out.print("Select an option: ");
                    // TODO: More options to be added like the ones below (commented out):
                    //System.out.println("7. Update Service Availability");
                    // System.out.println("8. Cancel or Complete an Appointment");
                    int option = scanner.nextInt(); // getting user choice
                    scanner.nextLine(); // newline
                    switch (option) {
                        case 1:
                            registerNewUser(conn, scanner);
                            break;
                        case 2:
                            bookAppointment(conn, scanner);
                            break;
                        case 3:
                            leaveReview(conn, scanner);
                            break;
                        case 4:
                            viewServicesByCategory(conn, scanner);
                            break;
                        case 5:
                            viewUpcomingAppointments(conn, scanner);
                            break;
                        case 6:
                            running = false;
                            System.out.println("You exited the app...");
                            //added
                            try {
                                conn.close();
                            }
                            catch (SQLException s){
                                //todo need to debug in case connection cannot be closed
                                System.out.println("An error occured triyng to close the connection...");
                            }
                            break;
                        case 7:
                            //Client client = Tests.test1();
                            //registerNewUser(conn, client);
                            Tests.test2(conn);
                            break;
                        default: System.out.println("Invalid option. Please try again.");
                    }
                }
            }
        catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
        }
        catch (SQLException se) {
            sqlCode = se.getErrorCode(); // Get SQLCODE
            sqlState = se.getSQLState(); // Get SQLSTATE
            // We have to update this error, for example if user tries to register with existing email...;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            //modified
            System.out.println(se.toString());
        }
    }
    public static void registerNewUser(Connection conn, Scanner scanner) {
        //TODO: for registering a new user
        //TODO: we could record how many users register by month
        //User user = new User();
        User user = User.makeNewUser(conn, scanner);
        addToDb(conn, user);
        System.out.println(user);
        //user name successfully registered to the service
    }
    //TODO FOR DEBUGGING ONLY
    private static void registerNewUser(Connection connection, User user){
        addToDb(connection, user);
    }

    private static void addToDb(Connection conn, User user){
        if (user instanceof Client){
            user.storeNewUserInDb(conn);
        }
        else if (user instanceof IndependentWorker){
            //TODO
        }
        else if (user instanceof Business){
            //TODO
        }
        else{
            //wtf just happened
        }
    }



    private static void bookAppointment(Connection conn, Scanner scanner) {
        //TODO: for booking an appointment for a service
        System.out.println("Appointment booked!"); // for testing
    }
    private static void leaveReview(Connection conn, Scanner scanner) {
        //TODO: for leaving a review for a service
    }
    //worked on by Abderrahman
    private static void viewServicesByCategory(Connection conn, Scanner scanner) {
        //TODO: for viewing/browsing services by category
    }
    private static void viewUpcomingAppointments(Connection conn, Scanner scanner) {
        //TODO:for viewing upcoming appointments for a client/provider
    }
    // TODO: We need to add more functions here
}

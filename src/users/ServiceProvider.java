package users;

import services.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public abstract class ServiceProvider extends User{
    //todo for now tmp final
    protected final String accountNum;
    protected final String transitNum;
    protected final String bankNum;
    protected float totalEarned;

    public ServiceProvider(String email, String password, String name, String phone, String address,
                           String accountNum, String transitNum, String bankNum) {
        super(email, password, name, phone, address);
        this.accountNum = accountNum;
        this.transitNum = transitNum;
        this.bankNum = bankNum;
        this.totalEarned = 0.0F;
    }
    public ServiceProvider(String email, String password, String name, String phone, String address,
                           String accountNum, String transitNum, String bankNum, float totalEarned) {
        super(email, password, name, phone, address);
        this.accountNum = accountNum;
        this.transitNum = transitNum;
        this.bankNum = bankNum;
        this.totalEarned = totalEarned;
    }

    public static ServiceProvider newServiceProviderMenu(User user, Connection connection, Scanner scanner){
        scanner.nextLine();
        System.out.println("We will need some banking information in order to continue");
        System.out.print("Please write your bank account number -> ");
        String accountNum = scanner.nextLine();
        //formatting
        System.out.print("Please write your transit number -> ");
        String transitNum = scanner.nextLine();
        System.out.print("Please write your bank branch number -> ");
        String bankNum = scanner.nextLine();
        System.out.println("What kind of service provider would you like to register as?");
        System.out.println("1. Business");
        System.out.println("2. Independent Worker");
        System.out.print("Select one of the options above-> ");
        while (true){
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    //todo business
                    Business business = new Business(user, accountNum, transitNum, bankNum, scanner);
                    business.storeNewServiceProvider(connection);
                    return business;
                case 2:
                    //todo indep worker
                    IndependentWorker independentWorker = new IndependentWorker(user, accountNum, transitNum, bankNum, scanner);
                    independentWorker.storeNewServiceProvider(connection);
                    return independentWorker;
                default:
                    System.out.println("Invalid option!");
                    System.out.print("Please select one of the 2 aforementioned options -> ");
            }
        }
    }

    protected void storeNewServiceProvider(Connection connection){
        String sql = "INSERT INTO ServiceProviders (userID,accountNum,transitNum,bankNum,totalEarned) " +
                "VALUES (?,?,?,?,?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, this.getUserID(connection));
            preparedStatement.setString(2, this.accountNum);
            preparedStatement.setString(3, this.transitNum);
            preparedStatement.setString(4, this.bankNum);
            preparedStatement.setString(5, String.valueOf(this.totalEarned));
            preparedStatement.execute();
            System.out.println("Successfully stored a new service provider in the database");

        } catch (SQLException e) {
            //todo
            throw new RuntimeException(e);
        }
    }

    public static ServiceProvider serviceProviderFromUser(User user, Connection connection){
        //first check if business or indep
        String userID = user.getUserID(connection);

        try{
            String sqlBasic = "SELECT accountNum,transitNum,bankNum,totalEarned FROM ServiceProviders " +
                    "WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlBasic);
            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String accNum = resultSet.getString(1);
            String transitNum = resultSet.getString(2);
            String bankNum = resultSet.getString(3);
            float totalEarned= Float.parseFloat(resultSet.getString(4));
            String sqlBus = "SELECT NEQ FROM Businesses WHERE userID = ?;";
            preparedStatement = connection.prepareStatement(sqlBus);
            preparedStatement.setString(1, userID);
            try {
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String neq = resultSet.getString(1);
                return new Business(user, accNum, transitNum, bankNum, totalEarned, neq);
            }
            catch (SQLException se){
                //it is not a business
                String sqlIndep = "SELECT socialNumber FROM IndependentWorkers WHERE userID = ?;";
                preparedStatement = connection.prepareStatement(sqlIndep);
                preparedStatement.setString(1, userID);
                resultSet = preparedStatement.executeQuery();
                //todo sketchy way of doing the operation
                resultSet.next();
                String ssn = resultSet.getString(1);
                return new IndependentWorker(user, accNum, transitNum, bankNum, totalEarned, ssn);
            }
        }
        catch (SQLException se){
            System.out.println("Error occurred at service provider from user method");
            System.out.println(se);
        }
        catch (NumberFormatException ne){
            System.out.println("Wrong value saved in db for a float??");
        }
        return null;
    }

    public static void serviceProviderMenu(ServiceProvider serviceProvider, Connection conn, Scanner scanner){
        boolean running = true;
        while (running){
            System.out.println("\n\t\tWelcome " + serviceProvider.name);
            System.out.println("-------------------------------------------------");
            System.out.println("Please select one the following options:");
            //depends on type of service provider
            System.out.println("1. Change user profile");
            System.out.println("2. Check user profile");
            System.out.println("3. Post a new service");
            System.out.println("4. Post a new schedule");
            //the below option is for how many clients booked, how much earned
            //how much earned by service, etc.
            //todo should we also add delete a service and a schedule???
            System.out.println("5. Update a service");
            System.out.println("6. Update a schedule");
            System.out.println("7. Check useful analytics");
            System.out.println("8. Browse available services");
            System.out.println("9. Become a client");
            System.out.println("10. Logout");
            System.out.print("-> ");
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    //todo may need to change if we remove final fields
                    serviceProvider.changeUserProfile(conn, scanner);
                    break;
                case 2:
                    if (serviceProvider instanceof Business business) {
                        System.out.println(business);
                    }
                    else{
                        IndependentWorker indep = (IndependentWorker) serviceProvider;
                        System.out.println(indep);
                    }
                    break;
                case 3:
                    //verify to not add services with a name already taken
                    Service.newService(serviceProvider, conn, scanner);
                    break;
                case 4:
                    //todo create a new schedule
                    Schedule.newSchedule(serviceProvider, conn, scanner);
                    break;
                case 5:
                    //todo update a service
                    Service.updateService(serviceProvider, conn, scanner);
                    break;
                case 6:
                    //todo update a schedule
                    //list all the schedules to user and make them select the
                    //one they want to update
                    break;
                case 7:
                    displayData(serviceProvider, conn, scanner);
                    break;
                case 8:
                    Service.browseServices(conn, scanner);
                    break;
                case 9:
                    //todo may remove that option
                    break;
                case 10:
                    running = false;
                    System.out.println("Successfully logged out");
                    break;
            }
        }
    }
    public abstract ServiceProvider createNewServiceProvider(User user, Connection connection, Scanner scanner);

    //may need to do some basic implementation
    public void serviceProviderMenu(String servProvName, Connection connection, Scanner scanner){
        String userId = this.getUserID(connection);
    }
    @Override
    protected void changeUserProfile(Connection conn, Scanner scanner){
        super.changeUserProfile(conn, scanner);
    }

    public float getTotalEarned(){return this.totalEarned;}

    //todo for menu
    //post new service
    //check how many total appointments
    //check total earned money
    //check average money
    //check average rating across all existing services
    //check average rating for each service

    public static void displayData(ServiceProvider serviceProvider, Connection conn, Scanner scanner){
        //scanner.nextLine();
        System.out.println("\t\tData Analytics Menu");
        System.out.println("-------------------------------");
        boolean running = true;
        while (running) {
            System.out.println("Choose an option to review data about your account and your customers");
            System.out.println("1. Average rating for each service");
            System.out.println("2. Overall rating, across all your services");
            System.out.println("3. Total money earned by service");
            System.out.println("4. Total money earned by category");
            System.out.println("5. Leave this menu");
            //others related to appointments
            System.out.println("-> ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    //todo
                    break;
                case 2:
                    //todo
                    Service.getOverallRating(serviceProvider, conn);
                    break;
                case 5:
                    running = false;
                    break;
            }
        }
    }

    @Override
    public String toString(){
        return super.toString() +
                "Account Number: " + this.accountNum +
                "\nTransit Number" + this.transitNum +
                "\nBank Branch Number: " + this.bankNum +
                "\nTotal Earned: " + this.totalEarned + "\n";
    }
}

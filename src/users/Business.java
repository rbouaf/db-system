package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public final class Business extends ServiceProvider{
    public Business(String email, String password, String name, String phone, String address, String accountNum, String transitNum, String bankNum) {
        super(email, password, name, phone, address, accountNum, transitNum, bankNum);
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
    public /* static */ void serviceProviderMenu(String servProvId, String servProvName, Scanner scanner){
        boolean running = true;
        while (running){
            System.out.println("Welcome" + servProvName);
            System.out.println("Please select one the following options:");
            //depends on type of service provider
            System.out.println("1. Change user profile");
            System.out.println("2. Post a new service");
            //the below option is for how many clients booked, how much earned
            //how much earned by service, etc.
            System.out.println("3. Update a service");
            System.out.println("4. Check useful analytics");
            System.out.println("5. Browse available services");
            System.out.println("6. Become a client");
            System.out.println("7. Logout");
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    running = false;
                    System.out.println("Successfully logged out");
                    break;
            }
        }
    }

    @Override
    protected void changeUserProfile(Connection conn, Scanner scanner) {
        super.changeUserProfile(conn, scanner);
    }

}

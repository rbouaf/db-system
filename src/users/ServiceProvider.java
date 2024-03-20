package users;

import java.sql.Connection;
import java.util.Scanner;

public abstract class ServiceProvider extends User{
    protected String accountNum;
    protected String transitNum;
    protected String bankNum;
    protected float totalEarned;

    public ServiceProvider(String email, String password, String name, String phone, String address,
                           String accountNum, String transitNum, String bankNum) {
        super(email, password, name, phone, address);
        this.accountNum = accountNum;
        this.transitNum = transitNum;
        this.bankNum = bankNum;
        this.totalEarned = 0.0F;
    }

    public static ServiceProvider newServiceProviderMenu(User user, Connection connection, Scanner scanner){
        System.out.println("What kind of service provider would you like to register as?");
        System.out.println("1. Business");
        System.out.println("2. Independent Worker");
        System.out.print("Select one of the options above-> ");
        boolean running = true;
        while (running){
            int choice = scanner.nextInt();
            switch (choice){
                case 1:
                    //todo business
                    running = false;
                    return null;
                case 2:
                    //todo indep worker
                    running = false;
                    return null;
                default:
                    System.out.println("Invalid option!");
                    System.out.println("Please select one of the 2 aforementioned options");
            }
        }
        return null;
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

    public void postNewService(Connection connection, Scanner scanner){

    }

    public float getTotalEarned(){return this.totalEarned;}

    //todo for menu
    //post new service
    //check how many total appointments
    //check total earned money
    //check average money
    //check average rating across all existing services
    //check average rating for each service
}

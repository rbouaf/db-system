package users;

import java.sql.Connection;
import java.util.Scanner;

public abstract class ServiceProvider extends User{
    String accountNum;
    String transitNum;
    String bankNum;
    //float totalEarned;

    public ServiceProvider(String email, String password, String name, String phone, String address,
                           String accountNum, String transitNum, String bankNum) {
        super(email, password, name, phone, address);
        this.accountNum = accountNum;
        this.transitNum = transitNum;
        this.bankNum = bankNum;
        //this.totalEarned = 0.0F;
    }

    //may need to do some basic implementation
    public void serviceProviderMenu(String servProvName, Connection connection, Scanner scanner){
        String userId = this.getUserID(connection);
    }
    @Override
    protected void changeUserProfile(Connection conn, Scanner scanner){
        super.changeUserProfile(conn, scanner);
    }

    //todo for menu
    //post new service
    //check how many total appointments
    //check total earned money
    //check average money
    //check average rating across all existing services
    //check average rating for each service
    public float getTotalEarned(Connection connection){
        return 0f;
    }
}

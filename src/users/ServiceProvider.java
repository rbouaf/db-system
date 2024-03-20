package users;

import java.sql.Connection;
import java.util.Scanner;

public abstract class ServiceProvider extends User{
    String accountNum;
    String transitNum;
    String bankNum;
    float totalEarned;

    public ServiceProvider(String email, String password, String name, String phone, String address,
                           String accountNum, String transitNum, String bankNum) {
        super(email, password, name, phone, address);
        this.accountNum = accountNum;
        this.transitNum = transitNum;
        this.bankNum = bankNum;
        this.totalEarned = 0.0F;
    }

    //may need to do some basic implementation
    protected abstract void serviceProviderMenu(String servProvId, String servProvName, Scanner scanner);
    @Override
    protected void changeUserProfile(Connection conn, Scanner scanner){
        super.changeUserProfile(conn, scanner);
    }
}

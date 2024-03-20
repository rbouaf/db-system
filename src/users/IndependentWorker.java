package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public final class IndependentWorker extends ServiceProvider{
    private final String SOCIAL_SECURITY_NUMBER;

    public IndependentWorker(String email, String password, String name, String phone, String address,
                             String accountNum, String transitNum, String bankNum, String socialSecurityNumber) {
        super(email, password, name, phone, address, accountNum, transitNum, bankNum);
        this.SOCIAL_SECURITY_NUMBER = socialSecurityNumber;
    }

    public IndependentWorker(User user, String accountNum, String transitNum, String bankNum, float total, String SSN){
        super(user.email, user.password, user.name, user.phone, user.address, accountNum, transitNum, bankNum, total);
        this.SOCIAL_SECURITY_NUMBER = SSN;
    }

    @Override
    public ServiceProvider createNewServiceProvider(User user, Connection connection, Scanner scanner) {
        return null;
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

    @Override
    public String toString(){
        return super.toString() +
                "Social Security Number: " + this.SOCIAL_SECURITY_NUMBER;
    }
}

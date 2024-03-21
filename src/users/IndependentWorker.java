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

    public IndependentWorker(User user, String accountNum, String transitNum, String bankNum, Scanner scanner) {
        super(user.email, user.password, user.name, user.phone, user.address, accountNum, transitNum, bankNum, 0.0F);
        System.out.print("Please enter your Social Security Number -> ");
        this.SOCIAL_SECURITY_NUMBER = scanner.nextLine();
    }

    @Override
    public void storeNewServiceProvider(Connection connection){
        super.storeNewServiceProvider(connection);
        String sqlStatement = "INSERT INTO IndependentWorkers (userID,socialNumber) " +
                "VALUES (?,?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, this.getUserID(connection));
            preparedStatement.setString(2, this.SOCIAL_SECURITY_NUMBER);
            preparedStatement.execute();
        }
        catch (SQLException se){
            System.out.println("An error occured trying to insert a new independent worker into the database");
            System.out.println("Error code: " + se.getErrorCode() + "\nSQL state: " + se.getSQLState());
        }
    }

    @Override
    public String toString(){
        return super.toString() +
                "Social Security Number: " + this.SOCIAL_SECURITY_NUMBER;
    }
}

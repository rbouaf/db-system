package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public final class Client extends User{
    private String CCNum;
    private String CCExp;
    public Client(String email, String password, String name, String phone, String address, String CCNum, String CCExp) {
        super(email, password, name, phone, address);
        this.CCNum = CCNum;
        this.CCExp = CCExp;
    }

    public Client(User user, String CCNum, String CCExp){
        super(user.email, user.password, user.name, user.phone, user.address);
        this.CCNum = CCNum;
        this.CCExp = CCExp;
    }

    static Client createClient(String email, String password, String name,
                               String phone, String address, Scanner scanner){
        System.out.println("You will need a credit card to continue");
        System.out.print("Please provide us with your credit card number -> ");
        //todo user scanner.nextLine()?
        String ccnum = scanner.next();
        System.out.print("Please enter the expiration date -> ");
        String ccexp = scanner.next();
        return new Client(email, password, name, phone, address, ccnum, ccexp);
    }

    static Client createClient(User user, Scanner scanner){
        System.out.println("You will need a credit card to continue");
        System.out.print("Please provide us with your credit card number -> ");
        String ccnum = scanner.next();
        System.out.print("Please enter the expiration date -> ");
        String ccexp = scanner.next();
        return new Client(user, ccnum, ccexp);
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

    public void clientFromUserToDb(Connection connection){
        String sql = "INSERT INTO Clients (userID, CCNum, CCExp) VALUES (?, ?, ?);";
        String userID = this.getUserID(connection);
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, this.CCNum);
            preparedStatement.setString(2, this.CCExp);
            preparedStatement.execute();
            System.out.println("Successfully saved client info into database");
        }
        catch (SQLException se){
            System.out.println("An error occured trying to save client info into database");
        }
    }

    public static void clientMenu(String clientId, User user, Scanner scanner){
        boolean running = true;
        System.out.println("Welcome " + user.getName());
        while (running){
            System.out.println("Please select one of the following options:");
            System.out.println("1. Change user profile");
            System.out.println("2. Book an appointment for a service");
            System.out.println("3. Leave a Review for a service");
            System.out.println("4. Browse available services");
            System.out.println("5. Become a service provider");
            System.out.println("6. Logout");
            System.out.print("-> ");
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
                    running = false;
                    //TODO THIS IS A VERY UNSECURE WAY OF LOGGING OUT, THE OBJECTS STILL EXIST IN MEMORY?
                    System.out.println("Successfully logged out");
                    break;
            }
        }
    }

    protected static void changeUserProfile(User user, Scanner scanner){
        //do other stuff
    }

    @Override
    public String toString(){
        String part = super.toString();
        return part +
                "\nCCNum: " + this.CCNum +
                "\nCCExp: " + this.CCExp;
    }
}

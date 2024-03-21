package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class Root {

    public static void rootMenu(Connection connection, Scanner scanner){
        //scanner.nextLine();
        boolean running = true;
        while (running) {
            System.out.println("\n\t\t\tRoot access menu");
            System.out.println("-------------------------------------");
            System.out.println("Please select a command");
            System.out.println("1. Ratio of normal users over total users (i.e. including clients and service providers)");
            System.out.println("2. Retrieve the category that generated the most money");
            System.out.println("3. Get the service provider with most cancelled appointments");
            System.out.println("4. Go back to main menu");
            System.out.print("-> ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    float obtainedRatio = ratio(connection);
                    if (obtainedRatio > 0){
                        System.out.println("Percentage obtained: " + obtainedRatio + "% " +
                                "of users are not registered as either clients or service providers");
                    }
                    else{
                        System.out.println("No ratio to show");
                    }
                    break;
                case 2:
                    getCategMostMoney(connection, scanner);
                    break;
                case 3:
                    break;
                case 4:
                    System.out.println("Exiting...");
                    running = false;
                    break;
            }
        }
    }

    private static float ratio(Connection connection){
        String sqlTotalUsers = "SELECT COUNT(userID) FROM users;";
        String sqlOnlyUsers = "SELECT COUNT(userID) FROM " +
                "(SELECT userID FROM Users EXCEPT " +
                "(SELECT userID FROM ServiceProviders UNION " +
                "SELECT userID FROM Clients));";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlTotalUsers);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int totalUsers = Integer.parseInt(resultSet.getString(1));
            if (totalUsers <= 0){
                System.out.println("There are no users in the database!");
                return 0.0f;
            }
            preparedStatement = connection.prepareStatement(sqlOnlyUsers);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int onlyUsers = Integer.parseInt(resultSet.getString(1));
            resultSet.close();
            preparedStatement.close();
            return ((float) onlyUsers / totalUsers) * 100;
        }
        catch (SQLException e) {
            System.out.println("An error occurred when trying to retrieve data from the database to evaluate ratio");
            System.out.println(e);
            return -1.0f;
        }
    }

    public static void getCategMostMoney(Connection connection, Scanner scanner){
        scanner.nextLine();
        System.out.print("Would you like to check that amount between a specific time period? (y/n) -> ");
        String choice = scanner.nextLine();
        String sql;
        if (choice.equalsIgnoreCase("y")){
            //do something
            String minDate = "SELECT MIN(issueDate) FROM invoices;";
            String maxDate = "SELECT MAX(issueDate) FROM invoices;";
            try {
                PreparedStatement preparedStatement1 = connection.prepareStatement(minDate);
                PreparedStatement preparedStatement2 = connection.prepareStatement(maxDate);
                ResultSet date1 = preparedStatement1.executeQuery();
                ResultSet date2 = preparedStatement2.executeQuery();
                if (date1.next() && date2.next()) {
                    minDate = date1.getString(1);
                    maxDate = date2.getString(1);
                    date1.close();
                    date2.close();
                    preparedStatement1.close();
                    preparedStatement2.close();
                }
                else{
                    System.out.println("There are no available dates to choose from");
                    date1.close();
                    date2.close();
                    preparedStatement1.close();
                    preparedStatement2.close();
                    return;
                }
            }
            catch (SQLException e) {
                System.out.println("An error occurred trying to fetch the minimum and maximum dates of invoices");
                System.out.println("Cannot proceed further");
                return;
            }
            System.out.println("Your selected dates must be between " + minDate + " and " + maxDate);
            System.out.print("Please select a beginning date in the form YYYY-MM-DD -> ");
            String date1 = scanner.nextLine();
            System.out.print("Please select a finishing date in the form YYYY-MM-DD -> ");
            String date2 = scanner.nextLine();
            Date beginDate = new Date(date1);
            Date finalDate = new Date(date2);
            Date minD = new Date(minDate);
            Date maxD = new Date(maxDate);
            if (beginDate.compareTo(minD) < 0 || finalDate.compareTo(maxD) > 0){
                System.out.println("The set of dates you issued seem to be out of scope of the data present in the database");
                return;
            }
            sql = "WITH tmp(groupsum,categoryid) AS (SELECT SUM(i.amount) AS groupSum,s.categoryID " +
                    "FROM Invoices AS i JOIN Services AS s ON i.serviceID = s.serviceID WHERE i.issuedate BETWEEN ? AND ? " +
                    "GROUP BY s.categoryID ORDER BY groupSum)" +
                    "SELECT CAST(name AS VARCHAR(10)) AS category,groupsum FROM tmp JOIN categories as c ON tmp.categoryid = c.categoryid " +
                    "WHERE groupsum = (SELECT MAX(groupsum) FROM tmp);";
            try {
                //could ask for a specific month also
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1,date1);
                preparedStatement.setString(2,date2);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    System.out.println("Category name: " + resultSet.getString(1) +
                            "\t\tTotal amount: " + resultSet.getString(2) + "$");
                }
                else{
                    System.out.println("Category name: N/A (none found)\t\tTotal amount: 0.00$");
                }
                resultSet.close();
                preparedStatement.close();
            }
            catch (SQLException e) {
                System.out.println("Error trying to query the category with most profit");
            }
        }
        else if (choice.equalsIgnoreCase("n")) {
            sql = "WITH tmp(groupsum,categoryid) AS (SELECT SUM(i.amount) AS groupSum,s.categoryID " +
                    "FROM Invoices AS i JOIN Services AS s ON i.serviceID = s.serviceID " +
                    "GROUP BY s.categoryID ORDER BY groupSum)" +
                    "SELECT CAST(name AS VARCHAR(10)) AS category,groupsum FROM tmp JOIN categories as c ON tmp.categoryid = c.categoryid " +
                    "WHERE groupsum = (SELECT MAX(groupsum) FROM tmp);";
            try {
                //could ask for a specific month also
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    System.out.println("Category name: " + resultSet.getString(1) +
                            "\t\tTotal amount: " + resultSet.getString(2) + "$");
                }
                else{
                    System.out.println("There does not seem to be a proper set of data linked to the amount in the database");
                }
                resultSet.close();
                preparedStatement.close();
            }
            catch (SQLException e) {
                System.out.println("Error trying to query the category with most profit");
            }
        }
        else {
            System.out.println("Invalid option!\nQuitting the command.");
        }
    }
    //can also check which month of the year is most profitable
    private static class Date implements Comparable<Date>{
        private final int year;
        private final int month;
        private final int day;
        public Date(String date){
            String[] arr = date.split("-");
            this.year = Integer.parseInt(arr[0]);
            this.month = Integer.parseInt(arr[1]);
            this.day = Integer.parseInt(arr[2]);
        }
        //1 = this > date
        //-1 = this < date
        //0 = this == date
        @Override
        public int compareTo(Date date) {
            if (this.year > date.year) return 1;
            else if (this.year < date.year) return -1;
            else {
                if (this.month > date.month) return 1;
                else if (this.month < date.month) return -1;
                else {
                    return Integer.compare(this.day, date.day);
                }
            }
        }
    }
}

package users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

public class Schedule {
    private String days;
    private String startTime;
    private String endTime;

    public Schedule(String days, String startTime, String endTime){
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static void newSchedule(ServiceProvider serviceProvider, Connection connection, Scanner scanner) {
        scanner.nextLine();
        System.out.println("Please select what day of the week you are available");
        System.out.println("Monday : M, Tuesday : T, Wednesday : W, " +
                "Thursday : R, Friday : F, Saturday : S, Sunday : U");
        System.out.println("Example: MUFR");
        System.out.print("-> ");
        String days = scanner.nextLine().toUpperCase();
        //skip checking for formatting
        System.out.println("Please select a start time, in the form HH:MM:SS");
        System.out.print("-> ");
        String startTime = scanner.nextLine();
        //skip checking for formatting
        System.out.println("Please select an end time, in the form HH:MM:SS");
        System.out.print("-> ");
        String endTime = scanner.nextLine();
        //Schedules schedules = new Schedules(days, startTime, endTime);
        String userID = serviceProvider.getUserID(connection);
        String sql = "INSERT INTO Schedule (providerID,days,startTime,endTime) " +
                "VALUES (?,?,?,?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userID);
            preparedStatement.setString(2, days);
            preparedStatement.setString(3, startTime);
            preparedStatement.setString(4, endTime);
            preparedStatement.execute();
            System.out.println("Successfully added a new schedule");
        }
        catch (SQLException e) {
            System.out.println("Error occurred trying to prepare a statement for making a new service");
            System.out.println(e);
        }
    }

    public static LinkedList<Schedule> getSchedule(Connection connection, String serviceProviderID){
        //todo not done
        String sql = "SELECT days,startTime,endTime FROM Schedule WHERE providerID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, serviceProviderID);
            LinkedList<Schedule> schedules = new LinkedList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Schedule schedule = new Schedule(resultSet.getString(1),resultSet.getString(2),
                                    resultSet.getString(3));
                schedules.add(schedule);
            }
            return schedules;
        }
        catch (SQLException e) {
                return null;
        }
    }
}

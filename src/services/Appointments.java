package services;

import users.Client;
import users.Schedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

public class Appointments {
    String scheduleID;
    String serviceID;
    String clientID;
    String status;
    String appointDate;
    String time;

    public Appointments(Schedule schedule, String serviceID, String clientID, String date){
        this.scheduleID = schedule.getScheduleId();
        this.serviceID = serviceID;
        this.time = schedule.getTimes()[0];
        this.clientID = clientID;
        this.status = "Pending";
        //must be a date
        this.appointDate = date;

    }

    public static void newAppointmentMenu(String clientId, Connection connection, Scanner scanner){
        //todo not done
        scanner.nextLine();
        int choice;
        //boolean running = true;
        while (true){
            System.out.println("Please select an available service id that you would like to book");
            System.out.print("If you are not sure, type '-1' to browse and then choose -> ");
            choice = scanner.nextInt();
            if (choice == -1) {
                // browse menu
                Service.browseServices(connection, scanner);
            }
            else{
                String[] results = Service.getServiceAvailabilityById(connection, choice);
                if (results == null) System.out.println("The given id does not exist!");
                else {
                    if (results[0].equals("1")) {
                        LinkedList<Schedule> schedules = Schedule.getSchedule(connection, results[1]);
                        if (schedules == null){
                            //todo an error occurred
                            System.out.println("There was a problem with the database trying to fetch the available schedules");
                        }
                        else{
                            if (schedules.isEmpty()){
                                System.out.println("There is unfortunately no schedule created by the service provider at the moment. Please select a different service id");
                            }
                            selectSchedule(choice, clientId, schedules, connection, scanner);
                        }
                        break;
                    }
                    else System.out.println("This service is not available!");
                }
            }
        }
        //check schedule
    }
    private static void selectSchedule(int serviceID, String clientID, LinkedList<Schedule> schedules, Connection connection, Scanner scanner){
        int counter = 1;
        for (Schedule schedule : schedules){
            System.out.println(counter + ". " + schedule);
            counter++;
        }
        System.out.print("Please select one of these schedules, or type -1 to cancel the process -> ");
        int choice = scanner.nextInt();
        if (choice == -1)
                System.out.println("Cancelling the process");
        else{
            try{
                scanner.nextLine();
                Schedule chosen = schedules.get(choice - 1);
                System.out.println(chosen);
                System.out.print("Please select a day of the week you would like to book\n" +
                                "Input the date in the numerical form YYYY-MM-DD-> ");
                //must be only one day
                String chosenDay = scanner.nextLine();
                /*char day = chosenDay.toUpperCase().charAt(0);
                String availableDays = chosen.getDays();
                for (int i = 0; i < availableDays.length(); i++){
                    if (day == availableDays.charAt(i)){
                        //insert new appointment to db
                        //update the available schedule for serviceprovider
                        return;
                    }
                }*/
                Appointments appointments = new Appointments(chosen, String.valueOf(serviceID), clientID, chosenDay);
                addAppnmtToDb(connection, appointments);
                //System.out.println("You selected a wrong day! Cancelling the process");
                //chosen.updateScheduleDb();
            }
            catch (IndexOutOfBoundsException ie){
                System.out.println("You provided an unavailable schedule!");
                System.out.println("Cancelling the process");
            }
        }
    }

    public static void addAppnmtToDb(Connection connection, Appointments appnmt){
        String sql = "INSERT INTO Appointments (scheduleID,serviceID, clientID, status, appointDate, time) " +
                "VALUES (?,?,?,?,?,?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, appnmt.scheduleID);
            preparedStatement.setString(2, appnmt.serviceID);
            preparedStatement.setString(3, appnmt.clientID);
            preparedStatement.setString(4, appnmt.status);
            preparedStatement.setString(5, appnmt.appointDate);
            preparedStatement.setString(6, appnmt.time);
            preparedStatement.execute();
            System.out.println("Successfully created a new appointment");
            preparedStatement.close();
        }
        catch (SQLException e) {
            System.out.println("An error occurred trying to insert a new appointment into the database");
            System.out.println("Error code: " + e.getErrorCode() + "\nSQL state: " + e.getSQLState());
        }
    }
}

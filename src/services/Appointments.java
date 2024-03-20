package services;

import users.Client;
import users.Schedule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Scanner;

public class Appointments {

    public static void newAppointmentMenu(Client client, Connection connection, Scanner scanner){
        //todo not done
        scanner.nextLine();
        int choice;
        while (true){
            System.out.println("Please select an available service id that you would like to book");
            System.out.print("If you are not sure, type '-1' to browse and then choose -> ");
            choice = scanner.nextInt();
            if (choice == -1) {
                //browse menu
                Service.browseServices(connection, scanner);
            }
            else{
                //check if the service exists
                String[] results = Service.getServiceAvailabilityById(connection, choice);
                if (results == null) System.out.println("The given id does not exist!");
                else {
                    if (results[0].equals("1")) {
                        LinkedList<Schedule> schedules = Schedule.getSchedule(connection, results[1]);
                        if (schedules == null){
                            //todo an error occured
                        }
                        else{

                        }
                        break;
                    }
                    else System.out.println("This service is not available!");
                }
            }
        }
        //check schedule

    }
}

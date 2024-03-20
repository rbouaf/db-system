package services;

import users.ServiceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

public class Service {
    private String name;
    private String description;
    private float priceRate;
    boolean availability;
    int numTimesProvided;
    float rating;
    int avgTimeToComplete;
    ServiceProvider serviceProvider;
    //categoryID INTEGER NOT NULL,
    //need to check all the services and how many different categories

    public Service(String name, String description, float priceRate, ServiceProvider serviceProvider){
        this.name = name;
        this.description = description;
        this.priceRate = priceRate;
        this.availability = true;
        this.numTimesProvided = 0;
        this.rating = 0f;
        this.avgTimeToComplete = 0;
        this.serviceProvider = serviceProvider;
    }

    public static void newService(ServiceProvider serviceProvider, Connection connection, Scanner scanner){
        System.out.print("Please name your new service -> ");
        String name = scanner.nextLine();
        //check if that service from that service provider already exists
        //for the moment not a condition in the database
        System.out.print("Please give a description of your service -> ");
        String description = scanner.nextLine();
        System.out.print("Set the price rate of your service (decimal) -> ");
        float priceRate = Float.parseFloat(scanner.nextLine());
        //create a new category if not available
        printCategories(displayCategories(connection));
        //or display a list of all possible categories
        Service service = new Service(name, description, priceRate, serviceProvider);
        service.storeInDb(connection);
    }

    private static LinkedList<String> displayCategories(Connection connection){
        String sql = "SELECT name FROM Categories;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            LinkedList<String> list = new LinkedList<>();
            while(resultSet.next()){
                String name = resultSet.getString(1);
                list.add(name);
            }
            return list;
        }
        catch (SQLException se){
            //todo
        }
        return null;
    }

    private static void printCategories(LinkedList<String> list){
        if (list == null) return;
        for (String item : list)
            System.out.println(item);
    }

    private void storeInDb(Connection connection){
        String sql = "INSERT INTO Services () VALUES ()";
    }
}

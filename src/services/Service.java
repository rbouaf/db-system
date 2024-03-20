package services;

import users.ServiceProvider;

import java.sql.*;
import java.util.Arrays;
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
    String serviceProviderID;
    String categoryID;
    //need to check all the services and how many different categories

    public Service(String name, String description, float priceRate, String categoryID, String serviceProviderID){
        this.name = name;
        this.description = description;
        this.priceRate = priceRate;
        this.availability = true;
        this.numTimesProvided = 0;
        this.rating = 0f;
        this.avgTimeToComplete = 0;
        this.serviceProviderID = serviceProviderID;
        this.categoryID = categoryID;
    }

    public static void newService(ServiceProvider serviceProvider, Connection connection, Scanner scanner){
        scanner.nextLine();
        System.out.print("Please name your new service -> ");
        String name = scanner.nextLine();
        //check if that service from that service provider already exists
        //for the moment not a condition in the database
        System.out.print("Please give a description of your service -> ");
        String description = scanner.nextLine();
        System.out.print("Set the price rate of your service (decimal) -> ");
        float priceRate;
        try {
            priceRate = Float.parseFloat(scanner.nextLine());
        }
        catch (NumberFormatException ne){
            System.out.println("Could not parse the floating point for price");
            System.out.println("Aborting");
            return;
        }
        System.out.println();
        LinkedList<String> list = getCategoriesFromDatabase(connection);
        String str = getCategoriesAsStr(list);
        System.out.println(str);
        LinkedList<String> choices = new LinkedList<>();
        while (true) {
            System.out.print("""
                    Select categories that are related to your service.
                    You may choose more than 1 (separate them with a space). e.g. '3 1 2'
                    Select only 0 if you wish to first create a new category.
                    ->\s""");
            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                String category = addNewCategoryToDb(connection, scanner);
                if (category != null) choices.add(category);
            }
            else{
                //they are selecting the index + 1 of list var
                LinkedList<String> parsed = parseChoice(list, choice);
                //also, choices must be unique (not picking twice #1 for e.g.)
                //for debugging purposes
                if (parsed != null) {
                    choices.addAll(parsed);
                    break;
                }
            }
        }
        for (String category : choices) {
            String categoryID = getCategoryId(connection, category);
            if (categoryID == null){
                System.out.println("An error occurred retrieving categoryID\nAborting...");
                break;
            }
            Service service = new Service(name, description, priceRate,
                    categoryID, serviceProvider.getUserID(connection));
            service.storeInDb(connection);
        }
        System.out.println("Successfully stored services in the database for all selected categories");
    }

    private static String addNewCategoryToDb(Connection connection, Scanner scanner){
        System.out.print("Give a name to your category -> ");
        String name = scanner.nextLine().toUpperCase();
        String sql = "INSERT INTO Categories (name) VALUES (?);";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            System.out.println("Successfully added a new category in the database.");
            return name;
        }
        catch (SQLException se){
            //todo
            System.out.println("Error in adding a new category in the database");
            System.out.println(se);
            return null;
        }
    }

    private static LinkedList<String> getCategoriesFromDatabase(Connection connection){
        String sql = "SELECT name FROM Categories;";
        LinkedList<String> list = new LinkedList<>();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String name = resultSet.getString(1);
                list.add(name);
            }
        }
        catch (SQLException se){
            //todo
        }
        return list;
    }

    private static String getCategoriesAsStr(LinkedList<String> list){
        if (list == null) return null;
        String str = "";
        int counter = 1;
        for (String item : list) {
            str += (counter + "." + item + "\n");
            counter++;
        }
        return str;
    }

    private static LinkedList<String> parseChoice(LinkedList<String> list, String choices){
        String[] arrChoices = choices.split("\\s");
        LinkedList<String> toReturn = new LinkedList<>();
        for (String index : arrChoices){
            try{
                //num given doesn't start at 0
                String category = list.get(Integer.parseInt(index) - 1);
                toReturn.add(category);
                if (category == null) throw new NullPointerException();
            }
            catch (NullPointerException ne){
                System.out.println("You must select a choice being an integer!");
                return null;
            }
            catch (IndexOutOfBoundsException ie){
                System.out.println("You chose an invalid value!");
                return null;
            }
        }
        return toReturn;
    }

    private void storeInDb(Connection connection){
        String sql = "INSERT INTO Services (name,description,pricerate,availability," +
                "numTimesProvided,rating,avgTimeToComplete,categoryID,providerID) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        //inefficient way of querying again for categories
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, this.name);
            preparedStatement.setString(2, this.description);
            preparedStatement.setString(3, String.valueOf(this.priceRate));
            preparedStatement.setString(4, String.valueOf(this.availability));
            preparedStatement.setString(5, String.valueOf(this.numTimesProvided));
            preparedStatement.setString(6, String.valueOf(this.rating));
            preparedStatement.setString(7, String.valueOf(this.avgTimeToComplete));
            preparedStatement.setString(8, this.categoryID);
            preparedStatement.setString(9, this.serviceProviderID);
            preparedStatement.execute();
            //may need to remove bcz in a loop
            //System.out.println("Successfully saved the service");
        }
        catch (SQLTimeoutException te){
            System.out.println("Bad connection, could not save the service to the database");
            //must shutdown at that time
        }
        catch (SQLException se){
            //todo
        }
    }

    private static String getCategoryId(Connection connection, String category) {
        String sql = "SELECT categoryID FROM categories WHERE name = ?;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, category);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        }
        catch (SQLTimeoutException te){
            System.out.println("Bad connection, could not get the id of the category");
            return null;
        }
        catch (SQLException se){
            //todo
            return null;
        }
    }
}

package services;

import users.ServiceProvider;

import java.sql.*;
import java.util.*;

public class Service {
    //may be null
    private String serviceId;
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
    String categoryName;
    private static LinkedList<Service> availableServices = new LinkedList<>();

    public Service(String name, String description, float priceRate, String categoryID, String serviceProviderID){
        this.serviceId = null;
        this.name = name;
        this.description = description;
        this.priceRate = priceRate;
        this.availability = true;
        this.numTimesProvided = 0;
        this.rating = 0f;
        this.avgTimeToComplete = 0;
        this.serviceProviderID = serviceProviderID;
        this.categoryID = categoryID;
        this.categoryName = null;
    }

    private Service(String name, String description, float priceRate, int numTimesProvided, float rating, int avgTimeToComplete, String categoryID, String serviceProviderID){
        this.name = name;
        this.description = description;
        this.priceRate = priceRate;
        this.availability = true;
        this.numTimesProvided = numTimesProvided;
        this.rating = rating;
        this.avgTimeToComplete = avgTimeToComplete;
        this.serviceProviderID = serviceProviderID;
        this.categoryID = categoryID;
    }

    public Service(String serviceId, String name, String description, float priceRate, int numTimesProvided, float rating, int avgTimeToComplete, String categoryID, String categoryName, String serviceProviderID){
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.priceRate = priceRate;
        this.availability = true;
        this.numTimesProvided = numTimesProvided;
        this.rating = rating;
        this.avgTimeToComplete = avgTimeToComplete;
        this.serviceProviderID = serviceProviderID;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }


    public static void browseServices(Connection connection, Scanner scanner){
        fetchAvailableServiceList(connection, "");
        printAvailableServices();
        System.out.print("\nChoose your next action:\n1- Filter Services by Category | 2- Refresh List | 3- Go Back to User Menu\n-> ");

        while(true){
            int c = scanner.nextInt();
            switch (c){
                case 1:
                    String idnamecatsSQL = "SELECT categoryID, name FROM Categories;";

                    System.out.println("Choose from these categories' IDs to filter the services. You may choose more than 1 (separate them with a space). e.g. '3 1 2' ");
                    System.out.println("Categories:");
                    System.out.println("------------");
                    scanner.nextLine();

                    try{
                        PreparedStatement categoriesDetails = connection.prepareStatement(idnamecatsSQL);
                        ResultSet categoriesResults = categoriesDetails.executeQuery();
                        while(categoriesResults.next()){
                            System.out.println(categoriesResults.getString("categoryID") +"- " + categoriesResults.getString("name"));
                        }
                    } catch (SQLException e) {
                        System.out.println("Failed to load the categories with their ids");
                    }

                    System.out.print("-> ");
                    String categories = scanner.nextLine();
                    fetchAvailableServiceList(connection,categories);
                    System.out.println("Results for categories: " + categories.replaceAll(" ", ","));
                    printAvailableServices();

                    System.out.print("\nChoose your next action:\n1- Filter Services by Category | 2- Refresh List | 3- Go Back to User Menu\n-> ");
                    break;
                case 2:
                    fetchAvailableServiceList(connection, "");
                    printAvailableServices();
                    System.out.print("\nChoose your next action:\n1- Filter Services by Category | 2- Refresh List | 3- Go Back to User Menu\n-> ");

                    break;
                case 3:
                    // Goes back to user menu
                    return;
                default:
                    System.out.println("Invalid input - Please try again\n");
                    printAvailableServices();
                    System.out.print("\nChoose your next action:\n1- Filter Services by Category | 2- Refresh List | 3- Go Back to User Menu\n-> ");

                    break;
            }
        }
    }

    public static void fetchAvailableServiceList(Connection connection, String categoryID){
        String fetchServicesSQL;
        String[] categories = categoryID.isEmpty() ? new String[0] : categoryID.split(" ");
        if(categories.length == 0)
            fetchServicesSQL = "SELECT s.serviceid,s.name, s.description, s.pricerate, s.numTimesProvided, s.rating, s.avgTimeToComplete, " +
                "c.name AS category,c.categoryid, s.providerID FROM Services AS s JOIN Categories AS c ON s.categoryid = c.categoryid " +
                    "WHERE s.availability = \"TRUE\" ;";
        else{
            fetchServicesSQL = "SELECT s.serviceid,s.name, s.description, s.pricerate, s.numTimesProvided, s.rating, s.avgTimeToComplete, " +
                    "c.name AS category,c.categoryid, s.providerID FROM Services AS s JOIN Categories AS c ON s.categoryid = c.categoryid " +
                    "WHERE availability = \"TRUE\" AND " +
                    "c.categoryID = ? OR".repeat(categories.length -1) + " c.categoryID = ?;";
        }

        //System.out.println(fetchServicesSQL);
        availableServices.clear();
        try{
            PreparedStatement fetchStatement = connection.prepareStatement(fetchServicesSQL);
            for (int i = 0; i < categories.length; i++){
                fetchStatement.setString(i+1, categories[i]);
            }
            ResultSet servicesResult = fetchStatement.executeQuery();
            while(servicesResult.next()){
                availableServices.add(new Service(
                        servicesResult.getString("serviceid"),
                        servicesResult.getString("name"),
                        servicesResult.getString("description"),
                        Float.parseFloat(servicesResult.getString("pricerate")),
                        Integer.parseInt(servicesResult.getString("numTimesProvided")),
                        Float.parseFloat(servicesResult.getString("rating")),
                        Integer.parseInt(servicesResult.getString("avgTimeToComplete")),
                        servicesResult.getString("category"),
                        servicesResult.getString("categoryid"),
                        servicesResult.getString("providerID")));
            }

        } catch (SQLException e) {
            System.out.println("An error occured while updating services");
        } catch (Exception e){
            System.out.println("An error occured while fetching new service");
        }
    }

    public static void printAvailableServices(){
        System.out.println("List of Available Services:");
        System.out.println("----------------------------");
        System.out.println("serviceId\t | name\t\t\t\t| description\t\t\t| priceRate | numTimesProvided | rating | avgTimeToComplete | categoryId\t\t| categoryName\t| serviceProviderID");
        for (Service s : availableServices) {
            System.out.println(s);
        }
        System.out.print("\nChoose your next action:\n1- Filter Services by Category | 2- Refresh List | 3- Go Back to previous Menu\n-> ");
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

    public static void updateService(ServiceProvider serviceProvider, Connection connection, Scanner scanner){
        //TODO
        scanner.nextLine();
        //perhaps query first, then list, and then ask which one to do
        System.out.print("Please name the service you would like to update -> ");
        boolean runnning = true;
        while (runnning) {
            String name = scanner.nextLine();
            String sql = "SELECT serviceID FROM Services WHERE EXISTS " +
                    "(SELECT * FROM Services WHERE name = ? AND providerID = ?);";
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, serviceProvider.getUserID(connection));
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String serviceID = resultSet.getString(1);
            }
            catch (SQLTimeoutException te){
                System.out.println("You are disconnected");
                break;
            }
            catch (SQLException se) {
                System.out.println("Could not properly fetch existing services");
                break;
            }
        }
    }

    public static void getOverallRating(ServiceProvider serviceProvider, Connection connection){
        String userID = serviceProvider.getUserID(connection);
        String sql = "SELECT AVG(*) FROM ";
        //todo
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
            System.out.println("Error code: " + se.getErrorCode() + "\nSQL state: " + se.getSQLState());
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
            System.out.println("Error code: " + se.getErrorCode() + "\nSQL state: " + se.getSQLState());
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

    public static String[] getServiceAvailabilityById(Connection connection, int id){
        //must be in the database
        //must be available
        String sql = "SELECT availability,providerID FROM Services WHERE serviceID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(id));
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return new String[]{resultSet.getString(1), resultSet.getNString(2)};
        }
        catch (SQLException e) {
            return null;
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
            System.out.println("There was an issue with the provided category ID");
            return null;
        }
    }

    @Override
    public String toString() {
        return matchLength(serviceId, 10) + "    " +
                matchLength(name, 16) + "   " +
                matchLength(description, 21)  + "    " +
                matchLength(String.format("%.2f", priceRate), 10) + " " +
                matchLength(String.format("%d", numTimesProvided), 16)+ "   " +
                matchLength(String.format("%.2f", rating), 6) + "   " +
                matchLength(String.format("%d", avgTimeToComplete), 17)+ "   " +
                matchLength(categoryName, 20) + "   " +
                matchLength(categoryID, 10) + "   " +
                matchLength(serviceProviderID, 17);
    }

    private static String matchLength(String str, int len){
        if(str.length() > len)
            return str.substring(0, len - 3) + "...";
        else if(str.length() == len) return str;
        else{
            int spaceCount = len - str.length();
            return str + " ".repeat(spaceCount);
        }
    }

    public static void leaveAReview(Connection connection, Scanner scanner, String clientID) {
//TODO: how to get the clientID
        try {
            // List of services the user has used
            System.out.println("Loading services you have used...");
            String fetchServicesSQL = "SELECT a.issueDate, a.invoiceID, s.serviceID, s.name FROM Services s, Invoices a WHERE s.serviceID = a.serviceID AND a.clientID = ? AND NOT EXISTS (SELECT r.invoiceID FROM Reviews r WHERE a.invoiceID = r.invoiceID)";
//            String fetchServicesSQL = "SELECT s.serviceID, s.name FROM Services s INNER JOIN Invoices a ON s.serviceID = a.serviceID WHERE a.clientID = ?";
            PreparedStatement stmt = connection.prepareStatement(fetchServicesSQL);
            stmt.setString(1, clientID);
            ResultSet rs = stmt.executeQuery();

            HashMap<String, String> invoiceToService = new HashMap<>();
//            int serviceCount = 0;
            System.out.println("Invoice ID | Issue Date | Service ID | Service Name");
            while (rs.next()) {
                invoiceToService.put(rs.getString("invoiceID"), rs.getString("serviceID"));
                System.out.println(matchLength(rs.getString("invoiceID"), 11) + "  " +
                        matchLength(rs.getString("issueDate"), 11) + "  " +
                        matchLength(rs.getString("serviceID"), 11) + "  " +
                        rs.getString("name"));
            }

            if(invoiceToService.size() > 0){
                // Asking the user to select a service to review from the services above
                //if null, they didnt receive service so they should not review,  i donk know how
                System.out.print("Enter the Invoice ID of the service you would like to review from above: ");
                String invoiceId = scanner.nextLine();
                // Getting the rating
                System.out.print("Enter your rating for the service (1 to 10): ");
                int rating = scanner.nextInt();
                scanner.nextLine();
                // Asking for the optional review text
                System.out.print("Enter your review (optional, hit enter to skip): ");
                String userComment = scanner.nextLine();
                // Inserting review into Reviews
                String insertReviewSQL = "INSERT INTO Reviews (clientID, serviceID, invoiceID, rating, userComment) VALUES (?, ?, ?, ?, ?);";
                PreparedStatement insertStmt = connection.prepareStatement(insertReviewSQL);
                insertStmt.setString(1, clientID);
                insertStmt.setString(2, invoiceToService.get(invoiceId));
                insertStmt.setString(3, invoiceId);
                insertStmt.setInt(4, rating);
                insertStmt.setString(5, userComment.isEmpty() ? null : userComment);//userComment could be empty since they can hit enter
                insertStmt.executeUpdate();
                System.out.println("Thank you for your review!\n");
            }
            else{
                System.out.println("You have no services to review\n");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while leaving a review: " + e.getMessage());
        }
    }
}

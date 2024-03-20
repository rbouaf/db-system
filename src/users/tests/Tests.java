package users.tests;

import users.Client;
import users.User;

import java.sql.Connection;
import java.sql.SQLException;

public class Tests {
    private Tests(){}

    public static Client test1(){
        return new Client("wfv@efwv", "93r4g8ytuieyhbj", "GSP",
                "30498", "34234", "324", "3453142");
    }
    public static void test2(Connection connection){
        try {
            User.getUserInfo(connection, "wfv@efwv");
        }
        catch (SQLException se){
            //todo
            System.out.println("Test 2 exception occurred when retrieving user info");
            System.out.println(se);
        }
    }
}

package fi.ooproject;

import java.io.*;
import java.sql.*;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Backend resources.
 * 
 * Handles HTTP calls from clients and saving to persistent storage (database).
 *
 * @author Juuso Pakarinen
 * @version 2016.1115
 * @since 1.8
 */
@Path("/list") 
public class ShoppingResource implements Serializable {
    
    /**
     * MYSQL database name.
     */
    private String db;
    
    /**
     * MYSQL username.
     */
    private String user;
    
    /**
     * MYSQL password.
     */
    private String password;

    /**
     * Constructor.
     * 
     * Reads "db.propeties" file.
     */
    public ShoppingResource() {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        try (InputStream input = loader.getResourceAsStream("db.properties")) {
            Class. forName( "com.mysql.jdbc.Driver" );
            properties.load(input);
            db = properties.getProperty("database");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns names of the tables found in the database.
     * 
     * @return JSON object containing info about query's success and
     *         array contining names of the tables if any found, error message
     *         if not.
     */
    @GET
    @Produces("application/json")
    public String getListNames() {
        String result = "";
        
        try (Connection conn = DriverManager.getConnection(
                ("jdbc:mysql://localhost/" + db), user , password)) {
            try(Statement statement = conn.createStatement()) {
                DatabaseMetaData md = conn.getMetaData();
                ResultSet rs = md.getTables(null, null, "%", null);
                result = "{\"success\": true, \"msg\": [";
                
                while (rs.next()) {
                    /* Column 3 (from getTables()) is the TABLE_NAME */
                    result += ("\"" + rs.getString(3) + "\",");
                }
                
                /* Remove last comma */
                result = result.substring(0, result.length() - 1);
                result += "]}";
            }
        }  catch (SQLException e) {
            e.printStackTrace();
            return "{\"success\": false, \"msg\": "
                    + "\"Error retrieving list names\"}";
        }
        
        return result;
    }

    /**
     * Returns contents of queried MYSQL table containing shopping list data.
     * 
     * @param name name of the queried table.
     * @return JSON object containing info about query's success and
     *         array containing shop item objects if any found, error message
     *         if not.
     */
    @GET
    @Path("/{name}")
    @Produces("application/json")
    public String loadShopList(@PathParam("name") String name) {
        String result = "";
    
        try (Connection conn = DriverManager.getConnection(
                ("jdbc:mysql://localhost/" + db), user , password)) {
            try(Statement statement = conn.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM " + name);
                result = "{\"success\": true, \"msg\": [";
                
                while (rs.next()) {
                    result += ("{\"amount\":" + rs.getString("amount") 
                           + ", \"product\":\"" + rs.getString("product") 
                           + "\"},");
                }
                
                /* Remove last comma */
                result = result.substring(0, result.length() - 1);
                result += "]}";
            }
        }  catch (SQLException e) {
            e.printStackTrace();
            return "{\"success\": false, \"msg\": "
                    + "\"Error retrieving list from database\"}";
        }
        
        return result;
    }

    /**
     * Save POSTed shopping list content to database.
     * 
     * @param input POST body.
     * @return JSON object containing info about query's success and
     *         message.
     */
    @POST
    @Produces("application/json")
    public String saveShopList(String input) {

        try (Connection conn = DriverManager.getConnection(
                ("jdbc:mysql://localhost/" + db), user , password)) {
            try(Statement statement = conn.createStatement()) {
                conn.setAutoCommit(false); 
                JsonReader reader = Json.createReader(new StringReader(input));
                JsonObject object = reader.readObject();
                JsonArray jsonArr = object.getJsonArray("items");
                String table = object.getString("name");
                
                String dropQuery = ("DROP TABLE IF EXISTS " + table);
                String createQuery = ("CREATE TABLE IF NOT EXISTS "
                        + table + " ("
                        + "ID int NOT NULL AUTO_INCREMENT,"
                        + "Amount int,"
                        + "Product varchar(35),"
                        + "PRIMARY KEY (ID))");
                
                int dropStatus = statement.executeUpdate(dropQuery);
                int createStatus = statement.executeUpdate(createQuery);

                for (int i = 0; i < jsonArr.size(); i++) {
                    JsonObject tmp = jsonArr.getJsonObject(i);
                    int amount = tmp.getInt("amount");
                    String product = tmp.getString("product");
                    
                    String insertQuery = "INSERT INTO " + table 
                            + " VALUES (null, " + amount 
                            + ", \"" + product + "\")";
                    
                    int insertStatus = statement.executeUpdate(insertQuery);
                    
                    if (insertStatus < 0) {
                        conn.rollback();
                        return "{\"success\": false, \"msg\": "
                                + "\"Error saving list to database\"}";
                    }
                }
                
                conn.commit();
                reader.close();
                return "{\"success\": true, \"msg\": "
                        + "\"list added to database!\"}";
            }
        } catch (SQLException | JsonParsingException | EJBException e) {
            e.printStackTrace();
            return "{\"success\": false, \"msg\": \"POST data not JSON\"}";
        } 
    }
}

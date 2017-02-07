package fi.ooproject;

import javax.json.stream.JsonParsingException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * Handles RESTful communication between application (client) and server.
 *
 * @author Juuso Pakarinen
 * @version 2016.1218
 * @since 1.8
 */
public class ServerConnection {

    /**
     * List containing data and functions for loading and saving the list.
     */
    private ShoppingList list;

    /**
     * CLient object.
     */
    private Client client;

    /**
     * The base URL for REST calls.
     */
    private WebTarget resourceTarget;

    /**
     * Stores the name of selected list.
     *
     * Used while {@link fi.ooproject.ShoppingGUI#openRemoteLoad(boolean)}
     * loading from remote storage.}
     */
    private String targetList;

    /**
     * Constructor.
     *
     * @param list instance of shopping list.
     */
    public ServerConnection(ShoppingList list) {
        this.list = list;
        client = ClientBuilder.newClient();
        WebTarget rootTarget =
                client.target("http://localhost:8080/ShoppingList/api");
        resourceTarget = rootTarget.path("list");
        targetList = "";
    }

    /**
     * Returns an array containing the names of found tables in MYSQL database.
     *
     * @return an array containing the names of found tables, error message if
     *         query to database failed at backend or null if connection
     *         failed.
     */
    public String[] getListNames() {
        String[] listnames;
        Invocation.Builder builder =
                resourceTarget.request(MediaType.APPLICATION_JSON);

        try {
            Response response = builder.get();
            JsonReader reader = Json.createReader(
                    new StringReader(response.readEntity(String.class)));
            JsonObject object = reader.readObject();

            if (object.getBoolean("success")) {
                JsonArray jsonArr = object.getJsonArray("msg");
                listnames = new String[jsonArr.size()];

                for(int i = 0; i < jsonArr.size(); i++) {
                    listnames[i] = jsonArr.get(i).toString();
                }

            } else {
                return new String[0];
            }

            return listnames;
        } catch (ProcessingException  e) {
            System.out.println("Unable to connect to the server.");
            return new String[0];
        } catch (JsonParsingException e) {
            System.out.println("Bad response.");
            return new String[0];
        }
    }

    /**
     * Makes GET request to the server.
     *
     * Requests list of {@link fi.ooproject.ShopItem items} stored in certain
     * table in database. If valid list is returned, adds items to the shopping
     * list.
     *
     * @param listName table from which the items are queried from.
*      @param combine whether the content of read file is appended
     *                to current shop list or overwritten.
     * @return whether server call was success and received items were added to
     *         the shopping list.
     */
    public boolean loadList(String listName, boolean combine) {
        WebTarget target = resourceTarget.path(listName);
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

        try {
            Response response = builder.get();
            JsonReader reader = Json.createReader(
                    new StringReader(response.readEntity(String.class)));
            JsonObject object = reader.readObject();

            if (object.getBoolean("success")) {
                JsonArray jsonArr = object.getJsonArray("msg");

                if (!combine) {
                    list.clearList();
                }

                for(int i = 0; i < jsonArr.size(); i++) {
                    System.out.println(jsonArr.get(i).toString());
                    reader = Json.createReader(
                            new StringReader(jsonArr.get(i).toString()));
                    JsonObject tmp = reader.readObject();
                    list.addItem(new ShopItem(tmp.getString("product"),
                            tmp.getInt("amount")));
                }

                return true;

            } else {
                System.out.println(object.getString("msg"));
                return false;
            }
        } catch (ProcessingException  e) {
            System.out.println("Unable to connect to the server.");
            return false;
        } catch (JsonParsingException e) {
            System.out.println("Bad response.");
            return false;
        }
    }

    /**
     * Makes POST request to the server.
     *
     * Sends the current state of the shopping list to server, so it could be
     * saved in certain table in database.
     *
     * @param listName name for created table in database.
     * @return whether server call was successful.
     */
    public boolean saveList(String listName) {
        String body = "{\"name\": \"" + listName + "\", \"items\": [";

        for (int i = 0; i < list.size(); i++) {
            ShopItem item = list.getItem(i);
            body += "{\"amount\": " + item.getQuantity()
                    + ", \"product\": \"" + item.getName() + "\"},";
        }

        /* Remove last comma */
        body = body.substring(0, body.length() - 1);
        body += "]}";

        try {
            Response response = resourceTarget
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON));
            JsonReader reader = Json.createReader(
                    new StringReader(response.readEntity(String.class)));
            JsonObject object = reader.readObject();

            System.out.println(object.getString("msg"));
            return object.getBoolean("success");
        } catch (ProcessingException e) {
            System.out.println("Unable to connect to the server.");
            return false;
        } catch (JsonParsingException e) {
            System.out.println("Bad response.");
            return false;
        }
    }

    /**
     * Returns the name of selected list.
     *
     * @return the name of selected list.
     */
    public String getTargetList() {
        return targetList;
    }

    /**
     * Sets the name of selected list.
     *
     * @param targetList name of selected list.
     */
    public void setTargetList(String targetList) {
        this.targetList = targetList;
    }
}

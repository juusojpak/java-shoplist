package fi.ooproject;

import fi.ooproject.utils.Tools;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Command line interface for Shopping list application.
 *
 * @author Juuso Pakarinen
 * @version 2016.1109
 * @since 1.8
 */
public class ShoppingCLI {

    /**
     * Scanner for user input.
     */
    private Scanner scan;

    /**
     * List containing data and functions for loading and saving the list.
     */
    private ShoppingList shoplist;

    /**
     * Manages uploading files to Dropbox.
     */
    private DropboxManager dropbox;

    /**
     * Manages REST calls to backend.
     */
    private ServerConnection connection;

    /**
     * Stores text strings.
     */
    private ResourceBundle messages;

    /**
     * Constructor.
     */
    public ShoppingCLI() {
        dropbox = new DropboxManager();
        shoplist = new ShoppingList();
        scan = new Scanner(System.in);
        messages = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);
        connection = new ServerConnection(shoplist);
    }

    /**
     * Loops the app until exit command is issued.
     *
     * Checks user input for commands or new items to be added to the list.
     * Prints out current status of the shopping list.
     */
    public void run() {

        System.out.println(messages.getString("appTitle"));
        System.out.println(messages.getString("school"));

        while (true) {

            System.out.println(messages.getString("askInput"));
            String input = scan.nextLine();
            input = input.toLowerCase();
            String[] parts;

            if (input.length() > 0) {

                if (input.equals("quit") || input.equals("exit")) {
                    System.exit(0);
                } else if (input.equals("clear")) {
                    shoplist.clearList();
                } else if (input.equals("help")) {
                    System.out.println();
                    System.out.println(messages.getString("help1"));
                    System.out.println(messages.getString("help2cli"));
                    System.out.println(messages.getString("help3cli"));
                    System.out.println();
                    System.out.println(messages.getString("help4cli"));
                    System.out.println(messages.getString("command1"));
                    System.out.println(messages.getString("command2"));
                    System.out.println(messages.getString("command3"));
                    System.out.println(messages.getString("command4"));
                    System.out.println(messages.getString("command5"));
                    System.out.println(messages.getString("command6"));
                    System.out.println(messages.getString("command7"));
                    System.out.println(messages.getString("command8"));
                    System.out.println(messages.getString("command9"));
                    System.out.println(messages.getString("command10"));
                    System.out.println(messages.getString("command11"));
                } else if (input.matches("^(remove [^\\s]+)$")) {
                    parts = input.split(" ");
                    ShopItem tmp = null;
                    boolean found = false;

                    for (int i = 0; i < shoplist.size(); i++) {
                        if (shoplist.getItem(i).getName().equals(parts[1])) {
                            tmp = shoplist.getItem(i);
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        shoplist.removeItem(tmp);
                        System.out.println(tmp.getName() + " "
                                + messages.getString("removed"));
                    }
                } else if (input.matches("^(load [^\\s]+)$")) {
                    parts = input.split(" ");

                    if (shoplist.loadFile(parts[1], false)) {
                        System.out.println(messages.getString("loadComp"));
                    } else {
                        System.out.println(messages.getString("loadFail"));
                    }
                } else if (input.matches("^(combine [^\\s]+)$")) {
                    parts = input.split(" ");

                    if (shoplist.loadFile(parts[1], true)) {
                        System.out.println(messages.getString("loadComp"));
                    } else {
                        System.out.println(messages.getString("loadFail"));
                    }
                } else if (input.matches("^(save [^\\s]+)$")) {
                    parts = input.split(" ");

                    if (shoplist.saveFile(parts[1])) {
                        System.out.println(messages.getString("saveComp")
                                + " " + parts[1]);
                    } else {
                        System.out.println(messages.getString("saveFail"));
                    }
                } else if (input.equals("server list")) {
                    String[] names = connection.getListNames();

                    if (names.length > 0) {
                        for (int i = 0; i < names.length; i++) {
                            names[i] = names[i].replace("\"", "");
                            System.out.println(names[i]);
                        }
                    }
                } else if (input.matches("^(server load [^\\s]+)$")) {
                    parts = input.split(" ");

                    if (connection.loadList(parts[2], false)) {
                        System.out.println(messages.getString("servLoadComp"));
                    } else {
                        System.out.println(messages.getString("servLoadFail"));
                    }
                } else if (input.matches("^(server combine [^\\s]+)$")) {
                    parts = input.split(" ");

                    if (connection.loadList(parts[2], true)) {
                        System.out.println(messages.getString("servLoadComp"));
                    } else {
                        System.out.println(messages.getString("servLoadFail"));
                    }
                } else if (input.matches("^(server save [^\\s]+)$")) {
                    parts = input.split(" ");

                    if (connection.saveList(parts[2])) {
                        System.out.println(messages.getString("servSaveComp"));
                    } else {
                        System.out.println(messages.getString("servSaveFail"));
                    }
                } else if (input.equals("dropbox")) {
                    if (!dropbox.isAuthenticated()) {
                        System.out.println(messages.getString("dboxHelp1")
                                + " " + dropbox.getAuthorizeURL());
                        System.out.println(messages.getString("dboxHelp2"));
                        System.out.println(messages.getString("dboxHelp3"));
                        String code = scan.nextLine().trim();
                        dropbox.authenticate(code);
                    }

                    System.out.println(messages.getString("dboxHelp4"));
                    String fileName = scan.nextLine().trim();

                    if (dropbox.upload(shoplist, fileName)) {
                        System.out.println(messages.getString("uploadComp"));
                    }
                } else {

                    try {

                        String[] listParts = input.split(";");

                        for (int i = 0; i < listParts.length; i++) {

                            if (listParts[i].length() > 0
                                    && !(Tools.isEmpty(listParts[i]))) {

                                if (listParts[i].charAt(0) == ' ') {
                                    listParts[i] = listParts[i].substring(1);
                                }

                                String[] itemParts = listParts[i].split(" ");
                                shoplist.addItem(new ShopItem(itemParts[1],
                                        Integer.parseInt(itemParts[0])));
                            }
                        }
                    } catch (NumberFormatException
                            | ArrayIndexOutOfBoundsException e) {
                        System.out.println(messages.getString("appuse"));
                        System.out.println(messages.getString("example"));
                        e.printStackTrace();
                    }
                }
            }

            System.out.println();
            System.out.println(messages.getString("listState"));

            for (int i = 0; i < shoplist.size(); i++) {
                System.out.println("  " + shoplist.getItem(i).getQuantity()
                        + " " + shoplist.getItem(i).getName());
            }

            System.out.println();
        }
    }

    /**
     * Creates new instance of {@link ShoppingCLI} and runs it.
     *
     * @param args Command line arguments. Not used.
     */
    public static void main(String[] args) {
        ShoppingCLI cli = new ShoppingCLI();
        cli.run();
    }
}

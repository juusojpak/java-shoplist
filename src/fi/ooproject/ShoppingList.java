package fi.ooproject;

import fi.ooproject.utils.MyLinkedList;
import fi.ooproject.utils.Tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Shopping list logic.
 *
 * This class contains methods for list operations and
 * writing to/reading from a file.
 *
 * @author Juuso Pakarinen
 * @version 2016.1109
 * @since 1.8
 */
public class ShoppingList {

    /**
     * Linked list to store {@link fi.ooproject.ShopItem shop items}.
     */
    private MyLinkedList<ShopItem> list;

    /**
     * Returns shop list object.
     *
     * @return shop list object.
     */
    public MyLinkedList<ShopItem> getList() {
        return list;
    }

    /**
     * Returns the number of stored items in the list.
     *
     * @return number of stored items in the list.
     */
    public int size() {
        return list.size();
    }

    /**
     * Constructor.
     */
    public ShoppingList() {
        list = new MyLinkedList<>();
    }

    /**
     * Adds {@link fi.ooproject.ShopItem an item} to shop list.
     *
     * Checks if list already has an item with same name in it. If such
     * item is found adds to that items quantity, if not adds item to list
     * as new item.
     *
     * @param item item to be added.
     */
    public void addItem(ShopItem item) {

        if (list.size() <= 0) {
            list.add(item);
        } else {

            boolean already = false;

            for (int i = 0; i < list.size(); i++) {
                ShopItem tmp = (ShopItem) list.get(i);

                if (tmp.getName().equals(item.getName())) {
                    tmp.setQuantity(tmp.getQuantity() + item.getQuantity());
                    already = true;
                }
            }

            if (!already) {
                list.add(item);
            }
        }
    }

    /**
     * Adds an array of {@link fi.ooproject.ShopItem items} to shop list.
     *
     * @param items array of shop items.
     */
    public void addItemList(ShopItem[] items) {

        for (ShopItem item : items) {

            if (list.size() <= 0) {
                list.add(item);
            } else {

                boolean already = false;

                for (int i = 0; i < list.size(); i++) {
                    ShopItem tmp = (ShopItem) list.get(i);

                    if (tmp.getName().equals(item.getName())) {
                        tmp.setQuantity(tmp.getQuantity() + item.getQuantity());
                        already = true;
                    }
                }

                if (!already) {
                    list.add(item);
                }
            }
        }
    }

    /**
     * Returns an item at given index from the list.
     *
     * @param index index of the queried item.
     * @return item at given index from the list.
     */
    public ShopItem getItem(int index) {
        return (ShopItem) list.get(index);
    }

    /**
     * Removes item from the list and returns whether the removal succeeded.
     *
     * @param item item to be removed.
     * @return whether the removal succeeded.
     */
    public boolean removeItem(ShopItem item) {

        if (list.remove(item)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Clears the list from saved items.
     */
    public void clearList() {
        list.clear();
    }

    /**
     * Loads list from file.
     *
     * Reads a file at given path and tries to convert it's contents to shop
     * list.
     *
     * @param src absolute path for reading file.
     * @param combine whether the content of read file is appended
     *                to current shop list or overwritten.
     * @return whether reading the file succeeded.
     */
    public boolean loadFile(String src, boolean combine) {

        try {

            if (!combine) {
                clearList();
            }

            Path path = new File(src).toPath();
            List<String> lines = Files.readAllLines(path,
                    Charset.defaultCharset());

            for (int i = 0; i < lines.size(); i++) {

                if (lines.get(i).length() > 0
                        && !(Tools.isEmpty(lines.get(i)))) {

                    String[] parts = lines.get(i).split(" ");

                    try {
                        addItem(new ShopItem(parts[1],
                                Integer.parseInt(parts[0])));
                    } catch (NumberFormatException
                            | ArrayIndexOutOfBoundsException e) {
                        System.out.println("Invalid file");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
            return false;
        } catch (InvalidPathException e) {
            System.out.println("Invalid path, use for example:" +
                    " C:/User/You/file.txt");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Writes current state of shop list to a text file.
     *
     * @param dest absolute path for writing file.
     * @return whether writing to the file succeeded.
     */
    public boolean saveFile(String dest) {

        try {

            List<String> lines = new ArrayList<>();
            String destination;

            for (int i = 0; i < list.size(); i++) {
                lines.add(list.get(i).toString());
            }

            if (dest.matches("(.*\\.txt)$")) {
                destination = dest;
            } else {
                destination = dest + ".txt";
            }

            Path path = Paths.get(destination);
            Files.write(path, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("Save failed");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

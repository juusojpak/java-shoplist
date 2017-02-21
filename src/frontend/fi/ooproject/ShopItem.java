package fi.ooproject;

/**
 * Class for representing items in the {@link ShoppingList shopping list}.
 *
 * @author Juuso Pakarinen
 * @version 2016.1109
 * @since 1.8
 */
public class ShopItem {

    /**
     * Name of the item.
     */
    private String name;

    /**
     * Item quantity.
     */
    private int quantity;

    /**
     * Returns the name of the item.
     *
     * @return name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     *
     * @param name new name for the item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns item quantity.
     *
     * @return item quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets item quantity.
     *
     * @param quantity new item quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Constructor.
     *
     * @param name item name.
     * @param quantity item quantity..
     */
    public ShopItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Returns a string containing item quantity and name.
     *
     * @return a string containing item quantity and name.
     */
    @Override
    public String toString() {
        return ("" + quantity + " " + name);
    }
}

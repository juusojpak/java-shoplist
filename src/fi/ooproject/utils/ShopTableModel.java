package fi.ooproject.utils;

import fi.ooproject.ShopItem;
import fi.ooproject.ShoppingList;

import javax.swing.table.AbstractTableModel;

/**
 * Table model for the table used in {@link fi.ooproject.ShoppingGUI}.
 *
 * @author Juuso Pakarinen
 * @version 2016.1115
 * @since 1.8
 */
public class ShopTableModel extends AbstractTableModel {

    /**
     * List containing data. Functions for loading and saving aren't used here.
     */
    private ShoppingList shoplist;

    /**
     * Column names.
     */
    private final String[] colNames = new String[]{"Amount", "Product"};

    /**
     * Column classes.
     */
    private final Class[] colClasses = new Class[]{Integer.class, String.class};

    /**
     * Constructor.
     *
     * @param shoplist list containing data.
     */
    public ShopTableModel(ShoppingList shoplist) {
        this.shoplist = shoplist;
    }

    /**
     * Returns a default name for the column.
     *
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc. If column cannot be found,
     * returns an empty string.
     *
     * @param colIndex the column being queried.
     * @return a string containing the default name of column.
     */
    @Override
    public String getColumnName(int colIndex) {
        return colNames[colIndex];
    }

    /**
     *  Returns <code>Object.class</code> regardless of columnIndex.
     *
     *  @param colIndex the column being queried.
     *  @return the Object.class.
     */
    @Override
    public Class <?> getColumnClass(int colIndex) {
        return colClasses[colIndex];
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return number of columns.
     */
    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return number of rows.
     */
    @Override
    public int getRowCount() {
        return shoplist.size();
    }

    /**
     * Returns the value for the cell at given row index and column index.
     *
     * @param rowIndex index of the row whose value is to be queried.
     * @param colIndex index of the column whose value is to be queried.
     * @return the value <code>Object</code> at the specified cell.
     */
    @Override
    public Object getValueAt(int rowIndex, int colIndex) {

        try {

            ShopItem row = shoplist.getItem(rowIndex);

            if (0 == colIndex) {
                return row.getQuantity();
            } else {
                return row.getName();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  Returns true.
     *
     *  Sets all cells in table to be editable.
     *
     *  @param  rowIndex the row being queried.
     *  @param  columnIndex the column being queried.
     *  @return true.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     *  Sets value at specified cell.
     *
     *  @param  aValue value to assign to cell.
     *  @param  rowIndex row of cell.
     *  @param  columnIndex column of cell.
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        try {
            ShopItem row = shoplist.getItem(rowIndex);

            switch (columnIndex) {
                case 0:
                    row.setQuantity((int)aValue);
                    break;
                case 1:
                    row.setName(aValue.toString());
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}

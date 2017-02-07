package fi.ooproject;

import fi.ooproject.utils.ShopTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Graphical user interface for Shopping list application.
 *
 * @author Juuso Pakarinen
 * @version 2016.1115
 * @since 1.8
 */
public class ShoppingGUI extends JFrame {

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
     * Object used for opening file manager.
     */
    private JFileChooser fileChooser;

    /**
     * Table used to display data.
     */
    private JTable table;

    /**
     * Label for displaying messages to user.
     */
    private JLabel msgLabel;

    /**
     * Label for displaying list names from remote storage.
     */
    private JLabel selectLabel;

    /**
     * Stores text strings.
     */
    private ResourceBundle messages;

    /**
     * Constructor.
     *
     * Initializes the frame and creates components.
     */
    public ShoppingGUI() {

        dropbox = new DropboxManager();
        shoplist = new ShoppingList();
        fileChooser = new JFileChooser();
        connection = new ServerConnection(shoplist);
        msgLabel = new JLabel(" ");
        selectLabel = new JLabel(" ");
        messages = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);
        setTitle("Shopping List");
        setSize(800, 400);
        setLayout(new BorderLayout());
        createMenuBar();
        createTable();
        createPanels();
        // centers frame.
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Creates menu bar for the frame.
     *
     * Creates menu bar containing items for loading, saving and combining
     * lists and for quitting the app.
     */
    public void createMenuBar() {

        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu(messages.getString("file"));
        JMenu saveMenu = new JMenu(messages.getString("saveMenu"));
        JMenu loadMenu = new JMenu(messages.getString("loadMenu"));
        JMenuItem exit = new JMenuItem(messages.getString("exit"));
        JMenuItem clear = new JMenuItem(messages.getString("clear"));
        JMenuItem help = new JMenuItem(messages.getString("help"));
        JMenuItem loadLocal = new JMenuItem(messages.getString("loadLocal"));
        JMenuItem saveLocal = new JMenuItem(messages.getString("saveLocal"));
        JMenuItem loadRemote = new JMenuItem(messages.getString("loadRemote"));
        JMenuItem saveRemote = new JMenuItem(messages.getString("saveRemote"));
        JMenuItem combineLocal = new JMenuItem(messages.getString("combineLocal"));
        JMenuItem combineRemote = new JMenuItem(messages.getString("combineRemote"));
        JMenuItem uploadDbox = new JMenuItem(messages.getString("uploadDbox"));

        file.setMnemonic(KeyEvent.VK_F);
        exit.setMnemonic(KeyEvent.VK_E);
        clear.setMnemonic(KeyEvent.VK_L);
        help.setMnemonic(KeyEvent.VK_H);

        saveMenu.setMnemonic(KeyEvent.VK_S);
        saveLocal.setMnemonic(KeyEvent.VK_L);
        saveRemote.setMnemonic(KeyEvent.VK_R);
        uploadDbox.setMnemonic(KeyEvent.VK_U);

        loadMenu.setMnemonic(KeyEvent.VK_O);
        loadLocal.setMnemonic(KeyEvent.VK_L);
        loadRemote.setMnemonic(KeyEvent.VK_R);
        combineLocal.setMnemonic(KeyEvent.VK_A);
        combineRemote.setMnemonic(KeyEvent.VK_E);

        exit.setToolTipText(messages.getString("exitTT"));
        clear.setToolTipText(messages.getString("clearTT"));
        help.setToolTipText(messages.getString("helpTT"));
        loadLocal.setToolTipText(messages.getString("loadLocalTT"));
        loadRemote.setToolTipText(messages.getString("loadRemoteTT"));
        saveLocal.setToolTipText(messages.getString("saveLocalTT"));
        saveRemote.setToolTipText(messages.getString("saveRemoteTT"));
        combineLocal.setToolTipText(messages.getString("combineLocalTT"));
        combineRemote.setToolTipText(messages.getString("combineRemoteTT"));
        uploadDbox.setToolTipText(messages.getString("uploadDboxTT"));

        exit.addActionListener((e) -> System.exit(0));
        clear.addActionListener((e) -> openClearConfirm());
        help.addActionListener((e) -> openHelp());
        loadLocal.addActionListener((e) -> openLoad(false));
        saveLocal.addActionListener((e) -> openSave());
        combineLocal.addActionListener((e) -> openLoad(true));
        loadRemote.addActionListener((e) -> openRemoteLoad(false));
        saveRemote.addActionListener((e) -> openRemoteSave());
        combineRemote.addActionListener((e) -> openRemoteLoad(true));
        uploadDbox.addActionListener((e) -> openDropbox());

        file.add(clear);
        file.add(help);
        file.add(exit);
        saveMenu.add(saveLocal);
        saveMenu.add(saveRemote);
        saveMenu.add(uploadDbox);
        loadMenu.add(loadLocal);
        loadMenu.add(loadRemote);
        loadMenu.add(combineLocal);
        loadMenu.add(combineRemote);
        menubar.add(file);
        menubar.add(saveMenu);
        menubar.add(loadMenu);
        setJMenuBar(menubar);
    }

    /**
     * Creates table and scroll pane to contain it.
     */
    public void createTable() {

        ShopTableModel tableModel = new ShopTableModel(shoplist);
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(300, 80));
        table.setFillsViewportHeight(true);

        TableColumnModel colModel = table.getColumnModel();
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        colModel.getColumn(0).setMaxWidth(100);
        colModel.getColumn(1).setCellRenderer(cellRenderer);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(16, 30, 20, 30));
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates panels and components.
     *
     * Creates panels and components for adding new items to the shopping list
     * and showing messages to the user.
     */
    public void createPanels() {

        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField nameInput = new JTextField(40);
        SpinnerModel spinModel = new SpinnerNumberModel(0, -1000, 1000, 1);
        JSpinner spinner = new JSpinner(spinModel);
        JButton addRowButton = new JButton(messages.getString("addButton"));
        JButton delRowButton = new JButton("Delete");

        addRowButton.addActionListener((e) -> {
            if (nameInput.getText().matches(".*\\w.*")) {
                shoplist.addItem(new ShopItem(nameInput.getText(),
                        (int)spinner.getValue()));

                nameInput.setText("");
                spinner.setValue(0);
                repaint();
            }
        });

        delRowButton.addActionListener((e) -> {

            try {
                int index = table.getSelectedRow();
                if (index >= 0) {
                    shoplist.removeItem(shoplist.getItem(index));
                }
                repaint();
            } catch (IndexOutOfBoundsException | NullPointerException ex) {
                ex.printStackTrace();
            }
        });

        inputPanel.add(spinner);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(nameInput);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(addRowButton);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(delRowButton);
        msgPanel.add(msgLabel);
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(msgPanel, BorderLayout.SOUTH);
        add(panel, BorderLayout.SOUTH);
    }

    /**
     * Opens file manager and loads selected file.
     *
     * @param combine whether the content of loaded file is appended
     *                to current shop list or overwritten.
     */
    public void openLoad(boolean combine) {

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String src = ("" + selectedFile.getAbsolutePath());
            Timer timer = new Timer(2000, (e) -> msgLabel.setText(" "));
            timer.setRepeats(false);

            if (shoplist.loadFile(src, combine)) {
                timer.start();
                msgLabel.setText(messages.getString("loadComp"));
            } else {
                timer.start();
                msgLabel.setText(messages.getString("loadFail"));
            }

            repaint();
        }
    }

    /**
     * Opens file manager and saves to selected file.
     */
    public void openSave() {

        int returnValue = fileChooser.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String dest = ("" + selectedFile.getAbsolutePath());
            Timer timer = new Timer(2000, (e) -> msgLabel.setText(" "));
            timer.setRepeats(false);

            if (shoplist.saveFile(dest)) {
                timer.start();
                msgLabel.setText(messages.getString("saveComp") + " " + dest);
            } else {
                timer.start();
                msgLabel.setText(messages.getString("saveFail"));
            }
        }
    }

    /**
     * Opens dialog for uploading current shop list to Dropbox.
     *
     * Contents of the dialog box varies depending on whether user has
     * authenticated his/her Dropbox account for current session.
     */
    public void openDropbox() {

        JTextField nameField = new JTextField(20);
        JTextField codeField = new JTextField(20);
        JPanel mainPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel inputPanel = new JPanel();

        if (!dropbox.isAuthenticated()) {

            String uri = dropbox.getAuthorizeURL();
            JPanel codePanel = new JPanel();
            JPanel namePanel = new JPanel();
            String str = ("<html>" + messages.getString("dboxHelp1") +
                    "<a href=\"" + uri + "\"> " + uri + "</a><br/>" +
                    messages.getString("dboxHelp2") + "<br/>" +
                    messages.getString("dboxHelp3") + "<br/>" +
                    "4. " + messages.getString("dboxHelp4") + "<br/>"
            );

            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            inputPanel.setBorder(new EmptyBorder(20, 0, 30, 0));
            inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
            namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            codePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            infoPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(uri));
                    } catch (URISyntaxException | IOException ex) {
                        System.out.println(messages.getString("desktopErr"));
                        ex.printStackTrace();
                    }
                }
            });

            infoPanel.add(new JLabel(str));
            codePanel.add(new JLabel(messages.getString("code")));
            codePanel.add(codeField);
            namePanel.add(new JLabel(messages.getString("name")));
            namePanel.add(nameField);
            inputPanel.add(codePanel);
            inputPanel.add(namePanel);
        } else {

            infoPanel.add(new JLabel(messages.getString("dboxHelp4")));
            inputPanel.add(nameField);
        }

        mainPanel.add(infoPanel);
        mainPanel.add(inputPanel);

        int result = JOptionPane.showConfirmDialog(null, mainPanel,
        messages.getString("uploadDbox"), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Timer timer = new Timer(2000, (e) -> msgLabel.setText(" "));
            timer.setRepeats(false);

            if (!dropbox.isAuthenticated()) {
                dropbox.authenticate(codeField.getText());
            }

            if (dropbox.upload(shoplist, nameField.getText())) {
                timer.start();
                msgLabel.setText(messages.getString("uploadComp"));
            } else {
                timer.start();
                msgLabel.setText(messages.getString("uploadFail"));
            }
        }
    }

    /**
     *  Opens dialog for saving current shop list to remote storage.
     */
    public void openRemoteSave() {

        JTextField inputField = new JTextField(35);
        JPanel mainPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel inputPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(new EmptyBorder(20, 0, 30, 0));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel(messages.getString("remoteSaveHelp")));
        inputPanel.add(inputField);
        mainPanel.add(infoPanel);
        mainPanel.add(inputPanel);

        int result = JOptionPane.showConfirmDialog(null, mainPanel,
                messages.getString("saveRemote"), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String listName;
            Timer timer = new Timer(2000, (e) -> msgLabel.setText(" "));
            timer.setRepeats(false);

            if (inputField.getText().length() < 1) {
                listName = "java-shoppinglist";
            } else {
                listName = inputField.getText();
            }

            if (connection.saveList(listName)) {
                timer.start();
                msgLabel.setText(messages.getString("servSaveComp"));
            } else {
                timer.start();
                msgLabel.setText(messages.getString("servSaveFail"));
            }
        }
    }

    /**
     * Opens dialog for loading/combining shop list from remote storage.
     *
     * @param combine whether the loaded content is appended to current shop
     *                list or overwritten.
     */
    public void openRemoteLoad(boolean combine) {

        String[] names = connection.getListNames();
        String title = "";
        JPanel mainPanel = new JPanel();
        JPanel namesPanel = new JPanel();
        JPanel infoPanel = new JPanel();
        JPanel selectPanel = new JPanel();

        namesPanel.setBorder(new EmptyBorder(20, 0, 30, 0));
        namesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        selectPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        if (names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].replace("\"", "");
                JLabel tmp = new JLabel(names[i]);
                tmp.setCursor(new Cursor(Cursor.HAND_CURSOR));
                tmp.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        connection.setTargetList(tmp.getText());
                        selectLabel.setText(connection.getTargetList());
                        validate();
                        repaint();
                    }
                });
                namesPanel.add(tmp);
            }
        } else {
            JLabel tmp = new JLabel("Nothing to show.");
            namesPanel.add(tmp);
        }

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel(messages.getString("remoteLoadHelp")));
        selectPanel.add(new JLabel(messages.getString("selected") + " "));
        selectPanel.add(selectLabel);
        mainPanel.add(infoPanel);
        mainPanel.add(namesPanel);
        mainPanel.add(selectPanel);

        if (combine) {
            title = messages.getString("combineRemote");
        } else {
            title = messages.getString("saveRemote");
        }

        int result = JOptionPane.showConfirmDialog(null, mainPanel, title,
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Timer timer = new Timer(2000, (e) -> msgLabel.setText(" "));
            timer.setRepeats(false);

            if (connection.loadList(connection.getTargetList(), combine)) {
                timer.start();
                repaint();
                msgLabel.setText(messages.getString("servLoadComp"));
            } else {
                timer.start();
                msgLabel.setText(messages.getString("servLoadFail"));
            }
        }
    }

    /**
     * Open confirm prompt to authorize clearing of the list.
     */
    public void openClearConfirm() {
        int result = JOptionPane.showConfirmDialog(null,
                "Remove every item from current list?",
                "Clear list",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            shoplist.clearList();
            repaint();
        }
    }

    /**
     * Open instructions prompt.
     */
    public void openHelp() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(new JLabel(messages.getString("help1")));
        mainPanel.add(new JLabel(messages.getString("help2gui")));
        mainPanel.add(new JLabel(messages.getString("help3gui")));
        mainPanel.add(new JLabel(messages.getString("help4gui")));

        int result = JOptionPane.showConfirmDialog(null, mainPanel,
                messages.getString("help"), JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Creates new instance of {@link ShoppingGUI}.
     *
     * @param args Command line arguments. Not used.
     */
    public static void main(String[] args) {
        ShoppingGUI gui = new ShoppingGUI();
    }
}

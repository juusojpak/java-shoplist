package fi.ooproject;

/**
 * Launcher to be used as a 'Main-Class' in JAR.
 *
 * @author Juuso Pakarinen
 * @version 2016.1129
 * @since 1.8
 */
public class Launcher {

    /**
     * Reads arguments and launches corresponding interface.
     *
     * @param args Command line arguments. If '-cli' runs {@link ShoppingCLI}.
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            if (args[0].matches("^([-]+cli)$")) {
                ShoppingCLI cli = new ShoppingCLI();
                cli.run();
            } else {
                ShoppingGUI gui = new ShoppingGUI();
            }
        } else {
            ShoppingGUI gui = new ShoppingGUI();
        }
    }
}

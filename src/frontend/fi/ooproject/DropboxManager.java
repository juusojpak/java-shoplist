package fi.ooproject;

import com.dropbox.core.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Handles Dropbox authentication and upload.
 *
 * @author Juuso Pakarinen
 * @version 2016.1204
 * @since 1.8
 */
public class DropboxManager {

    /**
     * Does the OAuth web-based authorization.
     */
    private DbxWebAuthNoRedirect webAuth;

    /**
     * Request configurations.
     *
     * A grouping of a few configuration parameters for how we should make
     * requests to the Dropbox servers.
     */
    private DbxRequestConfig reqConf;

    /**
     * Dropbox client object.
     */
    private DbxClient client;

    /**
     * Whether user has authenticated his/her Dropbox account.
     */
    private boolean isAuthenticated;

    /**
     * Constructor.
     */
    public DropboxManager() {

        String APP_KEY = "9u34v4yllfxwvq4";
        String APP_SECRET = "t8eew3fgy8y2j63";
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        reqConf = new DbxRequestConfig(
                "JavaShoppingList/1.0",
                Locale.getDefault().toString()
        );

        webAuth = new DbxWebAuthNoRedirect(reqConf, appInfo);
        isAuthenticated = false;
    }

    /**
     * Returns a String representing authorization URL.
     *
     * Returns a String representing URL where user can obtain the code
     * used for authentication.
     *
     * @return String representing URL.
     */
    public String getAuthorizeURL() {
        return webAuth.start();
    }

    /**
     * Tries to authenticate user's Dropbox account with given code.
     *
     * @param code given code.
     */
    public void authenticate(String code) {

        try {
            // This will fail if the user enters an invalid authorization code.
            DbxAuthFinish authFinish = webAuth.finish(code);
            String accessToken = authFinish.accessToken;
            client = new DbxClient(reqConf, accessToken);
            isAuthenticated = true;
            System.out.println("Linked account: "
                    + client.getAccountInfo().displayName);
        } catch (DbxException e) {
            System.out.println("Authentication failed!");
            e.printStackTrace();
        }
    }

    /**
     * Uploads current shopping list to user's Dropbox as new file.
     *
     * @param list current shopping list.
     * @param fileName name for the file.
     * @return whether upload was successful.
     */
    public boolean upload(ShoppingList list, String fileName) {

        if (isAuthenticated()) {
            if (fileName.length() > 0) {

                try {

                    File inputFile = File.createTempFile("shop-list", ".tmp");
                    Path path = Paths.get(inputFile.getAbsolutePath());
                    List<String> lines = new ArrayList<>();

                    for (int i = 0; i < list.size(); i++) {
                        lines.add(list.getItem(i).toString());
                    }

                    Files.write(path, lines, Charset.forName("UTF-8"));

                    try (FileInputStream inputStream =
                     new FileInputStream(inputFile)) {

                        DbxEntry.File uploadedFile = client.uploadFile(
                                "/"+ fileName +".txt",
                                DbxWriteMode.add(),
                                inputFile.length(),
                                inputStream
                        );

                        System.out.println("Uploaded: " 
                            + uploadedFile.toString());
                    }
                } catch(IOException | DbxException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                System.out.println("Name too short!");
                return false;
            }

            /* If no problems encountered. */
            return true;
        }

        return false;
    }

    /**
     * Returns whether user has authenticated his/her Dropbox account.
     *
     * @return whether user has authenticated his/her Dropbox account.
     */
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}

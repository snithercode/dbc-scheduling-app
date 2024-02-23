/**
 * @author Drew Haile
 *<p></p>
 * README.md / README.txt available at project root.
 * <p></p>
 * ## B: Lambda Expressions
 * **Justification Locations**:
 *     - Appointments:278
 *     - Appointments:678
 *     - Customers:308
 *     - Reports:101
 *     - Reports:160
 *     - Reports:364
 *     - Reports:392
 */

package app;

import helper.JDBC;
import helper.LocalizationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * The Main class is the entry point of the scheduling application.
 */
public class Main extends Application {

    public static Stage primaryStage;
    private final LocalizationService localizationService = new LocalizationService();

    /**
     * Returns the primary stage.
     *
     * @return The primary stage of the application.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Opens a connection with the database and launches the app.
     *
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }

    /**
     * Loads the FMXL file for the main screen of the scheduling application and creates the primary stage.
     *
     * @param primaryStage The primary window for the app
     * @throws IOException If there is an error loading the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Main.primaryStage = primaryStage;

        URL fxmlUrl = getClass().getResource("../view/UserLogin.fxml");
        if (fxmlUrl == null) {
            System.err.println("FXML file not found: ../view/UserLogin.fxml");
            return;
        }

        Parent root = FXMLLoader.load(fxmlUrl, localizationService.getBundle());
        primaryStage.setTitle(localizationService.getTranslation("sceneTitle"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

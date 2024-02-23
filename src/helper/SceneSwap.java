package helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * The SceneSwap class is a utility class with a single method that swaps the current scene of a JavaFX application
 * to a new FXML file.
 */
public class SceneSwap {

    private static final LocalizationService localizationService = new LocalizationService();

    /**
     * Swaps the current scene with a new scene loaded from an FXML file.
     *
     * @param currentStage The stage where the scene will be set.
     * @param fxmlFile     The file path to the FXML file for the new scene.
     */
    public static void swapScene(Stage currentStage, String fxmlFile) {
        try {
            URL fxmlUrl = SceneSwap.class.getResource(fxmlFile);
            if (fxmlUrl == null) {
                System.err.println("FXML file not found: " + fxmlFile);
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl, localizationService.getBundle());
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (IOException e) {
            System.err.println("An error occurred while swapping scenes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}




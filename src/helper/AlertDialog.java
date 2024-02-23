package helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Provides methods for displaying various alert dialogs.
 */
public class AlertDialog {

    /**
     * Displays an alert dialog of the entered type, with a title and alert message content.
     *
     * @param alertType The type of alert.
     * @param title     The title of the alert dialog.
     * @param content   The alert message content.
     */
    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation dialog with a title, content, and buttons for Yes and No.
     *
     * @param title   The title of the confirmation dialog.
     * @param content The alert message content.
     * @return An Optional containing user's choice (yes / no).
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait();
    }
}

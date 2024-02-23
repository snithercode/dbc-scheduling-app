package controller;

import DAO.UserDAO;
import app.Global;
import app.Main;
import helper.AlertDialog;
import helper.LocalizationService;
import helper.SceneSwap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The UserLogin controller class for the UserLogin view.
 * Manages user login, integrating {@link LocalizationService} for automatic localization based on system settings
 * and {@link UserDAO} for authentication.
 */
public class UserLogin {

    /**
     * Instance for handling localization and internationalization.
     */
    private static final LocalizationService localizationService = new LocalizationService();

    /**
     * Stores the username of the currently logged-in user.
     */
    public static String currentUser;

    /**
     * Flag to keep track of if the alert for upcoming appointments has been shown.
     */
    public static boolean upcomingAppointmentsAlertShown = false;

    /**
     * Field for the user to enter their username.
     */
    @FXML
    private TextField textFieldUsername;

    /**
     * Field for the user to enter their password.
     */
    @FXML
    private PasswordField passwordFieldPassword;

    /**
     * Field displaying the user's current zone.
     */
    @FXML
    private TextField textFieldZone;

    /**
     * Logs user login attempts, including the username and success or failure.
     * Writes log entries to login_activity.txt.
     *
     * @param username        The username of the user attempting to log in.
     * @param loginSuccessful Indicates whether the login attempt was successful.
     */
    public static void logUserLoginAttempt(String username, boolean loginSuccessful) {
        String logEntry = LocalDateTime.now().format(Global.LOG_DATE_FORMATTER) + " - Username: " + username + " - " +
                "Login Success: " + loginSuccessful + System.lineSeparator();

        try {
            Files.write(Paths.get(Global.LOGINS_LOG_PATH), logEntry.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out the current user and returns to the login view.
     * <p></p>
     * WARNING: Please do not use logout functionality while testing different timezones.
     * <p></p>
     * When the user gets sent to the login screen again, and initialize() gets called, the code tries to set the zone again.
     * But ZoneId.systemDefault() always returns the cached value even if I've updated the time zone while the app is running
     * and then clicked logout. The application detects zone accurately on launch, but I think I've discovered that the
     * JVM will not update its cached default time zone in response to changes in the systems timezone settings.
     */
    public static void userLogout() {
        currentUser = null;
        UserLogin.upcomingAppointmentsAlertShown = false;

        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle(localizationService.getTranslation("sceneTitle"));
        SceneSwap.swapScene(currentStage, "../view/UserLogin.fxml");
    }

    /**
     * Initializes the controller class and updates the region text field on the user login form with the current
     * system default zone ID.
     * <p></p>
     * NOTE: Want to see if there is a way to detect the ZoneId when a zone change occurs during runtime. and update it
     * non-explicitly. In the application's current iteration, {@code userLogout} is bringing the user back to the login
     * screen but the zoneId does not reflect changes made during runtime. when zoneId is set to ZoneId.systemDefault() again.
     */
    @FXML
    public void initialize() {
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle(localizationService.getTranslation("sceneTitle"));
        currentUser = null;
        ZoneId zoneId = ZoneId.systemDefault();
        textFieldZone.setText(zoneId.toString());
    }

    /**
     * Handles login button click events.
     * Validates input fields and verifies user credentials. On successful verification, checks for upcoming
     * appointment and navigates to the appropriate view. Displays alerts for empty fields, failed logins,
     * or SQLExceptions.
     *
     * @param actionEvent click event on login button
     */
    public void onLoginButtonClick(ActionEvent actionEvent) {
        System.out.println("Login button clicked.");

        if (!validateInput()) {
            return;
        }

        String enteredUsername = textFieldUsername.getText();
        String enteredPassword = passwordFieldPassword.getText();

        boolean loginSuccessful = verifyUserCredentials(enteredUsername, enteredPassword);
        logUserLoginAttempt(enteredUsername, loginSuccessful);

        if (!loginSuccessful) {
            showLoginFailedAlert();
            return;
        }

        System.out.println("Success. Login credentials verified.");
        currentUser = enteredUsername;

        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Reports");
        SceneSwap.swapScene(currentStage, "../view/Reports.fxml");
    }

    /**
     * Handles the clear fields button click event.
     * It clears the entered username and password from the respective text fields.
     *
     * @param actionEvent click event on clear fields button
     */
    public void onClearFieldsButtonClick(ActionEvent actionEvent) {
        textFieldUsername.clear();
        passwordFieldPassword.clear();
    }

    /**
     * Validates user input in login form fields.
     * Displays an alert if fields are empty.
     *
     * @return true if input is valid, false otherwise.
     */
    private boolean validateInput() {
        String enteredUsername = textFieldUsername.getText();
        String enteredPassword = passwordFieldPassword.getText();

        if (enteredPassword.isEmpty() || enteredUsername.isEmpty()) {
            showEmptyFieldsAlert();
            return false;
        }
        return true;
    }

    /**
     * Verifies user credentials against the database.
     * Handles SQLException by displaying an unexpected error alert.
     *
     * @param username The entered username.
     * @param password The entered password.
     * @return true if credentials are valid, false otherwise.
     */
    private boolean verifyUserCredentials(String username, String password) {
        try {
            return UserDAO.verifyLoginCredentials(username, password);
        } catch (SQLException e) {
            showUnexpectedErrorAlert(e);
            return false;
        }
    }

    /**
     * Displays an alert for empty input fields during login.
     */
    private void showEmptyFieldsAlert() {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                localizationService.getTranslation("alertTitleEmptyFields"),
                localizationService.getTranslation("alertEmptyFields")
        );
    }

    /**
     * Displays an alert for when login fails due to invalid credentials.
     */
    private void showLoginFailedAlert() {
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                localizationService.getTranslation("alertTitleLoginFailed"),
                localizationService.getTranslation("alertLoginFailed")
        );
    }

    /**
     * Displays an alert in case of an unexpected SQL error during login.
     *
     * @param e the SQLException
     */
    private void showUnexpectedErrorAlert(SQLException e) {
        System.out.println("SQLException occurred: " + e.getMessage());
        AlertDialog.showAlert(
                Alert.AlertType.ERROR,
                localizationService.getTranslation("alertTitleUnexpectedError"),
                localizationService.getTranslation("alertUnexpectedError")
        );
        e.printStackTrace();
    }

}


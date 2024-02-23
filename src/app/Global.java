package app;

import helper.SceneSwap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Global {

    /**
     * Start time of operating hours.
     */
    public static final LocalTime COMPANY_START_TIME = LocalTime.of(8, 0);
    /**
     * End time of operating hours.
     */
    public static final LocalTime COMPANY_END_TIME = LocalTime.of(22, 0);

    /**
     * File path for user log activity logging.
     */
    public static final String LOGINS_LOG_PATH = "login_activity.txt";

    /**
     * Date format for logging.
     */
    public static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    /**
     * Switches the current scene to the User Login view when the Logout button is clicked.
     *
     * @param actionEvent A click event on the logout button.
     */
    @FXML
    @SuppressWarnings("unused")
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Logout Button Clicked");
        Stage currentStage = Main.getPrimaryStage();
        SceneSwap.swapScene(currentStage, "../view/UserLogin.fxml");
    }
}

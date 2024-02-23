package controller;

import DAO.AppointmentDAO;
import DAO.ReportDAO;
import app.Main;
import helper.AlertDialog;
import helper.SceneSwap;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Appointment;
import model.Report;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controls the Reports view, managing the display and inputs for various report types.
 */
public class Reports {

    /**
     * List for storing reports.
     */
    private final ObservableList<Report> reports = FXCollections.observableArrayList();

    /**
     * For keeping track of the number of upcoming appointments.
     */
    int numUpcomingAppointments;

    /**
     * Text field for displaying results of actions within the reports view.
     */
    @FXML
    private Text textActionResultMessage;

    /**
     * Text field for user input needed for a query the report will run.
     */
    @FXML
    private TextField textFieldReportInput1;

    /**
     * Column displaying names of reports.
     */
    @FXML
    private TableColumn<Report, String> reportName;

    /**
     * Column for descriptions of the reports.
     */
    @FXML
    private TableColumn<Report, String> reportDescription;

    /**
     * Table for listing available reports and their descriptions
     */
    @FXML
    private TableView<Report> tableViewReports;

    /**
     * Table for showing reports that display appointment details.
     */
    @FXML
    private TableView<Appointment> tableViewAppointmentReports;

    /**
     * A flexible TableView using maps to show aggregated data, not limited to specific objects like appointments or
     * customers.
     */
    @FXML
    private TableView<Map<String, Object>> tableViewAggregateReports;

    /**
     * Label for the username of the current user.
     */
    @FXML
    private Label labelUsername;

    /**
     * A list of appointments for the current user with a start time begin within 15 minutes.
     */
    private List<Appointment> upcomingAppointments;

    /**
     * Initializes the Reports view. This method sets the current user's name, prepares the reports list,
     * and sets up the table view columns and event listeners. It also checks for any upcoming appointments
     * and displays any necessary alerts.
     * <p></p>
     * LAMBDA:
     * The lambda used within {@code Platform.runLater} is for delaying the upcoming appointments alert.
     * Without runLater, the alert was executing at the login screen before the scene fully transitioned.
     * I could have implemented it with an anonymous inner class instead but since the logic was simple here
     * and I only need to use it in one place, a lambda expression felt appropriate.
     */
    public void initialize() {

        labelUsername.setText(UserLogin.currentUser);
        setupReportList();
        setupReportTableColumns();
        setupEventListeners();

        tableViewReports.getSelectionModel().select(reports.get(0));
        displayUpcomingAppointments();

        if (!UserLogin.upcomingAppointmentsAlertShown && !upcomingAppointments.isEmpty()) {
            // Using runLater to delay alert until scene transition is completed.
            Platform.runLater(() -> {
                showUpcomingAppointmentsAlert();
                UserLogin.upcomingAppointmentsAlertShown = true;
            });
        }
    }

    /**
     * Sets up the list of reports with predefined report types and descriptions.
     */
    private void setupReportList() {
        reports.clear();
        reports.addAll(
                new Report(1, "Upcoming Appointments for Current User", "Displays list of appointments starting " +
                        "within 15 minutes from the current user's login time."),
                new Report(2, "Appointments Schedule by Contact", "Displays a schedule for each contact in the " +
                        "organization."),
                new Report(3, "Appointments Schedule by Customer", "Displays a list of scheduled appointments for the" +
                        " selected customer."),
                new Report(4, "Total Appointments by Type and Month", "Displays a breakdown of the number of " +
                        "appointments by type and month."),
                new Report(5, "Total Appointments by Contact and Quarter", "Displays a breakdown of the number of " +
                        "appointments by contact and quarter.")
        );
    }


    /**
     * Sets up the columns for the reports table view and sets the list of reports as its items.
     */
    private void setupReportTableColumns() {
        reportName.setCellValueFactory(new PropertyValueFactory<>("reportName"));
        reportDescription.setCellValueFactory(new PropertyValueFactory<>("reportDescription"));
        tableViewReports.setItems(reports);
        tableViewReports.refresh();
    }

    /**
     * Manages the visibility, editable state, and prompt text of textFieldReportInput1. This input becomes part of a db
     * query for the report that is to be run.
     * <p></p>
     * LAMBDA: Used to update the visibility, edit ability, and prompt text of textFieldReportInput1.
     * Lambda approach used for brevity.
     */
    private void setupEventListeners() {
        tableViewReports.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && (newValue.getId() == 2 || newValue.getId() == 3)) {
                textFieldReportInput1.setVisible(true);
                textFieldReportInput1.setEditable(true);
                textFieldReportInput1.setPromptText(newValue.getId() == 2 ? "Contact ID" : "Customer ID");
            } else {
                textFieldReportInput1.setVisible(false);
                textFieldReportInput1.setEditable(false);
            }
        });
    }

    /**
     * Switches the current scene to the Customer view when the Customer navigation tab is clicked.
     *
     * @param actionEvent A click event on the Customer Navigation Tab.
     */
    public void onCustomerTabButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Customer Navigation Tab Clicked");
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Customers");
        SceneSwap.swapScene(currentStage, "../view/Customers.fxml");
    }

    /**
     * Switches the current scene to the Appointments view when the Appointments navigation tab is clicked.
     *
     * @param actionEvent A click event on the Appointments Navigation Tab.
     */
    public void onAppointmentsTabButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Appointments Navigation Tab Clicked");
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Appointments");
        SceneSwap.swapScene(currentStage, "../view/Appointments.fxml");
    }

    /**
     * Validates report input field before displaying the selected report.
     * Alert is shown if validation fails. Otherwise, the report results are set up for viewing.
     */
    public void onViewReportButtonClick(ActionEvent actionEvent) {
        resetTextActionResultMessage();
        if (textFieldReportInput1.isEditable() && !validateReportInput()) {
            showEmptyFieldAlert();
            return;
        }
        setupReportResultsTableView(tableViewReports.getSelectionModel().getSelectedItem());
    }

    /**
     * This method ensures that the input field has some text entered into it.
     *
     * @return True if the input is not empty, false otherwise.
     */
    private boolean validateReportInput() {
        String input = textFieldReportInput1.getText();
        return input != null && !input.isEmpty();
    }

    /**
     * Displays an alert indicating that a required field needed in order for the report to run is empty.
     */
    private void showEmptyFieldAlert() {
        AlertDialog.showAlert(Alert.AlertType.ERROR, "Empty Field", "This report requires an entered ID to run. " +
                "Please enter a numeric ID and try again.");
    }

    /**
     * Handles the event for logout button clicks.
     * Logs out the current user by calling the {@code UserLogin.userLogout} method.
     *
     * @param actionEvent A click event on the logout button.
     */
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        UserLogin.userLogout();
    }

    /**
     * Based on the selected report for viewing, configures the table view to show the appropriate result.
     *
     * @param selectedReport The report selected for viewing
     */
    private void setupReportResultsTableView(Report selectedReport) {
        tableViewAppointmentReports.setVisible(false);
        tableViewAggregateReports.setVisible(false);

        switch (selectedReport.getId()) {
            case 1 -> displayUpcomingAppointments();
            case 2 -> displayAppointmentsForInput(3); // for contact schedule
            case 3 -> displayAppointmentsForInput(2); // for customer schedule
            case 4 -> displayAggregateReportByTypeAndMonth();
            case 5 -> displayAggregateReportByContactAndQuarter();
        }
    }


    /**
     * Displays appointments within the next 15 minutes for the current user.
     * Sets the UI message based on the number of upcoming appointments.
     */
    private void displayUpcomingAppointments() {
        setUpcomingAppointmentsForCurrentUser();
        if (!upcomingAppointments.isEmpty()) {
            textActionResultMessage.setText("You have " + numUpcomingAppointments + " upcoming appointment(s) within " +
                    "the next 15 minutes. See results below.");
        } else {
            textActionResultMessage.setText("You have no upcoming appointment(s) within the next 15 minutes.");
        }

        ObservableList<Appointment> appointmentData =
                FXCollections.observableArrayList(AppointmentDAO.selectUpcomingAppointmentsForCurrentUser());
        setupAppointmentTableViewColumns();
        tableViewAppointmentReports.setItems(appointmentData);
        tableViewAppointmentReports.setVisible(true);

    }

    /**
     * Displays an aggregate report for the number of appointments by type and month.
     */
    private void displayAggregateReportByTypeAndMonth() {
        ObservableList<Map<String, Object>> aggregateData =
                FXCollections.observableArrayList(ReportDAO.showNumAppointmentsByTypeAndMonth());
        setupAggregateReportByTypeAndMonthColumns();
        tableViewAggregateReports.setItems(aggregateData);
        tableViewAggregateReports.setVisible(true);
    }


    /**
     * Displays the appropriate appointments-based report based on the report ID, either for appointments
     * by contact ID or for appointments by customer ID.
     */
    private void displayAppointmentsForInput(int reportId) {
        ObservableList<Appointment> appointmentData;
        if (reportId == 3) {
            appointmentData =
                    FXCollections.observableArrayList(AppointmentDAO.selectAppointmentsByContactId(Integer.parseInt(textFieldReportInput1.getText())));
        } else { // reportId == 4
            appointmentData =
                    FXCollections.observableArrayList(AppointmentDAO.selectAppointmentsByCustomerId(Integer.parseInt(textFieldReportInput1.getText())));
        }
        setupAppointmentTableViewColumns();
        tableViewAppointmentReports.setItems(appointmentData);
        tableViewAppointmentReports.setVisible(true);
    }


    /**
     * Sets up the columns for the appointments table view with mappings to the Appointment class attributes.
     */
    private void setupAppointmentTableViewColumns() {
        // Clear previous columns and add new columns for Appointment
        tableViewAppointmentReports.getColumns().clear();

        // Create columns for the TableView
        TableColumn<Appointment, Integer> appointmentIdColumn = new TableColumn<>("ID");
        TableColumn<Appointment, String> appointmentTitleColumn = new TableColumn<>("Title");
        TableColumn<Appointment, String> appointmentDescriptionColumn = new TableColumn<>("Description");
        TableColumn<Appointment, String> appointmentLocationColumn = new TableColumn<>("Location");
        TableColumn<Appointment, String> appointmentContactColumn = new TableColumn<>("Contact");
        TableColumn<Appointment, String> appointmentTypeColumn = new TableColumn<>("Type");
        TableColumn<Appointment, LocalDateTime> appointmentStartColumn = new TableColumn<>("Start");
        TableColumn<Appointment, LocalDateTime> appointmentEndColumn = new TableColumn<>("End");
        TableColumn<Appointment, Integer> appointmentCustomerIdColumn = new TableColumn<>("Cust ID");
        TableColumn<Appointment, Integer> appointmentUserIdColumn = new TableColumn<>("User ID");

        // Link the columns to the attributes of Appointment
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName")); // Assuming there's
        // a getContactName method in Appointment
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // I'm adding columns to a list to resolve unchecked warning regarding type safety
        List<TableColumn<Appointment, ?>> appointmentColumns = new ArrayList<>();
        appointmentColumns.add(appointmentIdColumn);
        appointmentColumns.add(appointmentTitleColumn);
        appointmentColumns.add(appointmentDescriptionColumn);
        appointmentColumns.add(appointmentLocationColumn);
        appointmentColumns.add(appointmentContactColumn);
        appointmentColumns.add(appointmentTypeColumn);
        appointmentColumns.add(appointmentStartColumn);
        appointmentColumns.add(appointmentEndColumn);
        appointmentColumns.add(appointmentCustomerIdColumn);
        appointmentColumns.add(appointmentUserIdColumn);

        // Add columns to TableView
        tableViewAppointmentReports.getColumns().addAll(appointmentColumns);
    }

    /**
     * Initializes and configures columns for the aggregate report by type and month based on mapped data values.
     * <p></p>
     * LAMBDA: Used for linking the table column data with the model. Lambda approach is direct, clear, and easy to
     * read.
     */
    private void setupAggregateReportByTypeAndMonthColumns() {
        // Clear previous columns and add new columns for Aggregate Reports
        tableViewAggregateReports.getColumns().clear();

        // Create columns for the TableView
        TableColumn<Map<String, Object>, String> monthColumn = new TableColumn<>("Month");
        TableColumn<Map<String, Object>, String> typeColumn = new TableColumn<>("Type");
        TableColumn<Map<String, Object>, Integer> numAppointmentsColumn = new TableColumn<>("Num Appointments");

        // Link the columns to the keys in the map
        monthColumn.setCellValueFactory(mapData -> new SimpleStringProperty((String) mapData.getValue().get("Month")));
        typeColumn.setCellValueFactory(mapData -> new SimpleStringProperty((String) mapData.getValue().get("Type")));
        numAppointmentsColumn.setCellValueFactory(mapData -> new SimpleIntegerProperty((Integer) mapData.getValue().get("NumAppointments")).asObject());

        List<TableColumn<Map<String, Object>, ?>> aggregateColumns = new ArrayList<>();
        aggregateColumns.add(monthColumn);
        aggregateColumns.add(typeColumn);
        aggregateColumns.add(numAppointmentsColumn);

        tableViewAggregateReports.getColumns().addAll(aggregateColumns);
    }

    /**
     * Initializes and configures columns for the aggregate report by contact and quarter based on mapped data values.
     * <p></p>
     * LAMBDA: Used for linking the table column data with the model. Lambda approach is direct, clear, and easy to
     * read.
     */
    private void setupAggregateReportByContactAndQuarterColumns() {
        tableViewAggregateReports.getColumns().clear();

        TableColumn<Map<String, Object>, String> contactColumn = new TableColumn<>("Contact");
        TableColumn<Map<String, Object>, Integer> q1Column = new TableColumn<>("Q1");
        TableColumn<Map<String, Object>, Integer> q2Column = new TableColumn<>("Q2");
        TableColumn<Map<String, Object>, Integer> q3Column = new TableColumn<>("Q3");
        TableColumn<Map<String, Object>, Integer> q4Column = new TableColumn<>("Q4");

        contactColumn.setCellValueFactory(mapData -> new SimpleStringProperty((String) mapData.getValue().get(
                "Contact")));
        q1Column.setCellValueFactory(mapData -> new SimpleIntegerProperty((Integer) mapData.getValue().get("Q1")).asObject());
        q2Column.setCellValueFactory(mapData -> new SimpleIntegerProperty((Integer) mapData.getValue().get("Q2")).asObject());
        q3Column.setCellValueFactory(mapData -> new SimpleIntegerProperty((Integer) mapData.getValue().get("Q3")).asObject());
        q4Column.setCellValueFactory(mapData -> new SimpleIntegerProperty((Integer) mapData.getValue().get("Q4")).asObject());

        List<TableColumn<Map<String, Object>, ?>> aggregateColumns = new ArrayList<>();
        aggregateColumns.add(contactColumn);
        aggregateColumns.add(q1Column);
        aggregateColumns.add(q2Column);
        aggregateColumns.add(q3Column);
        aggregateColumns.add(q4Column);

        tableViewAggregateReports.getColumns().addAll(aggregateColumns);
    }

    /**
     * Displays an aggregate report for the number of appointments by contact and quarter.
     */
    private void displayAggregateReportByContactAndQuarter() {
        ObservableList<Map<String, Object>> aggregateData =
                FXCollections.observableArrayList(ReportDAO.showNumAppointmentsByContactAndQuarter());
        setupAggregateReportByContactAndQuarterColumns();
        tableViewAggregateReports.setItems(aggregateData);
        tableViewAggregateReports.setVisible(true);
    }

    /**
     * Checks on the number of upcoming appointments for the current active user.
     */
    private void setUpcomingAppointmentsForCurrentUser() {
        upcomingAppointments = AppointmentDAO.selectUpcomingAppointmentsForCurrentUser();
        numUpcomingAppointments = upcomingAppointments.size();
    }

    /**
     * Alerts the user of the number of upcoming appointments within the next 15 minutes.
     */
    private void showUpcomingAppointmentsAlert() {
        AlertDialog.showAlert(Alert.AlertType.CONFIRMATION, "Upcoming Appointments",
                "You have " + numUpcomingAppointments + " upcoming appointment(s) within the next 15 minutes." +
                        " Please see the schedule report in the table.");
    }

    /**
     * Clears the display message, which currently displays information about upcoming appointments.
     */
    private void resetTextActionResultMessage() {
        textActionResultMessage.setText(null);
    }


}
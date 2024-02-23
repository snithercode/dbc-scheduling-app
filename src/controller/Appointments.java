package controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
import DAO.UserDAO;
import app.Global;
import app.Main;
import helper.AlertDialog;
import helper.SceneSwap;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.User;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * The Appointments controller class for the Appointments view. Facilitates processing of Appointment data,
 * including insertion, updating, and deletion, through interactions with {@link AppointmentDAO}.
 */
public class Appointments {
    /**
     * List for storing appointments.
     */
    private final List<Appointment> appointments = new ArrayList<>();

    /**
     * List of all contacts.
     */
    private final List<Contact> contacts = ContactDAO.selectAllContacts();

    /**
     * Formats time according to the "h:mm a" pattern (ex: 7:00 pm)
     */
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    /**
     * The selected start date for an appointment.
     */
    private LocalDate selectedStartDate;

    /**
     * The selected end date for an appointment.
     */
    private LocalDate selectedEndDate;

    /**
     * Flag to indicate if a new appointment is being created.
     */
    private boolean inNewAppointmentState = false;

    /**
     * Flag to indicate if an existing appointment is being edited.
     */
    private boolean inEditAppointmentState = false;

    /**
     * Radio Button to display all appointments.
     */
    @FXML
    private RadioButton allAppointmentsRadioButton;

    /**
     * Radio Button to display appointments for the current month.
     */
    @FXML
    private RadioButton currentMonthAppointmentsRadioButton;

    /**
     * Radio Button to display appointments for the current week.
     */
    @FXML
    private RadioButton currentWeekAppointmentsRadioButton;

    /**
     * Label to display the username.
     */
    @FXML
    private Label labelUsername;

    /**
     * Displays the result of the performed action in the UI as a message
     */
    @FXML
    private Text textActionResultMessage;

    /**
     * Button to save an appointment.
     */
    @FXML
    private Button buttonSaveAppointment;

    /**
     * Button to delete a selected appointment.
     */
    @FXML
    private Button buttonDeleteAppointment;

    /**
     * Button to edit an appointment.
     */
    @FXML
    private Button buttonEditAppointment;

    /**
     * Button to create a new appointment.
     */
    @FXML
    private Button buttonNewAppointment;

    /**
     * Date Picker for selecting an appointment start date.
     */
    @FXML
    private DatePicker datePickerAppointmentStartDate;

    /**
     * Date Picker for selecting the end date of an appointment.
     */
    @FXML
    private DatePicker datePickerAppointmentEndDate;

    /**
     * Field for entering an appointment description.
     */
    @FXML
    private TextField textFieldAppointmentDescription;

    /**
     * Field where appointment ID will populate if editing an existing appointment.
     */
    @FXML
    private TextField textFieldAppointmentId;

    /**
     * Field for entering an appointment location.
     */
    @FXML
    private TextField textFieldAppointmentLocation;

    /**
     * Field for entering an appointment title.
     */
    @FXML
    private TextField textFieldAppointmentTitle;

    /**
     * Field for entering the appointment type.
     */
    @FXML
    private TextField textFieldAppointmentType;

    /**
     * Field for entering the customer ID for whom the appointment is for.
     */
    @FXML
    private TextField textFieldAppointmentCustomerId;

    /**
     * Field for entering a user ID associated with the appointment.
     */
    @FXML
    private TextField textFieldAppointmentUserId;

    /**
     * Selector for selecting the start time of the appointment.
     */
    @FXML
    private ComboBox<String> comboboxStartTime;

    /**
     * Selector for selecting the end time of the appointment.
     */
    @FXML
    private ComboBox<String> comboboxEndTime;

    /**
     * Selector for the contact the customer is being scheduled with for the appointment.
     */
    @FXML
    private ComboBox<String> comboboxContactName;

    /**
     * Table to display appointments.
     */
    @FXML
    private TableView<Appointment> tableviewAppointments;

    /**
     * Column for displaying the appointment contact.
     */
    @FXML
    private TableColumn<Appointment, String> appointmentContact;

    /**
     * Column for displaying the appointment description.
     */
    @FXML
    private TableColumn<Appointment, String> appointmentDescription;

    /**
     * Column for displaying the appointment location.
     */
    @FXML
    private TableColumn<Appointment, String> appointmentLocation;

    /**
     * Column for displaying the appointment title.
     */
    @FXML
    private TableColumn<Appointment, String> appointmentTitle;

    /**
     * Column for displaying the appointment type.
     */
    @FXML
    private TableColumn<Appointment, String> appointmentType;

    /**
     * Column for displaying the customer ID associated with the appointment.
     */
    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerId;

    /**
     * Column for displaying the appointment ID.
     */
    @FXML
    private TableColumn<Appointment, Integer> appointmentId;

    /**
     * Column for displaying the user ID associated with the appointment.
     */
    @FXML
    private TableColumn<Appointment, Integer> appointmentUserId;

    /**
     * Column for displaying the appointment's end time.
     */
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndTime;

    /**
     * Column for displaying the appointment's start time.
     */
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartTime;


    /**
     * Initializes UI components and populates the TableView.
     */
    public void initialize() {
        System.out.println("[Method Call]    Appointments.initialize ");
        labelUsername.setText(UserLogin.currentUser);
        setupAppointmentsTableView();
        allAppointmentsRadioButton.setSelected(true);
        populateContactComboBox();
        setupEventListeners();
    }

    /**
     * Sets up listeners for date, contact, and start time fields.
     * The listeners assist with populating data into start and end time combo boxes.
     * <p></p>
     * LAMBDA: The lambdas here were just used to reduce boilerplate code and the need for explicit interface
     * implementation. In return, this yields a marginal but meaningful improvement to readability.
     */
    private void setupEventListeners() {
        System.out.println("[Method Call]    setupEventListeners");

        // Start Date (Date Picker)
        datePickerAppointmentStartDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            // When a selection is made for the appointment start date, autofill the end date field.
            if (newValue != null) {
                selectedStartDate = newValue;
                datePickerAppointmentEndDate.setValue(selectedStartDate);

                if (canPopulateStartTimes()) {
                    populateStartTimeComboBox(selectedStartDate);
                }

                // disable dates before selected start date
                datePickerAppointmentEndDate.setDayCellFactory(endDatePicker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(newValue));
                    }
                });
            }
        });

        // End Date (Date Picker)
        datePickerAppointmentEndDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (datePickerAppointmentEndDate.valueProperty().getValue() != null) {
                selectedEndDate = newValue;
            }
            if (canPopulateStartTimes()) {
                populateStartTimeComboBox(selectedStartDate);
            }
        });

        // Start Time (Combo Box)
        comboboxStartTime.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Populate End Time dropdown when Start Time changes
            if (newValue != null) {
                LocalTime selectedStartTime = LocalTime.parse(newValue, timeFormatter);
                populateEndTimeComboBox(selectedStartTime, selectedStartDate);
            }
        });
    }

    /**
     * Toggles the UI state between default, new appointment, and edit appointment modes.
     * Enables or disables form fields and buttons based on the current mode.
     */
    private void toggleUIState() {
        System.out.println("[Method Call]    toggleUIState");

        // Switch to default UI state if not editing or creating a new appointment
        if (!inEditAppointmentState && !inNewAppointmentState) {
            System.out.println("                 Toggling to default UI state.");

            // Disable form fields
            textFieldAppointmentTitle.setDisable(true);
            textFieldAppointmentDescription.setDisable(true);
            textFieldAppointmentLocation.setDisable(true);
            comboboxContactName.setDisable(true);
            datePickerAppointmentStartDate.setDisable(true);
            datePickerAppointmentEndDate.setDisable(true);
            textFieldAppointmentType.setDisable(true);
            comboboxStartTime.setDisable(true);
            comboboxEndTime.setDisable(true);
            textFieldAppointmentCustomerId.setDisable(true);
            textFieldAppointmentUserId.setDisable(true);
            buttonSaveAppointment.setDisable(true);

            // Enable control buttons
            buttonEditAppointment.setDisable(false);
            buttonDeleteAppointment.setDisable(false);
            buttonNewAppointment.setDisable(false);

            // Reset button labels
            buttonNewAppointment.setText("New Appointment");
            buttonEditAppointment.setText("Edit Appointment");

            // Empty the appointment fields
            resetAppointmentFields();
        }

        // If already in either the edit or new appointment state, toggle back to default
        if (inNewAppointmentState || inEditAppointmentState) {
            System.out.println("                 Toggling to edit/new UI state.");

            // Enable form fields
            textFieldAppointmentTitle.setDisable(false);
            textFieldAppointmentDescription.setDisable(false);
            textFieldAppointmentLocation.setDisable(false);
            comboboxContactName.setDisable(false);
            datePickerAppointmentStartDate.setDisable(false);
            datePickerAppointmentEndDate.setDisable(false);
            textFieldAppointmentType.setDisable(false);
            comboboxStartTime.setDisable(false);
            comboboxEndTime.setDisable(false);
            textFieldAppointmentCustomerId.setDisable(false);
            textFieldAppointmentUserId.setDisable(false);
            buttonSaveAppointment.setDisable(false);

            // Disable control buttons
            buttonDeleteAppointment.setDisable(true);

            // Empty appointment fields
            resetAppointmentFields();

            // If in new appointment state, update related UI elements
            if (inNewAppointmentState && !inEditAppointmentState) {
                System.out.println("                           In New Appointment State.");
                buttonEditAppointment.setDisable(true);
                buttonNewAppointment.setText("Cancel New");
                textFieldAppointmentId.setPromptText("Appointment ID (db-gen)");
            }

            // If in edit appointment state, update related UI elements
            if (inEditAppointmentState && !inNewAppointmentState) {
                System.out.println("                 In Edit Appointment State.");
                buttonNewAppointment.setDisable(true);
                buttonEditAppointment.setText("Cancel Edit");
            }
        }
    }

    /**
     * Resets all form fields to their initial, empty state.
     */
    private void resetAppointmentFields() {
        System.out.println("[Method Call]    resetAppointmentFields");

        System.out.println("                 Clearing appointment form fields.");
        textFieldAppointmentId.clear();
        textFieldAppointmentTitle.clear();
        textFieldAppointmentDescription.clear();
        textFieldAppointmentLocation.clear();
        textFieldAppointmentType.clear();
        textFieldAppointmentCustomerId.clear();
        textFieldAppointmentUserId.clear();
        comboboxContactName.getSelectionModel().clearSelection();
        comboboxContactName.setValue(null);
        datePickerAppointmentStartDate.setValue(null);
        datePickerAppointmentEndDate.setValue(null);
        comboboxStartTime.getItems().clear();
        comboboxStartTime.setValue(null);
        comboboxEndTime.getItems().clear();
        comboboxEndTime.setValue(null);
    }

    /**
     * Refreshes the TableView with the list of appointments.
     * Converts the stored UTC time to the user's local time zone before displaying.
     */
    private void refreshTableView() {
        System.out.println("[Method Call]    refreshTableView");

        appointments.clear();

        // Check which radio button is selected and populate the appointments list accordingly
        if (currentWeekAppointmentsRadioButton.isSelected()) {
            appointments.addAll(AppointmentDAO.selectAppointmentsForCurrentWeek());
            tableviewAppointments.setItems(FXCollections.observableArrayList(appointments));
        } else if (currentMonthAppointmentsRadioButton.isSelected()) {
            appointments.addAll(AppointmentDAO.selectAppointmentsForCurrentMonth());
            tableviewAppointments.setItems(FXCollections.observableArrayList(appointments));
        } else if (allAppointmentsRadioButton.isSelected()) {
            appointments.addAll(AppointmentDAO.selectAllAppointments());
            tableviewAppointments.setItems(FXCollections.observableArrayList(appointments));
        }


        System.out.println("                 Appointments list size after refresh: " + appointments.size());

        // Update the data displayed in the tableview
        tableviewAppointments.refresh();
        System.out.println("                 TableView refreshed.");
    }

    /**
     * Fills the start time combo box with a range of appointment start times based on business operating hours, in the
     * user's local time. This avoids the issue of scheduling appointments outside operating hours.
     *
     * @param selectedDate The selected date for the appointment.
     */
    private void populateStartTimeComboBox(LocalDate selectedDate) {
        ZoneId localZone = ZoneId.systemDefault();
        ZoneId companyZone = ZoneId.of("America/New_York"); // Your company's time zone

        ZonedDateTime companyStartZoned = ZonedDateTime.of(selectedDate, Global.COMPANY_START_TIME, companyZone)
                .withZoneSameInstant(localZone);
        ZonedDateTime companyEndZoned = ZonedDateTime.of(selectedDate, Global.COMPANY_END_TIME, companyZone)
                .withZoneSameInstant(localZone);

        List<String> appointmentStartTimes = new ArrayList<>();
        for (ZonedDateTime time = companyStartZoned; time.isBefore(companyEndZoned); time = time.plusMinutes(15)) {
            appointmentStartTimes.add(time.toLocalTime().format(timeFormatter));
        }

        comboboxStartTime.getItems().setAll(appointmentStartTimes);
    }



    /**
     * Populates the end time combo box based on the selected start time and selected date.
     *
     * @param startTime The selected start time for the appointment.
     */
    private void populateEndTimeComboBox(LocalTime startTime, LocalDate selectedDate) {
        System.out.println("[Method Call]    populateEndTimeComboBox called with startTime: " + startTime);

        ZoneId localZone = ZoneId.systemDefault();
        ZoneId companyZone = ZoneId.of("America/New_York");
        List<String> appointmentEndTimes = new ArrayList<>();

        // Convert start time to local zone and align company end time with local zone
        ZonedDateTime startTimeZoned = ZonedDateTime.of(selectedDate, startTime, localZone);
        ZonedDateTime companyEndZoned = ZonedDateTime.of(selectedDate, Global.COMPANY_END_TIME, companyZone)
                .withZoneSameInstant(localZone);

        // Adjust end time options to be in 15-minute intervals after the selected start time.
        ZonedDateTime endTimeOption = startTimeZoned.plusMinutes(15);

        while (endTimeOption.isBefore(companyEndZoned) || endTimeOption.equals(companyEndZoned)) {
            appointmentEndTimes.add(endTimeOption.toLocalTime().format(timeFormatter));
            endTimeOption = endTimeOption.plusMinutes(15);
        }

        comboboxEndTime.getItems().setAll(appointmentEndTimes);
    }



    /**
     * Populates the Contact Name combo box with the names of available contacts.
     */
    private void populateContactComboBox() {
        System.out.println("[Method Call]    populateContactComboBox");

        for (Contact contact : contacts) {
            // System.out.println("                 Adding contact: " + contact.getName());
            comboboxContactName.getItems().add(contact.getName());
        }
    }

    /**
     * Responds to the click event for the "All Appointments" radio button.
     * Deselects other related radio buttons and refreshes the TableView to show all appointments.
     *
     * @param actionEvent A click event on the All Appointments radio button.
     */
    @FXML
    public void onAllAppointmentsRadioButtonClick(ActionEvent actionEvent) {
        currentMonthAppointmentsRadioButton.setSelected(false);
        currentWeekAppointmentsRadioButton.setSelected(false);
        System.out.println("[Action Event]   All Appointments Radio Button Clicked");
        refreshTableView();
    }

    /**
     * Responds to the click event for the "Current Month" radio button.
     * Deselects other related radio buttons and refreshes the TableView to show appointments for the current month.
     *
     * @param actionEvent A click event on the Month radio button.
     */
    @FXML
    public void onCurrentMonthAppointmentsRadioButtonClick(ActionEvent actionEvent) {
        allAppointmentsRadioButton.setSelected(false);
        currentWeekAppointmentsRadioButton.setSelected(false);
        System.out.println("[Action Event]   Current Month Appointments Radio Button Clicked");
        refreshTableView();
    }

    /**
     * Responds to the click event for the "Current Week" radio button.
     * Deselects other related radio buttons and refreshes the TableView to show appointments for the current week.
     *
     * @param actionEvent A click event on the Current Week radio button.
     */
    @FXML
    public void onCurrentWeekAppointmentsRadioButtonClick(ActionEvent actionEvent) {
        allAppointmentsRadioButton.setSelected(false);
        currentMonthAppointmentsRadioButton.setSelected(false);
        System.out.println("[Action Event]   Current Week Appointments Radio Button Clicked");
        refreshTableView();
    }

    /**
     * Responds to the click event for the Delete Appointment Button.
     * If an appointment is selected, it shows a confirmation dialog before proceeding to delete the appointment.
     * If no appointment is selected, an error alert is shown. The UI is updated upon successful deletion.
     *
     * @param actionEvent A click event on the Delete Appointment button.
     */
    @FXML
    public void onDeleteAppointmentButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Delete Appointment Button Clicked");
        textActionResultMessage.setText(null);

        Appointment selectedAppointment = tableviewAppointments.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            AlertDialog.showAlert(Alert.AlertType.ERROR, "No Appointment Selected", "Please select the appointment " +
                    "record you would like to delete.");
            return;
        }

        // Prevent accidental deletion with a confirmation dialog
        Optional<ButtonType> result = AlertDialog.showConfirmationDialog("Confirm Deletion", "Delete selected " +
                "appointment?");

        // Only proceed if user clicks yes
        if (result.isPresent() && result.get() == ButtonType.YES) {
            int selectedAppointmentId = selectedAppointment.getId();
            String selectedAppointmentTitle = selectedAppointment.getTitle();
            if (AppointmentDAO.deleteAppointment(selectedAppointment.getId())) {
                tableviewAppointments.getItems().remove(selectedAppointment);
                textActionResultMessage.setText("Appointment Record Deleted (ID: " + selectedAppointmentId + " Title: " + selectedAppointmentTitle + ")");
            } else {
                AlertDialog.showAlert(Alert.AlertType.ERROR, "Record Deletion Failed", "Unable to delete appointment.");
            }
        }
    }


    /**
     * Responds to the click event for the Edit Appointment button.
     * Toggles the UI state between editing an existing appointment and the default state.
     * If an appointment is selected, it populates the UI fields with the selected appointment's details.
     * If no appointment is selected, an error alert is shown.
     *
     * @param actionEvent A click event on the Edit Appointment button.
     */
    @FXML
    public void onEditAppointmentButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Edit Appointment Button Clicked");
        textActionResultMessage.setText(null);

        Appointment selectedAppointment = tableviewAppointments.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            inEditAppointmentState = !inEditAppointmentState;
            toggleUIState();

            // If in edit state, populate the form with the selected appointment's info.
            if (inEditAppointmentState) {
                toggleUIState();

                textFieldAppointmentId.setText(Integer.toString(selectedAppointment.getId()));
                textFieldAppointmentTitle.setText(selectedAppointment.getTitle());
                textFieldAppointmentDescription.setText(selectedAppointment.getDescription());
                textFieldAppointmentLocation.setText(selectedAppointment.getLocation());
                textFieldAppointmentType.setText(selectedAppointment.getType());
                textFieldAppointmentCustomerId.setText(String.valueOf(selectedAppointment.getCustomerId()));
                textFieldAppointmentUserId.setText(String.valueOf(selectedAppointment.getUserId()));

                // Perform conversions to user's local tz
                LocalDateTime startDateTime = selectedAppointment.getStartTime();
                LocalDateTime endDateTime = selectedAppointment.getEndTime();
                LocalDate startDate = startDateTime.toLocalDate();
                LocalTime startTime = startDateTime.toLocalTime();
                LocalDate endDate = endDateTime.toLocalDate();
                LocalTime endTime = endDateTime.toLocalTime();

                datePickerAppointmentStartDate.setValue(startDate);
                datePickerAppointmentEndDate.setValue(endDate);
                comboboxContactName.setValue(selectedAppointment.getContactName());
                comboboxStartTime.setValue(startTime.format(timeFormatter));
                comboboxEndTime.setValue(endTime.format(timeFormatter));
            }
        } else {
            AlertDialog.showAlert(Alert.AlertType.ERROR, "No Appointment Selected", "Please select an appointment to " +
                    "update.");
        }
    }

    /**
     * Responds to click event for New Appointment button.
     * Toggles the UI state between creating a new appointment and the default state.
     * Calls a method to update the UI elements based on the new state.
     *
     * @param actionEvent A click event on the New Appointment button.
     */
    @FXML
    public void onNewAppointmentButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   New Appointment Button Clicked");
        textActionResultMessage.setText(null);
        inNewAppointmentState = !inNewAppointmentState;
        toggleUIState();

    }

    /**
     * Responds to the click event for the Save Appointment Button.
     * Calls method for validation check to alert user of any empty fields.
     * Checks the current UI state to determine if the user is creating a new appointment or editing an existing one.
     * Checks for scheduling conflicts before creating or updating an appointment.
     * <p></p>
     * LAMBDA: The lambda used here is used when in the edit state, to filter out the currently selected appointment
     * from the list of conflicts, so that it can be saved without issue.
     *
     * @param actionEvent A click event on the Save Appointment button.
     */
    @FXML
    public void onSaveAppointmentButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Save Appointment Button Clicked");

        checkForEmptyFields();

        if (!validateAppointmentTimes()) {
            return;
        }

        // Grab start and end times from form
        LocalTime startTime = LocalTime.parse(comboboxStartTime.getValue(), timeFormatter);
        LocalDateTime startDateTime = datePickerAppointmentStartDate.getValue().atTime(startTime);
        LocalTime endTime = LocalTime.parse(comboboxEndTime.getValue(), timeFormatter);
        LocalDateTime endDateTime = datePickerAppointmentEndDate.getValue().atTime(endTime);

        if (hasAppointmentConflicts(startDateTime, endDateTime, inNewAppointmentState)) {
            showOverlappingAppointmentAlert();
            return;
        }

        if (inNewAppointmentState) {
            createNewAppointment(startDateTime, endDateTime);
        } else if (inEditAppointmentState) {
            updateExistingAppointment(startDateTime, endDateTime);
        }
    }

    /**
     * Used to check for scheduling conflicts before creating or updating an appointment.
     * In the case of editing an existing appointment, it excludes the appointment being edited from the conflict check.
     *
     * @param startDateTime The desired start datetime for the appointment.
     * @param endDateTime The desired end datetime for the appointment.
     * @param isNewAppointment Flag to indicate if the operation is for creating a new appointment (true) or editing an existing one (false).
     * @return true if a scheduling conflict exists (ex: if there is an overlapping appointment), false otherwise.
     */
    private boolean hasAppointmentConflicts(LocalDateTime startDateTime, LocalDateTime endDateTime, boolean isNewAppointment) {
        // Checking for appt conflicts in db
        List<Appointment> appointmentConflicts = AppointmentDAO.selectAppointmentsWithRangeConflictForCustomerID(
                Integer.parseInt(textFieldAppointmentCustomerId.getText()),
                startDateTime,
                endDateTime
        );

            if (isNewAppointment) {
            return !appointmentConflicts.isEmpty();
        } else {
            Appointment selectedAppointment = tableviewAppointments.getSelectionModel().getSelectedItem();
            int selectedAppointmentId = selectedAppointment.getId();

            // Filter out currently selected appt from conflicts so user can edit and save without error alert
            appointmentConflicts.removeIf(appointment -> appointment.getId() == selectedAppointmentId);

            return !appointmentConflicts.isEmpty();
        }
    }



    /**
     * Switches the current scene to the Customer Management view when the Customer navigation tab is clicked.
     *
     * @param actionEvent A click event on the Customer Navigation Tab.
     */
    @FXML
    public void onCustomerTabButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Customer Navigation Tab Clicked");
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Customers");
        SceneSwap.swapScene(currentStage, "../view/Customers.fxml");
    }

    /**
     * Switches the current scene to the Reports view when the Reports navigation tab is clicked.
     *
     * @param actionEvent A click event on the Reports Navigation Tab.
     */
    @FXML
    public void onReportsTabButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Reports Navigation Tab Clicked");
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Reports");
        SceneSwap.swapScene(currentStage, "../view/Reports.fxml");
    }

    /**
     * Switches the current scene to the User Login view when the Logout button is clicked.
     *
     * @param actionEvent A click event on the logout button.
     */
    @FXML
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Logout Button Clicked");
        UserLogin.userLogout();
    }

    /**
     * Gets the username corresponding to the user ID specified in the appointment form.
     *
     * @return The username associated with the user ID in the form.
     */
    private String getUserNameFromUserIdField() {
        System.out.println("[Method Call]    getUserNameFromUserIdField");
        int userId = Integer.parseInt(textFieldAppointmentUserId.getText());
        User user = UserDAO.selectUserById(userId);

        if (user == null) {
            System.err.println("No user found with ID: " + userId);
            return "User Not Found";
        }

        return user.getName();
    }

    /**
     * Gets the ID of the contact selected in the Contact Name combo box.
     *
     * @return The ID corresponding to the selected contact name.
     */
    private int getContactIdFromComboBox() {
        System.out.println("[Method Call]    getContactIdFromComboBox");
        String selectedContactName = comboboxContactName.getSelectionModel().getSelectedItem();
        Contact contact = ContactDAO.selectContactByName(selectedContactName);

        if (contact == null) {
            System.err.println("No contact found for name: " + selectedContactName);
            return -1;
        }
        return contact.getId();
    }

    /**
     * Converts a given LocalDateTime to its UTC equivalent.
     *
     * @param localDateTime The local date-time to convert.
     * @return The UTC equivalent of the given local datetime.
     */
    private LocalDateTime convertToUTC(LocalDateTime localDateTime) {
        // System.out.println("[Method Call]    convertToUTC");
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * Converts a given UTC LocalDateTime to its local time equivalent.
     *
     * @param utcDateTime The UTC date-time to convert.
     * @return The local time equivalent of the given UTC datetime.
     */
    private LocalDateTime convertFromUTC(LocalDateTime utcDateTime) {
        System.out.println("[Method Call]    convertFromUTC");
        return utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Updates an existing appointment record. Gets the appointment from the table, updates the data
     * with the form field values, and applies these changes to the database using DAO methods. Refreshes
     * the UI and displays a message indicating whether the update was successful or failed, with an added alert dialog
     * on failure.
     *
     * @param startDateTime The new local start time for the appointment.
     * @param endDateTime   The new local end time for the appointment.
     */
    private void updateExistingAppointment(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        System.out.println("[Method Call]    updateExistingAppointment called with startDateTime: " + startDateTime + " and endDateTime: " + endDateTime);

        // Get the selected appointment from the TableView
        Appointment selectedAppointment = tableviewAppointments.getSelectionModel().getSelectedItem();
        System.out.println("                 Selected Appointment ID: " + (selectedAppointment != null ?
                selectedAppointment.getId() : "None"));

        // If no appointment is selected, display an alert and exit the method
        if (selectedAppointment == null) {
            System.out.println("                 No appointment selected. Exiting method.");
            AlertDialog.showAlert(Alert.AlertType.ERROR, "No Appointment Selected", "Please select an appointment to " +
                    "edit.");
            return;
        }

        // If an appointment is selected, create an appointment object with updated details
        System.out.println("                 Creating updated appointment object.");
        Appointment updatedAppointment = new Appointment(
                Integer.parseInt(textFieldAppointmentId.getText()),
                textFieldAppointmentTitle.getText(),
                textFieldAppointmentDescription.getText(),
                textFieldAppointmentLocation.getText(),
                textFieldAppointmentType.getText(),
                startDateTime,
                endDateTime,
                selectedAppointment.getCreateDate(),
                getUserNameFromUserIdField(),
                Timestamp.valueOf(LocalDateTime.now()),
                UserLogin.currentUser,
                Integer.parseInt(textFieldAppointmentCustomerId.getText()),
                Integer.parseInt(textFieldAppointmentUserId.getText()),
                getContactIdFromComboBox(),
                comboboxContactName.getValue()
        );

        // Update the appointment in the database.
        // If update operation returns true, it was successful. Toggle back to default UI state and refresh/reset.
        System.out.println("                 Attempting to update appointment in database.");
        if (AppointmentDAO.updateAppointment(updatedAppointment)) {
            System.out.println("                 Update successful.");
            textActionResultMessage.setText("Record updated.");
            refreshTableView();
            inEditAppointmentState = false;
            toggleUIState();
        } else {
            System.out.println("                 Update failed.");
            AlertDialog.showAlert(Alert.AlertType.ERROR, "Update Failed.", "Failed to update appointment. Please try " +
                    "again.");
            textActionResultMessage.setText("Update not applied.");
        }
    }

    /**
     * Gets data from the form fields and creates a new appointment object. The new appointment object is used in
     * conjunction with DAO methods to insert a new appointment into the database. Upon success, the UI is refreshed
     * to show the updated list of appointments. If insertion fails, an alert and UI message is displayed.
     *
     * @param startDateTime The local start time of the appointment.
     * @param endDateTime   The local end time of the appointment.
     */
    private void createNewAppointment(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        System.out.println("[Method Call]    createNewAppointment called with startDateTime: " + startDateTime + " " +
                "and endDateTime: " + endDateTime);


        // Create a new appointment obj using the data from the form fields
        Appointment newAppointment = new Appointment(
                null, // id yet to be assigned by db
                textFieldAppointmentTitle.getText(),
                textFieldAppointmentDescription.getText(),
                textFieldAppointmentLocation.getText(),
                textFieldAppointmentType.getText(),
                startDateTime,
                endDateTime,
                LocalDateTime.now(), // createDate
                UserLogin.currentUser, // createdBy
                Timestamp.valueOf(LocalDateTime.now()), // lastUpdate
                UserLogin.currentUser, // lastUpdatedBy
                Integer.parseInt(textFieldAppointmentCustomerId.getText()),
                Integer.parseInt(textFieldAppointmentUserId.getText()),
                getContactIdFromComboBox(),
                comboboxContactName.getValue()
        );

        // If insertion operation returns true, it was successful. Toggle back to default UI state and refresh/reset.
        if (AppointmentDAO.insertAppointment(newAppointment)) {
            System.out.println("                 Insertion successful.");
            textActionResultMessage.setText("Appointment added.");
            refreshTableView();
            inNewAppointmentState = false;
            toggleUIState();
        } else {
            AlertDialog.showAlert(Alert.AlertType.ERROR, "Insertion Failed", "Failed to add new appointment. Please " +
                    "try again.");
            textActionResultMessage.setText("Failed to create.");
        }
    }

    /**
     * Performs a validation check to ensure that all fields have been filled out in the appointment form.
     * If any fields are empty, an alert dialog is shown and the user is asked to complete the form before trying again.
     */
    private void checkForEmptyFields() {
        System.out.println("[Method Call]    checkForEmptyFields");
        // Check if any of the required fields are empty or not selected
        if (textFieldAppointmentTitle.getText().isEmpty() ||
                textFieldAppointmentDescription.getText().isEmpty() ||
                textFieldAppointmentLocation.getText().isEmpty() ||
                textFieldAppointmentType.getText().isEmpty() ||
                comboboxContactName.getValue() == null ||
                comboboxStartTime.getValue() == null ||
                comboboxEndTime.getValue() == null ||
                datePickerAppointmentStartDate.getValue() == null ||
                datePickerAppointmentEndDate.getValue() == null ||
                textFieldAppointmentCustomerId.getText().isEmpty()) {

            AlertDialog.showAlert(Alert.AlertType.ERROR, "Empty Fields", "The appointment form contains empty fields." +
                    " Please complete the form before trying again.");
        }
    }

    /**
     * Performs a check to make sure that date fields are set before start times are populated.
     *
     * @return true if values have been selected for start date and end date, otherwise false.
     */
    private boolean canPopulateStartTimes() {
        System.out.println("[Method Call]    canPopulateStartTimes");
        System.out.println("                 Checking values for required fields...");
        System.out.println("                 selectedStartDate: " + selectedStartDate);
        System.out.println("                 selectedEndDate: " + selectedEndDate);

        // Check if all the needed values are not null
        boolean result = selectedEndDate != null && selectedStartDate != null;
        System.out.println("                 Result says: " + result);
        return result;
    }

    /**
     * Initializes the appointments table view with column mappings and loads appointments from the database.
     */
    private void setupAppointmentsTableView() {
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEndTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));

        appointments.addAll(AppointmentDAO.selectAllAppointments());

        tableviewAppointments.setItems(FXCollections.observableList(appointments));
    }

    /**
     * To alert the user that there is an appointment conflict due to an overlap of appointment times.
     */
    private void showOverlappingAppointmentAlert() {
        AlertDialog.showAlert(Alert.AlertType.ERROR, "Scheduling Conflict", "A pre-existing appointment for " +
                "this customer overlaps with your desired date and time.");
    }

    /**
     * Validation check to make sure an appointment's end date/time is after the start date/time.
     * Helpful particularly for the UTC+00:00 timezone, where the local conversion of business operating hours is 1300-0300.
     * @return true if end date/time is after start date/time, false otherwise
     */
    private boolean validateAppointmentTimes() {
        LocalDate startDate = datePickerAppointmentStartDate.getValue();
        LocalTime startTime = LocalTime.parse(comboboxStartTime.getValue(), timeFormatter);
        LocalDate endDate = datePickerAppointmentEndDate.getValue();
        LocalTime endTime = LocalTime.parse(comboboxEndTime.getValue(), timeFormatter);

        ZonedDateTime startDateTime = ZonedDateTime.of(startDate, startTime, ZoneId.systemDefault());
        ZonedDateTime endDateTime = ZonedDateTime.of(endDate, endTime, ZoneId.systemDefault());

        // Check if the end date/time is after the start date/time.
        if (endDateTime.isAfter(startDateTime)) {
            return true;
        } else {
            AlertDialog.showAlert(Alert.AlertType.ERROR,"Check Appointment Date & Time",
                    "The end date and time must be after the start date and time. If the end time falls on the next day, please remember to adjust the end date accordingly.");
            return false;
        }
    }
}

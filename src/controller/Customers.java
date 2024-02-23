package controller;

import DAO.CountryDAO;
import DAO.CustomerDAO;
import DAO.DivisionDAO;
import app.Main;
import helper.AlertDialog;
import helper.SceneSwap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Customer;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * The Customers controller class for the Customers view. Facilitates processing of customer data,
 * including insertion, updating, and deletion, through interactions with {@link CustomerDAO}.
 */

public class Customers {

    /**
     * List of customers, observable for UI updates.
     */
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    /**
     * Flag for when user is adding a new customer.
     */
    private boolean inNewCustomerState = false;

    /**
     * Flag for when user is editing a customer.
     */
    private boolean inEditCustomerState = false;

    /**
     * Field for customer ID.
     */
    @FXML
    private TextField textFieldCustomerId;

    /**
     * Input field for customer name.
     */
    @FXML
    private TextField textFieldName;

    /**
     * Field for entering customer's address.
     */
    @FXML
    private TextField textFieldAddress;

    /**
     * Field for customer's phone number.
     */
    @FXML
    private TextField textFieldPhoneNumber;

    /**
     * Postal code input field.
     */
    @FXML
    private TextField textFieldPostalCode;

    /**
     * Dropdown for selecting state or province.
     */
    @FXML
    private ComboBox<String> comboboxStateProvince;

    /**
     * Combo box selector to pick a country.
     */
    @FXML
    private ComboBox<String> comboboxCountry;

    /**
     * Button for editing a selected customer
     */
    @FXML
    private Button buttonEditCustomer;

    /**
     * Button to add a new customer
     */
    @FXML
    private Button buttonNewCustomer;

    /**
     * Button to delete a selected customer.
     */
    @FXML
    private Button buttonDeleteCustomer;

    /**
     * But to save customer data.
     */
    @FXML
    private Button buttonSaveCustomer;

    /**
     * TableView for showing customer data.
     */
    @FXML
    private TableView<Customer> tableviewCustomers;

    /**
     * Column for customer IDs.
     */
    @FXML
    private TableColumn<Customer, Integer> customerID;

    /**
     * Column for the customer name.
     */
    @FXML
    private TableColumn<Customer, String> customerName;

    /**
     * Column for the customer address.
     */
    @FXML
    private TableColumn<Customer, String> customerAddress;

    /**
     * Column for the customer phone number.
     */
    @FXML
    private TableColumn<Customer, String> customerPhoneNumber;

    /**
     * Column for the customer's state or province.
     */
    @FXML
    private TableColumn<Customer, String> customerStateProvince;

    /**
     * Column for displaying the customer's postal code.
     */
    @FXML
    private TableColumn<Customer, String> customerPostal;

    /**
     * Column for displaying the customer's country.
     */
    @FXML
    private TableColumn<Customer, String> customerCountry;

    /**
     * Label for showing the username.
     */
    @FXML
    private Label labelUsername;

    /**
     * Text field for showing results of actions.
     */
    @FXML
    private Text textActionResultMessage;

    /**
     * Initializes UI components, populates the TableView, and sets up dynamic population of combo boxes.
     */
    public void initialize() {
        System.out.println("[Method Call]    Customers.initialize ");

        // Update username displayed at upper right
        labelUsername.setText(UserLogin.currentUser);


        customers.addAll(CustomerDAO.selectAllCustomers());
        tableviewCustomers.setItems(FXCollections.observableList(customers));


        customerID.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerStateProvince.setCellValueFactory(new PropertyValueFactory<>("stateProvince"));
        customerCountry.setCellValueFactory(new PropertyValueFactory<>("country"));


        populateCountryAndStateProvinceComboBoxes();

    }

    /**
     * Toggles the UI state between default, new customer, and edit customer modes.
     * Enables or disables form fields and buttons based on the current mode.
     */
    private void toggleUIState() {
        System.out.println("[Method Call]    toggleUIState");

        // Switch to default UI state if not editing or creating a new customer
        if (!inEditCustomerState && !inNewCustomerState) {
            System.out.println("                 Toggling to default UI state.");

            // Disable form fields
            textFieldName.setDisable(true);
            textFieldPhoneNumber.setDisable(true);
            textFieldAddress.setDisable(true);
            textFieldPostalCode.setDisable(true);
            comboboxCountry.setDisable(true);
            comboboxStateProvince.setDisable(true);
            buttonSaveCustomer.setDisable(true);

            // Enable control buttons
            buttonEditCustomer.setDisable(false);
            buttonDeleteCustomer.setDisable(false);
            buttonNewCustomer.setDisable(false);

            // Reset button labels
            buttonNewCustomer.setText("New Customer");
            buttonEditCustomer.setText("Edit Customer");

            // Empty the customer fields
            resetCustomerFields();
        }

        // If already in either the edit or new customer state, toggle back to default
        if (inNewCustomerState || inEditCustomerState) {
            System.out.println("                 Toggling to edit/new UI state.");

            // Enable form fields
            textFieldName.setDisable(false);
            textFieldPhoneNumber.setDisable(false);
            textFieldAddress.setDisable(false);
            textFieldPostalCode.setDisable(false);
            comboboxCountry.setDisable(false);
            comboboxStateProvince.setDisable(false);
            buttonSaveCustomer.setDisable(false);

            // Disable control buttons
            buttonDeleteCustomer.setDisable(true);

            // Empty customer fields
            resetCustomerFields();
        }

        // If in new customer state, update related UI elements
        if (inNewCustomerState && !inEditCustomerState) {
            System.out.println("          In New Customer State.");
            buttonEditCustomer.setDisable(true);
            buttonNewCustomer.setText("Cancel New");
            textFieldCustomerId.setPromptText("Customer ID (db-gen)");
        }

        // If in edit customer state, update related UI elements
        if (inEditCustomerState && !inNewCustomerState) {
            System.out.println("                 In Edit Customer State.");
            buttonNewCustomer.setDisable(true);
            buttonEditCustomer.setText("Cancel Edit");
        }
    }

    /**
     * Resets all form fields to their initial, empty state.
     */
    private void resetCustomerFields() {
        System.out.println("[Method Call]    resetCustomerFields");

        // Empty the customer form fields
        System.out.println("                 Clearing customer form fields.");

        textFieldCustomerId.clear();
        textFieldName.clear();
        textFieldPhoneNumber.clear();
        textFieldAddress.clear();
        textFieldPostalCode.clear();
        comboboxCountry.getSelectionModel().clearSelection();
        comboboxStateProvince.getSelectionModel().clearSelection();
        comboboxCountry.setValue(null);
        comboboxStateProvince.setValue(null);
    }

    /**
     * Refreshes the TableView with the list of customers.
     */
    private void refreshTableView() {
        System.out.println("[Method Call]    refreshTableView");
        customers.clear();
        customers.addAll(CustomerDAO.selectAllCustomers());
        tableviewCustomers.setItems(FXCollections.observableArrayList(customers));
        tableviewCustomers.refresh();
        System.out.println("                 TableView refreshed.");
    }

    /**
     * Populates the country ComboBox with country names from the database.
     * Also sets up a listener to enable/disable the state/province ComboBox
     * based on the country selection and to populate state/province ComboBox when a country is selected.
     * <p></p>
     * LAMBDA: The lambda here was used as a more compact way to define the desired behavior in response to a listener
     * event.
     */
    private void populateCountryAndStateProvinceComboBoxes() {
        // Clear any pre-existing items and get the list of country names
        comboboxCountry.getItems().clear();
        List<String> countryNames = CountryDAO.selectAllCountryNames();

        // Iterate through the list to add each country name to the combobox options
        for (String countryName : countryNames) {
            comboboxCountry.getItems().add(countryName);
        }

        comboboxStateProvince.setDisable(true);

        // Add a listener to the combobox to enable/disable the comboboxStateProvince
        // based on whether a country is selected or not
        comboboxCountry.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            comboboxStateProvince.setDisable(newValue == null || newValue.isEmpty());

            if (newValue != null && !newValue.trim().isEmpty()) {
                // Populate State/Province ComboBox based on selected country
                String selectedCountryName = comboboxCountry.getValue();
                int selectedCountryId = CountryDAO.selectCountryIDByName(selectedCountryName);

                // Fetch the list of division names based on the selected country ID
                List<String> divisionNames = DivisionDAO.selectDivisionNamesByCountryID(selectedCountryId);

                // Clear any pre-existing items
                comboboxStateProvince.getItems().clear();

                // Populate the ComboBox with fetched division names
                comboboxStateProvince.setItems(FXCollections.observableArrayList(divisionNames));
            }
        });
    }

    /**
     * Switches the current scene to the Appointments view when the Appointments navigation tab is clicked.
     *
     * @param actionEvent A click event on the Appointments Navigation Tab.
     */
    public void onAppointmentsTabButtonClick(ActionEvent actionEvent) {
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Appointments");
        SceneSwap.swapScene(currentStage, "../view/Appointments.fxml");
    }

    /**
     * Switches the current scene to the Reports view when the Reports navigation tab is clicked.
     *
     * @param actionEvent A click event on the Reports Navigation Tab.
     */
    public void onReportsTabButtonClick(ActionEvent actionEvent) {
        Stage currentStage = Main.getPrimaryStage();
        currentStage.setTitle("Wisebook - Reports");
        SceneSwap.swapScene(currentStage, "../view/Reports.fxml");
    }

    /**
     * Responds to the click event for the Edit Customer button.
     * Toggles the UI state between editing an existing customer and the default state.
     * If a customer is selected, it populates the UI fields with the selected customer's details.
     * If no customer is selected, an error alert is shown.
     *
     * @param actionEvent A click event on the Edit Customer button.
     */
    public void onEditCustomerButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Edit Customer Button Clicked");
        textActionResultMessage.setText(null);

        // Get the selected customer
        Customer selectedCustomer = tableviewCustomers.getSelectionModel().getSelectedItem();


        // If the user selected a customer in the tableview to edit, toggle the edit state and set up the UI.
        if (selectedCustomer != null) {
            inEditCustomerState = !inEditCustomerState;
            toggleUIState();

            // If in edit state, populate the form with the selected customer's info.
            if (inEditCustomerState) {
                textFieldCustomerId.setText(Integer.toString(selectedCustomer.getId()));
                textFieldName.setText(selectedCustomer.getName());
                textFieldAddress.setText(selectedCustomer.getAddress());
                textFieldPhoneNumber.setText(selectedCustomer.getPhone());
                textFieldPostalCode.setText(selectedCustomer.getPostalCode());
                comboboxStateProvince.setValue(selectedCustomer.getStateProvince());
                comboboxCountry.setValue(selectedCustomer.getCountry());
            }
        } else {
            // Show alert dialog if no customer was selected for modification.
            AlertDialog.showAlert(Alert.AlertType.ERROR, "No Customer Selected", "Please select a customer to " +
                    "update.");
        }
    }

    /**
     * Responds to click event for New Customer button.
     * Toggles the UI state between creating a new customer and the default state.
     * Calls a method to update the UI elements based on the new state.
     *
     * @param actionEvent A click event on the New Customer button.
     */
    public void onNewCustomerButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   New Customer Button Clicked");
        textActionResultMessage.setText(null);

        // Switch between default and new customer state. Calls toggleUIState method to set UI accordingly.
        inNewCustomerState = !inNewCustomerState;
        toggleUIState();
    }

    /**
     * Responds to the click event for the Save Customer Button.
     * Depending on current UI state, either creates a new customer or updates an existing customer.
     */
    public void onSaveCustomerButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Save Customer Button Clicked");
        checkForEmptyFields();
        // Check UI State Context to perform correct action
        if (inNewCustomerState) {
            createNewCustomer();
        } else if (inEditCustomerState) {
            updateExistingCustomer();
        }
    }

    /**
     * Responds to the click event for the Delete Customer Button.
     * If a customer is selected, it shows a confirmation dialog before proceeding to delete the customer.
     * If no customer is selected, an error alert is shown. The UI is updated upon successful deletion.
     *
     * @param actionEvent A click event on the Delete Customer button.
     */
    public void onDeleteCustomerButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Delete Customer Button Clicked");
        textActionResultMessage.setText(null);
        Customer selectedCustomer = tableviewCustomers.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            AlertDialog.showAlert(AlertType.ERROR, "No Customer Selected", "Please select the customer record you " +
                    "would like to delete.");
            return;
        }

        // Prevent accidental deletion with a confirmation dialog
        Optional<ButtonType> result = AlertDialog.showConfirmationDialog("Confirm Deletion", "Delete selected " +
                "customer?");

        // Only proceed if user clicks yes
        if (result.isPresent() && result.get() == ButtonType.YES) {
            int selectedCustomerId = selectedCustomer.getId();
            String selectedCustomerName = selectedCustomer.getName();
            if (CustomerDAO.deleteCustomer(selectedCustomer.getId())) {
                tableviewCustomers.getItems().remove(selectedCustomer);
                textActionResultMessage.setText("Customer Record Deleted (ID: " + selectedCustomerId + " Name: " + selectedCustomerName + ")");
            } else {
                AlertDialog.showAlert(Alert.AlertType.ERROR, "Record Deletion Failed", "Unable to delete customer.");
            }
        }
    }

    /**
     * Handles the event for logout button clicks.
     * Logs out the current user by calling the {@code UserLogin.userLogout} method.
     *
     * @param actionEvent A click event on the logout button.
     */
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        System.out.println("[Action Event]   Logout Button Clicked");
        UserLogin.userLogout();
    }


    /**
     * Performs a validation check to ensure that all fields have been filled out in the customer form.
     * If any fields are empty, an alert dialog is shown and the user is asked to complete the form before trying again.
     */
    private void checkForEmptyFields() {
        System.out.println("[Method Call]    checkForEmptyFields");
        // Check if any of the required fields are empty or not selected
        if (textFieldName.getText().isEmpty() ||
                textFieldAddress.getText().isEmpty() ||
                textFieldPhoneNumber.getText().isEmpty() ||
                textFieldPostalCode.getText().isEmpty() ||
                comboboxCountry.getValue() == null ||
                comboboxStateProvince.getValue() == null) {
            // Pop up an error alert if there are any empty or unselected fields
            AlertDialog.showAlert(Alert.AlertType.ERROR, "Empty Fields", "The customer form contains empty fields." +
                    " Please complete the form before trying again.");
        }
    }

    /**
     * Gets data from the form fields and creates a new customer object. The new customer object is used in
     * conjunction with DAO methods to insert a new customer into the database. Upon success, the UI is refreshed
     * to show the updated list of customers. If insertion fails, an alert and UI message is displayed.
     */
    private void createNewCustomer() {
        System.out.println("[Method Call]    createNewCustomer");

        // Create an updated customer object using the data from the form fields and the ID from the selected customer
        Customer newCustomer = new Customer(
                null, // id yet to be assigned by db
                textFieldName.getText(),
                textFieldAddress.getText(),
                textFieldPhoneNumber.getText(),
                comboboxStateProvince.getValue(),
                textFieldPostalCode.getText(),
                comboboxCountry.getValue(),
                LocalDateTime.now(), // createDate
                UserLogin.currentUser, // createdBy
                Timestamp.valueOf(LocalDateTime.now()), // lastUpdate
                UserLogin.currentUser // lastUpdatedBy
        );

        // If insertion operation returns true, it was successful. Toggle back to default UI state and refresh/reset.
        if (CustomerDAO.insertCustomer(newCustomer)) {
            System.out.println("                 Insertion successful.");
            textActionResultMessage.setText("Customer added.");
            refreshTableView();
            inNewCustomerState = false;
            toggleUIState();
        } else {
            // If insertion fails, show alert dialog and prompt user to try again.
            AlertDialog.showAlert(Alert.AlertType.ERROR, "Insertion Failed", "Failed to add new customer. Please " +
                    "try again.");
            textActionResultMessage.setText("Failed to create.");
        }
    }

    /**
     * Updates an existing customer record. Gets the customer from the table, updates their data
     * with the form field values, and applies these changes to the database using DAO methods. Refreshes
     * the UI and displays a message indicating whether the update was successful or failed, with an added alert dialog
     * on failure.
     */
    private void updateExistingCustomer() {
        System.out.println("[Method Call]    updateExistingCustomer");

        // Get the selected customer from the TableView
        Customer selectedCustomer = tableviewCustomers.getSelectionModel().getSelectedItem();
        System.out.println("                 Selected Customer ID: " + (selectedCustomer != null ?
                selectedCustomer.getId() : "None"));

        // If no customer is selected, display an alert and exit the method
        if (selectedCustomer == null) {
            System.out.println("                 No customer selected. Exiting method.");
            AlertDialog.showAlert(Alert.AlertType.ERROR, "No Customer Selected", "Please select customer to " +
                    "edit.");
            return;
        }

        // If a customer is selected, create a customer object with updated details
        System.out.println("                 Creating updated customer object.");
        Customer updatedCustomer = new Customer(
                Integer.parseInt(textFieldCustomerId.getText()),
                textFieldName.getText(),
                textFieldAddress.getText(),
                textFieldPhoneNumber.getText(),
                comboboxStateProvince.getValue(),
                textFieldPostalCode.getText(),
                comboboxCountry.getValue(),
                null, // Default value for createDate - Not being updated, already exists in db
                "", // Default value for createdBy - Not being updated, already exists in db
                Timestamp.valueOf(LocalDateTime.now()),
                UserLogin.currentUser // lastUpdatedBy
        );

        // Update the customer in the database.
        // If update operation returns true, it was successful. Toggle back to default UI state and refresh/reset.
        System.out.println("                 Attempting to update customer in database.");
        if (CustomerDAO.updateCustomer(updatedCustomer)) {
            System.out.println("                 Update successful.");
            textActionResultMessage.setText("Record updated.");
            refreshTableView();
            inEditCustomerState = false;
            toggleUIState();
        } else {
            // If update fails, show alert dialog and prompt user to try again.
            System.out.println("                 Update failed.");
            AlertDialog.showAlert(Alert.AlertType.ERROR, "Update Failed.", "Failed to update customer. Please try " +
                    "again.");
            textActionResultMessage.setText("Update not applied.");
        }
    }

    /**
     * Converts a given LocalDateTime to its UTC equivalent.
     *
     * @param localDateTime The local date-time to convert.
     * @return The UTC equivalent of the given local datetime.
     */
    private LocalDateTime convertToUTC(LocalDateTime localDateTime) {
        System.out.println("[Method Call]    convertToUTC");
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}




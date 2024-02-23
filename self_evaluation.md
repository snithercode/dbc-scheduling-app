# Evaluation of Application Requirements

## A: GUI-Based Application
- **Location:** Entire application, see the project files in the IDE. GUI built using JavaFX & SceneBuilder.

## A1: Log-in Form
- `UserLogin.java`:
  - Username & Password Validation: `onLoginButtonClick()` initiates field check `validateInput()` and database authentication via `verifyUserCredentials()`.
  - Display Error Messages: `showLoginFailedAlert()`, `showEmptyFieldsAlert()`, `showUnexpectedErrorAlert()`
  - User Location Display: `textFieldRegion.setText(zoneId.toString())` in `initialize()`
  - Localization (English/French): `localizationService.getTranslation()` gets translations for the user based on system locale.


## A2:Customer Record Functionalities

- `Customers.java`:
  - **Add, Update, Delete Customer Records:**
    - Adding: `onNewCustomerButtonClick()` toggles UI state, `onSaveCustomerButtonClick()` calls `createNewCustomer()` if `inNewCustomerState` is true.
    - Updating: `onEditCustomerButtonClick()` toggles UI state, `onSaveCustomerButtonClick()` calls `updateExistingCustomer()` if `inEditCustomerState` is true.
    - Deleting: `onDeleteCustomerButtonClick()` checks customer selection, confirms via dialog, and deletes via `CustomerDAO.deleteCustomer(selectedCustomer.getId())`.

  - **Text Fields for Customer Data:**
    - Customer IDs: Auto-generation is handled by database auto-increment rules and the new ID is retrievable post-insertion, shown in TableView after saving a new customer record.
    - Name: `textFieldName`
    - Address: `textFieldAddress`
    - Postal Code: `textFieldPostalCode`
    - Phone Number: `textFieldPhoneNumber`

  - **ComboBoxes for Customer Data:**
    - Country: `comboboxCountry`, populated in `populateCountryAndStateProvinceComboBoxes()`
    - State/Province: `comboboxStateProvince`, drop-down dynamically populated via `populateCountryAndStateProvinceComboBoxes()`

  - **Display Customer Data in TableView:**
    - `tableviewCustomers`, columns set up in `initialize()` and are populated with items from an observable list for UI updates.

  - **Custom Message on Customer Record Deletion:** `onDeleteCustomerButtonClick()` includes an alert dialog on failure or a custom UI message on successful customer record deletion: `textActionResultMessage.setText("Customer Record Deleted.");`


## A3a: Scheduling Functionality: Add, Update, and Delete

- `Appointments.java`:
    - Adding: `onNewAppointmentButtonClick()` toggles UI state, `onSaveAppointmentButtonClick()` calls `createNewAppointment()` if `inNewAppointmentState` is true.
    - Updating: `onEditAppointmentButtonClick()` toggles UI state, `onSaveAppointmentButtonClick()` calls `updateExistingAppointment()` if `inEditAppointmentState` is true.
    - Deleting: `onDeleteAppointmentButtonClick()` checks appointment selection, confirms via dialog, and deletes via `AppointmentDAO.deleteAppointment(selectedAppointment.getId())`.

## A3b: Appointment Schedule Views

- `Appointments.java`:
    - **View By Month/Week/All:**
        - `onAllAppointmentsRadioButtonClick()` triggers view refresh to show all appointments.
        - `onCurrentMonthAppointmentsRadioButtonClick()` triggers view refresh to show appointments for the current month.
        - `onCurrentWeekAppointmentsRadioButtonClick()` triggers view refresh to show appointments for the current week.
    - **Radio Button Logic:** `refreshTableView()` contains the conditionals determining the selected radio button and then sets the appointments in the table (week, month, or all).

## A3c: Time Zones

- `Appointments.java`:
    - **Adjust Appointment Times:** Below methods called throughout the class to convert appointment dates and times between UTC (for database storage) and the user's local time zone (for UI display). 
      - To Local Time Zone: `convertFromUTC()`
      - To UTC Time Zone: `convertToUTC()`

    - **Note:** When testing in the VM, I noticed MySQL was configured a little differently than it was on my local machine. In the VM, MySQL server timezone settings were (UTC+00:00) while my local MySQL configuration had by default been set to my local timezone (UTC-05:00). So the entire time I'd been doing extra manual conversions to make sure the times in my tables were in UTC. So, even though I have these methods in my code, I did end up removing instances where they were used since I didn't need them anymore.

## A3d: Input Validation and Logical Error Checks

- `Appointments.java`:
    - **Prevent Scheduling Outside Business Hours:** `populateStartTimeComboBox()` and `populateEndTimeComboBox()` both limit selections to appointment times within the range of operating hours for the business. 
    - **Avoid Overlapping Appointments:** When `onSaveAppointmentButtonClick` triggers, a list of `appointmentConflicts` is created before an insertion or update is made. Operation does not complete if a true conflict exists.


- `AppointmentDAO.java`:
  - **Defining Conflicts:** `selectAppointmentsWithRangeConflictForCustomerID()` method queries the database for any existing appointments that conflict with the desired appointment times for a given customer. The method checks for three types of overlaps:
    - Appointments that overlap the start of the desired time range.
    - Appointments entirely within the desired time range.
    - Appointments that overlap the end of the desired time range.
  - This approach ensures no overlapping appointments are scheduled for the same customer.


  - **Error Check Alert Messages:**
    - Outside Business Hours: Restrictions placed in UI to limit selections within range of business hours. No alert necessary.
    - Overlapping Appointment: `Appointments.showOverlappingAppointmentAlert()` appointment conflicts.
    - Invalid Credentials: `UserLogin.showLoginFailedAlert()` for incorrect username or password.


## A3e: Alerts
- `UserLogin.java` 
    - **Flag for Appointment Alert:** `upcomingAppointmentsAlertShown` is used to track if the upcoming appointment alert has been shown to the user.


- `Reports.java`:
    - **Alert for Appointment Within 15 Minutes:** `initialize()` checks `UserLogin.upcomingAppointmentsAlertShown` flag; if false, calls `showUpcomingAppointmentsAlert()` to display an alert dialog informs user of upcoming appointment(s).
    - **Upcoming Appointments Message:** `displayUpcomingAppointments()` updates `textActionResultMessage` with either "no upcoming appointment(s)" or the count of upcoming ones.
    - **Upcoming Appointment Details:** In cases where upcoming appointments exist, appointment details for those appointments will be visible in the TableView for the user's viewing.


## B: Lambda Expressions
- **Justifications in JavaDocs**:
    - `Appointments.java`:278
    - `Appointments.java`:678
    - `Customers.java`:308
    - `Reports.java`:101
    - `Reports.java`:160
    - `Reports.java`:364
    - `Reports.java`:392

## C: Track User Activity

- `UserLogin.java`:
    - Login attempts are logged using `logUserLoginAttempt()`, which records the username, date, time, and success status of each login attempt.
    - The log entries are written to `login_activity.txt` in the project's root folder.

## D: Javadoc Comments
- Javadoc comments throughout the codebase. `index.html` available in the javadocs folder.

## E: README.txt
- Location: README.txt file in the project root directory.
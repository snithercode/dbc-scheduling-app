package DAO;

import helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles data access operations for reporting purposes.
 */
public class ReportDAO {

    public static List<Map<String, Object>> showNumAppointmentsByTypeAndMonth() {
        String sql = "SELECT MONTHNAME(Start) as Month, Type, COUNT(*) as NumAppointments " +
                "FROM client_schedule.appointments " +
                "GROUP BY MONTHNAME(Start), Type " +
                "ORDER BY FIELD(Month, 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', " +
                "'September', 'October', 'November', 'December'), Type";

        return runReport(sql);
    }

    /**
     * Gets appointment counts by contact for each quarter, returning contact names with their respective quarterly
     * totals.
     */
    public static List<Map<String, Object>> showNumAppointmentsByContactAndQuarter() {
        List<Map<String, Object>> results = new ArrayList<>();

        String sql = "SELECT c.Contact_Name, " +
                "SUM(CASE WHEN QUARTER(a.Start) = 1 THEN 1 ELSE 0 END) AS Q1, " +
                "SUM(CASE WHEN QUARTER(a.Start) = 2 THEN 1 ELSE 0 END) AS Q2, " +
                "SUM(CASE WHEN QUARTER(a.Start) = 3 THEN 1 ELSE 0 END) AS Q3, " +
                "SUM(CASE WHEN QUARTER(a.Start) = 4 THEN 1 ELSE 0 END) AS Q4 " +
                "FROM appointments a " +
                "JOIN contacts c ON a.Contact_ID = c.Contact_ID " +
                "GROUP BY c.Contact_Name " +
                "ORDER BY c.Contact_Name";

        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("Contact", resultSet.getString("Contact_Name"));
                row.put("Q1", resultSet.getInt("Q1"));
                row.put("Q2", resultSet.getInt("Q2"));
                row.put("Q3", resultSet.getInt("Q3"));
                row.put("Q4", resultSet.getInt("Q4"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }


    /**
     * Runs a dynamic query with the given parameters and returns the result as a list of maps, each
     * representing a row of data. Adds a layer of flexibility intended to support some more diverse reporting needs
     * that may be implemented later on.
     */
    public static List<Map<String, Object>> runReport(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            PreparedStatement statement = JDBC.connection.prepareStatement(sql);

            // Loop through SQL placeholders to fill with real values
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            ResultSet resultSet = statement.executeQuery();

            // Get details for processing columns in each row of the result set
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Iterate over each row in the result set, extracting and storing column data
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = columnName.equalsIgnoreCase("NumAppointments") ?
                            resultSet.getInt(i) : resultSet.getObject(i); // gets an int value if col name is NumAppointments, or generic obj for all other cols
                    row.put(columnName, columnValue);
                }
                results.add(row); // Adds completed row map to results list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}

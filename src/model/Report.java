package model;

/**
 * Represents a report with an ID, name, and description.
 */
public class Report {

    /**
     * The report id.
     */
    private final int id;

    /**
     * The report name.
     */
    private final String reportName;

    /**
     * The report description.
     */
    private final String reportDescription;


    /**
     * Constructs a Report with an ID, name, and description.
     */
    public Report(int id, String reportName, String reportDescription) {
        this.id = id;
        this.reportName = reportName;
        this.reportDescription = reportDescription;
    }

    /**
     * Returns the report id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the report name.
     *
     * @return the report name
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * Returns the report description.
     *
     * @return the report description
     */
    public String getReportDescription() {
        return reportDescription;
    }
}

import javax.mail.*;
import java.sql.*;
import java.time.LocalDate;

public class SalaryHtmlReportNotifier {

    private Connection connection;
    private Statiment statiment;
    private ResultSet results = null;


    public SalaryHtmlReportNotifier(Connection databaseConnection, Statiment statiment) throws SQLException {
        this.connection = databaseConnection;
        this.statiment = statiment;

    }

    public void setResults(ResultSet results) {
        this.results = results;
    }

    public ResultSet getResults() {
        return results;
    }

    public Connection getConnection() {
        return connection;
    }

    public Statiment getStatiment() {
        return statiment;
    }
}

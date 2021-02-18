import java.sql.*;


//Создает запрос в БД и передает результат следующему Хендлеру

public class SelectSQLHandler implements Handler{
    private final Handler next;

    public SelectSQLHandler(Handler next) {
        this.next = next;
    }

    @Override
    public void handle(SalaryHtmlReportNotifier salaryHtmlReportNotifier) {
        PreparedStatement ps;
        try {
            ps = salaryHtmlReportNotifier.getConnection().prepareStatement("select emp.id as emp_id, emp.name as amp_name, sum(salary) as salary from employee emp left join" +
                    "salary_payments sp on emp.id = sp.employee_id where emp.department_id = ? and" +
                    " sp.date >= ? and sp.date <= ? group by emp.id, emp.name");

            ps.setString(0, salaryHtmlReportNotifier.getStatiment().getDepartmentId());
            ps.setDate(1, new java.sql.Date(salaryHtmlReportNotifier.getStatiment().getDateFrom().toEpochDay()));
            ps.setDate(2, new java.sql.Date(salaryHtmlReportNotifier.getStatiment().getDateTo().toEpochDay()));
            salaryHtmlReportNotifier.setResults(ps.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("SQLHandler Create");
        next.handle(salaryHtmlReportNotifier);
    }
}
import java.sql.SQLException;


//Генерирует результат запроса из БД и передает результат следующему Хендлеру

public class GenerateHtmlHandler implements Handler{
   SendHandler sendListeners;
    public GenerateHtmlHandler(SendHandler sendListeners) {
        this.sendListeners = sendListeners;
    }

    @Override
    public void handle(SalaryHtmlReportNotifier salaryHtmlReportNotifier) {
        StringBuilder resultingHtml = new StringBuilder();
        resultingHtml.append("<html><body><table><tr><td>Employee</td><td>Salary</td></tr>");
        double totals = 0;
        try {
            while (salaryHtmlReportNotifier.getResults().next()) {
                resultingHtml.append("<tr>");
                resultingHtml.append("<td>").append(salaryHtmlReportNotifier.getResults().getString("emp_name")).append("</td>");
                resultingHtml.append("<td>").append(salaryHtmlReportNotifier.getResults().getDouble("salary")).append("</td>");
                resultingHtml.append("</tr>");
                totals += salaryHtmlReportNotifier.getResults().getDouble("salary");
            }
            resultingHtml.append("<tr><td>Total</td><td>").append(totals).append("</td></tr>");
            resultingHtml.append("</table></body></html>");
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("GenerateHandler Create");
   sendListeners.sendTo(resultingHtml.toString() , salaryHtmlReportNotifier.getStatiment().getRecipients());
        System.out.println("Message sended");
    }
}

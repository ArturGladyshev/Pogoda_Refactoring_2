import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SalaryHtmlReportNotifier.class)

public  class TestSalaryHtmlReportNotifier {

    @Test
    public void test() throws Exception {
        //На основе фиктовной базы данный создается  SalaryHtmlReportNotifier
        Connection someFakeConnection = mock(Connection.class);
        ResultSet mockResultSet = getMockedResultSet(someFakeConnection);
        when(mockResultSet.getString("emp_name")).thenReturn("John Doe", "Jane Dow");
        when(mockResultSet.getDouble("salary")).thenReturn(100.0, 100.0, 50.0, 50.0);
        LocalDate dateFrom = LocalDate.of(2014, Month.JANUARY, 1);
        LocalDate dateTo = LocalDate.of(2014, Month.DECEMBER, 31);
        Statiment statiment = new Statiment("10", dateFrom, dateTo, "somebody@gmail.com");

//Запуск цепочки Хенждеолв и проверка отработки цепочки методов
        SendListenerImp sendListenerImp = mock(SendListenerImp.class);
        SalaryHtmlReportNotifier salaryHtmlReportNotifier = new SalaryHtmlReportNotifier(someFakeConnection , statiment );
        SendHandler sendHandler = mock(SendHandler.class);
       sendHandler.addSendListener(sendListenerImp);
        SelectSQLHandler selectSQLHandler = new SelectSQLHandler(new GenerateHtmlHandler(sendHandler));
        selectSQLHandler.handle(salaryHtmlReportNotifier);
        MessageHelper messageHelper = getMockedMimeMessageHelper();
        ArgumentCaptor<String> messageTextArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(sendHandler).sendTo(messageTextArgumentCaptor.capture(), any());

    }


    private ResultSet getMockedResultSet(Connection someFakeConnection) throws SQLException {
        //Тестирование фейковой базы данных
        PreparedStatement someFakePreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);
        when(someFakePreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(someFakeConnection.prepareStatement(anyString())).thenReturn(someFakePreparedStatement);
        when(mockResultSet.next()).thenReturn(true, true, false);
        return mockResultSet;
    }

        private MessageHelper getMockedMimeMessageHelper() throws Exception {
           //Тестирование внутренней логики слушателя
            SenderImp mockMailSender = mock(SenderImp.class);
            MimeMessage mockMimeMessage = mock(MimeMessage.class);
            when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
            MessageHelper mockMimeMessageHelper = mock(MessageHelper.class);
           whenNew(MessageHelper.class).withArguments(mockMimeMessage).thenReturn(mockMimeMessageHelper);
            SendListenerImp mockListener = mock(SendListenerImp.class);
            whenNew(SendListenerImp.class).withArguments("sdfdsf", "sdfsdf").thenReturn(mockListener);
            return mockMimeMessageHelper;
        }
}



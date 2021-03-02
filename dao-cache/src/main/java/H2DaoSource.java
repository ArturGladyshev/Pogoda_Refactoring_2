
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.h2.tools.Server;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*Класс для работы с базой данных. В базе хранится уникальное имя класса со значением и банарное
// представление возвращаемого значения, а также таблица числ Фибоначчи. База данных создается путем команд:

CREATE TABLE IF NOT EXISTS CACHE(
id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
methodname VARCHAR(200),
val BLOB(10000)

);

CREATE UNIQUE INDEX IF NOT EXISTS UNIQUE_CACHE ON CACHE(methodname);


CREATE TABLE IF NOT EXISTS FIBONACHCHI(
id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
number LONG(200)
);

CREATE UNIQUE INDEX IF NOT EXISTS UNIQUE_ID ON FIBONACHCHI(id);


 */

public class H2DaoSource implements Source {


    static final String FIND_LIST_FIBONACHCHI = "select * from fibonachchi c where c.number<=?";
    static final String FIND_MAX_FIBONACHCHI = "select max(number) from fibonachchi";
    static final String INSERT_NUMBER_FIBONACHCHi = "insert into fibonachchi (number) values (?)";
    static final String GET_VALUE_FIBONACHCHi = "select * from fibonachchi f where f.id=?";

    static final String INSERT_VALUE_METHOD_SQL = "insert into cache (methodname, val) values (?, ?)";
    static final String GET_VALUE_METHOD_SQL = "select * from cache c where c.methodname=?";
    private String url = null;
    private String name = null;
    private String password = null;

    public H2DaoSource(String url, String name, String password) {
        this.url = url;
        this.name = name;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Connection connection(H2DaoSource h2DaoSource) throws SQLException {
        final Connection connection = DriverManager.getConnection(h2DaoSource.getUrl(), h2DaoSource.getName(), h2DaoSource.getPassword());
        connection.setAutoCommit(true);

        return connection;
    }

    //Создание базы данных
    public void createDb() {
        String sql;
        try {
        sql = IOUtils.toString(H2DaoSource.class.getResourceAsStream("/data.sql"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = H2DaoSource.connection(this)
                .prepareStatement(sql)) {
            statement.execute();
            Server.createTcpServer().start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //Поиск значения по уникальным строке имени метода и возвращаемому значению
    @Override
    public Object findValueOfMethod(String nameMethod) throws NoFoundValueException {
        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(GET_VALUE_METHOD_SQL)) {
            statement.setString(1, nameMethod);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            return resultSetForValue(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //Поиск по стороке и байтовому массиву, когда байтовое предствление возвращаемого значения уже известно
    public Object findValueOfMethod(String nameMethod, byte[] bytes) throws NoFoundValueException {
        Object ob = null;
        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(GET_VALUE_METHOD_SQL)) {
            statement.setString(1, nameMethod);
            statement.setBytes(2, bytes);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            ob = resultSetForValue(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ob;
    }


    //Добавление строки в таблицу
    @Override
    public Object createValueMethod(String nameMethod, Object value) throws NoFoundValueException {
        Object ob = null;
        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(INSERT_VALUE_METHOD_SQL)) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(value);
            byte[] valueByte = byteStream.toByteArray();
            byteStream.close();
            objectStream.close();
            statement.setString(1, nameMethod);
            statement.setBytes(2, valueByte);
            statement.execute();
            ob = findValueOfMethod(nameMethod, valueByte);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ob;
    }


    //Обработка результата
    private Object resultSetForValue(ResultSet resultSet) throws NoFoundValueException {
        Object obj = null;
        try {
            String nameMethod = resultSet.getString(2);
            if (resultSet.wasNull()) {
                throw new NoFoundValueException("Не найдено имя метода");
            }
            byte[] bytes = resultSet.getBytes(3);
            ByteArrayInputStream byteReader = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(byteReader);
            obj = in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JdbcSQLNonTransientException ej) {
            throw new NoFoundValueException("Не найдено имя метода");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        } catch (Exception io) {
            io.printStackTrace();
        }
        return obj;
    }


   //Вывод значений в базе данных
    @Override
    public Set<Object> getValues() {
        HashSet<Object> values = null;
        try (Statement statement = H2DaoSource.connection(this).createStatement()) {
            statement.execute("select * from cache");
            ResultSet resultSet = statement.getResultSet();
            values = new HashSet<>();
            while (resultSet.next()) {
                Object val = resultSetForValue(resultSet);
                values.add(val);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return values;
    }

   //Поиск листа Фибоначчи в базе
    public List<Long> findNumberFibonachchi(Long index) throws NoFoundValueException {
        List<Long> listLong = new ArrayList<>();
        Long res = null;
        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(GET_VALUE_FIBONACHCHi)) {
            statement.setLong(1, index);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            res = (Long) resultSetForFibonachchi(resultSet , 2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(FIND_LIST_FIBONACHCHI)) {
            statement.setLong(1, res);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            listLong = resultListForFibonachchi(resultSet , res);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listLong;
    }

      //Обработка результата искокомго значения
        private Long resultSetForFibonachchi(ResultSet resultSet , int colomn) throws NoFoundValueException {
        Long fibonachchi = null;
        try {
            fibonachchi = resultSet.getLong(colomn);
            if (resultSet.wasNull()) {
                throw new NoFoundValueException("Нет значения");
            }
        } catch (SQLException throwables) {
            throw new NoFoundValueException("Нет значения");
        }
        return fibonachchi;
    }


   //Добавление первого значения , если таблица пуста
    private Long resultSetForMax(ResultSet resultSet , int colomn) throws TableEmptyException {
        Long fibonachchi = null;
        try {
            fibonachchi = resultSet.getLong(colomn);
            if (resultSet.wasNull()) {
                throw new TableEmptyException();
            }
        } catch (TableEmptyException t) {
            try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement
                    ("insert into fibonachchi (number) values(0)")) {
            }catch (SQLException th) {
                th.printStackTrace();
            }
        } catch (SQLException throwables) {
            throw new NoFoundValueException("Нет значения");
        }
        return fibonachchi;
    }


    //Добавление числа и всех предшествующих ему, если их не было в базе
    @Override
    public List<Long> addNumbersFibonachchi(Long index, List<Long> fibonachchiList) {
        Long maxDao = null;
        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(FIND_MAX_FIBONACHCHI)) {
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            maxDao = resultSetForMax(resultSet ,1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try (PreparedStatement statement = H2DaoSource.connection(this).prepareStatement(INSERT_NUMBER_FIBONACHCHi)) {
            for (int i = fibonachchiList.indexOf(maxDao); i < fibonachchiList.size(); ++i) {
                statement.setLong(1, fibonachchiList.get(i));
                statement.addBatch();
            }
            statement.executeBatch();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return fibonachchiList;
    }

      //Добавление результатов возвращаемого значения в лист и их возвращение
        private List<Long> resultListForFibonachchi(ResultSet resultSet, long max) throws NoFoundValueException {
        long l;
        List<Long> fibonachchiList = new ArrayList<>();
      try {
       while(resultSet.next()) {
           l = resultSet.getLong(2);
            fibonachchiList.add(l);
        }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return fibonachchiList;
    }

}
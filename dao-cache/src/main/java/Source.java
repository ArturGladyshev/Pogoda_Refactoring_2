import java.util.List;
import java.util.Set;

//Интерфейс для классов, работающих с кэшем данных
public interface Source {

    public Object findValueOfMethod(String nameMethod);
public Object createValueMethod (String nameMethod, Object value);
    public Set<Object> getValues();
public List<Long> findNumberFibonachchi(Long number);
   public List<Long> addNumbersFibonachchi(Long number , List<Long> fibonachchi);
}

import java.util.ArrayList;
import java.util.List;

//Метод для поиска чисел Фибоначчи
public class ListFibonachchiImp implements ListFibonachchi{
    public long fibonachchi(long n) {
        if (n <= 1l) return n;
        else return fibonachchi(n-1l) + fibonachchi(n-2l);
    }


    public List<Long> searchFibonachchi(long n) {
     List<Long> list = new ArrayList<>();
     for (long i = 0; i < n; i++){
           long f = fibonachchi(i);
            if ( f < 0 || f >= Long.MAX_VALUE) {throw new IllegalArgumentException(); }
       list.add(f);
    }
  return list;
    }
}

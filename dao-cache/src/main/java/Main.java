import org.junit.Test;

public class Main {
    @Test
    public void testing() {
        SumOfNumbersImp s = new SumOfNumbersImp();
        SumOfNumbers sumOfNumberImp = ProxyCache.getProxyInstance(s);
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 10));
        System.out.println(sumOfNumberImp.sumOfNumbers(200, 10));
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 200));
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 10));
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 10));
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 10));
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 10));
        System.out.println(sumOfNumberImp.sumOfNumbers(40, 40));
        System.out.println(sumOfNumberImp.printNull());
        System.out.println(sumOfNumberImp.sumOfNumbers(10, 10f));
        System.out.println(sumOfNumberImp.printNull());
    }

    @Test
    public void testing2() {
        ListFibonachchiImp l = new ListFibonachchiImp();
        ListFibonachchi l2 = ProxyCache.getProxyInstance(l);
       l2.searchFibonachchi(8l );
       l2.searchFibonachchi(15l);
      l2.searchFibonachchi(1l);
       l2.searchFibonachchi(7l);
       l2.searchFibonachchi(20l);
       l2.searchFibonachchi(15l);
      l2.searchFibonachchi(10l);
        l2.searchFibonachchi(21l);
    }
    }

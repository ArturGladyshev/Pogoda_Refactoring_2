import java.util.List;

public interface ListFibonachchi {
    public long fibonachchi(long n);

    @Cachable(value = H2DaoSource.class , isFindFibonachch = true)
    public List<Long> searchFibonachchi(long n);
}

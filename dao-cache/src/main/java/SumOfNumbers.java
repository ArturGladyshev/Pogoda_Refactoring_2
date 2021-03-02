public interface SumOfNumbers {
    @Cachable(H2DaoSource.class)
    int sumOfNumbers(int one, int two);

    @Cachable(H2DaoSource.class)
    int sumOfNumbers(int one, double two);

    @Cachable(H2DaoSource.class)
    default Integer printNull() {
        Integer n = null;
        return  n;
    }

}

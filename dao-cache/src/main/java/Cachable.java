import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cachable {

    Class<? extends Source> value();
boolean isFindFibonachch() default false;
    String url() default "jdbc:h2:~/test";
String user() default "user";
String password() default " ";

}



import java.lang.reflect.*;
import java.util.List;

//Класс кэширует данные исходя из логики интерфейса Source
public class ProxyCache implements InvocationHandler {
    private Object delegate;
    private Source source;
    private String previosMethodName = null;

    public ProxyCache(Object delegate) {
        this.delegate = delegate;

    }

    protected static <T> T getProxyInstance(Object delegate) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), delegate.getClass().getInterfaces(),
                new ProxyCache(delegate));
    }


    public void setProperty(Cachable cachableAnnotation) {
        // Проверка класса и устноавка настроек при первом вызове метода Invoke или при смене состояния объекта source
        Class sourseValue = cachableAnnotation.value();

        if (sourseValue.getName().equals("H2DaoSource")) {
            H2DaoSource h2DaoSourceThis = (H2DaoSource) source;
            if (source == null) {
                H2DaoSource h2DaoSource = new H2DaoSource(cachableAnnotation.url(), cachableAnnotation.user(), cachableAnnotation.password());
         h2DaoSource.createDb();
          source = h2DaoSource;
            } else if (h2DaoSourceThis.getUrl() != cachableAnnotation.url() || h2DaoSourceThis.getName() != cachableAnnotation.url()) {
                h2DaoSourceThis.setName(cachableAnnotation.user());
                h2DaoSourceThis.setUrl(cachableAnnotation.url());
                h2DaoSourceThis.setPassword(cachableAnnotation.password());
              h2DaoSourceThis.createDb();
                source = h2DaoSourceThis;
            }
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object value = null;
        Object val = null;
        try {
            if (!method.isAnnotationPresent(Cachable.class)) {
                System.out.println("Метод без аннотации Cache");
                    return method.invoke(delegate, args);
                }

            Cachable cachableAnnotation = method.getAnnotation(Cachable.class);
            if (previosMethodName == null && previosMethodName != method.getName()) {
                this.setProperty(cachableAnnotation);
            }
                previosMethodName = method.getName();
            if(cachableAnnotation.isFindFibonachch() == true) {
                try {
                    value = (List<Long>)source.findNumberFibonachchi((Long)args[0]);
                    System.out.println("Вернулось значение из кэша" + value);
                    return value;
                }catch (NoFoundValueException em) {
                    val = method.invoke(delegate, args);
                    value = (List<Long>)source.addNumbersFibonachchi((Long)args[0], (List<Long>)val);
                    System.out.println("Добавлено значение в кэш " + value);
               return value;
                }
            }

            Pair<Method, Object> key = new Pair<Method, Object>(method, args);
            try {
            value = source.findValueOfMethod(key.toString());
        }catch (NoFoundValueException em) {
                val = method.invoke(delegate, args);
           value = source.createValueMethod(key.toString(), val) ;
        }
        }  catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return value;
    }


}
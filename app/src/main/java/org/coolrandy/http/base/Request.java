package org.coolrandy.http.base;

/**
 * Created by admin on 2016/1/7.
 * 网络请求类：是一个抽象类，用于扩展
 * 对于网络请求而言，用户请求得到的数据格式是不确定的，可能是xml或json或raw text的，所以要做成泛型的
 * 对于Response而言，返回的数据类型都是Stream，也就是原始数据为二进制流，所以针对此必须预留方法来解析
 * Response返回的具体类型，这里返回类型虽然不一样，但处理逻辑是一样的
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * http请求方法的枚举，这里只考虑常用的4种：get、post、put、delete
     */
    public static enum HttpMethod{

        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        /*http request type*/
        private String mHttpMethod = "";
        private HttpMethod(String method){

            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 优先级枚举，请求将会按照优先级由高到低执行，以FIFO的顺序执行
     */
    public enum Priority{

        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * 对于POST或PUT参数的默认编码格式
     */
    private static final String DEFAULT_ENCODE = "utf-8";


}

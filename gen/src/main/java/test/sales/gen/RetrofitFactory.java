package test.sales.gen;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitFactory {
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static ObjectMapper objectMapper;

    public static OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Interceptor connectionInterceptor = chain -> chain.proceed(chain.request().newBuilder().addHeader("Connection", "close").build());
            okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieManager(context)).connectTimeout(8, TimeUnit.SECONDS).addInterceptor(loggingInterceptor).addInterceptor(connectionInterceptor).build();
        }
        return okHttpClient;
    }

    public static Retrofit getInstance(Context context) {
        if (retrofit == null)
            retrofit =
                    new Retrofit.Builder()
                            .baseUrl("http://172.20.51.176:8081/sales_web/")
                            .addConverterFactory(JacksonConverterFactory.create(getObjectMapper()))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(getOkHttpClient(context))
                            .build();
        return retrofit;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null)
            objectMapper = new ObjectMapper();
        return objectMapper;
    }
}
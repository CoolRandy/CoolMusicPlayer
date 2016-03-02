package com.android.volley.toolbox;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2016/3/2.
 */
public class OkHttpStack extends HurlStack {

    private final OkHttpClient okHttpClient;

    public OkHttpStack(){

        this(new OkHttpClient());
    }

    public OkHttpStack(OkHttpClient okHttpClient){
        if (okHttpClient == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.okHttpClient = okHttpClient;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        OkUrlFactory okUrlFactory = new OkUrlFactory(okHttpClient);
        return okUrlFactory.open(url);
    }


}

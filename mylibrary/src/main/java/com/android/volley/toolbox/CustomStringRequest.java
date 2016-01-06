package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.UnsupportedEncodingException;

/**
 * Created by ${randy} on 2015/9/13.
 */
public class CustomStringRequest extends Request<String> {

    private long cacheTime;
    private final Response.Listener<String> mListener;

    /**
     *
     * @param method
     * @param url
     * @param listener
     * @param errorListener
     */
    public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(method, url, errorListener);
        mListener = listener;
        setShouldCache(false);
    }

    /**
     *
     * @param method
     * @param url
     * @param listener
     * @param errorListener
     * @param cacheTime
     */
    public CustomStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, long cacheTime) {
        super(method, url, errorListener);
        mListener = listener;
        if(cacheTime > 0) {
            this.cacheTime = cacheTime;
            setShouldCache(true);
        }else{
            setShouldCache(false);
        }
    }

    public CustomStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void deliverResponse(String response) {
        if(mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;

        if(response.headers.containsKey("Set-Cookie")){
            String sessionId = response.headers.get("Set-Cookie");
            if(sessionId != null && sessionId.length() > 0){
            }
        }


        try{
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

        }catch (UnsupportedEncodingException e){
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));//��ʱ����
    }
}

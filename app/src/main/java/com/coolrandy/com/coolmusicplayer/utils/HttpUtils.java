package com.coolrandy.com.coolmusicplayer.utils;

import android.util.Log;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


/**
 * Created by ${randy} on 2015/9/13.
 * desc: 网络请求工具
 */
public class HttpUtils {

    private static final String NET_TAG = "net_tag";


    public static <T> void get(String url,final Class<T> bean, final RequestCallBack callback, RequestQueue requestQueue, int connectTimeout){
        try {
            Log.i(NET_TAG, "**********************网络请求 start***********************");
            Log.i(NET_TAG, "请求方式--Get");
            //处理url参数  这里不需要额外处理
            //url = urlGetParam(urlParam, url);
            Log.i(NET_TAG, "请求Url--" + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(NET_TAG, "onResponse:" + response);
                    Log.i(NET_TAG, "**********************网络请求 end success ***********************");

                    Object responseBeanObject = null;
                    try {
                        //TODO 可以看到这里采用的是Gson来解析
                        responseBeanObject = new Gson().fromJson(response, bean);
                        Log.e("TAG", "responseBeanObject : " + responseBeanObject.toString());
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }

                    if (responseBeanObject == null) {
                        if (callback != null) {
                            callback.onFail(new VolleyError());
                            return;
                        }
                    }
                    if (callback != null) {
                        callback.onSuccess(responseBeanObject);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(NET_TAG, "onErrorResponse:" + error.toString());
                    Log.i(NET_TAG, "**********************网络请求 end fail***********************");
                    error.printStackTrace();
                    if (callback != null) {
                        callback.onFail(error);
                    }
                }
            });
            if (connectTimeout != 0 && connectTimeout != DefaultRetryPolicy.DEFAULT_TIMEOUT_MS) {// 默认10000
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(connectTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
            //将请求添加到队列中  NPE
            Log.e("TAG", "请求数据成功: " + stringRequest.toString());//请求获取成功了
            //MyApplication.getmInstance().mRequestQueue.add(stringRequest);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get 处理url参数
     *
     * @param urlParam
     * @param url
     */
    public static String urlGetParam(final List<String> urlParam, final String url) {
        if (urlParam != null && urlParam.size() > 0) {

            try {
                //采用StringBuilder速度更快，不考虑线程安全问题
                final StringBuffer sBuffer = new StringBuffer();
                final StringBuffer urlBuffer = new StringBuffer(url);
                for (int i = 0; i < urlParam.size(); i++) {
                    sBuffer.append("{" + i + "}");
                    final int start = urlBuffer.indexOf(sBuffer.toString());
                    if (start == -1) {
                        sBuffer.delete(0, sBuffer.toString().length());
                        continue;
                    }
                    final int len = sBuffer.length();
                    if (urlParam != null && urlParam.size() > i) {
                        String s = urlParam.get(i);
                        if (s != null) {
                            s = URLEncoder.encode(String.valueOf(s), "utf-8");
                            urlBuffer.replace(start, start + len, s);
                            sBuffer.delete(0, sBuffer.toString().length());
                        }
                    }
                }
                Log.i(NET_TAG, "URL Param 处理之后:" + urlBuffer.toString());
                return urlBuffer.toString();
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return url;
    }

    public static <T> void get(String url, final RequestCallBack callback, RequestQueue requestQueue, int connectTimeout){
        try {
            Log.i(NET_TAG, "/***********************网络请求 start************************/");
            Log.i(NET_TAG, "请求方式--Get");
            //处理url参数  这里不需要额外处理
            //url = urlGetParam(urlParam, url);
            Log.i(NET_TAG, "请求Url--" + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(NET_TAG, "onResponse:" + response);
                    Log.i(NET_TAG, "/***********************网络请求 end success ************************/");

                    if (response == null) {
                        if (callback != null) {
                            callback.onFail(new VolleyError());
                            return;
                        }
                    }
                    if (callback != null) {
                        callback.onSuccess(response);
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(NET_TAG, "onErrorResponse:" + error.toString());
                    Log.i(NET_TAG, "/***********************网络请求 end fail************************/");
                    error.printStackTrace();
                    if (callback != null) {
                        callback.onFail(error);
                    }
                }
            });
            if (connectTimeout != 0 && connectTimeout != DefaultRetryPolicy.DEFAULT_TIMEOUT_MS) {// 默认10000
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(connectTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
            //将请求添加到队列中  NPE
            Log.e("TAG", "请求数据成功: " + stringRequest.toString());//请求获取成功了
            //MyApplication.getmInstance().mRequestQueue.add(stringRequest);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
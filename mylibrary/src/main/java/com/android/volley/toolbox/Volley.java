/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;

/**
 * 经过下面对代码的分析可以知道：我们平常大多采用Volly.newRequestQueue(context)的默认实现来构建 RequestQueue
 * 实际上从代码分析可以看到，我们完全可以自己抛开这个Volley工具类构建自定义的RequestQueue，采用自定义的HttpStatck，
 * 采用自定义的Network实现，采用自定义的 Cache 实现等来构建RequestQueue
 * 可以看到该框架具有很高的可扩展性
 */
public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     * 创建一个默认的请求队列，可以设置一个最大字节的硬盘缓存
     * 分析这个方法可以看到：RequestQueue、HttpStack、Network、Cache都可以由我们自定义来构建请求队列，可扩展性非常强
     * 这里涉及到HttpClient和HttpURLConnection的选择：
     * 在 Froyo(2.2) 之前，HttpURLConnection 有个重大 Bug，调用 close() 函数会影响连接池，导致连接复用失效，所以在 Froyo 之前使用 HttpURLConnection 需要关闭 keepAlive
     * Gingerbread(2.3) HttpURLConnection 默认开启了 gzip 压缩，提高了 HTTPS 的性能，Ice Cream Sandwich(4.0) HttpURLConnection 支持了请求结果缓存
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        //下面的代码是设置请求头User-Agent 字段，设置形式为packageName/versionCode，这一块是针对HttpClient的，HttpURLConnection 默认是有 User-Agent 的
        //这里注意在2.1后一种可以获取系统默认的User-Agent，另一种就是在自定义的Request中重写getHeaders方法设置User-Agent
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }
        //得到一个HttpStack，然后通过它构造一个代表网络的具体实现：BasicNetwork
        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                //AndroidHttpClient在android5.0以后已经被移除了,所以只考虑使用HttpURLConnection对应的HurlStack
                //TODO 当然这里可以采用HttpClient来替代AndroidHttpClient  需要添加apache的httpclient的jar包
                /*HttpClient httpClient = new DefaultHttpClient();
                httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, userAgent);*/
                //TODO With httpcomponents 4.3 you should use the client builder to set the user agent
                HttpClient httpClient = HttpClients.custom().setUserAgent(userAgent).build();
                stack = new HttpClientStack(httpClient);
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }
        Network network = new BasicNetwork(stack);

        //构造一个代表缓存的基于Disk的具体实现DiskBasedCache，然后将网络对象和缓存对象传入构建一个RequestQueue，启动这个RequestQueue
        RequestQueue queue;
        if (maxDiskCacheBytes <= -1)
        {
        	// No maximum size specified 没有指定最大硬盘缓存大小
        	queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        }
        else
        {
        	// Disk cache size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network);
        }
        //创建队列完成后，启动该队列
        queue.start();

        return queue;
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, int maxDiskCacheBytes) {
        return newRequestQueue(context, null, maxDiskCacheBytes);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack)
    {
    	return newRequestQueue(context, stack, -1);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }

}


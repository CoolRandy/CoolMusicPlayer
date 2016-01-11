package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/1/7.
 */
public class DownloadRequest extends StringRequest {

    public DownloadRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);

        //如果下载文件较大，就让多尝试连接几次
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //关闭gzip  这里关闭gzip，分析认为是考虑到后面要做断点下载
        setmShouldGzip(false);
    }

    //目标url   是否恢复下载
    private String target;
    private boolean isResumed;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isResumed() {
        return isResumed;
    }

    public void setIsResumed(boolean isResumed) {
        this.isResumed = isResumed;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        File file = new File(target);
        final long fileLen = file.length();
        if(isResumed && fileLen > 0){
            Map<String, String> headers = new HashMap<>();
            headers.put("Range", "bytes=" + fileLen + "-");
            return headers;
        }
        return super.getHeaders();
    }
}

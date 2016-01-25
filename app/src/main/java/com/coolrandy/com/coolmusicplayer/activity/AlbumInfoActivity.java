package com.coolrandy.com.coolmusicplayer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolrandy.com.coolmusicplayer.R;
import com.coolrandy.com.coolmusicplayer.model.AlbumBean;
import com.coolrandy.com.coolmusicplayer.utils.TrackApi;
import com.coolrandy.com.coolmusicplayer.view.AVLoadingIndicatorView;
import com.coolrandy.com.coolmusicplayer.view.CircleImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by admin on 2016/1/22.
 * 专辑详情页
 */
public class AlbumInfoActivity extends AppCompatActivity {

    private static final String ALBUM_ID = "album_id";
    private static final String ALBUM_PAGE = "album_page";
    //这里依然采用OkHttp来请求数据
    private OkHttpClient okHttpClient;
    private Request request;
    private String url;
    @InjectView(R.id.load_layout)
    public LinearLayout loadLayout;
    @InjectView(R.id.avloadingIndicatorView)
    public AVLoadingIndicatorView indicatorView;
//    @InjectView(R.id.test)
//    public TextView textView;
    @InjectView(R.id.page_imageview)
    public CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_info_layout);
        ButterKnife.inject(this);
//        initView();
        initData();
        okHttpClient = new OkHttpClient();
        requestGetData(url);
    }

    private void initData(){
        long albumId = getIntent().getLongExtra(ALBUM_ID, 0);
        String albumPage = getIntent().getStringExtra(ALBUM_PAGE);
        url = TrackApi.ALBUM_INFO_URL + albumId;
        Picasso.with(this).load(albumPage).into(circleImageView);
    }

    private void initView(){


    }

    public void startAnim(){
        loadLayout.setVisibility(View.VISIBLE);
        indicatorView.setVisibility(View.VISIBLE);
    }

    public void stopAnim(){
        loadLayout.setVisibility(View.GONE);
        indicatorView.setVisibility(View.GONE);
    }

    /**
     * 发送Get请求
     * callback有两种回调方式，一种是call.execute,该方式不会开启新的线程，需手动开启，然后再线程中调用该方式
     * 另外一种是使用下面的方式call.enqueue
     * @param url
     */
    public void requestGetData(String url){
        request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        // 开启异步线程访问网络
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopAnim();
                    }
                });
                final String res = response.body().string();
                Log.e("TAG", "res--->" + res);


                if (null == res) {
                    stopAnim();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        textView.setText(res);
                    }
                });
            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopAnim();
                    }
                });
            }
        });
    }
}

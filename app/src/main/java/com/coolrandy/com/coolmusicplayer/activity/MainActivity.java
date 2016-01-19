package com.coolrandy.com.coolmusicplayer.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.coolrandy.com.coolmusicplayer.AlbumAdapter;
import com.coolrandy.com.coolmusicplayer.R;
import com.coolrandy.com.coolmusicplayer.model.AlbumTrack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * https://github.com/telecapoland/jamendo-android
 */
public class MainActivity extends AppCompatActivity {

    public static final String url = "http://api.jamendo.com/get2/id+name+url+image+rating+artist_name/album/json/?n=20&order=ratingweek_desc";
    private static final int REFRESHDATA = 10;
    private List<AlbumTrack> albumTracks = new ArrayList<>();
//    @InjectView(R.id.text)

    private OkHttpClient okHttpClient;
    private Request request;
    @InjectView(R.id.recycler_view)
    public RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;

    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 10){
                /*List<AlbumTrack> albumTrackList = (List <AlbumTrack>)msg.obj;
                for(AlbumTrack track: albumTrackList){
                    textView.setText(track.toString());
                    textView.setText("\n");
                }*/
//                textView.setText(albumTracks.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
//        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //创建默认的线性LayoutManager  通过布局管理器LayoutManager控制显示方式
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //确定每个item的高度是固定的，以提升性能
        recyclerView.setHasFixedSize(true);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        recyclerView.addItemDecoration(new DividerItemDecoration(
//                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        //设置adapter
        albumAdapter = new AlbumAdapter(this, albumTracks);
        recyclerView.setAdapter(albumAdapter);

        okHttpClient = new OkHttpClient();

        requestGetData(url);

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
                String res = response.body().string();
                Log.e("TAG", "res--->" + res);
                albumTracks = new Gson().fromJson(res, new TypeToken<List<AlbumTrack>>(){}.getType());
                Log.e("TAG", "albumTracks--->" + albumTracks.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        albumAdapter.setAlbumList(albumTracks);
                    }
                });

//                Message msg = new Message();
//                msg.what = REFRESHDATA;
//                msg.obj = albumTracks;
//                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

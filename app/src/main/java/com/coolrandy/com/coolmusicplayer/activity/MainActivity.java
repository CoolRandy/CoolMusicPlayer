package com.coolrandy.com.coolmusicplayer.activity;

import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.OkHttpStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coolrandy.com.coolmusicplayer.AlbumAdapter;
import com.coolrandy.com.coolmusicplayer.R;
import com.coolrandy.com.coolmusicplayer.model.AlbumBean;
import com.coolrandy.com.coolmusicplayer.utils.HttpUtils;
import com.coolrandy.com.coolmusicplayer.utils.RequestCallBack;
import com.coolrandy.com.coolmusicplayer.view.AVLoadingIndicatorView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * https://github.com/telecapoland/jamendo-android
 */
public class MainActivity extends AppCompatActivity {

    public static final String url = "http://api.jamendo.com/get2/id+name+url+image+rating+artist_name/album/json/?n=20&order=ratingweek_desc";
    private static final int REFRESHDATA = 10;
    private static final String ALBUM_ID = "album_id";
    private static final String ALBUM_PAGE = "album_page";
    private List<AlbumBean> albumBeans = new ArrayList<>();

    private RequestQueue requestQueue;
    private OkHttpClient okHttpClient;
    private Request request;
    @InjectView(R.id.recycler_view)
    public RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    @InjectView(R.id.avloadingIndicatorView)
    public AVLoadingIndicatorView indicatorView;
    @InjectView(R.id.load_layout)
    public LinearLayout linearLayout;

    private boolean reqWithVolley = true;

    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("TAG", "receive data");
            if(msg.what == REFRESHDATA){
                List<AlbumBean> albumTrackList = (List <AlbumBean>)msg.obj;
                for(AlbumBean track: albumTrackList){
                    Log.e("TAG", "track id:" + track);
                }
                albumAdapter.setAlbumList(albumTrackList);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "点击导航栏", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.share:
//                        if(item.getIcon().equals(ContextCompat.getDrawable(MainActivity.this, R.mipmap.share_unpressed))){
//                        if(item.getIcon().equals(getResources().getDrawable(R.mipmap.share_unpressed))){
//                            item.setIcon(R.mipmap.share_pressed);
//                            Toast.makeText(MainActivity.this, "分享给你的小伙伴", Toast.LENGTH_SHORT).show();
//                        }else if(item.getIcon().equals(getResources().getDrawable(R.mipmap.share_pressed))){
//                            item.setIcon(R.mipmap.share_unpressed);
//                            Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
//                        }
                        Toast.makeText(MainActivity.this, "分享给你的小伙伴", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        requestQueue = Volley.newRequestQueue(this, new OkHttpStack());


        //创建默认的线性LayoutManager  通过布局管理器LayoutManager控制显示方式
        //可以通过线性布局管理器查找第一个可见的item或最后可见的item
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        //确定每个item的高度是固定的，以提升性能
        recyclerView.setHasFixedSize(true);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        recyclerView.addItemDecoration(new DividerItemDecoration(
//                this, DividerItemDecoration.VERTICAL_LIST));
        //设置adapter
        albumAdapter = new AlbumAdapter(this, albumBeans);
        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Toast.makeText(MainActivity.this, "点击专辑item", Toast.LENGTH_SHORT).show();
                if (albumBeans != null) {
                    Log.e("TAG", "album id-->" + albumBeans.get(position).getId());
                    Intent intent = new Intent(MainActivity.this, AlbumInfoActivity.class);
                    intent.putExtra(ALBUM_ID, albumBeans.get(position).getId());
                    intent.putExtra(ALBUM_PAGE, albumBeans.get(position).getImage());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(albumAdapter);

        //设置触摸监听
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        okHttpClient = new OkHttpClient();
        startAnim();
        if(reqWithVolley){
            requestWithVolley(url);
        }else {
            requestGetData(url);
        }
    }

    public void startAnim(){
        linearLayout.setVisibility(View.VISIBLE);
        indicatorView.setVisibility(View.VISIBLE);
    }

    public void stopAnim(){
        linearLayout.setVisibility(View.GONE);
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
                String res = response.body().string();
                Log.e("TAG", "res--->" + res);
                if (null == res) {
                    stopAnim();
                    return;
                }
                albumBeans = new Gson().fromJson(res, new TypeToken<List<AlbumBean>>() {
                }.getType());
                Log.e("TAG", "albumBeans--->" + albumBeans.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        albumAdapter.setAlbumList(albumBeans);
                    }
                });

//                Message msg = new Message();
//                msg.what = REFRESHDATA;
//                msg.obj = albumBeans;
//                mHandler.sendMessage(msg);
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

    private RequestCallBack requestCallBack;

    public void requestWithVolley(String url){

        requestCallBack = new RequestCallBack() {
            @Override
            public void onFail(Exception e) {
                startAnim();
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String response) {
                stopAnim();
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(response).getAsJsonArray();
                ArrayList<AlbumBean> albumBeans = new ArrayList<>();
                for (JsonElement obj: jsonArray){
                    AlbumBean albumBean = gson.fromJson(obj, AlbumBean.class);
                    albumBeans.add(albumBean);
                    Log.e("TAG", "albumBean: " + albumBean.toString());
                }
                Message msg = new Message();
                msg.what = REFRESHDATA;
                msg.obj = albumBeans;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onSuccess(Object response) {
                //TODO
            }
        };
        startAnim();
        HttpUtils.get(url, requestCallBack, requestQueue, 50000);
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
            Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

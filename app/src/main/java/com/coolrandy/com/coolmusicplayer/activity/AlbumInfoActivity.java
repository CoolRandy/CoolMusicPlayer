package com.coolrandy.com.coolmusicplayer.activity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.coolrandy.com.coolmusicplayer.R;
import com.coolrandy.com.coolmusicplayer.model.AlbumBean;
import com.coolrandy.com.coolmusicplayer.model.TrackBean;
import com.coolrandy.com.coolmusicplayer.service.MusicService;
import com.coolrandy.com.coolmusicplayer.utils.TrackApi;
import com.coolrandy.com.coolmusicplayer.view.AVLoadingIndicatorView;
import com.coolrandy.com.coolmusicplayer.view.CircleImageView;
import com.coolrandy.com.coolmusicplayer.view.WaterLampText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by admin on 2016/1/22.  这里不采用MediaController，该接口类呈现一个标准的接口，包含暂停、前进、后退
 * 专辑详情页
 */
public class AlbumInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String ALBUM_ID = "album_id";
    private static final String ALBUM_PAGE = "album_page";
    //这里依然采用OkHttp来请求数据
    private OkHttpClient okHttpClient;
    private Request request;
    private String url;
    //记录当前播放状态  这个会关联到service，后台播放状态  默认进来就是播放状态
    private boolean isPlaying = true;
    //播放动画
    private Animation operatingAnim;
    private ObjectAnimator objectAnimator;

    //音乐播放器
    private MediaPlayer mediaPlayer;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();

    //service
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;

    //track详情
    private List<TrackBean> trackBeans = new ArrayList<>();

    @InjectView(R.id.load_layout)
    public LinearLayout loadLayout;
    @InjectView(R.id.avloadingIndicatorView)
    public AVLoadingIndicatorView indicatorView;
//    @InjectView(R.id.test)
//    public TextView textView;
    @InjectView(R.id.page_imageview)
    public CircleImageView circleImageView;

    @InjectView(R.id.preward_play)
    public ImageView preImageView;
    @InjectView(R.id.forward_play)
    public ImageView forImageView;
    @InjectView(R.id.play_icon)
    public ImageView playImageView;
    @InjectView(R.id.seek_bar)
    public SeekBar seekbar;
    @InjectView(R.id.play_name)
    public WaterLampText waterLampText;

    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {

            //首先获取当前的播放位置
            timeElapsed = mediaPlayer.getCurrentPosition();
            //根据当前播放位置设置seekBar
            seekbar.setProgress((int)timeElapsed);
            //设置剩余时间
            double timeRemining = finalTime - timeElapsed;
            //TODO 可以设置一个TextView实时显示剩余播放时间
            durationHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_info_layout);
        ButterKnife.inject(this);
        initView();
        initData();
        okHttpClient = new OkHttpClient();
        requestGetData(url);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(null == playIntent){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    /**
     * 初始化view
     */
    private void initView(){
        //实例化mediaPlayer
        mediaPlayer =MediaPlayer.create(this, R.raw.star);
        finalTime = mediaPlayer.getDuration();
        seekbar.setMax((int) finalTime);
        seekbar.setClickable(false);
        seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        long albumId = getIntent().getLongExtra(ALBUM_ID, 0);
        String albumPage = getIntent().getStringExtra(ALBUM_PAGE);
        url = TrackApi.ALBUM_INFO_URL + albumId;
        Picasso.with(this).load(albumPage).into(circleImageView);

//        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.view_rotate);
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);

//        if(operatingAnim != null){
//            circleImageView.startAnimation(operatingAnim);
//        }
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        objectAnimator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0, 359);
        objectAnimator.setDuration(5000);
        objectAnimator.setRepeatCount(-1);
        //设置匀速转动
        objectAnimator.setInterpolator(linearInterpolator);
//        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();
        playImageView.setOnClickListener(this);
//        play();
//        musicService.playSong();
    }

    /**
     * connect to service
     */
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //获取service
            musicService = binder.getService();
            //TODO 传递数据过去
            musicService.setTrackUrl(trackBeans.get(0).getStream());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            musicBound = false;
        }
    };

    /**
     * 定义seekBar监听器
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            seekbar.setProgress(seekBar.getProgress());
//            durationHandler.postDelayed(updateSeekBarTime, 100);
        }
    };

    //对音乐的一些操作封装
    /**
     * 播放
     * @param
     */
    /*public void play(){

        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int)timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }*/

    /**
     * 暂停
     * @param
     */
    /*public void pause(){
        mediaPlayer.pause();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.play_icon:
                if(isPlaying){
                    playImageView.setImageResource(R.mipmap.pause);
                    isPlaying = false;
//                    pause();
                    mediaPlayer.pause();
                    //TODO 保存暂停状态，下次重新从暂停处开始
//                    circleImageView.clearAnimation();
//                    operatingAnim.setFillAfter(true);
//                    operatingAnim.cancel();
                    objectAnimator.pause();
                    Toast.makeText(this, "暂停", Toast.LENGTH_SHORT).show();
                }else {
                    playImageView.setImageResource(R.mipmap.play);
                    isPlaying = true;
//                    play();
                    musicService.playSong();
                    if(objectAnimator != null) {
//                        circleImageView.startAnimation(objectAnimator);
                        objectAnimator.resume();
                    }
                    Toast.makeText(this, "播放", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
                trackBeans = new Gson().fromJson(res, new TypeToken<List<TrackBean>>() {}.getType());

                if (null == res) {
                    stopAnim();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 首次进来播放返回列表的第一首，可以考虑随机选择播放
//                        waterLampText.setText(trackBeans.get(0).getName());
                        waterLampText.setText("岁月是场有去无回的旅行，好的坏的都是风景");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(playIntent);
        unbindService(musicConnection);
        musicService = null;
    }
}

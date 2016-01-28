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
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.coolrandy.com.coolmusicplayer.R;
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
public class AlbumInfoActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayerControl{

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
//    private MediaPlayer mediaPlayer;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    //activity and playback pause flags
    private boolean paused=false, playbackPaused=false;
    //service
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;

    private static final String ACTION_PLAY = "play";

    //track详情
    private ArrayList<TrackBean> trackBeans = new ArrayList<>();

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
//    @InjectView(R.id.seek_bar)
//    public SeekBar seekbar;
    @InjectView(R.id.play_name)
    public WaterLampText waterLampText;
    @InjectView(R.id.toolbar)
    public Toolbar toolbar;

    private ProgressBar progressBar;
    private SeekBar seekbar;
    //seekbar 拖动状态
    private boolean mDragging;

    private final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what){
                case 100:
                    pos = setProgress();
                    Log.e("TAG", "pos-->" + pos + ", mDragging-->" + mDragging + ", isPlaying-->" + isPlaying());
                    if(!mDragging && isPlaying()){
                        msg = obtainMessage(100);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {

            //首先获取当前的播放位置
            timeElapsed = musicService.mediaPlayer.getCurrentPosition();
            //根据当前播放位置设置seekBar
            progressBar.setProgress((int)timeElapsed);
            //设置剩余时间
            double timeRemining = finalTime - timeElapsed;
            //TODO 可以设置一个TextView实时显示剩余播放时间
            mHandler.postDelayed(this, 100);
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

    /**
     * 初始化view
     */
    private void initView(){
        //设置toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //TODO 添加Activity的切换动画  系统自带了一些动画效果，也可以自定义
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                    case R.id.action_shuffle:
                        //随机播放某首曲目
                        musicService.setShuffle();
                        break;
                }
                return true;
            }
        });
        //实例化mediaPlayer
//        mediaPlayer = MediaPlayer.create(this, R.raw.star);
//        finalTime = mediaPlayer.getDuration();

        //实例化seekBar
        progressBar = (ProgressBar)findViewById(R.id.seek_bar);
        if(progressBar != null){
            if(progressBar instanceof SeekBar){
                seekbar = (SeekBar)progressBar;
                seekbar.setClickable(true);
                seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
            }
            //根据实际歌曲长度进行换算
            progressBar.setMax(1000);
        }

//        seekbar = (SeekBar)findViewById(R.id.seek_bar);
//        seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
//        seekbar.setMax(1000);

    }

    /**
     * 初始化数据
     */
    private void initData(){

        //Activity跳转  通过intent传递数据
        long albumId = getIntent().getLongExtra(ALBUM_ID, 0);
        String albumPage = getIntent().getStringExtra(ALBUM_PAGE);
        url = TrackApi.ALBUM_INFO_URL + albumId;
        //加载封面图
        Picasso.with(this).load(albumPage).into(circleImageView);

//        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.view_rotate);
//        LinearInterpolator lin = new LinearInterpolator();
//        operatingAnim.setInterpolator(lin);

//        if(operatingAnim != null){
//            circleImageView.startAnimation(operatingAnim);
//        }
        //设置封面图转动
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        objectAnimator = ObjectAnimator.ofFloat(circleImageView, "rotation", 0, 359);
        objectAnimator.setDuration(5000);
        objectAnimator.setRepeatCount(-1);
        //设置匀速转动
        objectAnimator.setInterpolator(linearInterpolator);
//        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();
        playImageView.setOnClickListener(this);
    }

    /**
     * connect to service
     */
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //通过binder与service建立通信连接，执行相应的操作
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //获取service
            musicService = binder.getService();
            //TODO 传递数据过去
            musicService.setAlbumList(trackBeans);
            musicBound = true;

//            musicService.playSong();
//            setProgress();
//            mHandler.postDelayed(updateSeekBarTime, 100);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(null == playIntent){
            playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(ACTION_PLAY);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    /**
     * 设置进度
     * @return
     */
    private int setProgress(){

        if(musicService.mediaPlayer == null || mDragging){
            return 0;
        }

        int position = musicService.mediaPlayer.getCurrentPosition();
        int duration = musicService.mediaPlayer.getDuration();
        if(progressBar != null){
            if(duration > 0){
                //采用long型，避免溢出
                long pos = 1000L * position / duration;
                progressBar.setProgress((int)pos);
            }
            int percent = getBufferPercentage();
            progressBar.setSecondaryProgress(percent * 10);
        }

        return position;
    }

    /**
     * 定义seekBar监听器
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if(!fromUser){
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = musicService.mediaPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            seekTo((int)newPosition);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            mDragging = true;
            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            //主要是避免在拖动的过程中，handler仍然在执行
            mHandler.removeMessages(100);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            mDragging = false;
            setProgress();
            updatePlayPauseStatus();
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(100);
        }
    };

    /**
     * 更新播放按钮状态
     */
    private void updatePlayPauseStatus(){

        if(null == playImageView){
            return;
        }
        if(isPlaying()){
            playImageView.setImageResource(R.mipmap.play);
        }else {
            playImageView.setImageResource(R.mipmap.pause);
        }
    }

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


    /**
     * 播放上一曲、下一曲  调用MusicService中的方法
     * @param
     */
    //play previous
    private void playPrev(){
        musicService.playPrev();
        playbackPaused = false;
    }
    //play next
    private void playNext(){
        musicService.playNext();
        playbackPaused = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.play_icon:
                if(isPlaying){
                    playImageView.setImageResource(R.mipmap.pause);
                    isPlaying = false;
                    musicService.pauseSong();
//                    setProgress();
//                    mHandler.sendEmptyMessage(100);
                    objectAnimator.pause();
                    Toast.makeText(this, "暂停", Toast.LENGTH_SHORT).show();
                }else {
                    playImageView.setImageResource(R.mipmap.play);
                    isPlaying = true;
                    musicService.playSong();
                    //更新进度
//                    setProgress();
//                    mHandler.sendEmptyMessage(100);
                    mHandler.postDelayed(updateSeekBarTime, 100);
                    if(objectAnimator != null) {
                        objectAnimator.resume();
                    }
                    Toast.makeText(this, "播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.preward_play://TODO 其实这里可以做一个监听器
                //上一曲
                playPrev();
//                setProgress();
                mHandler.sendEmptyMessage(100);
                break;
            case R.id.forward_play:
                //下一曲
                playNext();
//                setProgress();
                mHandler.sendEmptyMessage(100);
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
                Log.e("TAG", "trackBeans: " + trackBeans.toString());
                if (null == res) {
                    stopAnim();
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 首次进来播放返回列表的第一首，可以考虑随机选择播放  退出时可考虑记下当前所听歌曲状态，下次进入该专辑
                        //TODO 播放上次所听歌曲
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
    public void start() {

        musicService.go();
    }

    @Override
    public void pause() {

        playbackPaused = true;
        musicService.pauseSong();
    }

    @Override
    public int getDuration() {
        if(musicService != null && musicBound && musicService.isPlaying()){
            return musicService.getDuration();
        }else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if(musicService != null && musicBound && musicService.isPlaying()){
            return musicService.getPos();
        }else {
            return 0;
        }

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seekTo(pos);
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        if(musicService != null && musicBound){
            return musicService.isPlaying();
        }else {
            return isPlaying;
        }
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.track_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_shuffle:
                musicService.setShuffle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(playIntent);
        unbindService(musicConnection);
        musicService = null;
    }

}


package com.coolrandy.com.coolmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.coolrandy.com.coolmusicplayer.model.TrackBean;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by admin on 2016/1/26.
 * https://github.com/SueSmith/android-music-player
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
                                    , MediaPlayer.OnCompletionListener{

    //media player
    private MediaPlayer mediaPlayer;
    //播放音乐的url
    private String trackUrl;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //记录播放状态
    private boolean isPlaying = false;
    //当前的播放位置
    private int songPos;
    //专辑歌曲列表
    private ArrayList<TrackBean> trackBeans;
    //随机播放标志
    private boolean shuffle = false;
    private Random random;

    @Override
    public void onCreate() {
        //create service
        super.onCreate();
        //初始化pos为0
        songPos = 0;
        //创建随机实例
        random = new Random();
        //创建mediaPlayer实例
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    /**
     * 传递整个专辑列表
     * @param trackBeans
     */
    public void setAlbumList(ArrayList<TrackBean> trackBeans){

        this.trackBeans = trackBeans;
    }

    /**
     * 传递当前播放单曲的url
     * @param url
     */
    public void setTrackUrl(String url){
        trackUrl = url;
    }

    /**
     * 初始化player
     */
    public void initMusicPlayer(){

        //设置一些MusicPlayer的一些属性
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //启动service之后，回调start play
        mp.start();
        //添加通知栏
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of track
        if(mediaPlayer.getCurrentPosition() > 0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public class MusicBinder extends Binder{

        public MusicService getService(){
            return MusicService.this;
        }
    }

    /**
     * 播放
     */
    public void playSong(){

        isPlaying = true;
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(trackUrl);
//            mediaPlayer.prepare();
            mediaPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
//        mediaPlayer.start();
    }

    /**
     * 暂停
     */
    public void pauseSong(){

        isPlaying = false;
        mediaPlayer.pause();
    }

    public int getDuration(){

        return mediaPlayer.getDuration();
    }
    //获取当前播放的位置
    public int getPos(){
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void seekTo(int pos){

        mediaPlayer.seekTo(pos);
    }

    public void go(){
        mediaPlayer.start();
    }

    /**
     * 播放前一曲
     */
    public void playPrev(){

        songPos--;
        if(songPos < 0){//循环
            songPos = trackBeans.size() - 1;
        }
        playSong();
    }

    /**
     * 播放下一曲
     */
    public void playNext(){

        if(shuffle){

            int newSong = songPos;
            while (newSong == songPos){
                newSong = random.nextInt(trackBeans.size());
            }
            songPos = newSong;
        }else if(songPos < trackBeans.size() - 1){
            songPos++;
        }else {
            songPos = 0;
        }
        playSong();
    }

    /**
     * 设置随机播放
     * @param
     */
    public void setShuffle() {
        if(shuffle){
            shuffle = false;
        }else {
            shuffle = true;
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}

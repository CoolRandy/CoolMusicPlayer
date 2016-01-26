package com.coolrandy.com.coolmusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.io.IOException;

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

    @Override
    public void onCreate() {
        //create service
        super.onCreate();
        //创建mediaPlayer实例
        mediaPlayer = new MediaPlayer();

        initMusicPlayer();
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

    public void setTrackUrl(String url){
        trackUrl = url;
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

        //start play
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
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

    public void pauseSong(){

        isPlaying = false;
        mediaPlayer.pause();
    }

    public int getDuration(){

        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
}

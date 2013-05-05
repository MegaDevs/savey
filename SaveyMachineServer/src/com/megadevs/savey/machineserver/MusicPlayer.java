package com.megadevs.savey.machineserver;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer implements MediaPlayer.OnPreparedListener {

    private static MusicPlayer instance;

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    private MediaPlayer mPlayer;
    private boolean ready = false;

    private MusicPlayer() {}

    public void init(Context context) {
//        try {
//            mPlayer = new MediaPlayer();
//            mPlayer.setDataSource(context, Uri.parse("assets://coffee.mp3"));
//            mPlayer.setOnPreparedListener(this);
//            mPlayer.prepareAsync();
//        } catch (Exception e) {
//            Logg.e("Error while initializing MediaPlayer: %s", e.getMessage());
//            e.printStackTrace();
//        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        ready = true;
    }

    public void play() {
        if (mPlayer != null && !mPlayer.isPlaying() && ready) {
            mPlayer.start();
        }
    }

    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying() && ready) {
            mPlayer.pause();
        }
    }

    public void reset() {
        if (mPlayer != null && ready) {
            mPlayer.reset();
        }
    }

    public void destroy() {
        if (mPlayer != null) {
            pause();
            mPlayer.release();
        }
    }
}

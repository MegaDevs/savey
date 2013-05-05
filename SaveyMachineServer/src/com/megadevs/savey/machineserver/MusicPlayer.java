package com.megadevs.savey.machineserver;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import com.megadevs.savey.machinecommon.Logg;

import java.lang.ref.WeakReference;

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
    private WeakReference<Context> context;

    private MusicPlayer() {}

    public void init(Context context) {
        this.context = new WeakReference<Context>(context);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        ready = true;
        mp.start();
    }

    public void play(Track track) {
        destroy();
        try {
            Context mContext = context.get();
            if (context == null) {
                return;
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(mContext, Uri.parse(track.getValue()));
            mPlayer.setOnPreparedListener(this);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            Logg.e("Error while initializing MediaPlayer: %s", e.getMessage());
            e.printStackTrace();
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

    public enum Track {
        ERROR("assets://error.mp3"),
        COINS("assets://coins.mp3"),
        MAKE_COFFEE("assets://make.mp3");

        String value;
        Track(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

}

package com.megadevs.savey.machineserver;

import android.content.Context;
import android.media.MediaPlayer;
import com.megadevs.savey.machinecommon.Logg;

import java.lang.ref.WeakReference;

public class MusicPlayer implements MediaPlayer.OnPreparedListener {

    private static MusicPlayer instance;
    private static WeakReference<Context> context;

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    private MediaPlayer mPlayer;

    private MusicPlayer() {}

    public static void init(Context context) {
        MusicPlayer.context = new WeakReference<Context>(context);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void play(Track track) {
        destroy();
        try {
            Context mContext = context.get();
            if (mContext == null) {
                return;
            }
            mPlayer = MediaPlayer.create(mContext, track.getValue());
            mPlayer.setOnPreparedListener(this);
            mPlayer.prepare();
        } catch (Exception e) {
            Logg.e("Error while initializing MediaPlayer: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    public void reset() {
        if (mPlayer != null) {
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
        ERROR(R.raw.error),
        COINS(R.raw.coins),
        MAKE_COFFEE(R.raw.espresso2);

        int value;
        Track(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }

}

package com.dictionary.codebhak;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * Created by ThangTB on 09/02/2015.
 */
public class SoundPlayer {
    private MediaPlayer p = null;
    private Context mContext;

    public SoundPlayer(Context context) {
        this.mContext = context;
        p = new MediaPlayer();
    }

    public void playSound(String fileName) {

        try {
            AssetFileDescriptor afd = mContext.getAssets().openFd("sound/"+fileName);
            p.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            p.prepare();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        p.start();
    }
}

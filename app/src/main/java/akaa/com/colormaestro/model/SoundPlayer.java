package akaa.com.colormaestro.model;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import akaa.com.colormaestro.R;

/**
 * Created by confo on 12/30/2017.
 */

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 5;

    private static SoundPool soundPool;
    private static int touchSound;
    private static int bombSound;
    private static int frenzySound;
    private static int bonusSound;
    private static int hpLossSound;
    private static int illConfirmedSound;
    private static int suspenseSound;

    public SoundPlayer(Context context){
        //Soundpool is deprecated in api lvl 21 (lollipop)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();
        }else{
            //SoundPool (int maxStreams,int streamType,int srcQuality)
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        touchSound = soundPool.load(context, R.raw.touchsound, 1);
        bombSound = soundPool.load(context,R.raw.bombsound, 1);
        frenzySound = soundPool.load(context,R.raw.frenzysound, 1);
        bonusSound = soundPool.load(context,R.raw.bonussound, 1);
        hpLossSound = soundPool.load(context,R.raw.losehpsound, 1);
        illConfirmedSound = soundPool.load(context,R.raw.illconfirmed, 1);
        suspenseSound = soundPool.load(context,R.raw.suspense, 1);
    }


    public void playTouchSound(){
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(touchSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playBombSound(){
        soundPool.play(bombSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playFrenzySound(){
        soundPool.play(frenzySound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playBonusSound(){
        soundPool.play(bonusSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHpLossSound(){
        soundPool.play(hpLossSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playIllSound(){
        soundPool.play(illConfirmedSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playSuspenseSound(){
        soundPool.play(suspenseSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}

package akaa.com.colormaestro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.game_modes.ClassicGame;

public class PauseMenu extends AppCompatActivity {

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_menu);
        GlobalVariables.continueMusic = false;

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha); //declaring animation variables
        //resume button setup
        Button resumeButton = (Button) findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    v.startAnimation(animAlpha);
                    //delay to allow animation to show (button feedback to user is important)
                    TimerTask delay = new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                        }
                    };
                    Timer delayTimer = new Timer();
                    delayTimer.schedule(delay, 100);
                }
            }
        });

        //Retry button setup
        Button retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    v.startAnimation(animAlpha);
                    //delay to allow animation to show (button feedback to user is important)
                    TimerTask delay = new TimerTask() {
                        @Override
                        public void run() {
                            relaunchGame();
                        }
                    };
                    Timer delayTimer = new Timer();
                    delayTimer.schedule(delay, 100);
                }
            }
        });


        //Home button setup
        Button homeButton = (Button) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    v.startAnimation(animAlpha);
                    //delay to allow animation to show (button feedback to user is important)
                    TimerTask delay = new TimerTask() {
                        @Override
                        public void run() {
                            launchMainMenu();
                        }
                    };
                    Timer delayTimer = new Timer();
                    delayTimer.schedule(delay, 100);
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        if(MainActivity.soundTrack != null) {
            if (GlobalVariables.continueMusic == false && MainActivity.soundTrack.isPlaying()) {
                MainActivity.soundTrack.pause();
            }
        }
        if(MainActivity.soundTrack2 != null) {
            if (GlobalVariables.continueMusic == false && MainActivity.soundTrack2.isPlaying()) {
                MainActivity.soundTrack2.pause();
            }
        }
        if(MainActivity.soundTrack3 != null) {
            if (GlobalVariables.continueMusic == false && MainActivity.soundTrack3.isPlaying()) {
                MainActivity.soundTrack3.pause();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(MainActivity.soundTrack != null) {
            if (MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false) {
                if (ClassicGame.classicPrefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack.start();
                }
            }
        }
        if(MainActivity.soundTrack2 != null) {
            if (MainActivity.soundTrack2 != null && MainActivity.soundTrack2.isPlaying() == false
                    && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack2ActivationThreshold && GlobalVariables.queryInt("score") < GlobalVariables.soundtrack3ActivationThreshold) {
                if (ClassicGame.classicPrefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack2.start();
                }
            }
        }
        if(MainActivity.soundTrack3 != null) {
            if (MainActivity.soundTrack3 != null && MainActivity.soundTrack3.isPlaying() == false && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack3ActivationThreshold) {
                if (ClassicGame.classicPrefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack3.start();
                }
            }
        }

    }

    private void launchMainMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        System.exit(0);
    }

    private void relaunchGame(){
        Intent intent = new Intent(this, ClassicGame.class);
        startActivity(intent);
        System.exit(0);
    }

}



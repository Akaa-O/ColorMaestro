package akaa.com.colormaestro;

/**
 * Created by Akachukwu Okonkwo on 9/18/2017.
 */

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Timer;
import java.util.TimerTask;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class MainActivity extends AppCompatActivity {

    private Button playButton;
    private Button statsButton;
    private Button helpButton;
    private Button settingsButton;
    private Button rateButton;

    private boolean buttonPressed = false; //prevents double pressing of the buttons


    private int mainBackground;

    public static MediaPlayer soundTrack;
    public static MediaPlayer soundTrack2;
    public static MediaPlayer soundTrack3;

    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.main);

        prefs = getApplicationContext().getSharedPreferences(GlobalVariables.appPreference,Context.MODE_PRIVATE);
        soundTrack = MediaPlayer.create(this,R.raw.mainsoundtrack);
        soundTrack.setLooping(true);
        if(prefs.getBoolean(GlobalVariables.musicSwitch,true)) {
            soundTrack.start();
        }

        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);  //declaring animation variables
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);


        //mainLayout.setBackgroundColor(Color.BLACK);
       mainLayout.setBackgroundResource(R.drawable.greybackground);

        playButton = (Button) findViewById(R.id.playButton); //button assignments
        statsButton = (Button) findViewById(R.id.statsButton);
        helpButton = (Button) findViewById(R.id.helpButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        rateButton = (Button) findViewById(R.id.rateButton);


        playButton.setOnClickListener(new View.OnClickListener(){  //onclick listeners for buttons
            @Override
            public void onClick(View view){
                if(!buttonPressed){
                    buttonPressed = true;
                    view.startAnimation(animRotate); //buttonclick animation
                    final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
                    final TimerTask buttonTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            launchModeSelect();
                        }
                    };
                    buttonTimer.schedule(buttonTimerTask,350);
                }

            }
        });
        statsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
                    final TimerTask buttonTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            launchStatsMenu();
                        }
                    };
                    buttonTimer.schedule(buttonTimerTask, 200);
                }
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
                    final TimerTask buttonTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            launchHelpMenu();
                        }
                    };
                    buttonTimer.schedule(buttonTimerTask, 200);
                }
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
                    final TimerTask buttonTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            launchSettingsMenu();
                        }
                    };
                    buttonTimer.schedule(buttonTimerTask, 200);
                }
            }
        });
        rateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
                    final TimerTask buttonTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            rateApp();
                        }
                    };
                    buttonTimer.schedule(buttonTimerTask, 200);
                }
            }
        });


    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(GlobalVariables.continueMusic == false && soundTrack.isPlaying()){
            soundTrack.pause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(soundTrack != null && soundTrack.isPlaying() == false){
            if(prefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                soundTrack.start();
            }
        }

    }
    private void launchModeSelect(){
        GlobalVariables.continueMusic = true;
        Intent intent = new Intent(this, ModeSelect.class);
        startActivity(intent);
    }

    private void launchSettingsMenu(){
        GlobalVariables.continueMusic = true;
        Intent intent = new Intent(this, SettingsMenu.class);
        startActivity(intent);
    }

    private void launchStatsMenu(){
        GlobalVariables.continueMusic = true;
        Intent intent = new Intent(this, StatsMenu.class);
        startActivity(intent);
    }

    private void launchHelpMenu(){
        GlobalVariables.continueMusic = true;
        Intent intent = new Intent(this, HelpMenu.class);
        startActivity(intent);
    }

    public void rateApp(){
        try{
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e){
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else{
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

}

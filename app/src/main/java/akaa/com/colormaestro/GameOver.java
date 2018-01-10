package akaa.com.colormaestro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.game_modes.ClassicGame;

public class GameOver extends AppCompatActivity {
    TextView finalScore;
    TextView bestScore;

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        GlobalVariables.continueMusic = false;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("playerData", Context.MODE_PRIVATE);
        int highScore = prefs.getInt(GlobalVariables.classicHighScore, 0);

        finalScore = (TextView) findViewById(R.id.finalScoreView);
        bestScore = (TextView) findViewById(R.id.bestScoreView);
        finalScore.setText(Integer.toString(GlobalVariables.queryInt("score")));
        bestScore.setText(Integer.toString(highScore));

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha); //declaring animation variables

        Button retryButton = (Button) findViewById(R.id.retryButtonG);
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


        Button homeButton = (Button) findViewById(R.id.homeButtonG);
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
        if(GlobalVariables.continueMusic == false && MainActivity.soundTrack.isPlaying()){
            MainActivity.soundTrack.pause();
        }
        if(GlobalVariables.continueMusic == false && MainActivity.soundTrack2.isPlaying()){
            MainActivity.soundTrack2.pause();
        }
        if(GlobalVariables.continueMusic == false && MainActivity.soundTrack3.isPlaying()){
            MainActivity.soundTrack3.pause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false){ //all activities opened from a game mode uses the game mode's preferences to avoid null pointers upon mode restart
            if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack.start();
            }
        }
        if(MainActivity.soundTrack2 != null && MainActivity.soundTrack2.isPlaying() == false
                && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack2ActivationThreshold && GlobalVariables.queryInt("score") < GlobalVariables.soundtrack3ActivationThreshold){
            if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack2.start();
            }
        }
        if(MainActivity.soundTrack3 != null && MainActivity.soundTrack3.isPlaying() == false && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack3ActivationThreshold){
            if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack3.start();
            }
        }

    }

    @Override
    public void onBackPressed(){

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

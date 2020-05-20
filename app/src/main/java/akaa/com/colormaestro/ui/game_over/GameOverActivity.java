package akaa.com.colormaestro.ui.game_over;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;

import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;
import akaa.com.colormaestro.ui.game_modes.classic.ClassicActivity;
import akaa.com.colormaestro.ui.main.MainActivity;

public class GameOverActivity extends AppCompatActivity implements GameOverView{
    TextView finalScore;
    TextView bestScore;

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        GlobalVariables.continueMusic = false;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("playerData", Context.MODE_PRIVATE);
        highScore = prefs.getInt(GlobalVariables.classicHighScore, 0);

        if(GlobalVariables.signedInAccount != null) {
            updateLeaderboards(highScore);
        }else{
            startSignInIntent();
        }
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
                if (ClassicActivity.classicPrefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack.start();
                }
            }
        }
        if(MainActivity.soundTrack2 != null) {
            if (MainActivity.soundTrack2 != null && MainActivity.soundTrack2.isPlaying() == false
                    && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack2ActivationThreshold && GlobalVariables.queryInt("score") < GlobalVariables.soundtrack3ActivationThreshold) {
                if (ClassicActivity.classicPrefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack2.start();
                }
            }
        }
        if(MainActivity.soundTrack3 != null) {
            if (MainActivity.soundTrack3 != null && MainActivity.soundTrack3.isPlaying() == false && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack3ActivationThreshold) {
                if (ClassicActivity.classicPrefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack3.start();
                }
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
        Intent intent = new Intent(this, ClassicActivity.class);
        startActivity(intent);
        System.exit(0);
    }

    private void updateLeaderboards(int highScore) {
        GlobalVariables.mLeaderboardsClient = Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this));
        GlobalVariables.mLeaderboardsClient.submitScore(getString(R.string.leaderboard_best_score), highScore);
    }

    public void startSignInIntent() {
        GlobalVariables.mGoogleSignInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = GlobalVariables.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, GlobalVariables.RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalVariables.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GlobalVariables.signedInAccount = result.getSignInAccount();
                updateLeaderboards(highScore);

            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Failed to Sign In to Google Play Services";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }
}

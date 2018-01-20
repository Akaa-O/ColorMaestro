package akaa.com.colormaestro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static akaa.com.colormaestro.MainActivity.RC_SIGN_IN;

public class StatsMenu extends AppCompatActivity {
    private TextView totalGamesPlayed;
    private TextView classicBest;

    private Button backButton;
    private Button leaderBoards;

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_menu);
        GlobalVariables.continueMusic = false;

        int classicHighScore = MainActivity.prefs.getInt(GlobalVariables.classicHighScore,0);
        int gamesPlayed = MainActivity.prefs.getInt(GlobalVariables.gamesPlayed,0);
        totalGamesPlayed = (TextView) findViewById(R.id.gamesplayedView);
        classicBest = (TextView) findViewById(R.id.cBestView);

        totalGamesPlayed.setText(Integer.toString(gamesPlayed));
        classicBest.setText(Integer.toString(classicHighScore));

        backButton = (Button) findViewById(R.id.statsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    finish();
                }
            }
        });

        leaderBoards = (Button) findViewById(R.id.leaderboardsButton);
        leaderBoards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLeaderboards();
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
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(MainActivity.soundTrack != null) {
            if (MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false) {
                if (MainActivity.prefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack.start();
                }
            }
        }
    }

    public void ShowLeaderboards() {
        if(MainActivity.signedInAccount != null) {
            MainActivity.mLeaderboardsClient = Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this));
            MainActivity.mLeaderboardsClient.getLeaderboardIntent(getString(R.string.leaderboard_best_score))
                    .addOnSuccessListener(new OnSuccessListener<Intent>() {
                        @Override
                        public void onSuccess(Intent intent) {
                            startActivityForResult(intent, MainActivity.RC_UNUSED);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleException(e, getString(R.string.leaderboards_exception));
                        }
                    });
        }else{
            startSignInIntent();
        }
    }

    private void handleException(Exception e, String details) {
        int status = 0;

        if (e instanceof ApiException) {
            ApiException  apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = getString(R.string.status_exception_error, details, status, e);

        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    public void startSignInIntent() {
        MainActivity.mGoogleSignInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = MainActivity.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                MainActivity.signedInAccount = result.getSignInAccount();

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

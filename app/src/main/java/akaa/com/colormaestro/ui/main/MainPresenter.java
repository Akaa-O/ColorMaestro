package akaa.com.colormaestro.ui.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;
import akaa.com.colormaestro.ui.game_modes.ModeSelectActivity;
import akaa.com.colormaestro.ui.help.HelpActivity;
import akaa.com.colormaestro.ui.settings.SettingsActivity;
import akaa.com.colormaestro.ui.stats.StatsActivity;

class MainPresenter {
    private MainView mainView;

    private boolean buttonPressed = false; //prevents double pressing of the buttons


    // tag for debug logging
    private static final String TAG = "TanC";

    MainPresenter(MainView view) {
        mainView = view;
    }

    void handleActivityResult(int requestCode, Intent data) {
        if (requestCode == GlobalVariables.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GlobalVariables.signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "Failed to Sign In to Google Play Services";
                }
                mainView.showAlertDialog(message);
            }
        }
    }

    void toggleButtonPressed() {
        buttonPressed = !buttonPressed;
    }

    void signIn(Context context) {
        GlobalVariables.mGoogleSignInClient = GoogleSignIn.getClient(context,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = GlobalVariables.mGoogleSignInClient.getSignInIntent();
        mainView.launchActivityResult(intent, GlobalVariables.RC_SIGN_IN);
    }

    void signInSilently(final Context context, Activity activity) {
        Log.d(TAG, "signInSilently()");

        GlobalVariables.mGoogleSignInClient.silentSignIn().addOnCompleteListener(activity,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult(), context);
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }

    void playButtonClicked(View view, final Context context) {
        if(!buttonPressed){
            buttonPressed = true;
            mainView.animate(view, AnimationUtils.loadAnimation(context, R.anim.anim_rotate)); //buttonclick animation
            //should be using a handler
            final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
            final TimerTask buttonTimerTask = new TimerTask() {
                @Override
                public void run() {
                    GlobalVariables.continueMusic = true;
                    Intent intent = new Intent(context, ModeSelectActivity.class);
                    mainView.launchActivity(intent);
                }
            };
            buttonTimer.schedule(buttonTimerTask,350);
        }
    }

    void statsButtonClicked(View view, final Context context) {
        if(!buttonPressed){
            buttonPressed = true;
            mainView.animate(view, AnimationUtils.loadAnimation(context, R.anim.anim_alpha)); //buttonclick animation
            //should be using a handler
            final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
            final TimerTask buttonTimerTask = new TimerTask() {
                @Override
                public void run() {
                    GlobalVariables.continueMusic = true;
                    Intent intent = new Intent(context, StatsActivity.class);
                    mainView.launchActivity(intent);
                }
            };
            buttonTimer.schedule(buttonTimerTask,350);
        }
    }

    void helpButtonClicked(View view, final Context context) {
        if(!buttonPressed){
            buttonPressed = true;
            mainView.animate(view, AnimationUtils.loadAnimation(context, R.anim.anim_alpha)); //buttonclick animation
            //should be using a handler
            final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
            final TimerTask buttonTimerTask = new TimerTask() {
                @Override
                public void run() {
                    GlobalVariables.continueMusic = true;
                    Intent intent = new Intent(context, HelpActivity.class);
                    mainView.launchActivity(intent);
                }
            };
            buttonTimer.schedule(buttonTimerTask,350);
        }
    }

    void settingsButtonClicked(View view, final Context context) {
        if(!buttonPressed){
            buttonPressed = true;
            mainView.animate(view, AnimationUtils.loadAnimation(context, R.anim.anim_alpha)); //buttonclick animation
            //should be using a handler
            final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
            final TimerTask buttonTimerTask = new TimerTask() {
                @Override
                public void run() {
                    GlobalVariables.continueMusic = true;
                    Intent intent = new Intent(context, SettingsActivity.class);
                    mainView.launchActivity(intent);
                }
            };
            buttonTimer.schedule(buttonTimerTask,350);
        }
    }

    void rateButtonClicked(View view, final Context context, final String packageName) {
        if(!buttonPressed){
            buttonPressed = true;
            mainView.animate(view, AnimationUtils.loadAnimation(context, R.anim.anim_alpha)); //buttonclick animation
            //should be using a handler
            final Timer buttonTimer = new Timer();  //timer to delay button function to allow full button animation to show
            final TimerTask buttonTimerTask = new TimerTask() {
                @Override
                public void run() {
                    try{
                        Intent rateIntent = rateIntentForUrl("market://details", packageName);
                        mainView.launchActivity(rateIntent);
                    }
                    catch (ActivityNotFoundException e){
                        Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details", packageName);
                        mainView.launchActivity(rateIntent);
                    }
                }
            };
            buttonTimer.schedule(buttonTimerTask,350);
        }
    }

    private Intent rateIntentForUrl(String url, String packageName){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, packageName)));
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

    private void onConnected(GoogleSignInAccount googleSignInAccount, Context context) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        GlobalVariables.mLeaderboardsClient = Games.getLeaderboardsClient(context, googleSignInAccount);
        //loadAndPrintEvents();
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");
        GlobalVariables.mLeaderboardsClient = null;
    }


}


//    private void signOut() {
//        Log.d(TAG, "signOut()");
//
//        if (!isSignedIn()) {
//            Log.w(TAG, "signOut() called, but was not signed in!");
//            return;
//        }
//
//        mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        boolean successful = task.isSuccessful();
//                        Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));
//
//                        onDisconnected();
//                    }
//                });
//    }

//private boolean isSignedIn() {
//    return GoogleSignIn.getLastSignedInAccount(this) != null;
//}

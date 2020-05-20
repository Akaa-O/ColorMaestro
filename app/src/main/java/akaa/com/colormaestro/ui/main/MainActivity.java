package akaa.com.colormaestro.ui.main;

/**
 * Created by Akachukwu Okonkwo on 9/18/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.animation.Animation;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;


public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter presenter = new MainPresenter(this);

    public static MediaPlayer soundTrack;
    public static MediaPlayer soundTrack2;
    public static MediaPlayer soundTrack3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.main);

        presenter.signIn(this);

        GlobalVariables.prefs = getApplicationContext().getSharedPreferences(GlobalVariables.appPreference,Context.MODE_PRIVATE);
        soundTrack = MediaPlayer.create(this,R.raw.mainsoundtrack);
        soundTrack.setLooping(true);
        if(GlobalVariables.prefs .getBoolean(GlobalVariables.musicSwitch,true)) {
            soundTrack.start();
        }

        mainLayout.setBackgroundResource(R.drawable.greybackground);

        Button playButton = (Button) findViewById(R.id.playButton); //button assignments
        Button statsButton = (Button) findViewById(R.id.statsButton);
        Button helpButton = (Button) findViewById(R.id.helpButton);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        Button rateButton = (Button) findViewById(R.id.rateButton);


        playButton.setOnClickListener(new View.OnClickListener(){  //onclick listeners for buttons
            @Override
            public void onClick(View view){
                presenter.playButtonClicked(view, getApplicationContext());
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                presenter.statsButtonClicked(view, getApplicationContext());
            }
        });
        helpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                presenter.helpButtonClicked(view, getApplicationContext());
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                presenter.settingsButtonClicked(view, getApplicationContext());
            }
        });
        rateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                presenter.rateButtonClicked(view, getApplicationContext(), getPackageName());
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
        presenter.toggleButtonPressed();
        if(soundTrack != null && soundTrack.isPlaying() == false){
            if(GlobalVariables.prefs .getBoolean(GlobalVariables.musicSwitch,true)) {
                soundTrack.start();
            }
        }

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        presenter.signInSilently(this, this);

    }

    @Override
    public void launchActivityResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void animate(View view, Animation animation){
        view.startAnimation(animation);
    }

    @Override
    public void showAlertDialog(String message) {
        new AlertDialog.Builder(this).setMessage(message)
                .setNeutralButton(android.R.string.ok, null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.handleActivityResult(requestCode, data);
    }

}



package akaa.com.colormaestro;

/**
 * Created by Akachukwu Okonkwo on 9/18/2017.
 */

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import akaa.com.colormaestro.game_modes.ClassicGame;
import akaa.com.colormaestro.game_modes.InvertedGame;
import akaa.com.colormaestro.game_modes.SplitGame;

public class ModeSelect extends AppCompatActivity {

    private Button classicButton;
    private Button splitButton;
    private Button invertedButton;

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);
        GlobalVariables.continueMusic = false;

        ConstraintLayout modeSelectLayout = (ConstraintLayout) findViewById(R.id.modeSelect);
        modeSelectLayout.setBackgroundResource(R.drawable.greybackground);

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha); //declaring animation variables

        classicButton = (Button) findViewById(R.id.classicButton);
        splitButton = (Button) findViewById(R.id.splitButton);
        invertedButton = (Button) findViewById(R.id.invertedButton);

        classicButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    if(MainActivity.prefs.getBoolean(GlobalVariables.firstTimePlayer,true)){
                        launchFirstTimeHelp();
                    }else{
                        launchClassicGame();
                    }

                }

            }
        });
        splitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    launchSplitGame();
                }
            }
        });
        invertedButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(!buttonPressed) {
                    buttonPressed = true;
                    view.startAnimation(animAlpha);
                    launchInvertedGame();
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
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(MainActivity.soundTrack != null) {
            if (MainActivity.soundTrack.isPlaying() == false) {
                if (MainActivity.prefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack.start();
                }
            }
        }
    }

    private void launchClassicGame(){
        Intent intent = new Intent(this, ClassicGame.class);
        startActivity(intent);
    }

    private void launchSplitGame(){
        Intent intent = new Intent(this,SplitGame.class);
        startActivity(intent);
    }

    private void launchInvertedGame(){
        Intent intent = new Intent(this, InvertedGame.class);
        startActivity(intent);
    }

    private void launchFirstTimeHelp(){
        GlobalVariables.continueMusic = true;
        Intent intent = new Intent(this, FirstHelpMenu.class);
        startActivity(intent);
    }

}

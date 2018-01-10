package akaa.com.colormaestro;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Switch;

public class SettingsMenu extends AppCompatActivity {
    private Button backButton;
    private Switch musicSwitch;
    private Switch soundsSwitch;

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        GlobalVariables.continueMusic = false;

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        backButton = (Button) findViewById(R.id.settingsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    finish();
                }
            }
        });

        final SharedPreferences.Editor editor = MainActivity.prefs.edit();

        musicSwitch = (Switch) findViewById(R.id.musicSwitch);
        if(MainActivity.prefs.getBoolean(GlobalVariables.musicSwitch,true)){ //if true is returned
            musicSwitch.setChecked(true);
        }else{
            musicSwitch.setChecked(false);
        }
        musicSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicSwitch.isChecked()){
                    editor.putBoolean(GlobalVariables.musicSwitch,true);
                    editor.commit();
                    if(MainActivity.soundTrack.isPlaying() == false){
                        MainActivity.soundTrack.start();
                    }
                }else{
                    editor.putBoolean(GlobalVariables.musicSwitch,false);
                    editor.commit();
                    if(MainActivity.soundTrack.isPlaying()){
                        MainActivity.soundTrack.pause();
                    }
                }
            }
        });

        soundsSwitch = (Switch) findViewById(R.id.soundsSwitch);
        if(MainActivity.prefs.getBoolean(GlobalVariables.soundSwitch,true)){ //if true is returned
            soundsSwitch.setChecked(true);
        }else{
            soundsSwitch.setChecked(false);
        }
        soundsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundsSwitch.isChecked()){
                    editor.putBoolean(GlobalVariables.soundSwitch,true);
                    editor.commit();
                }else{
                    editor.putBoolean(GlobalVariables.soundSwitch,false);
                    editor.commit();
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
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false){
            if(MainActivity.prefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack.start();
            }
        }

    }
}

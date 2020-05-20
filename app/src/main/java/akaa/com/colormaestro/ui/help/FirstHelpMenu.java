//version of the help menu displayed the first time the game mode is run
package akaa.com.colormaestro.ui.help;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;
import akaa.com.colormaestro.ui.game_modes.classic.ClassicActivity;
import akaa.com.colormaestro.ui.main.MainActivity;

public class FirstHelpMenu extends AppCompatActivity implements HelpMenuView {
    private boolean buttonPressed = false; //prevents double pressing of the buttons

    Button forwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_help_menu);
        GlobalVariables.continueMusic = false;

        SharedPreferences.Editor editor = GlobalVariables.prefs.edit();
        editor.putBoolean(GlobalVariables.firstTimePlayer,false);
        editor.commit();

        forwardButton = (Button) findViewById(R.id.helpForwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    launchClassicGame();
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
                if (GlobalVariables.prefs.getBoolean(GlobalVariables.musicSwitch, true)) {
                    MainActivity.soundTrack.start();
                }
            }
        }
    }


    private void launchClassicGame(){
        Intent intent = new Intent(this, ClassicActivity.class);
        startActivity(intent);
    }
}

//version of the help menu displayed the first time the game mode is run
package akaa.com.colormaestro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import akaa.com.colormaestro.game_modes.ClassicGame;

public class FirstHelpMenu extends AppCompatActivity {
    private boolean buttonPressed = false; //prevents double pressing of the buttons

    Button forwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_help_menu);
        GlobalVariables.continueMusic = false;

        SharedPreferences.Editor editor = MainActivity.prefs.edit();
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
    public void onResume(){
        super.onResume();
        buttonPressed = false;
        if(MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false){
            if(MainActivity.prefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack.start();
            }
        }
    }

    private void launchClassicGame(){
        Intent intent = new Intent(this, ClassicGame.class);
        startActivity(intent);
    }
}

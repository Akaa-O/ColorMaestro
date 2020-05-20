package akaa.com.colormaestro.ui.help;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;
import akaa.com.colormaestro.ui.main.MainActivity;

public class HelpActivity extends AppCompatActivity implements HelpMenuView{

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_menu);
        GlobalVariables.continueMusic = false;

        backButton = (Button) findViewById(R.id.helpBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonPressed) {
                    buttonPressed = true;
                    finish();
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
}

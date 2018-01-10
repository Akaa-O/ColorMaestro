package akaa.com.colormaestro.game_modes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.MainActivity;
import akaa.com.colormaestro.R;

public class InvertedGame extends AppCompatActivity {

    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inverted_game);
        GlobalVariables.continueMusic = false;

        backButton = (Button) findViewById(R.id.invertedBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        if(MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false){
            if(MainActivity.prefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack.start();
            }
        }

    }
}

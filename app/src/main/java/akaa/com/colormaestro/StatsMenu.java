package akaa.com.colormaestro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsMenu extends AppCompatActivity {
    private TextView totalGamesPlayed;
    private TextView classicBest;

    private Button backButton;

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

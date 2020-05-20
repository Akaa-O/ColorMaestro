package akaa.com.colormaestro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.LeaderboardsClient;

import java.util.ArrayList;

import akaa.com.colormaestro.model.game_blocks.GameBlock;
import akaa.com.colormaestro.model.game_blocks.HitBox;

/**
 * Created by Akachukwu Okonkwo on 9/27/2017.
 */

public class GlobalVariables {
    public static SharedPreferences prefs;

    public static GoogleSignInClient mGoogleSignInClient;
    public static GoogleSignInAccount signedInAccount;
    public static LeaderboardsClient mLeaderboardsClient;

    // request codes we use when invoking an external activity
    public static final int RC_UNUSED = 5001;
    public static final int RC_SIGN_IN = 9001;

    public static boolean continueMusic = false; //allows us to pause music when navigated out of app but continue if activities are changed (onpause functions)

    public static final String appPreference = "playerData";
    public static final String classicHighScore = "classicHighScoreKey";
    public static final String musicSwitch = "musicSwitchKey";
    public static final String soundSwitch = "soundSwitchKey";
    public static final String gamesPlayed = "gamesPlayedKey";
    public static final String firstTimePlayer = "firstTimePlayerKey";

    public static ArrayList<GameBlock> myGameBlocks = new ArrayList<>();
    public static ArrayList<HitBox> myHitBoxes = new ArrayList<>();

    public static Context classicGameContext; //Classic game mode context for use in frenzy mode when generating frenzy blocks

    public static boolean isGamePaused = false; //switch for paused game, ensures everything that's supposed to be inactive during pause, doesnt
    //When the game is already paused, and the home button or multitask button is pressed, the timer in on resume that was
    //activated in the initial pause continues to run and proceeds to resume the blocks. This is originally prevented by not allowing
    //the user to pause with the pause button and back button during the resume countdown sequence but obviously
    //the application will inevitably pause when the home or multitask button is pressed. Therefore this variable, as used in the
    //onpause and onresume methods of the gamemode activity, prevents the activated onresumetimer from resuming movement and
    // every other action related to the isGamePaused variable(eg.powerupblock generations) in this case
    //until the app is reentered and a new onresume is called.
        public static boolean isGamePaused2 = false;
    //
    public static boolean gameAlreadyPaused= false; //this switch is to prevent the onresume method from applying the 3 second delay the first time the mode is started
    public static boolean gameOver = false;
    public static boolean oncreateCalled = false;




    //variables associated with frenzy mode
    public static boolean frenzy = false; //switch for frenzy mode, turned on for 5 seconds when frenzy powerup is touched
    private static int frenzyDuration = 5000;
    public static String frenzyColor = "MAGENTA";

    public static double blockDimenFactor = 0.09375; //we multiply the screenlength by this to get the ideal block dimensions that fit the screensize
                                                    // in this case, the blocks are 9.375% of the screensize
    private static int previousPos = 75;   //keeps track of position of previously generated block to avoid conflicts
    private static int score = 0;
    private static int hp = 3;
    private static int observeCheck = 0;
    private static int penalizeSwitch = 1; //This ensures that players aren't penalized for blocks that are basically past screen during screen change
                                            //it is set to 0 right when the screen changes and set back to 1 a few ms later


    public static int soundtrack2ActivationThreshold = 60;
    public static int soundtrack3ActivationThreshold = 110;


    private static String screenColor;


    public static void changeHp (int operation){
        if (operation == 0){
            --hp;
        }else if (operation == 1){
            ++hp;
        }else if(operation == 2){
            hp = 3;
        }

        if (hp == 0){
            gameOver = true;
        }
    }

    public static void changeScore(int operation){
        if (operation == 0){
            --score;
        }else if (operation == 1){
            ++score;
        }else if(operation == 2){
            score = 0;
        }else {
            score = score+operation;
        }
    }

    public static int queryInt(String request){
        if(request == "hp"){
            return hp;
        }else if(request == "score"){
            return score;
        }else if(request == "observecheck"){
            return observeCheck;
        }else if(request == "penalizeswitch"){
            return penalizeSwitch;
        }else if(request == "frenzyduration"){
            return frenzyDuration;
        }else if(request == "previouspos"){
            return previousPos;
        }else{
            Log.d("Error:", "Incorrect Command");
        }
        return 0;
    }

    public static String queryString (String request){
        if(request == "screencolor"){
            return screenColor;
        }

        return "error";
    }

    public static void changeObserve(int operation){
        if(operation == 1){
            observeCheck = 1;
        }else if(operation == 0){
            observeCheck = 0;
        }

    }

    public static void changePenalizeSwitch(int operation){
        if(operation == 0){
            penalizeSwitch = 0;
        }else if(operation == 1){
            penalizeSwitch = 1;
        }
    }

    public static void changeScreenColor(String color){
        screenColor = color;
    }

    public static void changePreviousPos(int pos){
        previousPos = pos;
    }

    public static void storeClassicContext(Context context){
        classicGameContext = context;
    }






    //we need to clear all global variables when restarting an activity
    public static void reset(){
//        for(int i=0;i<myGameBlocks.size();++i){
//            myGameBlocks.get(i).destroy();
//            myHitBoxes.get(i).destroy();
//        }
        myGameBlocks.clear();
        myHitBoxes.clear();
        myGameBlocks = new ArrayList<>();
        myHitBoxes = new ArrayList<>();

        classicGameContext = null;

        isGamePaused = false;
        isGamePaused2 = false;
        gameAlreadyPaused= false;
        gameOver = false;
        oncreateCalled = false;



        frenzy = false;
        frenzyDuration = 5000;
        frenzyColor = "MAGENTA";

        blockDimenFactor = 0.09375;
        previousPos = 75;
        score = 0;
        hp = 3;
        observeCheck = 0;
        penalizeSwitch = 1;

        screenColor = null;

        System.gc();
    }



}

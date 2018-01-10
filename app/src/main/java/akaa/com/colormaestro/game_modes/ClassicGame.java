package akaa.com.colormaestro.game_modes;

/**
 * Created by Akachukwu Okonkwo on 9/18/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.GameOver;
import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.MainActivity;
import akaa.com.colormaestro.PauseMenu;
import akaa.com.colormaestro.R;
import akaa.com.colormaestro.SoundPlayer;
import akaa.com.colormaestro.game_blocks.BonusScoreBlock;
import akaa.com.colormaestro.game_blocks.DestroyBlock;
import akaa.com.colormaestro.game_blocks.FrenzyBlock;
import akaa.com.colormaestro.game_blocks.GameBlock;
import akaa.com.colormaestro.game_blocks.HitBox;
import akaa.com.colormaestro.game_blocks.HpBlock;


public class ClassicGame extends AppCompatActivity {
    public static SharedPreferences classicPrefs;

    public static SoundPlayer sound;

    private Button pauseButton;

    private static ConstraintLayout classicGameLayout;
    private Context context;

    private static TextView countdown;
    private boolean countdownActive = false; //prevents pause menu  from being activated when countdown is active
    private static int currentCountdownNum = 3; //controls the countdown value shown when resuming a paused game in onResume
    private double blockDimen; //width and height of the block

    public static int sizeX; //maximum width of screen
    public static int screenLength; // maximum length of screen
    private final int sizeXOffset = 160; // offset needed to keep all of the block on screen for the max coordinates
    private int currentTimerIndex = 0; //the current index of the myBlockTimers array, to cycle through timers, canceling used ones
    private int currentTaskIndex = 0;
    private final int numOfTimersnTasks = 30; //number of timers and timertasks stored in array for timer restarts

    private int blockGenerationInterval = 1000; // controls the intervals between each block generation 1000
    private final int intervalDecay = 80; //how much gets taken off the interval each block generation frequency change
    private final int minimumInterval = 200; //the lowest interval between each block generation allowed
    private final int generationIntervalDifficulty = 10; //the number of points that need to be achived between diffulty transitions

    private int powerupGenInterval;
    private final int powerupIntervalLowerBound = 5; //lower bound for the random choosing of the number of seconds between each powerup
    private final int powerupIntervalUpperBound = 7; //upper bound for the random choosing of the number of seconds between each powerup

    private int lowerRandBound = 3500; //controls the lower and upper bounds of the screen change for the rand function
    private int upperRandBound = 4000; //reduces with increasing difficulty
    private final int minLowerRandBound = 1500; //the lowest screen color change interval allowed
    private final int boundsDecay = 1000; //how much gets taken ofd the lower and upper bounds each screen color switch interval change
    private final int colorChangeDifficulty = 20; //the number of points that need to be achived between diffulty transitions

    public static int blockSpeed = 2500; //2500
    public final static int blockSpeedLimit = 1700;
    private final int blockSpeedDecay = 400;
    private final int speedDifficulty = 50; //the number of points that need to be achieved between difficulty transitions



    private Boolean allowFreqChange = true; //allows or disallows block generation frequency change
    private Boolean allowColorIntervalChange = true; //allows or disallows changing of the screen color change interval
    private Boolean allowBlockSpeedChange = true;

    private static TextView scoreView;

    private static ImageView actionBarView;
    private static ImageView hp1;
    private static ImageView hp2;
    private static ImageView hp3;

    private Activity thisActivity = this; //to pass this activity to gameblock to allow run on thread

    //Timers
    private Timer healthBoxTimer = new Timer();  //timer for checking for hp changes, then running the task to update hp boxes
    private Timer scoreBoxTimer = new Timer(); //timer for checking for score changes, then running the task to update score box

    final ArrayList<Timer> myBlockTimers = new ArrayList<>();
    final Timer blockFrequencyTimer = new Timer();
    final Timer warningTimer = new Timer();
    final Timer screenTimer = new Timer();
    final Timer hpBlockTimer = new Timer();

    private boolean buttonPressed = false; //prevents double pressing of the buttons

    private boolean soundtrack2Activated = false;
    private boolean soundtrack3Activated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_game);
        //overridePendingTransition(0,0);
        classicPrefs = getApplicationContext().getSharedPreferences(GlobalVariables.appPreference, Context.MODE_PRIVATE);

        //updating games played stat
        SharedPreferences.Editor editor = classicPrefs.edit();
        int gamesPlayed = classicPrefs.getInt(GlobalVariables.gamesPlayed,0);
        ++gamesPlayed;
        editor.putInt(GlobalVariables.gamesPlayed,gamesPlayed);
        editor.commit();

        sound = new SoundPlayer(this);

        if(MainActivity.soundTrack != null){
            MainActivity.soundTrack.stop();
        }
        MainActivity.soundTrack = MediaPlayer.create(this,R.raw.mainsoundtrack);
        MainActivity.soundTrack2 = MediaPlayer.create(this,R.raw.mainsoundtrackx12);
        MainActivity.soundTrack3 = MediaPlayer.create(this,R.raw.mainsoundtrackx14);
        MainActivity.soundTrack.setLooping(true);
        if(classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
            MainActivity.soundTrack.start();
        }

        TimerTask soundTrackTask = new TimerTask() {
            @Override
            public void run() {
                if(GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack2ActivationThreshold && GlobalVariables.queryInt("score") < GlobalVariables.soundtrack3ActivationThreshold
                        &&!soundtrack2Activated){
                    soundtrack2Activated = true;

                    if(MainActivity.soundTrack != null){
                        MainActivity.soundTrack.stop();
                    }
                    MainActivity.soundTrack2.setLooping(true);
                    if(classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                        MainActivity.soundTrack2.start();
                    }
                }else if(GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack3ActivationThreshold && !soundtrack3Activated){
                    soundtrack3Activated = true;

                    if(MainActivity.soundTrack2 != null){
                        MainActivity.soundTrack2.stop();
                    }
                    MainActivity.soundTrack3.setLooping(true);
                    if(classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                        MainActivity.soundTrack3.start();
                    }
                }
            }
        };
        Timer soundTrackTimer = new Timer();
        soundTrackTimer.schedule(soundTrackTask,50,50);


        GlobalVariables.oncreateCalled = true;
        countdown = (TextView) findViewById(R.id.countdownView); //assigning countdown view

        blockDimen = screenLength*GlobalVariables.blockDimenFactor;

        //get and store this application's context in global variables for frenzy purposes
        context = getApplicationContext();
        GlobalVariables.storeClassicContext(context);



        classicGameLayout = (ConstraintLayout) findViewById(R.id.classicGame);
        hp1 = (ImageView) findViewById(R.id.hp1);
        hp2 = (ImageView) findViewById(R.id.hp2);
        hp3 = (ImageView) findViewById(R.id.hp3);
        hp1.bringToFront();
        hp2.bringToFront();
        hp3.bringToFront();

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha); //declaring animation variables

        //pause button setup
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {   //pause button setup
            @Override
            public void onClick(final View view) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (countdownActive == false && !buttonPressed) {
                                buttonPressed = true;
                                view.startAnimation(animAlpha);
                                launchPauseMenu();
                            }
                        }
                    });
            }
        });

        scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.bringToFront();
        actionBarView = (ImageView) findViewById(R.id.actionBarView);

        TimerTask healthBoxUpdate = new TimerTask() {
            @Override
            public void run() {  //keeps the health box updated at all times
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(GlobalVariables.queryInt("hp") == 3) {
                            hp1.setImageResource(R.drawable.heart);
                            hp2.setImageResource(R.drawable.heart);
                            hp3.setImageResource(R.drawable.heart);
                        }else if(GlobalVariables.queryInt("hp") == 2){
                            hp1.setImageResource(R.drawable.heart);
                            hp2.setImageResource(R.drawable.heart);
                            hp3.setImageResource(R.drawable.greybackground);
                        }else if(GlobalVariables.queryInt("hp") == 1){
                            hp1.setImageResource(R.drawable.heart);
                            hp2.setImageResource(R.drawable.greybackground);
                            hp3.setImageResource(R.drawable.greybackground);
                        }else if(GlobalVariables.queryInt("hp") == 0) {
                            hp1.setImageResource(R.drawable.greybackground);
                            hp2.setImageResource(R.drawable.greybackground);
                            hp3.setImageResource(R.drawable.greybackground);
                        }

                        if(GlobalVariables.gameOver){
                            healthBoxTimer.cancel();
                            scoreBoxTimer.cancel();
                            endGame();
                        }
                    }
                });

            }
        };
        TimerTask scoreBoxUpdate = new TimerTask() { //keeps the score box updated at all times
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoreView.setText(Integer.toString(GlobalVariables.queryInt("score")));
                    }
                });
            }
        };
        healthBoxTimer.schedule(healthBoxUpdate,1,1); //keeps the health box updated at all times
        scoreBoxTimer.schedule(scoreBoxUpdate,1,1); //keeps the score box updated at all times



        //randomly choose initial starting color
        Random rand = new Random();
        int randColor = rand.nextInt(6)+1;
        if (randColor == 1) {
            classicGameLayout.setBackgroundColor(Color.BLACK);
            GlobalVariables.changeScreenColor("BLACK");
        }else if(randColor == 2){
            classicGameLayout.setBackgroundColor(Color.WHITE);
            GlobalVariables.changeScreenColor("WHITE");
        }else if(randColor == 3){
            classicGameLayout.setBackgroundColor(Color.GREEN);
            GlobalVariables.changeScreenColor("GREEN");
        }else if(randColor == 4){
            classicGameLayout.setBackgroundColor(Color.YELLOW);
            GlobalVariables.changeScreenColor("YELLOW");
        }else if(randColor == 5){
            classicGameLayout.setBackgroundColor(Color.BLUE);
            GlobalVariables.changeScreenColor("BLUE");
        }else if(randColor == 6){
            classicGameLayout.setBackgroundColor(Color.RED);
            GlobalVariables.changeScreenColor("RED");
        }


        Display display = getWindowManager().getDefaultDisplay();  //to get the max screen coordinates of device
        final Point size = new Point();
        display.getSize(size);
        sizeX = (size.x - sizeXOffset);
        screenLength = size.y;


        final ArrayList<TimerTask> myBlockTaskArray = new ArrayList<>(); //array for storing duplicate block generation tasks for rescheduling
        //an array of these block generation tasks is needed to allow for task rescheduling cause you cant cancel and
        //reschedule the same task
        //Based on our current Interval decay, we only need 16 timer tasks but we will use 30 just in case lol
        for(int i=0;i<numOfTimersnTasks;++i) {
            final TimerTask blockGenerationTask = new TimerTask() {
                int positionX = GlobalVariables.queryInt("previouspos"); //ensures that the while loop below always runs at least once
                @Override
                public void run() {
                    Random rand = new Random();

                    while (positionX >= (GlobalVariables.queryInt("previouspos")-(blockDimen+300)) &&
                            positionX <= (GlobalVariables.queryInt("previouspos")+(blockDimen+300))) {  //avoids block generation location conflicts
                        positionX = rand.nextInt(sizeX);                                      //offset of  used to avoid overlapping of blocks
                    }
                    GlobalVariables.changePreviousPos(positionX);

                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    //creates new block if game not paused or ended
                    if(GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false){
                        generateBlock(thisActivity,getApplicationContext(),positionX);
                    }


                    changeBlockSpeed(); //changes block speed if required difficulty(score) is reached

                }

            };
            myBlockTaskArray.add(blockGenerationTask);
        }
        //We also need different timers cause you cant cancel and reschedule the same timers
        //Based on our current Interval decay, we only need 16 timers but we will use 30 just in case lol
        for(int i=0;i<numOfTimersnTasks;++i){
            Timer timer  = new Timer();
            myBlockTimers.add(timer);
        }

        //This is the initial block generation call with the biggest interval
        myBlockTimers.get(currentTimerIndex).schedule(myBlockTaskArray.get(currentTaskIndex),1,blockGenerationInterval);


        //The following generates a powerup(or not at certain intervals)
        hpBlockLoop(); //loop for generating a hp powerup
        generatePowerup(); //loop for generating all other powerups (cause they're based on score)

        //The following lines are for reducing the time between each block generation based on the current score.
        //allowFreqChange ensures that once the frequency is changed for a certain score, it doesn't get changed again for
        //the same score when the blockFrequencyTimer is called again.
        TimerTask blockFrequencyTask = new TimerTask() {
            @Override
            public void run() {
                if (((GlobalVariables.queryInt("score")%generationIntervalDifficulty) == 0) && (blockGenerationInterval > minimumInterval) &&
                        (GlobalVariables.queryInt("score")!=0) && allowFreqChange==true){
                    allowFreqChange = false;
                    myBlockTimers.get(currentTimerIndex).cancel();
                    ++currentTimerIndex;
                    ++currentTaskIndex;
                    blockGenerationInterval = blockGenerationInterval - intervalDecay;
                    myBlockTimers.get(currentTimerIndex).schedule(myBlockTaskArray.get(currentTaskIndex),1,blockGenerationInterval); //reschedules block generation with new interval

                }else if (blockGenerationInterval <= minimumInterval){ //ends the block frequency changer when max frequency is achieved
                    blockFrequencyTimer.cancel();
                }

                if((GlobalVariables.queryInt("score")%generationIntervalDifficulty) != 0){
                    allowFreqChange = true;
                }
            }
        };
        blockFrequencyTimer.schedule(blockFrequencyTask,1,1);

        changeScreenColor(); //called once to initially start the recursion

    }




    @Override
    public void onBackPressed(){
        if(countdownActive == false){
            launchPauseMenu();
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        if(GlobalVariables.oncreateCalled){
            Log.d("PAUSEDDD", "pausedddd");
            GlobalVariables.gameAlreadyPaused = true;
            if(GlobalVariables.isGamePaused == true){ //onpause is being activated twice, meaning the home or multitask button was pressed while already paused
                GlobalVariables.isGamePaused2 = true;
            }else{
                GlobalVariables.isGamePaused = true;
            }

            for(int i=0; i<GlobalVariables.myGameBlocks.size();++i) {
                GlobalVariables.myGameBlocks.get(i).pauseBlock();
                GlobalVariables.myHitBoxes.get(i).pauseBlock();
            }
        }

        if(GlobalVariables.continueMusic == false && MainActivity.soundTrack.isPlaying()){
            MainActivity.soundTrack.pause();
        }
        if(GlobalVariables.continueMusic == false && MainActivity.soundTrack2.isPlaying()){
            MainActivity.soundTrack2.pause();
        }
        if(GlobalVariables.continueMusic == false && MainActivity.soundTrack3.isPlaying()){
            MainActivity.soundTrack3.pause();
        }


    }

    @Override
    public void onResume(){
        Log.d("RESUMEE", "resumedddd");
        super.onResume();
        buttonPressed = false;
        final Timer resumeDelayTimer = new Timer();
        TimerTask resumeDelay = new TimerTask() {
            @Override
            public void run() {
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(GlobalVariables.isGamePaused2 == false) {
                            GlobalVariables.isGamePaused = false;
                            for (int i = 0; i < GlobalVariables.myGameBlocks.size(); ++i) {
                                GlobalVariables.myGameBlocks.get(i).resumeBlock();
                                GlobalVariables.myHitBoxes.get(i).resumeBlock();
                            }
                        }else{
                            GlobalVariables.isGamePaused2 = false;
                            GlobalVariables.isGamePaused = true;
                        }
                    }
                });

                resumeDelayTimer.cancel();
            }
        };

        if(GlobalVariables.gameAlreadyPaused == true) {
            startResumeCountdown();
            resumeDelayTimer.schedule(resumeDelay,3000);
        }

        if(MainActivity.soundTrack != null && MainActivity.soundTrack.isPlaying() == false){
            if(classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack.start();
            }
        }
        if(MainActivity.soundTrack2 != null && MainActivity.soundTrack2.isPlaying() == false
                && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack2ActivationThreshold && GlobalVariables.queryInt("score") < GlobalVariables.soundtrack3ActivationThreshold){
            if(classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack2.start();
            }
        }
        if(MainActivity.soundTrack3 != null && MainActivity.soundTrack3.isPlaying() == false && GlobalVariables.queryInt("score") >= GlobalVariables.soundtrack3ActivationThreshold){
            if(classicPrefs.getBoolean(GlobalVariables.musicSwitch,true)) {
                MainActivity.soundTrack3.start();
            }
        }
    }



    private void changeScreenColor(){
        //The following reduce the upper and lower value that are used to generate the random screen color
        //change interval, according to difficulty(every 20 points or whatever the colorChangeDifficulty's value is)
        if (((GlobalVariables.queryInt("score") % colorChangeDifficulty) == 0) && GlobalVariables.queryInt("score") != 0 &&
                lowerRandBound > minLowerRandBound && allowColorIntervalChange == true) {
            allowColorIntervalChange = false;
            lowerRandBound = lowerRandBound - boundsDecay;
            upperRandBound = upperRandBound - boundsDecay;

        }
        if ((GlobalVariables.queryInt("score") % colorChangeDifficulty) != 0) {
            allowColorIntervalChange = true;
        }

        Random rand = new Random();
        int colorChangeInterval = rand.nextInt(upperRandBound) + lowerRandBound; //randomly pick a color switch interval within these bounds

        //gives warning pulse before screen color change
        TimerTask warningTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(GlobalVariables.frenzy == false && GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false) {
                            Animation animation = new AlphaAnimation(1f, 0.8f);
                            animation.setDuration(125);
                            animation.setInterpolator(new LinearInterpolator());
                            animation.setRepeatCount(3);

                            classicGameLayout.startAnimation(animation);
                        }
                    }
                });


            }
        };
        warningTimer.schedule(warningTask, (colorChangeInterval - 500)); //starts warning pulse timer to go off 500ms b4 screenchange


        Random rand1 = new Random();
        final int colorChoice = rand1.nextInt(6) + 1;
        TimerTask screenTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //do not change screen color if frenzy is on,screen color is already changed to frenzy color,or if game is paused/over
                        if (GlobalVariables.frenzy == false && GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false){

                            if (colorChoice == 1) {
                                classicGameLayout.setBackgroundColor(Color.BLACK);
                                GlobalVariables.changeScreenColor("BLACK");
                            } else if (colorChoice == 2) {
                                classicGameLayout.setBackgroundColor(Color.WHITE);
                                GlobalVariables.changeScreenColor("WHITE");
                            } else if (colorChoice == 3) {
                                classicGameLayout.setBackgroundColor(Color.GREEN);
                                GlobalVariables.changeScreenColor("GREEN");
                            } else if (colorChoice == 4) {
                                classicGameLayout.setBackgroundColor(Color.YELLOW);
                                GlobalVariables.changeScreenColor("YELLOW");
                            } else if (colorChoice == 5) {
                                classicGameLayout.setBackgroundColor(Color.BLUE);
                                GlobalVariables.changeScreenColor("BLUE");
                            } else if (colorChoice == 6) {
                                classicGameLayout.setBackgroundColor(Color.RED);
                                GlobalVariables.changeScreenColor("RED");
                            }
                            flipPenalizeSwitch();
                        }
                    }
                });
                changeScreenColor();  //recursively call the change screen color once to allow the screenTimer to run the screenTimerTask
                //with updated colorChangeInterval, after which this will be called again
            }
        };
        screenTimer.schedule(screenTimerTask, colorChangeInterval); //since this is a recursive call, the color change interval can be changed each call
    }


    //This ensures that players aren't penalized for blocks that are basically past screen during screen change
    //it is set to 0 right when the screen changes and set back to 1 a few ms later after which player can be penalized
    private void flipPenalizeSwitch(){
        GlobalVariables.changePenalizeSwitch(0);
        Timer timer = new Timer();
        TimerTask switchTask = new TimerTask() {
            @Override
            public void run() {
                GlobalVariables.changePenalizeSwitch(1);
            }
        };
        timer.schedule(switchTask,1000); //turns the penalize switch back on after a second
    }

    //reduces the block speed which reduces the animation duration of the blocks which increases their speed across the screen
    private void changeBlockSpeed(){
        if(blockSpeed>blockSpeedLimit && (GlobalVariables.queryInt("score")%speedDifficulty == 0) && GlobalVariables.queryInt("score")!=0 && allowBlockSpeedChange==true){
            allowBlockSpeedChange = false;
            blockSpeed = blockSpeed - blockSpeedDecay;
        }

        if((GlobalVariables.queryInt("score")%speedDifficulty != 0)){
            allowBlockSpeedChange = true;
        }
    }

    public static void generateBlock(Activity activity, Context context, int xPos){
        GameBlock block = new GameBlock(activity,classicGameLayout,context,xPos,
                actionBarView, hp1, hp2, hp3, scoreView, screenLength, blockSpeed); //creates new block
        HitBox hitBox = new HitBox(activity,classicGameLayout,context,xPos,
                actionBarView, hp1, hp2, hp3, scoreView, screenLength, blockSpeed,block); //creates accompanying hitbox

        //this needs to be synchronized to avoid concurrent modification exception with iterator in all Powerup block's moveBlock()
        synchronized (GlobalVariables.class){
            GlobalVariables.myGameBlocks.add(block);  //adds blocks to array list to keep track
            if(GlobalVariables.myGameBlocks.size() >= 50) {//cuts the list in half when it reaches certain size, to aid performance
                GlobalVariables.myGameBlocks.subList(0, ((GlobalVariables.myGameBlocks.size() - 1) / 2)).clear();
            }
        }

        GlobalVariables.myHitBoxes.add(hitBox); // every gameblock has its own hitbox in same position in a diffrent array
        if(GlobalVariables.myHitBoxes.size() >= 50){//cuts the list in half when it reaches certain size, to aid performance
            GlobalVariables.myHitBoxes.subList(0, ((GlobalVariables.myHitBoxes.size() - 1) / 2)).clear();
        }
    }



    private void hpBlockLoop(){
        final int twoHpProb = 1; //probability of getting hp powerup when hp is 2 (value is div by 10 so 20% = 2%)
        final int oneHpProb = 2; //probability of getting hp powerup when hp is 1
        final int hpBlockTaskInterval = 4000;

        TimerTask hpBlockTask = new TimerTask() {
            @Override
            public void run() {
                if(GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false) {
                    Random rand = new Random();
                    int m = rand.nextInt(10000000);
                    if (GlobalVariables.queryInt("hp") == 2) {
                        if ((m % 10) < twoHpProb) {
                            HpBlock hpBlock = new HpBlock(thisActivity, classicGameLayout, getApplicationContext(),
                                    actionBarView, hp1, hp2, hp3, scoreView, screenLength, sizeX);
                        }
                    } else if (GlobalVariables.queryInt("hp") == 1) {
                        if ((m % 10) < oneHpProb) {
                            HpBlock hpBlock = new HpBlock(thisActivity, classicGameLayout, getApplicationContext(),
                                    actionBarView, hp1, hp2, hp3, scoreView, screenLength, sizeX);
                        }
                    }
                }else{
                    hpBlockTimer.cancel();
                }
            }
        };
        hpBlockTimer.schedule(hpBlockTask,1,hpBlockTaskInterval);

    }

    private void frenzyBlockGen(){
        FrenzyBlock frenzyBlock = new FrenzyBlock(thisActivity, classicGameLayout, getApplicationContext(),
                actionBarView, hp1, hp2, hp3, scoreView, screenLength, sizeX);
    }

    private void destroyBlockGen(){
        DestroyBlock destroyBlock = new DestroyBlock(thisActivity, classicGameLayout, getApplicationContext(),
                actionBarView, hp1, hp2, hp3, scoreView, screenLength, sizeX);
    }

    private void bonusBlockGen(){
        BonusScoreBlock bonusBlock = new BonusScoreBlock(thisActivity, classicGameLayout, getApplicationContext(),
                actionBarView, hp1, hp2, hp3, scoreView, screenLength, sizeX);
    }

    //controls the generation of all powerups except hp
    private void generatePowerup(){
        final Timer powerupGenTimer = new Timer();
        Random rand1 = new Random();
        powerupGenInterval = (rand1.nextInt(powerupIntervalUpperBound)+powerupIntervalLowerBound)*1000;//we multiply by 1000 to convert to milliseconds
        TimerTask powerupGenTask = new TimerTask() {
            @Override
            public void run() {
                if(GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false) {
                    Random rand2 = new Random();
                    int m = rand2.nextInt(10000000);
                    //30% probability for destroy and bonus block, 40% prob for frenzy block
                    if ((m % 10) < 4) {
                        if(GlobalVariables.frenzy == false) {
                            frenzyBlockGen();
                        }
                    } else if ((m % 10) > 3 && (m % 10) < 7) {
                        destroyBlockGen();
                    } else if ((m % 10) > 6 && (m % 10) < 10) {
                        bonusBlockGen();
                    }
                }

                powerupGenTimer.cancel();
                if(GlobalVariables.gameOver == false){
                    generatePowerup();
                }

            }
        };
        powerupGenTimer.schedule(powerupGenTask,powerupGenInterval);

    }

    private void launchPauseMenu(){
        GlobalVariables.continueMusic = true;
        Intent intent = new Intent(this, PauseMenu.class);
        startActivity(intent);
    }

    private void startResumeCountdown(){
        final Timer countDownTimer = new Timer();
        final Timer countDownWatchTimer = new Timer();

        countdownActive = true;
        thisActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countdown.setVisibility(View.VISIBLE);
                countdown.bringToFront();
            }
        });

        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String screenCol = GlobalVariables.queryString("screencolor");
                        if(screenCol == "BLACK"){
                            countdown.setTextColor(Color.WHITE);
                        }else if (screenCol == "WHITE" || screenCol == "RED"){
                            countdown.setTextColor(Color.BLACK);
                        }else if (screenCol == "GREEN"){
                            countdown.setTextColor(Color.RED);
                        }else if (screenCol == "YELLOW"){
                            countdown.setTextColor(Color.BLUE);
                        }else if (screenCol == "BLUE"){
                            countdown.setTextColor(Color.YELLOW);
                        }

                        countdown.setText(Integer.toString(currentCountdownNum));



                        --currentCountdownNum;
                    }
                });

            }
        };
        TimerTask resetVisibility = new TimerTask() {
            @Override
            public void run() {
                countDownTimer.cancel();
                thisActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentCountdownNum = 3;
                        countdown.setVisibility(View.INVISIBLE);
                        countdownActive = false;

                    }
                });
                countDownWatchTimer.cancel();
            }
        };

        countDownTimer.schedule(countdownTask,0,1000);
        countDownWatchTimer.schedule(resetVisibility,3000);
    }


    private void endGame(){
        GlobalVariables.continueMusic = true;
        //stops all moving blocks
        for(int i=0; i<GlobalVariables.myGameBlocks.size();++i) {
            GlobalVariables.myGameBlocks.get(i).pauseBlock();
            GlobalVariables.myHitBoxes.get(i).pauseBlock();
        }
        //screen wobble effect
        final Animation animWobble = AnimationUtils.loadAnimation(this, R.anim.anim_wobble); //declaring animation variables
        findViewById(R.id.classicGame).startAnimation(animWobble);


        final Intent intent = new Intent(this, GameOver.class);
        TimerTask endTask = new TimerTask() {
            @Override
            public void run() {
                finish();
                startActivity(intent);
            }
        };
        Timer endTimer = new Timer();
        endTimer.schedule(endTask,1000);
        updateHighScore(GlobalVariables.queryInt("score"));
    }

    private void updateHighScore(int score){
            int currentHighScore;
            currentHighScore = classicPrefs.getInt(GlobalVariables.classicHighScore, 0); //returns a 0 if a highscore pref hasn't been created yet

            SharedPreferences.Editor editor = classicPrefs.edit();
            if(score > currentHighScore){
                editor.putInt(GlobalVariables.classicHighScore,score);
                editor.commit();
            }
    }

    private void reset(){
        pauseButton = null;

        classicGameLayout = null;
        context = null;

        countdown = null;
        countdownActive = false;
        currentCountdownNum = 3;
        blockDimen = 0;

        sizeX = 0;
        screenLength = 0;
        currentTimerIndex = 0;
        currentTaskIndex = 0;

        blockGenerationInterval = 1000;

        powerupGenInterval = 0;

        blockSpeed = 2500; //2500




        allowFreqChange = true;
        allowColorIntervalChange = true;
        allowBlockSpeedChange = true;

        scoreView = null;

        actionBarView = null;
        hp1 = null;
        hp2 = null;
        hp3 = null;



        healthBoxTimer = new Timer();
        scoreBoxTimer = new Timer();

        thisActivity = this;
    }


}

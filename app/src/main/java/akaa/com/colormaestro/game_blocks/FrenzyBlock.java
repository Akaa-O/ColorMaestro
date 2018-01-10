package akaa.com.colormaestro.game_blocks;

/**
 * Created by Akachukwu Okonkwo on 10/15/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.game_modes.ClassicGame;
import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;

//This block doesn't move3000 straight down like the normal blocks. it appears and disappears at different X coordinates,
//while also going down the screen by changing its y coordinates
//This block induces a frenzy mode where screen color and block color stay the same, and blocks move3000 faster, for 5 seconds,
public class FrenzyBlock {
    private int posX = 0;
    private int posY = 0;
    private int speed = 300; //speed of the block down the screen (how much y is incremented by during location change)
    private int moveInterval = 600; //the interval(in milliseconds) between each change of the block's location
    private int screenLength; //max length of screen
    private int screenWidth; //max width of screen
    private int frenzyGenerationInterval = 500;
    private int cycle = 1; //controls the current color of the frenzy block
    private double blockDimen; //width and height of the block

    public boolean blockTouched = false;
    private boolean locationConflict = true; //to check for this block's location conflict with other blocks

    private ConstraintLayout classicLayout;
    private Activity myActivity;

    private ImageView blockView;
    private ImageView actionBar;
    private ImageView health1;
    private ImageView health2;
    private ImageView health3;

    private TextView scoreBox;

    private Timer moveTimer = new Timer();

    public FrenzyBlock(Activity activity, ConstraintLayout classicGameLayout, Context context,
                   ImageView bar, ImageView hp1, ImageView hp2, ImageView hp3, TextView scoreView, int sizeY, int Xmax) {

        myActivity = activity;
        blockView = new ImageView(context);
        classicLayout = classicGameLayout;
        actionBar = bar;
        health1 = hp1;
        health2 = hp2;
        health3 = hp3;
        scoreBox = scoreView;
        screenLength = sizeY;
        screenWidth = Xmax;
        blockDimen = screenLength*GlobalVariables.blockDimenFactor;


        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                classicLayout.addView(blockView);
                actionBar.bringToFront();    //makes sure action bar, health boxes and score bar aren't obstructed by blocks
                health1.bringToFront();
                health2.bringToFront();
                health3.bringToFront();
                scoreBox.bringToFront();

                setupTouchListener(); //sets up listener for touch on a block

                android.view.ViewGroup.LayoutParams layoutParams = blockView.getLayoutParams();
                layoutParams.width = (int)blockDimen; //adjust block size and put on layout on initial position
                layoutParams.height = (int)blockDimen;
                blockView.setLayoutParams(layoutParams);
                blockView.setX(posX);
                blockView.setY(posY);


                //changes the frenzy block's color at a rapid rate
                TimerTask imageCycle = new TimerTask() {
                    @Override
                    public void run() {
                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (cycle == 1) {
                                    blockView.setImageResource(R.drawable.black_gem);
                                }else if(cycle == 2){
                                    blockView.setImageResource(R.drawable.white_gem);
                                }else if(cycle == 3){
                                    blockView.setImageResource(R.drawable.green_gem);
                                }else if(cycle == 4){
                                    blockView.setImageResource(R.drawable.yellow_gem);
                                }else if(cycle == 5){
                                    blockView.setImageResource(R.drawable.blue_gem);
                                }else if(cycle == 6){
                                    blockView.setImageResource(R.drawable.red_gem);
                                }
                            }
                        });

                        if (cycle >= 6){
                            cycle = 0;
                        }
                        ++cycle;
                    }
                };
                Timer imageCycleTimer = new Timer();
                imageCycleTimer.schedule(imageCycle,1,100);

                moveBlock();


            }
        });


    }


    public void moveBlock(){  //function for animating block across screen

        TimerTask move = new TimerTask() {
            @Override
            public void run() {
                if(posY >= screenLength){ //deletes block when past end of screen
                    moveTimer.cancel();
                    deleteBlock();
                }

                if(GlobalVariables.isGamePaused == false) {
                    //This ensures that the block doesn't conflict with the locations of other blocks on the screen
                    while (locationConflict == true && blockTouched == false) {
                        posX = generateXCoordinate();
                        //this needs to be synchronized to avoid concurrent modification exception
                        synchronized (GlobalVariables.class) {
                            for (GameBlock block : GlobalVariables.myGameBlocks) {
                                int[] location = block.getLocation();
                                if (posX != location[0] && posY != location[1]) {
                                    locationConflict = false;
                                }
                            }
                        }

                    }
                    myActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            blockView.setX(posX);
                            blockView.setY(posY);
                        }
                    });

                    locationConflict = true;
                    posY = posY + speed;  //brings the block down across screen at steady rate
                }
            }
        };
        moveTimer.schedule(move,1,moveInterval);
    }

    private int generateXCoordinate () { //generates random x coordinate for next location change
        Random rand = new Random();
        int x = rand.nextInt(screenWidth) + 5;
        return x;
    }




    public void deleteBlock() {   //deletes block from view and starts frenzy
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //to allow animation to finish b4 removing block from view
                Timer deleteTimer = new Timer();
                TimerTask deleteTask = new TimerTask() {
                    @Override
                    public void run() {
                        myActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                classicLayout.removeView(blockView);
                            }
                        });

                    }
                };
                deleteTimer.schedule(deleteTask,500);

                if(blockTouched == true) {  //block was touched
                    GlobalVariables.frenzy = true;
                    classicLayout.setBackgroundColor(Color.MAGENTA);
                    final String screenColorTemp = GlobalVariables.queryString("screencolor");
                    GlobalVariables.changeScreenColor(GlobalVariables.frenzyColor);

                    final int blockSpeedTemp = ClassicGame.blockSpeed;
                    ClassicGame.blockSpeed = ClassicGame.blockSpeedLimit;
                    generateFrenzyBlocks();

                    //everything is brought back to normal once frenzy ends after the frenzyDuration
                    Timer frenzyTimer = new Timer();
                    TimerTask frenzyEndTask = new TimerTask() {
                        @Override
                        public void run() {
                            GlobalVariables.frenzy = false;
                            restoreScreenColor(screenColorTemp);
                            GlobalVariables.changeScreenColor(screenColorTemp);
                            ClassicGame.blockSpeed = blockSpeedTemp; //Note:This can undo the blockspeed decay if player was still in frenzy when the score reached
                            // a multiple of the speeddifficulty, thereby delaying the intended speed increase of the blocks. Player's lucky day lol
                        }
                    };
                    //gives warning pulse before screen color change
                    Timer warningTimer = new Timer();
                    TimerTask warningTask = new TimerTask() {
                        @Override
                        public void run() {
                            myActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        Animation animation = new AlphaAnimation(1f, 0.8f);
                                        animation.setDuration(125);
                                        animation.setInterpolator(new LinearInterpolator());
                                        animation.setRepeatCount(3);
                                        classicLayout.startAnimation(animation);
                                }
                            });


                        }
                    };

                    warningTimer.schedule(warningTask, (GlobalVariables.queryInt("frenzyduration") - 500)); //starts warning pulse timer to go off 500ms b4 screenchange
                    frenzyTimer.schedule(frenzyEndTask,GlobalVariables.queryInt("frenzyduration"));

                }
            }
        });

    }

    public void setupTouchListener(){   //listens for touch event
        blockView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false && blockTouched == false) {
                        blockTouched = true;
                        handleBlockTouch();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    public void handleBlockTouch(){ //handles the touch event accordingly
        moveTimer.cancel();
        if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.soundSwitch,true)) {
            ClassicGame.sound.playFrenzySound();
        }
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                collapse(blockView);
            }
        });
        deleteBlock();
    }

    public void restoreScreenColor(final String color){
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(color == "BLACK"){
                    classicLayout.setBackgroundColor(Color.BLACK);
                }else if(color == "WHITE"){
                    classicLayout.setBackgroundColor(Color.WHITE);
                }else if(color == "BLUE"){
                    classicLayout.setBackgroundColor(Color.BLUE);
                }else if(color == "RED"){
                    classicLayout.setBackgroundColor(Color.RED);
                }else if(color == "YELLOW"){
                    classicLayout.setBackgroundColor(Color.YELLOW);
                }else if(color == "GREEN"){
                    classicLayout.setBackgroundColor(Color.GREEN);
                }
            }
        });



    }

    //This separately generate frenzy blocks in addition to the normally generated blocks that have been turned to frenzy mode,with increased frequency, hence the word frenzy
    public void generateFrenzyBlocks(){
        final Timer blockGenerationTimer = new Timer();
        Timer blockGenerationWatchDog = new Timer();
        final TimerTask blockGenerationTask = new TimerTask() {
            int positionX = GlobalVariables.queryInt("previouspos"); //ensures that the while loop below always runs at least once
            @Override
            public void run() {
                Random rand = new Random();

                while (positionX >= (GlobalVariables.queryInt("previouspos")-GlobalVariables.queryInt("blockwidth")) &&
                        positionX <= (GlobalVariables.queryInt("previouspos")+GlobalVariables.queryInt("blockwidth"))) {  //avoids block generation location conflicts
                    positionX = rand.nextInt(ClassicGame.sizeX);                                      //offset of 75 used(half of block width) to avoid overlapping of blocks
                }
                GlobalVariables.changePreviousPos(positionX);

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                //creates block
                if(GlobalVariables.isGamePaused == false){
                    ClassicGame.generateBlock(myActivity,GlobalVariables.classicGameContext,positionX);
                }


            }

        };
        blockGenerationTimer.schedule(blockGenerationTask,1,frenzyGenerationInterval);

        //cancels frenzy block generation after frenzy mode is done
        final TimerTask watchDogTask = new TimerTask() {
            @Override
            public void run() {
                blockGenerationTimer.cancel();
            }
        };
        blockGenerationWatchDog.schedule(watchDogTask,GlobalVariables.queryInt("frenzyduration"));
    }

    public int[] getLocation(){
        int[] locations = new int[2];
        blockView.getLocationOnScreen(locations);  //x=location[0] and y=location[1]
        return locations;

    }

    //animation for collapsing while also fading the block
    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        //fading animation
        final Animation animAlpha = AnimationUtils.loadAnimation(myActivity,R.anim.anim_alpha);
        animAlpha.setDuration(250);
        animAlpha.setFillAfter(true);

        AnimationSet animSet = new AnimationSet(false);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(500);
        animSet.addAnimation(animAlpha);
        animSet.addAnimation(a);
        v.startAnimation(animSet);
    }
}

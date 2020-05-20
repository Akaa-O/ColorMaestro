package akaa.com.colormaestro.model.game_blocks;

/**
 * Created by Akachukwu Okonkwo on 10/14/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import akaa.com.colormaestro.ui.game_modes.classic.ClassicActivity;
import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;

//This block doesn't move3000 straight down like the normal blocks. it appears and disappears at different X coordinates,
//while also going down the screen by changing its y coordinates
//This game block gives player +1 hp when touched if hp is below 3
public class HpBlock {
    private int posX = 0;
    private int posY = 0;
    private int speed = 300; //speed of the block down the screen (how much y is incremented by during location change)
    private int moveInterval = 600; //the interval(in milliseconds) between each change of the block's location
    private int screenLength; //max length of screen
    private int screenWidth; //max width of screen
    private double blockDimen; //width and height of the block

    private boolean blockTouched = false;
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

    public HpBlock(Activity activity, ConstraintLayout classicGameLayout, Context context,
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

        final Animation animPulsate = AnimationUtils.loadAnimation(myActivity, R.anim.anim_pulsate);

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

                blockView.setImageResource(R.drawable.heart);
                blockView.startAnimation(animPulsate);
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




    public void deleteBlock() {   //deletes block from view
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(blockTouched == true) {  //block was touched
                    if(GlobalVariables.queryInt("hp") < 3) { //increases hp if hp not full
                        GlobalVariables.changeHp(1);
                    }
                }
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
        if(ClassicActivity.classicPrefs.getBoolean(GlobalVariables.soundSwitch,true)) {
            ClassicActivity.sound.playTouchSound();
        }
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                collapse(blockView);
            }
        });
        deleteBlock();
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

package akaa.com.colormaestro.game_blocks;

/**
 * Created by Akachukwu Okonkwo on 9/20/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorInflater;
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

import akaa.com.colormaestro.game_modes.ClassicGame;
import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;

//This is the basic type of game block
public class GameBlock {
    private int posX = 0;
    private int posY = 0;
    private double blockDimen; //width and height of the block
    private int blockColorPick;
    private int speed; //speed of the block
    private int screenLength; //ma length of screen
    private int blockEqualsScreenProb = 6; //probability of block color being the same as screen color (value is div by 10 so 20% = 2)

    public boolean blockTouched = false;
    public boolean destroyBlockTouched = false; //made true when the destroy powerup block is touched, allowing for score increment with no chance of hp decrement
    public boolean handleBlockCalled = false; //prevents calling of handleblock by hitbox if already called by block itself, and vice versa

    public String blockColor;

    private ConstraintLayout classicLayout;
    private Activity myActivity;

    public ImageView blockView;
    private ImageView actionBar;
    private ImageView health1;
    private ImageView health2;
    private ImageView health3;

    private TextView scoreBox;

    Animator animator;

    public GameBlock(Activity activity, ConstraintLayout classicGameLayout, Context context,int positionX,
                     ImageView bar, ImageView hp1, ImageView hp2, ImageView hp3, TextView scoreView, int sizeY, int blkSpeed) {

        myActivity = activity;
        blockView = new ImageView(context);
        classicLayout = classicGameLayout;
        posX = positionX;
        actionBar = bar;
        health1 = hp1;
        health2 = hp2;
        health3 = hp3;
        scoreBox = scoreView;
        screenLength = sizeY;
        speed = blkSpeed;
        blockDimen = screenLength*GlobalVariables.blockDimenFactor;

                //Log.d("screen",Integer.toString(screenLength));
        //To accomodate for different screenlengths, common standard ones listed below
        //Note 8: 2896
        //Galaxy s8+: 2770
        //Pixel 2 xl: 2644
        //Galaxy s8: 2518
        //Nexus 6 & 6p & pixel XL: 2392
        //Galaxy s7 edge: 1920
        //Nexus 5X: 1794
        //Pixel & Nexus 5: 1776
        //4.7 wxga: 1280
        //Nexus 4 & galaxy nexus : 1184
        //Nexus s: 800

        //setup animation and its listeners
        if(screenLength >= 2897){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move3000);
        }else if(screenLength >= 2781 && screenLength <= 2906){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move2896);
        }else if(screenLength >= 2655 && screenLength <= 2780){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move2770);
        }else if(screenLength >= 2529 && screenLength <= 2654){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move2644);
        }else if(screenLength >= 2403 && screenLength <= 2528){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move2518);
        }else if(screenLength >= 1931 && screenLength <= 2402){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move2392);
        }else if(screenLength >= 1805 && screenLength <= 1930){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move1920);
        }else if(screenLength >= 1787 && screenLength <= 1804){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move1794);
        }else if(screenLength >= 1291 && screenLength <= 1786){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move1776);
        }else if(screenLength >= 1195 && screenLength <= 1290){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move1280);
        }else if(screenLength >= 810 && screenLength <= 1194){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move1184);
        }else if(screenLength <= 800){
            animator = AnimatorInflater.loadAnimator(context, R.animator.move800);
        }
        animator.setTarget(blockView);
        animator.setDuration(speed);
        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener(){
            public void onAnimationStart(Animator animator){

            }

            public void onAnimationEnd(Animator animator){
                if(blockTouched == false && destroyBlockTouched == false){  //tells us that block is at end of screen if block not touched before animation ends
                    deleteBlock();
                }
            }

            public void onAnimationRepeat(Animator animator){

            }

            public void onAnimationCancel(Animator animator){

            }
        };
        animator.addListener(animatorListener);



        //Algorithm to increase the probability of the block color being the screencolor
        Random randInit = new Random();
        int x = randInit.nextInt(10000000);
        int z = x % 10; //rand number is modded by 10,
        if(z<blockEqualsScreenProb){   //probability of block color being the screencolor
            blockColorPick = 7;
        }else{
            Random rand = new Random();    //this time the current screen color has an equal probability of being picked as block color
            blockColorPick = rand.nextInt(6)+1;
        }

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

                if(GlobalVariables.frenzy == true){ //when frenzy is on, the block color of each new block is the same as the screen color
                    frenzyColor();
                }else {
                    if (blockColorPick == 1) {
                        blockView.setImageResource(R.drawable.black_gem);
                        blockColor = "BLACK";
                    } else if (blockColorPick == 2) {
                        blockView.setImageResource(R.drawable.white_gem);
                        blockColor = "WHITE";
                    } else if (blockColorPick == 3) {
                        blockView.setImageResource(R.drawable.blue_gem);
                        blockColor = "BLUE";
                    } else if (blockColorPick == 4) {
                        blockView.setImageResource(R.drawable.red_gem);
                        blockColor = "RED";
                    } else if (blockColorPick == 5) {
                        blockView.setImageResource(R.drawable.yellow_gem);
                        blockColor = "YELLOW";
                    } else if (blockColorPick == 6) {
                        blockView.setImageResource(R.drawable.green_gem);
                        blockColor = "GREEN";
                    } else if (blockColorPick == 7) { //when 7, block must equal screencolor
                        blockColor = GlobalVariables.queryString("screencolor");
                        if (blockColor == "BLACK") {  //changing blockcolor image resource to screen color
                            blockView.setImageResource(R.drawable.black_gem);
                        } else if (blockColor == "WHITE") {
                            blockView.setImageResource(R.drawable.white_gem);
                        } else if (blockColor == "BLUE") {
                            blockView.setImageResource(R.drawable.blue_gem);
                        } else if (blockColor == "RED") {
                            blockView.setImageResource(R.drawable.red_gem);
                        } else if (blockColor == "YELLOW") {
                            blockView.setImageResource(R.drawable.yellow_gem);
                        } else if (blockColor == "GREEN") {
                            blockView.setImageResource(R.drawable.green_gem);
                        }

                    }
                }
                moveBlock();


//                        if(GlobalVariables.queryInt("observecheck") == 0) { //makes sure that getviewtreeobserver runs once
//                            classicLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                public void onGlobalLayout() {
//                                    classicLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                                    GlobalVariables.changeObserve(1);
//
//                                }
//                            });
//                        }
//
//                        int[] locations = new int[2];
//                        blockView.getLocationOnScreen(locations);
//                        //int x = locations[0];
//                        currentPosY = locations[1];
//

            }
        });


    }


    public void moveBlock(){  //function for animating block across screen
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        });
    }

    public void pauseBlock(){
        animator.pause();
    }

    public void resumeBlock(){
        animator.resume();
    }

    public void setupTouchListener(){   //listens for touch event
        blockView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false) {
                        handleBlockTouch();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    public void handleBlockTouch(){ //handles the touch event accordingly
        if(handleBlockCalled == false) { //prevents call conflict from block and its hitbox
            handleBlockCalled = true;
            if(destroyBlockTouched == false) {
                blockTouched = true; //when block is touched, the animation end action in moveblock() is called so this ensures deleteblock is not called again
            }
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    collapse(blockView);
                }
            });
            deleteBlock();
        }
    }

    public void deleteBlock() {   //deletes block from view
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (blockTouched == true && destroyBlockTouched == false) {  //block was touched
                    if (blockColor == GlobalVariables.queryString("screencolor") || blockColor == GlobalVariables.frenzyColor) {
                        if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.soundSwitch,true)){
                            ClassicGame.sound.playTouchSound();
                        }

                        GlobalVariables.changeScore(1);
                    } else {
                        if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.soundSwitch,true)) {
                            ClassicGame.sound.playHpLossSound();
                        }
                        GlobalVariables.changeHp(0);
                    }
                } else if (blockTouched == false && destroyBlockTouched == false) {  //block reached end of screen
                    if (blockColor == GlobalVariables.queryString("screencolor") && GlobalVariables.frenzy == false) { //remove hp if block reached end of screen with same color as screen
                        if (GlobalVariables.queryInt("penalizeswitch") == 1) { //if the penalize switch is on, we can go ahead and penalize the player
                            if(ClassicGame.classicPrefs.getBoolean(GlobalVariables.soundSwitch,true)) {
                                ClassicGame.sound.playHpLossSound();
                            }
                            GlobalVariables.changeHp(0);
                        }
                    }
                } else if (destroyBlockTouched){ //destroy block powerup was touched, award score
                    GlobalVariables.changeScore(1);
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
                deleteTimer.schedule(deleteTask, 500);

            }
        });
    }

    //gives the current location of the block on the screen
    public int[] getLocation(){
//        if(GlobalVariables.queryInt("observecheck") == 0) { //makes sure that getviewtreeobserver runs once
//            classicLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                public void onGlobalLayout() {
//                    classicLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    GlobalVariables.changeObserve(1);
//
//                }
//            });
//        }

        int[] locations = new int[2];
        blockView.getLocationOnScreen(locations);  //x=location[0] and y=location[1]
        return locations;

    }

    private void frenzyColor(){ //to change all newly generated blocks (after frenzy) to frenzy when frenzy activated
        blockColor = GlobalVariables.frenzyColor;
        blockView.setImageResource(R.drawable.frenzy_gem);
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

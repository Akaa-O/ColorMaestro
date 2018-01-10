package akaa.com.colormaestro.game_blocks;

/**
 * Created by confo on 11/2/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import akaa.com.colormaestro.GlobalVariables;
import akaa.com.colormaestro.R;


//this invisible block accompanies each normal game block, thereby slightly increasing the touchable area of each block

public class HitBox {
    private int posX = 0;
    private double blockDimen; //width and height of the block
    private double speed; //speed of the block

    //since the hitbox and parentblock are of different sizes, a speed offset is required to keep them both aligned at all times
    //we need a speed offset that is 2 percent the current block speed. we then add this to the speed(hence making it 2 percent slower)
    private double speedOffset;

    private int screenLength; //ma length of screen
    private int hitBoxCoverage = 150; //how much length or width around the game block is covered by the hit box
    private int posXOffset = (hitBoxCoverage/2)/2; //offset needed to align parent block x cord in middle of hit box
    private int posY = -(hitBoxCoverage/2); //y position is also offset to align parent block y cord in middle of hit box

    public boolean hitBoxTouched = false;



    private ConstraintLayout classicLayout;
    private Activity myActivity;

    public ImageView blockView;
    private ImageView actionBar;
    private ImageView health1;
    private ImageView health2;
    private ImageView health3;

    private TextView scoreBox;

    private GameBlock parentBlock;

    Animator animator;

    public HitBox (Activity activity, ConstraintLayout classicGameLayout, Context context,int positionX,
                     ImageView bar, ImageView hp1, ImageView hp2, ImageView hp3, TextView scoreView, int sizeY, int blkSpeed, GameBlock block) {

        myActivity = activity;
        blockView = new ImageView(context);
        classicLayout = classicGameLayout;
        posX = positionX - posXOffset;
        actionBar = bar;
        health1 = hp1;
        health2 = hp2;
        health3 = hp3;
        scoreBox = scoreView;
        screenLength = sizeY;
        speedOffset = (blkSpeed * 0.08);  //2 percent of block speed
        speed = blkSpeed + speedOffset;
        parentBlock = block;

        blockDimen = screenLength*GlobalVariables.blockDimenFactor;

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
            Log.d("wow", "in here");
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
        final int intspeed = (int) speed; //double speed has to be cast to int for animation function
        animator.setDuration(intspeed);
        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener(){
            public void onAnimationStart(Animator animator){

            }

            public void onAnimationEnd(Animator animator){
                if(hitBoxTouched == false){  //tells us that block is at end of screen if block not touched before animation ends
                    deleteBlock();
                }
            }

            public void onAnimationRepeat(Animator animator){

            }

            public void onAnimationCancel(Animator animator){

            }
        };
        animator.addListener(animatorListener);

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                classicLayout.addView(blockView);
                parentBlock.blockView.bringToFront();
                actionBar.bringToFront();    //makes sure action bar, health boxes,parentblock and score bar aren't obstructed by blocks
                health1.bringToFront();
                health2.bringToFront();
                health3.bringToFront();
                scoreBox.bringToFront();

                setupTouchListener(); //sets up listener for touch on a block

                android.view.ViewGroup.LayoutParams layoutParams = blockView.getLayoutParams();
                layoutParams.width = (int)blockDimen + (hitBoxCoverage/2); //adjust block size and put on layout on initial position
                layoutParams.height =(int)blockDimen + hitBoxCoverage;
                blockView.setLayoutParams(layoutParams);
                blockView.setX(posX);
                blockView.setY(posY);

                blockView.setImageResource(R.drawable.blackbonusblock); //makes the hitbox square shape
                blockView.setAlpha(0f);
                moveBlock();

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




    public void deleteBlock() {   //deletes block from view
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(parentBlock.blockTouched == false && hitBoxTouched == true) {
                        parentBlock.handleBlockTouch(); //might throw error if parent block doesn't exist
                    }
                }catch (NullPointerException e){

                }

                classicLayout.removeView(blockView);
            }
        });

    }

    public void setupTouchListener(){   //listens for touch event
        blockView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(GlobalVariables.isGamePaused == false && GlobalVariables.gameOver == false){
                        handleBlockTouch();
                        return true;
                    }

                }
                return false;
            }
        });
    }


    public void handleBlockTouch(){ //handles the touch event accordingly
        //Log.d("Hitbox:", "touched");
        hitBoxTouched = true; //when block is touched, the animation end action in moveblock() is called so this ensures deleteblock is not called again
        deleteBlock();

    }

}

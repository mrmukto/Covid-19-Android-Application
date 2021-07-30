package com.shoutlab.coronabd.Onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;
import com.shoutlab.coronabd.CreateProfileActivity;
import com.shoutlab.coronabd.PreferenceManager;
import com.shoutlab.coronabd.R;

public class OnboardingActivity extends AppCompatActivity {

    private Swipe swipe;
    private int board = 1;
    private TextView boardingTitle, boardingText;
    private ImageView boardingImage;
    private View swipeInstruction, board4View;
    private Animation fadeIn1, fadeOut1, fadeIn2, fadeOut2, fadeIn3, fadeOut3, fadeIn4, fadeOut4, fadeIn5, fadeOut5, moveLeft, moveRight, moveLeftReset, moveRightReset, fadeIn6, fadeOut6;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean swipeFlag = false;
    private ImageView maleChar, femaleChar, distanceMeter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        fadeIn1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_in);
        fadeIn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_in);
        fadeIn3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_in);
        fadeIn4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_in);
        fadeIn5 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_in);
        fadeIn6 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_in);

        fadeOut1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_out);
        fadeOut2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_out);
        fadeOut3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_out);
        fadeOut4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_out);
        fadeOut5 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_out);
        fadeOut6 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.intro_fade_out);

        moveLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_char_left);
        moveRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_char_right);

        moveLeftReset = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_reset);
        moveRightReset = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_reset);

        PreferenceManager preferenceManager = new PreferenceManager(OnboardingActivity.this);

        boardingTitle = findViewById(R.id.boardingTitle);
        boardingText = findViewById(R.id.boardingText);
        boardingImage = findViewById(R.id.boardingImage);
        swipeInstruction = findViewById(R.id.swipeInstruction);
        board4View = findViewById(R.id.board4View);
        maleChar = findViewById(R.id.maleChar);
        femaleChar = findViewById(R.id.femaleChar);
        distanceMeter = findViewById(R.id.viewDistance);

        if(!preferenceManager.getIsLoggedIn()){
            swipeInstruction.setVisibility(View.VISIBLE);
        }

        boardingTitle.setText("Stay at home");
        boardingText.setText("Stay at home if you begin to feel unwell, even with mild symptoms such as headache and slight runny nose, until you recover.");
        boardingImage.setImageDrawable(getResources().getDrawable(R.drawable.board1_img));

        swipe = new Swipe();
        swipe.setListener(new SwipeListener() {
            @Override
            public void onSwipingLeft(MotionEvent event) {

            }

            @Override
            public boolean onSwipedLeft(MotionEvent event) {
                swipeFlag = false;
                board++;
                switchBoard();
                return false;
            }

            @Override
            public void onSwipingRight(MotionEvent event) {

            }

            @Override
            public boolean onSwipedRight(MotionEvent event) {
                if(board > 1){
                    swipeFlag = true;
                    board--;
                    switchBoard();
                }
                return false;
            }

            @Override
            public void onSwipingUp(MotionEvent event) {

            }

            @Override
            public boolean onSwipedUp(MotionEvent event) {
                return false;
            }

            @Override
            public void onSwipingDown(MotionEvent event) {

            }

            @Override
            public boolean onSwipedDown(MotionEvent event) {
                return false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void switchBoard() {
        switch (board){
            case 1:
                boardingTitle.startAnimation(fadeOut1);
                boardingText.startAnimation(fadeOut1);
                boardingImage.startAnimation(fadeOut1);
                fadeOut1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        boardingTitle.setText("Stay at home");
                        boardingText.setText("Stay at home if you begin to feel unwell, even with mild symptoms such as headache and slight runny nose, until you recover.");
                        boardingImage.setImageDrawable(getResources().getDrawable(R.drawable.board1_img));

                        boardingTitle.startAnimation(fadeIn1);
                        boardingText.startAnimation(fadeIn1);
                        boardingImage.startAnimation(fadeIn1);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case 2:
                boardingTitle.startAnimation(fadeOut2);
                boardingText.startAnimation(fadeOut2);
                boardingImage.startAnimation(fadeOut2);
                fadeOut2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        boardingTitle.setText("Work from home");
                        boardingText.setText("Stay aware of the latest information on the COVID-19 outbreak and continue your work from home.");
                        boardingImage.setImageDrawable(getResources().getDrawable(R.drawable.board2_img));

                        boardingTitle.startAnimation(fadeIn2);
                        boardingText.startAnimation(fadeIn2);
                        boardingImage.startAnimation(fadeIn2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case 3:
                boardingTitle.startAnimation(fadeOut3);
                boardingText.startAnimation(fadeOut3);

                if(swipeFlag){
                    board4View.startAnimation(fadeOut3);
                } else {
                    boardingImage.startAnimation(fadeOut3);
                }

                fadeOut3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        boardingTitle.setText("Wash your hands frequently");
                        boardingText.setText("Washing your hands with soap and water or using alcohol-based hand rub kills viruses that may be on your hands.");
                        boardingImage.setImageDrawable(getResources().getDrawable(R.drawable.board3_img));

                        if(swipeFlag){
                            maleChar.startAnimation(moveLeftReset);
                            femaleChar.startAnimation(moveRightReset);
                            boardingImage.setVisibility(View.VISIBLE);
                            board4View.setVisibility(View.INVISIBLE);
                            distanceMeter.setVisibility(View.INVISIBLE);
                        }

                        boardingTitle.startAnimation(fadeIn3);
                        boardingText.startAnimation(fadeIn3);
                        boardingImage.startAnimation(fadeIn3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case 4:
                boardingTitle.startAnimation(fadeOut4);
                boardingText.startAnimation(fadeOut4);
                boardingImage.startAnimation(fadeOut4);

                fadeOut4.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        maleChar.startAnimation(moveLeftReset);
                        femaleChar.startAnimation(moveRightReset);
                        boardingImage.setVisibility(View.INVISIBLE);
                        board4View.setVisibility(View.VISIBLE);

                        boardingTitle.setText("Maintain social distancing");
                        boardingText.setText("Maintain at least 1 metre (3 feet) distance between yourself and anyone who is coughing or sneezing.");
                        boardingImage.setImageDrawable(getResources().getDrawable(R.drawable.board4_img));

                        boardingTitle.startAnimation(fadeIn4);
                        boardingText.startAnimation(fadeIn4);
                        board4View.startAnimation(fadeIn4);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                fadeIn4.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        maleChar.startAnimation(moveLeft);
                        femaleChar.startAnimation(moveRight);
                        distanceMeter.startAnimation(fadeIn6);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                fadeIn6.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        distanceMeter.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case 5:
                boardingTitle.startAnimation(fadeOut5);
                boardingText.startAnimation(fadeOut5);
                board4View.startAnimation(fadeOut5);

                fadeOut5.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        board4View.setVisibility(View.INVISIBLE);
                        distanceMeter.setVisibility(View.INVISIBLE);

                        boardingTitle.setText("Wear a mask");
                        boardingText.setText("Masks are effective only when used in combination with frequent hand-cleaning with hand rub or soap and water.");
                        boardingImage.setImageDrawable(getResources().getDrawable(R.drawable.board5_img));

                        boardingTitle.startAnimation(fadeIn5);
                        boardingText.startAnimation(fadeIn5);
                        boardingImage.startAnimation(fadeIn5);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                fadeIn5.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        boardingImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case 6:
                Intent intent = new Intent(OnboardingActivity.this, CreateProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        swipeInstruction.setVisibility(View.GONE);
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        swipe.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(OnboardingActivity.this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}

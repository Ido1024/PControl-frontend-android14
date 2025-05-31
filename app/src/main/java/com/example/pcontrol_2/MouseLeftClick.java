package com.example.pcontrol_2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.time.Duration;
import java.time.Instant;

public class MouseLeftClick extends View {
    private static final String TAG = "Log MouseLeftClick";
    public static final int HOLD_TIME_NEEDED = 150;
    private boolean isFingerTouchedFirstTime = false;
    private boolean isPressedOnce;
    private boolean isReleaseOnce;
    public float touchPointX;
    public float touchPointY;
    private Instant start, end;
    private boolean isFingerHold;
    int leftClickPadX;
    int leftClickPadY;

    public MouseLeftClick(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        leftClickPadX = findViewById(R.id.leftClick).getWidth();//can't do final because the view isn't loaded
        leftClickPadY = findViewById(R.id.leftClick).getHeight();//can't do final because the view isn't loaded
        touchPointX = event.getX();//get x
        touchPointY = event.getY();//get y
        if (isFingerOnPad(touchPointX, touchPointY)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { //if finger touch once on screen
                start = Instant.now(); // getting the time(MS) right now
                isFingerTouchedFirstTime = true;
                isPressedOnce = false;
                isReleaseOnce = false;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                end = Instant.now();// getting the time(MS) right now
                Duration timeElapsed = Duration.between(start, end);
                Log.d(TAG, "onTouchEvent: Time taken: " + timeElapsed.toMillis() + " milliseconds");
                if (timeElapsed.toMillis() > HOLD_TIME_NEEDED) {
                    isFingerHold = true;
                    Log.d(TAG, "onTouchEvent: Finger holding the button");
                    if (isFingerTouchedFirstTime && !isPressedOnce) {
                        Log.d(TAG, "onTouchEvent: sending to server left click pressed and hold (after first touch)");
                        MainActivity.sendMouseClickToServer("Left Click Press");//send left click to MainActivity
                        isPressedOnce = true;
                    }
                } else {
                    isFingerHold = false;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP && !isFingerHold) {
                Log.d(TAG, "onTouchEvent: sending to server first touch on left click button - pressed");
                MainActivity.sendMouseClickToServer("Left Click First Touch");//send left click to MainActivity
                isFingerTouchedFirstTime = false;

            } else if (event.getAction() == MotionEvent.ACTION_UP && isFingerTouchedFirstTime && (!isReleaseOnce)) {
                    Log.d(TAG, "onTouchEvent: finger up: sending to server left click release(only after hold)");
                    MainActivity.sendMouseClickToServer("Left Click Release");
                    isFingerTouchedFirstTime = false;
                    isReleaseOnce = true;
            }
        } else {
            if (!isReleaseOnce) {
                Log.d(TAG, "onTouchEvent: finger is not touching in the pad");
                MainActivity.sendMouseClickToServer("Left Click Release");
                isFingerTouchedFirstTime = false;
                isReleaseOnce = true;
            }
        }
        return true;
    }

    public boolean isFingerOnPad(float x, float y) {
        //not on area
        return (x > 0 && x < leftClickPadX) && (y > 0 && y < leftClickPadY);
    }
}
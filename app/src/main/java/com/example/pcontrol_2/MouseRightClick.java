package com.example.pcontrol_2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.time.Duration;
import java.time.Instant;

public class MouseRightClick extends View {
    private static final String TAG = "Log MouseRightClick";
    final int holdTimeNeeded = MouseLeftClick.HOLD_TIME_NEEDED;
    private boolean isFingerTouchedFirstTime = false;
    private boolean isPressedOnce;
    private boolean isReleaseOnce;
    public float touchPointX;
    private float touchPointY;
    private Instant start, end;
    private boolean isFingerHold;
    int rightClickPadX;
    int rightClickPadY;

    public MouseRightClick(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        rightClickPadX = findViewById(R.id.rightClick).getWidth();
        rightClickPadY = findViewById(R.id.rightClick).getHeight();
        touchPointX = event.getX();//get x
        touchPointY = event.getY();//get y
        if (isFingerOnPad(touchPointX, touchPointY)) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { //if finger touch once on screen
                start = Instant.now(); // getting the time(MS) right now
                Log.d(TAG, "onTouchEvent: sending to server first touch on right click button - pressed");
                isFingerTouchedFirstTime = true;
                isPressedOnce = false;
                isReleaseOnce = false;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                end = Instant.now();// getting the time(MS) right now
                Duration timeElapsed = Duration.between(start, end);
                Log.d(TAG, "onTouchEvent: Time taken: " + timeElapsed.toMillis() + " milliseconds");
                if (timeElapsed.toMillis() > holdTimeNeeded) {
                    isFingerHold = true;
                    Log.d(TAG, "onTouchEvent: Finger holding the button");
                    if (isFingerTouchedFirstTime && !isPressedOnce) {
                        Log.d(TAG, "onTouchEvent: sending to server right click pressed and hold (after first touch)");
                        MainActivity.sendMouseClickToServer("Right Click Press");//send right click to MainActivity
                        isPressedOnce = true;
                    }
                } else {
                    isFingerHold = false;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP && !isFingerHold) {
                Log.d(TAG, "onTouchEvent: sending to server first touch on right click button - pressed");
                MainActivity.sendMouseClickToServer("Right Click First Touch");//send right click to MainActivity
                isFingerTouchedFirstTime = false;

            } else if (event.getAction() == MotionEvent.ACTION_UP && isFingerTouchedFirstTime && !isReleaseOnce) {
                    Log.d(TAG, "onTouchEvent: finger up: sending to server right click release(only after hold)");
                    MainActivity.sendMouseClickToServer("Right Click Release");
                    isFingerTouchedFirstTime = false;
                    isReleaseOnce = true;

            }
        } else {
            if (!isReleaseOnce) {
                Log.d(TAG, "onTouchEvent: finger is not touching in the touchPad");
                MainActivity.sendMouseClickToServer("Right Click Release");
                isFingerTouchedFirstTime = false;
                isReleaseOnce = true;
            }
        }
        return true;
    }

    public boolean isFingerOnPad(float x, float y) {
        //not on area
        return (x > 0 && x < rightClickPadX) && (y > 0 && y < rightClickPadY);
    }
}
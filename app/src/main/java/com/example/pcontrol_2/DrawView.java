package com.example.pcontrol_2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    private static final String TAG = "Log DrawView";
    public static final int MAKE_CIRCLE_DRAW_DISAPPEAR = -10000;
    Paint paint0 = new Paint();
    Paint paint1 = new Paint();
    public boolean isFingerOnScreen;
    public float touchPointX0;
    public float touchPointY0 = MAKE_CIRCLE_DRAW_DISAPPEAR;
    public float touchPointX1;
    public float touchPointY1 = MAKE_CIRCLE_DRAW_DISAPPEAR;
    public boolean isTwoFingersOnScreen = false;
    public int numOfFingerOnScreen;
    int touchPadX;// x length of touchpad - can't get the value from here. the view didn't rendered
    int touchPadY;// y length of touchpad - can't get the value from here. the view didn't rendered

    public DrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint0.setColor(Color.RED);
        paint0.setStrokeWidth(10);
        paint0.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(touchPointX0, touchPointY0, 30, paint0);
        paint1.setColor(Color.RED);
        paint1.setStrokeWidth(10);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(touchPointX1, touchPointY1, 30, paint1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchPadX = findViewById(R.id.TouchPadAreaView).getWidth();
        touchPadY = findViewById(R.id.TouchPadAreaView).getHeight();
        numOfFingerOnScreen = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isFingerOnScreen = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isTwoFingersOnScreen = true;
                break;

            case MotionEvent.ACTION_POINTER_UP://2nd finger leave the screen(only 1 finger in the screen touching)
                isTwoFingersOnScreen = true; //waiting for 2nd finger to leave
                isFingerOnScreen = false;
                break;

            case MotionEvent.ACTION_UP:
                isFingerOnScreen = false;
                isTwoFingersOnScreen = false;
                touchPointY1 = -10000;//make the circle draw disappear
                break;

            case MotionEvent.ACTION_MOVE:
                touchPointX0 = event.getX();//get x for id=0 touch
                touchPointY0 = event.getY();//get y for id=0 touch
                if (numOfFingerOnScreen == 2) {
                    touchPointX1 = event.getX(1);//get x for id=1 touch
                    touchPointY1 = event.getY(1);//get y for id=1 touch
                    sendZoomInOrOutToServer(touchPointX0, touchPointY0, touchPointX1, touchPointY1, isFingerOnScreen);
                } else if (isTwoFingersOnScreen) {
                    isFingerOnScreen = false;
                    sendZoomInOrOutToServer(touchPointX0, touchPointY0, touchPointX1, touchPointY1, isFingerOnScreen);// send again 1 more time to get isFingerOnScreen = false
                } else if (numOfFingerOnScreen == 1) {
                    sendPointToServer(touchPointX0, touchPointY0, isFingerOnScreen);
                }
                break;
            default:
                Log.d(TAG, "onTouchEvent: Action not identify");
        }
        invalidate();// circle being redrawn
        return true;
    }

    public void sendPointToServer(float touchPointX0, float touchPointY0,
                                  boolean isFingerOnScreen) {
        if (!isPointOnTouchPad(touchPointX0, touchPointY0)) {
            touchPointX0 = 0;
            touchPointY0 = 0;
        }
        Log.d(TAG, "sendPointToServer: sending to server one finger touching the screen");
        MainActivity.sendMouseLocationToServer(touchPointX0, touchPointY0, isFingerOnScreen);
    }

    public void sendZoomInOrOutToServer(float touchPointX0, float touchPointY0, float touchPointX1, float touchPointY1,
                                        boolean isFingerOnScreen) {
        if (!isPointOnTouchPad(touchPointX0, touchPointY0) || (!isPointOnTouchPad(touchPointX1, touchPointY1))) {
            touchPointX0 = 0;
            touchPointY0 = 0;
            touchPointX1 = 0;
            touchPointY1 = 0;
        }
        Log.d(TAG, "sendZoomInOrOutToServer: sending to server two fingers touching the screen");
        MainActivity.sendMouseZoomInOrOutToServer(touchPointX0, touchPointY0, touchPointX1, touchPointY1, isFingerOnScreen);
    }

    public boolean isPointOnTouchPad(float x, float y) {
        return x < touchPadX && x > 0 && ((y < touchPadY && y > 0) || y == MAKE_CIRCLE_DRAW_DISAPPEAR);
    }
}
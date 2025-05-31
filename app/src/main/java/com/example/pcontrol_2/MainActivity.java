package com.example.pcontrol_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
//todo this is the final android 14 works good!
public class MainActivity extends AppCompatActivity {
    public static final int PORT_AND_IP_KEY = 1;
    public static final String KEYBOARD_KEY = "com.example.PControl.KEYBOARD";
    public static int portMessageInput = -1;
    public static String ipMessageInput = "null";
    public static Context mainActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityContext = getContext();
    }

    public void keyboardButtonClicked(View view) {
//navigate Keyboard activity after click the button 'keyboard'
        Intent intent = new Intent(MainActivity.this, KeyboardActivity.class); //move from MainActivity to KeyboardActivity
        intent.putExtra(KEYBOARD_KEY, portMessageInput + ":" + ipMessageInput);
        startActivity(intent);
    }

    public void helpButtonClicked(View view) {
//navigate Keyboard activity after click the button 'keyboard'
        Intent intent = new Intent(MainActivity.this, HelpActivity.class); //move from MainActivity to KeyboardActivity
        startActivity(intent);
    }

    public void LoginButtonClicked(View view) {
//navigate login activity after click the button 'login'
        Intent intent = new Intent(MainActivity.this, LoginActivity.class); //move from MainActivity to LoginActivity
        startActivityForResult(intent, PORT_AND_IP_KEY);//need to get the port and the ip that entered on LoginActivity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed before
        if (requestCode == PORT_AND_IP_KEY && (resultCode == RESULT_OK)) {
            String resultFromLoginActivity = data.getStringExtra(LoginActivity.PORT_AND_IP_MESSAGE_KEY);
            System.out.println("resultFromLoginActivity: " + resultFromLoginActivity);
            if (!resultFromLoginActivity.equals("wrong value")) {
                String[] resultArray = resultFromLoginActivity.split(":", 2);
                portMessageInput = Integer.parseInt(resultArray[0]);
                ipMessageInput = resultArray[1];
            }
        }
    }

    public static void sendMouseLocationToServer(float x0, float y0, boolean isFingerOnScreen) {
        sendMessageToUDPClass("move mouse:" + x0 + ":" + y0 + ":" + isFingerOnScreen);
    }

    public static void sendMouseZoomInOrOutToServer(float x0, float y0, float x1, float y1, boolean isFingerOnScreen) {
        sendMessageToUDPClass("twoFingers:" + x0 + ":" + y0 + ":" + x1 + ":" + y1 + ":" + isFingerOnScreen);//change to twoFingers
    }

    public static void sendMouseClickToServer(String click) {//sending to server left click or right click
        sendMessageToUDPClass("click:" + click);
    }

    public static void sendMessageToUDPClass(String msgToUdpClass) {
        Log.d("Log MainActivity", "sendMessageToUDPClass: sending the message = " + msgToUdpClass + " to the udp class");
        UdpClient.messageToSendToServer = msgToUdpClass;
        UdpClient.server_ip = ipMessageInput;
        UdpClient.serverPort = portMessageInput;
        UdpClient udpClient = new UdpClient();
        udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mainActivityContext);
    }

    public Context getContext() {
        return this;
    }
}

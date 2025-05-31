package com.example.pcontrol_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class KeyboardActivity extends AppCompatActivity {
    private static final String TAG = "Log KeyboardActivity";
    public static final String PRESS_ENTER = "Press Key:Enter";
    public static final String PRESS_DELETE = "Press Key:Delete";
    public static String msgEditTextInput;
    public static int portMessageInput;
    public static String ipMessageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        Intent intent = getIntent();
        String messageFromMainActivity = intent.getStringExtra(MainActivity.KEYBOARD_KEY);
        String[] portAndIpAndIsLoginSuccessfulArray = messageFromMainActivity.split(":", 2);
        portMessageInput = Integer.parseInt(portAndIpAndIsLoginSuccessfulArray[0]);
        ipMessageInput = portAndIpAndIsLoginSuccessfulArray[1];
    }

    public void sendMsgToServerButtonClicked(View view) {
        EditText msgEditText = (EditText) findViewById(R.id.msg_edit_text);
        msgEditTextInput = (msgEditText.getText()).toString();
        msgEditText.setText("");//clear input place
        Log.d(TAG, "SendMsgToServerButtonClicked: msg from client from keyboard activity:" + msgEditTextInput);
        UdpClient.messageToSendToServer = "msg from keyboard:" + msgEditTextInput;
        UdpClient.server_ip = ipMessageInput;
        UdpClient.serverPort = portMessageInput;
        UdpClient udpClient = new UdpClient();
        udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    public void sendPressDeleteToServerButtonClicked(View view) {
        UdpClient.messageToSendToServer = PRESS_DELETE;
        UdpClient.server_ip = ipMessageInput;
        UdpClient.serverPort = portMessageInput;
        UdpClient udpClient = new UdpClient();
        udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    public void sendPressEnterToServerButtonClicked(View view) {
        UdpClient.messageToSendToServer = PRESS_ENTER;
        UdpClient.server_ip = ipMessageInput;
        UdpClient.serverPort = portMessageInput;
        UdpClient udpClient = new UdpClient();
        udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    public void backButtonClicked(View view) {
        finish();
    }

}
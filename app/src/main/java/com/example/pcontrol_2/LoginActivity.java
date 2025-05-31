package com.example.pcontrol_2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Log LoginActivity";
    public static final String PORT_AND_IP_MESSAGE_KEY = "com.example.PControl.PORTANDIP";
    private static final String LOGIN = UdpClient.CLIENT_CONNECTION_MSG;
    private static final Pattern PATTERN_IP = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Pattern PATTERN_PORT = Pattern.compile(
            "^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$");
    public static final String IP_DEFAULT = UdpClient.IP_DEFAULT;//before login
    public static final int PORT_DEFAULT = UdpClient.PORT_DEFAULT;//before login
    public static String portMessageInput;
    public static String ipMessageInput;
    private static String msgForMainActivity = "wrong value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void ipAndPortButtonClicked(View view) {
        EditText portEditText = (EditText) findViewById(R.id.port_edit_text);
        portMessageInput = portEditText.getText().toString();
        EditText ipEditText = (EditText) findViewById(R.id.ip_edit_text);
        ipMessageInput = ipEditText.getText().toString();
        Log.d(TAG, "IpAndPortButtonClicked:trying| port:" + portMessageInput + "\nip:" + ipMessageInput);
        msgForMainActivity = portMessageInput + ":" + ipMessageInput;
        if (ipMessageInput.equals("") || portMessageInput.equals("")) {
            Toast.makeText(this, "Please enter ip or port", Toast.LENGTH_SHORT).show();
            msgForMainActivity = "wrong value";
        }
        if (!isIpValid(ipMessageInput) || !isPortInRange(portMessageInput)) {
            Toast.makeText(this, "Invalid ip or port", Toast.LENGTH_SHORT).show();
            msgForMainActivity = "wrong value";
        }
        if (!msgForMainActivity.equals("wrong value")) {
            UdpClient.messageToSendToServer = LOGIN;
            UdpClient.server_ip = ipMessageInput;
            UdpClient.serverPort = Integer.parseInt(portMessageInput);
            UdpClient udpClient = new UdpClient();
            udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
        }
    }

    public void logoutButtonClicked(View view) {
        UdpClient.messageToSendToServer = "connection: disconnection";
        try {
            UdpClient.server_ip = ipMessageInput;
            UdpClient.serverPort = Integer.parseInt(portMessageInput);
        } catch (Exception e) {
            Log.d(TAG, "logoutButtonClicked: user didn't enter port and ip");
            UdpClient.server_ip = IP_DEFAULT;
            UdpClient.serverPort = PORT_DEFAULT;
        }
        UdpClient udpClient = new UdpClient();
        udpClient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
    }

    public void backButtonClicked(View view) {
        Intent sendIntent = new Intent();
        sendIntent.putExtra(PORT_AND_IP_MESSAGE_KEY, msgForMainActivity);
        setResult(Activity.RESULT_OK, sendIntent);
        finish();
    }

    //CHECKING VALID IP
    public static boolean isIpValid(String ip) {
        return PATTERN_IP.matcher(ip).matches();
    }

    //    checking valid port
    public static boolean isPortInRange(String port) {
        return PATTERN_PORT.matcher(port).matches();
    }
}
package com.example.pcontrol_2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class UdpClient extends AsyncTask<Context, Void, String> {
    private static final String TAG = "Log UDP_Client";
    private static final byte[] BUFFER = new byte[2048];
    public static final String CLIENT_CONNECTION_MSG = "connection: Hello server, can you read me?";
    private static final String CIPHER_MODE = "RSA/ECB/PKCS1Padding";
    private static final String CONNECTION_SUCCESS = "connection success";
    private static final String DISCONNECTION = "connection: disconnection";
    private static final String CONNECTION_FAILED_MSG = "connection failed";
    private static final String DISCONNECTION_MSG = "disconnection";
    private static final String PLEASE_CONNECT_TO_SERVER = "Please Connect First\n Using Login Button ";
    public static final int PORT_DEFAULT = -1;//before login
    public static final String IP_DEFAULT = "null";//before login ("null" and not null, because the value from the keyboard activity return "null")
    public static String messageToSendToServer;
    private String encryptedMessageToServer;
    public static String server_ip;
    public static int serverPort;
    private static Context connectionContext;
    private static String publicKeyString;
    private static boolean isLogin = false;


    @Override
    protected String doInBackground(Context... params) {
        InetAddress inetAddress;
        DatagramSocket datagramSocket = null;
        DatagramPacket datagramPacket;
        //checking if the user set an ip and port on login activity
        if (server_ip.equals(IP_DEFAULT) && serverPort == PORT_DEFAULT) {
            Log.d(TAG, "doInBackground: print pls connect to screen: " + PLEASE_CONNECT_TO_SERVER);
            connectionContext = params[0];
            return PLEASE_CONNECT_TO_SERVER;
        }
        try {
            inetAddress = InetAddress.getByName(server_ip);
            datagramSocket = new DatagramSocket();
            if (messageToSendToServer.equals(CLIENT_CONNECTION_MSG) && !isLogin) {
                datagramPacket = new DatagramPacket(messageToSendToServer.getBytes(), 0, messageToSendToServer.length(), inetAddress, serverPort);
                datagramSocket.send(datagramPacket);
                datagramSocket.setSoTimeout(1000);// set the timeout in milliSeconds.
                String msgReceived;
                try {
                    datagramPacket = new DatagramPacket(BUFFER, BUFFER.length);
                    datagramSocket.receive(datagramPacket);
                    msgReceived = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                    String[] msgFromServer = msgReceived.split(":", 2);
                    msgReceived = msgFromServer[0];
                    publicKeyString = msgFromServer[1];
                    System.out.println("publicKeyString = " + publicKeyString);
                    System.out.println("Do connection success?: " + msgReceived);
                    System.out.println(getPublicKeyFromString(publicKeyString));
                } catch (SocketTimeoutException e) {
                    Log.d(TAG, "doInBackground: timeout - connection failed");
                    connectionContext = params[0];
                    return CONNECTION_FAILED_MSG;
                }
                if (msgReceived.equals(CONNECTION_SUCCESS)) {
                    connectionContext = params[0];
                    isLogin = true;
                    return CONNECTION_SUCCESS;
                }

            } else {
                if (messageToSendToServer.equals(DISCONNECTION) && isLogin) {
                    isLogin = false;
                    Log.d(TAG, "doInBackground: disconnection from the server");
                    datagramPacket = new DatagramPacket(messageToSendToServer.getBytes(), 0, messageToSendToServer.length(), inetAddress, serverPort);
                    datagramSocket.send(datagramPacket);
                    MainActivity.ipMessageInput = "null";
                    MainActivity.portMessageInput = -1;
                    Log.d(TAG, "doInBackground: return to default");
                    return DISCONNECTION_MSG;
                }
                if (isLogin) {
                    encryptedMessageToServer = encryptMessageToServer(messageToSendToServer, publicKeyString);
                    datagramPacket = new DatagramPacket(encryptedMessageToServer.getBytes(), 0, encryptedMessageToServer.length(), inetAddress, serverPort);
                    datagramSocket.send(datagramPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        if (string != null) {
            Toast.makeText(connectionContext, string, Toast.LENGTH_SHORT).show();
        }
    }

    public static String encryptMessageToServer(String message, String publicKeyString) throws Exception {
        UdpClient rsa = new UdpClient();
        String encryptedMessage = rsa.encrypt(message, publicKeyString);
        return encryptedMessage;
    }

    public String encrypt(String message, String publicKeyString) throws Exception {
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);//cipher mode
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKeyFromString(publicKeyString));
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private PublicKey getPublicKeyFromString(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {//decode
        X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpecPublic);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}

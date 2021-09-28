package com.example.mqttandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button btnConnect;
    private Button btnPublish;
    private MqttAndroidClient mqttAndroidClient;
    private Button btnSubscribe;
    private TextView tvMsg;
    private TextView tvStatus;
    private EditText inputMsg;

    String topic = "testtopic";
    String serverURI = "tcp://broker.emqx.io:1883";
    String clientId = "MqttAndroid";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btnConnect = findViewById(R.id.btn_connect);
        btnPublish = findViewById(R.id.btn_publish);
        btnSubscribe = findViewById(R.id.btn_subscribe);

        btnConnect.setOnClickListener(this);
        btnPublish.setOnClickListener(this);
        btnSubscribe.setOnClickListener(this);

        tvMsg = findViewById(R.id.tv_msg);
        tvStatus = findViewById(R.id.text);

        inputMsg = findViewById(R.id.edt_input);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_connect:

                tvStatus.setText("connect...");
                connect();

                break;


            case R.id.btn_subscribe:
                Log.e(TAG, "onClick: subscribe");
                subscribe();
                break;


            case R.id.btn_publish:

                publish();
                break;
        }
    }

    //发布
    private void publish() {

        MqttMessage message = new MqttMessage();
        message.setQos(0);
        message.setRetained(false);
        String msg = inputMsg.getText().toString();
        message.setPayload((System.currentTimeMillis() + "-" + msg).getBytes());
        try {
            mqttAndroidClient.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    tvStatus.setText("publish onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    tvStatus.setText("publish onFailure");
                    Log.e(TAG, "onFailure: " + exception.getMessage());

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    //订阅
    private void subscribe() {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.e(TAG, "onSuccess: " + asyncActionToken.getClient().getClientId());
                    tvStatus.setText("subscribe onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    tvStatus.setText("subscribe onFailure:" + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    //连接
    private void connect() {

//        Broker:broker-cn.emqx.io
//
//        TCP 端口:1883
//
//        Websocket 端口:8083
//
//        TCP/TLS 端口:8883
//
//        Websocket/TLS 端口:8084
//

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverURI, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost: ");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                Log.e(TAG, "messageArrived: " + topic + ":" + message.toString());

                tvMsg.setText("time: " + System.currentTimeMillis() + "\r\n" + "topic: " + topic + "\r\n" + "message: " + message.toString());


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

                Log.e(TAG, "deliveryComplete: ");
            }
        });


        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setAutomaticReconnect(true);

        try {
            mqttAndroidClient.connect(connectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.e(TAG, "connect onSuccess: " + asyncActionToken.getClient().getClientId());

                    Toast.makeText(MainActivity.this, "connect onSuccess", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("connect onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    tvStatus.setText("connect onFailure");
                    Log.e(TAG, "connect onFailure: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


}
package com.example.mqtt;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import pl.pawelkleczkowski.customgauge.CustomGauge;


public class MainActivity extends AppCompatActivity {

    private Button btnpublish;
    private TextView recep;
    private TextView current;
    private TextView voltage;
    private TextView power;
    private TextView cost;

    CustomGauge gauge1;
    CustomGauge gauge2;
    CustomGauge gauge3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnpublish = findViewById(R.id.button2);
        recep = findViewById(R.id.textView);
        current = findViewById(R.id.textView3);
        voltage = findViewById(R.id.textView4);
        power = findViewById(R.id.textView6);
        gauge1 = findViewById(R.id.gauge);
        gauge2 = findViewById(R.id.gauge2);
        gauge3 = findViewById(R.id.gauge3);
        cost = findViewById(R.id.textView7);


        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                  //  Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }




        btnpublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "currentOut";
                String topic2 = "voltageOut";
                String topic3 = "powerOut";
                String topic4 = "priceOut";


                int qos = 1;
                try {
                    IMqttToken subToken = client.subscribe(topic, qos);
                    client.subscribe(topic2,qos);
                    client.subscribe(topic3,qos);
                    client.subscribe(topic4,qos);

                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //Toast.makeText(MainActivity.this,new String(message.getPayload()), Toast.LENGTH_SHORT).show();
                Log.w("Debug",message.toString());

                if (topic.contains("currentOut")){
                    current.setText(message.toString() + " mA");
                    gauge1.setValue(Integer.valueOf(message.toString()));
                }


                else if (topic.contains("voltageOut")){
                    voltage.setText(message.toString() + " V");
                    gauge2.setValue(Integer.valueOf(message.toString()));
                }

                else if (topic.contains("powerOut")){
                    power.setText(message.toString() + " W");
                    gauge3.setValue(Integer.valueOf(message.toString()));
                }

                else if (topic.contains("priceOut")){
                    cost.setText("Cost: RM " + message.toString());

                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }
}

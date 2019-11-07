package com.mahmoudmabrok.appclient;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String SERVER_IP = "192.16.137.2";
    private static final int SERVER_PORT = 7000;
    TextView textView;
    private PrintWriter output;
    private BufferedReader input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tvContented);
        new Thread(new ConnectThread()).start();

    }

    public void accept(View view) {
        new Thread(new Thread3("accept")).start();
    }

    public void reject(View view) {
        new Thread(new Thread3("reject")).start();
    }

    class ConnectThread implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
                Log.d(TAG, "run:connected  ");
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Connected\n");
                    }
                });

                new Thread(new Thread2()).start();


            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + e.getMessage());
            }
        }
    }

    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    Log.d(TAG, "Thread2: " + message);
                    if (message != null) {
                        Log.d(TAG, "input: " + message);

                        if (message.equals("reject")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,
                                            "Client:: get Reject from server",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        } else {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            startActivity(intent);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread3 implements Runnable {
        private String message;

        Thread3(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            output.write(message + '\n');
            output.flush();
        }
    }
}

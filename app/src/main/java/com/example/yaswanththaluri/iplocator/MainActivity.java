package com.example.yaswanththaluri.iplocator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    EditText ip;
    String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) findViewById(R.id.getLocation);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = (EditText) findViewById(R.id.ip);
                ipAddress = ip.getText().toString();
                check();
            }
        });
    }

    void check() {
        int count = 0;
        for (int i = 0; i < ipAddress.length(); i++) {
            if (ipAddress.charAt(i) == '.') {
                count = count + 1;
            }
        }

        if (count == 3) {
            Intent i = new Intent(MainActivity.this, Info.class);
            i.putExtra("IPADDRESS", ipAddress);
            startActivity(i);
        } else {
            Toast.makeText(this, "Invalid IP Address", Toast.LENGTH_SHORT).show();
        }
    }
}

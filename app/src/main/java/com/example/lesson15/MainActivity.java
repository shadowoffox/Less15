package com.example.lesson15;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private final int permissionRequestCode = 12345;
    EditText smsPN;
    EditText smsET;
    Button sendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(this,permissions,permissionRequestCode);
        }


        sendBtn.setOnClickListener(v -> {
            String number = smsPN.getText().toString();
            String text = smsET.getText().toString();
            sendMSG(number,text);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == permissionRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Спасибо!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Извините, апп без данного разрешения может работать неправильно",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews(){
        smsPN = findViewById(R.id.sms_edit_phone);
        smsET = findViewById(R.id.sms_edittext);
        sendBtn =findViewById(R.id.send_button);
    }

    private void sendMSG(String number, String text){
        SmsManager msg = SmsManager.getDefault();
        msg.sendTextMessage(number,null,text,null,null);
        Toast.makeText(getApplicationContext(),
                "SMS отправлено!",Toast.LENGTH_LONG).show();
    }
}

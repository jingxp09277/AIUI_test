package com.ask.myapplication;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ask.myapplication.mywebsocketclient.AIUIGO1;
import com.ask.myapplication.okhttpwebsocket.AIUIGO2;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    static String TAG = MainActivity.class.getSimpleName();


    int RC_PERMISSION = 1;

    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Heeeeeello",
                    RC_PERMISSION, perms);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        methodRequiresTwoPermission();

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            AIUIGO1.init();
        } catch (UnsupportedEncodingException | URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        //TODO AIUIGO2 没有调通
        /*AIUIGO2 aiuigo2 = AIUIGO2.getInstance();
        try {
            aiuigo2.onAIUIGO();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
    }


}

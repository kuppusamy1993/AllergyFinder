package com.kuppu.allergyfinder.authentication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.kuppu.allergyfinder.R;
import com.kuppu.allergyfinder.ScannerActivity;

public class ScreenLockActivity extends AppCompatActivity {
    private static final int LOCK_REQUEST=11;
    private static final int SECURITY_SETTING=13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_lock);
        authenticateApp();

    }
    private void authenticateApp(){
        KeyguardManager keyguardManager=(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        Intent i=keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock),getResources().getString(R.string.confirm_pattern));
        try {
            startActivityForResult(i,LOCK_REQUEST);
        }catch (Exception e){
            Intent intent=new Intent(Settings.ACTION_SECURITY_SETTINGS);
            try{
                startActivityForResult(intent,SECURITY_SETTING);
            }catch (Exception es){

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case LOCK_REQUEST:
                if(resultCode==RESULT_OK){
                    Intent intent=new Intent(getApplicationContext(), ScannerActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                }

                break;
            case SECURITY_SETTING:
                if (isDevicesecure()){
                    Toast.makeText(this, getResources().getString(R.string.device_is_secure), Toast.LENGTH_SHORT).show();

                }else {

                }
                break;
        }
    }


    private boolean isDevicesecure(){
        KeyguardManager keyguardManager=(KeyguardManager)getSystemService(KEYGUARD_SERVICE);

        return Build.VERSION.SDK_INT >=Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }


}

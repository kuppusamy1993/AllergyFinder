package com.kuppu.allergyfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kuppu.allergyfinder.authentication.LoginActivity;
import com.kuppu.allergyfinder.authentication.RegisterActivity;
import com.kuppu.allergyfinder.authentication.ScreenLockActivity;

public class MainActivity extends AppCompatActivity  {
private Button  login_btn,register_btn;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        // check if user is null
        if(firebaseUser!=null){
            Intent intent=new Intent(getApplicationContext(), ScreenLockActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn=findViewById(R.id.login);
        register_btn=findViewById(R.id.register);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }


}

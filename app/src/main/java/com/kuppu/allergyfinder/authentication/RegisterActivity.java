package com.kuppu.allergyfinder.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kuppu.allergyfinder.MainActivity;
import com.kuppu.allergyfinder.R;
import com.kuppu.allergyfinder.Viewmodel.MainViewModel;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username,email,password;
    private Button btn_register;
private MainViewModel mainViewModel;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username=(EditText)findViewById(R.id.username_edt);
        email=(EditText)findViewById(R.id.email_edt);
        password=(EditText)findViewById(R.id.password_edt);
        btn_register=(Button) findViewById(R.id.btn_register);

        mainViewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username=username.getText().toString();
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();

                if(TextUtils.isEmpty(txt_username)|| TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if(txt_password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password must be alteast 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    RegisterUser(txt_username,txt_email,txt_password);
                }

            }
        });
    }

    private void RegisterUser(final String username, String email, String password) {


        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Log.e("psr","success 1");
                            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                            assert firebaseUser !=null;
                            String id=firebaseUser.getUid();

                            databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(id);

                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",id);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            Log.e("psr","success 2");
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.e("psr","success3");
                                    if(task.isSuccessful()){
                                       Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                      startActivity(intent);

                                        Log.e("psr","success4");
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(getApplication().getApplicationContext(), "you are not register with this email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}

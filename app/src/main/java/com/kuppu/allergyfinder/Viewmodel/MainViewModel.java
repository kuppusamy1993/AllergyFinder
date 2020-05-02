package com.kuppu.allergyfinder.Viewmodel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kuppu.allergyfinder.MainActivity;
import com.kuppu.allergyfinder.authentication.RegisterActivity;

import java.util.HashMap;

public class MainViewModel extends AndroidViewModel {


    public MainViewModel(@NonNull Application application) {
        super(application);
    }


}

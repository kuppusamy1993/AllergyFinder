package com.kuppu.allergyfinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kuppu.allergyfinder.Viewmodel.AllergyViewModel;
import com.kuppu.allergyfinder.Viewmodel.EditAllergyViewModel;

import java.util.List;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements AllergyListAdapter.OnDeleteClickListener{

    private Button addnewitem_btn,add_btn;
    LinearLayout additemlayout;
    private EditText edt_addnew_item_text;
    RecyclerView recyclerView;

    private   String TAG=this.getClass().getSimpleName();
    private AllergyViewModel allergyViewModel;
    public static final int UPDATE_NOTE_ACTIVITY_REQUEST_CODE=2;
    private AllergyListAdapter allergyListAdapter;

    String note_idrandom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        addnewitem_btn=findViewById(R.id.additem);
        add_btn=findViewById(R.id.add_txt_item_btn);
        edt_addnew_item_text=findViewById(R.id.text_item_edt);
        additemlayout=findViewById(R.id.additem_layout);

        recyclerView=findViewById(R.id.recyclerview_itemlist);
        allergyListAdapter=new AllergyListAdapter(this,this);
        recyclerView.setAdapter(allergyListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allergyViewModel= ViewModelProviders.of(this).get(AllergyViewModel.class);



        addnewitem_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additemlayout.setVisibility(View.VISIBLE);
            }
        });

          note_idrandom= UUID.randomUUID().toString();
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String allergyitemname=edt_addnew_item_text.getText().toString();
                AllergyNote allergyNote=new AllergyNote(note_idrandom,allergyitemname);
                allergyViewModel.insert(allergyNote);
                Toast.makeText(ProfileActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                edt_addnew_item_text.setText("");
                note_idrandom="";
            }
        });



        allergyViewModel.getAllItem().observe(this, new Observer<List<AllergyNote>>() {
            @Override
            public void onChanged(List<AllergyNote> allergyNotes) {
                allergyListAdapter.setNotes(allergyNotes);
            }
        });



    }

    @Override
    public void OnDeleteClickListener(AllergyNote allergyNote) {
        allergyViewModel.delete(allergyNote);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_NOTE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // Code to update the note
            AllergyNote note = new AllergyNote(
                    data.getStringExtra(EditNoteItemActivity.NOTE_ID),
                    data.getStringExtra(EditNoteItemActivity.UPDATED_NOTE));
            allergyViewModel.update(note);

            Toast.makeText(
                    getApplicationContext(),
                    R.string.updated,
                    Toast.LENGTH_LONG).show();

        }
        else {
            Toast.makeText(getApplicationContext(),R.string.not_saved,Toast.LENGTH_LONG).show();
        }
    }
}

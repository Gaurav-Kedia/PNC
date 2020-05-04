package com.gaurav.pnc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference rootref;
    private String currentuserid;

    private EditText name;
    private Button updatebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();

        initializefields();
        retrieveuserinfo();

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = name.getText().toString().trim();
                rootref.child("Users").child(currentuserid).child("name").setValue(user_name);

            }
        });
    }

    private void retrieveuserinfo() {
    }

    private void initializefields() {
        name = findViewById(R.id.fullname);
        updatebutton = findViewById(R.id.update_button);
    }
}

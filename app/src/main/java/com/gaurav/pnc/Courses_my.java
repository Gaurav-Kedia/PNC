package com.gaurav.pnc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc.Adapters.Coursesmy_adpter;
import com.gaurav.pnc.Models.Course_list_model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Courses_my extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Course_list_model> courselist = new ArrayList<>();
    private Coursesmy_adpter adapter;

    private FirebaseAuth mAuth;
    private String currentuserid;
    private DatabaseReference course_list_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_my);
        getSupportActionBar().setTitle("My Courses");

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.courses_my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        course_list_ref = FirebaseDatabase.getInstance().getReference("Users").child(currentuserid).child("Course_access");
        course_list_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courselist = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    TextView txt = findViewById(R.id.no_one_course);
                    txt.setVisibility(View.INVISIBLE);
                }
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String name = snap.getKey();
                    Course_list_model crs = new Course_list_model();
                    crs.setCourse(name);
                    courselist.add(crs);
                }
                if (courselist.size() == 0) {
                    TextView txt = findViewById(R.id.no_one_course);
                    txt.setVisibility(View.VISIBLE);
                }
                adapter = new Coursesmy_adpter(Courses_my.this, courselist);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
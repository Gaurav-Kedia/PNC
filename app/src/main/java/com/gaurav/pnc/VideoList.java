package com.gaurav.pnc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class VideoList extends AppCompatActivity {

    private TextView fullTitle ;
    private String CourseName,subject,chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        CourseName = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");
        chapter = getIntent().getStringExtra("Chapter");
        fullTitle = findViewById(R.id.fullTitle);

        fullTitle.setText("Display the video list of "+CourseName+", "+subject+", "+chapter);

    }
}

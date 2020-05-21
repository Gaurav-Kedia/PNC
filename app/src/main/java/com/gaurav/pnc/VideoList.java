package com.gaurav.pnc;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.gaurav.pnc.Models.Chapter;
import com.gaurav.pnc.Models.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VideoList extends AppCompatActivity {

    private TextView fullTitle ;
    private String CourseName,subject,chapter;
    private Button play;
    private DatabaseReference rootref;
    private DatabaseReference vdoListref;
    private String chapterSl ;

    public FirebaseRecyclerAdapter adapter;
    private RecyclerView videoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        CourseName = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");
        chapter = getIntent().getStringExtra("Chapter");
        chapterSl = getIntent().getStringExtra("code");
        fullTitle = findViewById(R.id.fullTitle);

        videoList = findViewById(R.id.videoList);

        videoList.setLayoutManager(new LinearLayoutManager(this));

        rootref = FirebaseDatabase.getInstance().getReference();
        vdoListref = rootref.child("Cources").child(CourseName).child(subject).child("Chapters").child(chapterSl).child("video");

        fullTitle.setText("Display the video list of "+CourseName+", "+subject+", "+chapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        play = findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlayVideo.class);
                startActivity(i);
            }
        });

        loadVideos();

    }

    private void loadVideos() {
        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading...");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        Query query = vdoListref;

        FirebaseRecyclerOptions<Video> options =
                new FirebaseRecyclerOptions.Builder<Video>()
                        .setQuery(query, new SnapshotParser<Video>() {
                            @Override
                            public Video parseSnapshot(DataSnapshot snapshot) {
                                loadingBar.dismiss();
                                return new Video(snapshot.child("code").toString(),snapshot.child("name").toString(),Integer.parseInt(snapshot.getKey()));
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Video,MyVideoViewHolder>(options) {
            @NonNull
            @Override
            public MyVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_row, parent, false);
                return new MyVideoViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyVideoViewHolder myVideoViewHolder, int i, @NonNull Video video) {
                myVideoViewHolder.name.setText(video.getSlno() + "."+video.getName());
            }
        };
        videoList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

class MyVideoViewHolder extends RecyclerView.ViewHolder{

    TextView name;

    public MyVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.course_head);
    }



}

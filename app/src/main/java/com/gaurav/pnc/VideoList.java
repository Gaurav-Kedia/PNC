package com.gaurav.pnc;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.gaurav.pnc.Models.Video;
import com.gaurav.pnc.config.YouTubeConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class VideoList extends AppCompatActivity {


    private String CourseName,subject,chapter;
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setTitle("Videos");

        videoList = findViewById(R.id.videoList);
        videoList.setLayoutManager(new LinearLayoutManager(this));

        rootref = FirebaseDatabase.getInstance().getReference();
        vdoListref = rootref.child("Cources").child(CourseName).child(subject).child("Chapters").child(chapterSl).child("video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadVideos();
    }

    private void loadVideos() {
        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading...");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        vdoListref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query = vdoListref;

        FirebaseRecyclerOptions<Video> options =
                new FirebaseRecyclerOptions.Builder<Video>()
                        .setQuery(query, new SnapshotParser<Video>() {
                            @Override
                            public Video parseSnapshot(DataSnapshot snapshot) {
                                loadingBar.dismiss();
                                return new Video(snapshot.child("code").getValue().toString(),snapshot.child("name").getValue().toString(),Integer.parseInt(snapshot.getKey()));
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Video,MyVideoViewHolder>(options) {
            @NonNull
            @Override
            public MyVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_video_card, parent, false);
                return new MyVideoViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyVideoViewHolder myVideoViewHolder, int i, @NonNull final Video video) {
                myVideoViewHolder.name.setText(video.getSlno() + "."+video.getName());
                Log.d("Image Tag","https://img.youtube.com/vi/"+video.getCode()+"/mqdefault.jpg");
                Picasso.get().load("https://img.youtube.com/vi/"+video.getCode()+"/mqdefault.jpg")
                        .into(myVideoViewHolder.img);
                try {
                    myVideoViewHolder.time.setText(" "+getDuration(video.getCode()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myVideoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),PlayVideo.class);
                        i.putExtra("code",video.getCode());
                        startActivity(i);
                    }
                });
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

    public String getDuration(String code) throws IOException {

        final String USER_AGENT = "Mozilla/5.0";
        final String GET_URL = "https://www.googleapis.com/youtube/v3/videos?id="+code+"&part=contentDetails&key="+ new YouTubeConfig().getAPI_KEY();

        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());

            try {

                JSONObject o = new JSONObject(response.toString());
                JSONArray items = new JSONArray(o.get("items").toString());
                JSONObject contentDetails = (JSONObject) items.getJSONObject(0).get("contentDetails");

                Log.d("My App", contentDetails.get("duration").toString());
                String string2 = contentDetails.get("duration").toString();
                String t = string2.replaceAll("[PT]","");
                String t2 = t.replace("M"," : ").replace("H"," : ").replace("S","");
                return t2;
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + t.getMessage() + "\"");
            }
        } else {
                System.out.println("GET request not worked");
            }
        return "0";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class MyVideoViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    ImageView img;
    TextView time;

    public MyVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.course_head);
        img = itemView.findViewById(R.id.thumb);
        time = itemView.findViewById(R.id.time);
    }
}



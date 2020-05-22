package com.gaurav.pnc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
<<<<<<< HEAD
    YouTubePlayerView youTubePlayerView;
=======
    private DatabaseReference rootref;
    private DatabaseReference vdoListref;
    private String chapterSl ;

    public FirebaseRecyclerAdapter adapter;
    private RecyclerView videoList;

>>>>>>> 33ffc0c403c7e282df62cd812d6230dfc17dd1e4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        CourseName = getIntent().getStringExtra("cource");
        subject = getIntent().getStringExtra("sujectName");
        chapter = getIntent().getStringExtra("Chapter");
<<<<<<< HEAD
//        fullTitle = findViewById(R.id.fullTitle);

//        fullTitle.setText("Display the video list of "+CourseName+", "+subject+", "+chapter);
=======
        chapterSl = getIntent().getStringExtra("code");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setTitle("Videos");

        videoList = findViewById(R.id.videoList);

        videoList.setLayoutManager(new LinearLayoutManager(this));

        rootref = FirebaseDatabase.getInstance().getReference();
        vdoListref = rootref.child("Cources").child(CourseName).child(subject).child("Chapters").child(chapterSl).child("video");

>>>>>>> 33ffc0c403c7e282df62cd812d6230dfc17dd1e4

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

<<<<<<< HEAD
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "NHAIiAmxTAU";
                youTubePlayer.loadVideo(videoId, 1f);
                addFullScreenListenerToPlayer();

=======

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
>>>>>>> 33ffc0c403c7e282df62cd812d6230dfc17dd1e4
            }
        });

    }

    @Override
    public void onBackPressed() {
<<<<<<< HEAD
        if (youTubePlayerView.isFullScreen())
            youTubePlayerView.exitFullScreen();
        else
            super.onBackPressed();
=======
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
>>>>>>> 33ffc0c403c7e282df62cd812d6230dfc17dd1e4
    }
}

    private void addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

<<<<<<< HEAD
            }

            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }}
=======
>>>>>>> 33ffc0c403c7e282df62cd812d6230dfc17dd1e4

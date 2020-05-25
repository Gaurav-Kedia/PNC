package com.gaurav.pnc;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.gaurav.pnc.Models.Subject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Courses extends AppCompatActivity {

    private String cource ;
//    private TextView cname;
    public RecyclerView subjects;
    private DatabaseReference rootref;
    private DatabaseReference courseref;

    public FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
//        cname = findViewById(R.id.cname);
        subjects = findViewById(R.id.subjects);

        rootref = FirebaseDatabase.getInstance().getReference();
        cource = getIntent().getStringExtra("Course");
        courseref = rootref.child("Cources").child(cource);
//        cname.setText(cource);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        subjects.setLayoutManager(new GridLayoutManager(this, (int) (dpWidth/180)));

        getSupportActionBar().setTitle(cource);
        loadSubjects();
    }

//    public boolean hasAccess(){
//        final boolean[] bol = {false};
//
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if(firebaseUser.getUid() !=null ){
//            rootref.child("Users").child(firebaseUser.getUid()).child("Course_access").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child(cource).child("has_access").getValue().toString().equals("YES")){
//                        bol[0] = true;
//                        System.out.println("Bool : "+bol[0]);
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//        return bol[0];
//    }

    public void loadSubjects(){
        final ProgressDialog loadingBar;
        loadingBar = new ProgressDialog(this);
        loadingBar.setCancelable(false);
        loadingBar.setTitle("Loading....!");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        courseref.addValueEventListener(new ValueEventListener() {
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

        Query query = courseref;
        FirebaseRecyclerOptions<Subject> options =
                new FirebaseRecyclerOptions.Builder<Subject>()
                        .setQuery(query, new SnapshotParser<Subject>() {
                            @Override
                            public Subject parseSnapshot(DataSnapshot snapshot) {
                                Log.d("My Snap :",snapshot.toString());
                                loadingBar.dismiss();
                                return new Subject(snapshot.getKey(),snapshot.child("img").getValue().toString());
                            }

                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Subject, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_subject,parent,false);
                return new MyViewHolder(viewHolder);
            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final Subject model) {
                holder.subname.setText(model.getName());
                Picasso.get().load(model.getImg())
                        .fit()
                        .into(holder.img);

                holder.subject_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if(firebaseUser.getUid() !=null ){
                            rootref.child("Users").child(firebaseUser.getUid()).child("Course_access").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    System.out.println("Hello "+dataSnapshot.getValue().toString());
                                    if (dataSnapshot.child(cource).child("has_access").getValue().toString().equals("YES")){
                                        Intent i = new Intent(getApplicationContext(),SubjectPageActivity.class);
                                        i.putExtra("cource",cource);
                                        i.putExtra("sujectName",model.getName());
                                        startActivity(i);
                                    }else{
                                        Toast.makeText(getApplicationContext(),"You Have not access to this course ! Please Contact Your admin or Faculty",Toast.LENGTH_LONG).show();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });

            }
        };

        subjects.setAdapter(adapter);


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


}


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView subname;
        ImageView img;
        CardView subject_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subject_card = itemView.findViewById(R.id.subject_card);
            subname = itemView.findViewById(R.id.subname);
            img = itemView.findViewById(R.id.sub_img);
        }
    }


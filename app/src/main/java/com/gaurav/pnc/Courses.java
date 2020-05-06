package com.gaurav.pnc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Courses extends AppCompatActivity {

    private String cource ;
    private TextView cname;
    public RecyclerView subjects;
    private DatabaseReference rootref;
    private DatabaseReference courseref;

    private ArrayList<String> cources = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        cname = findViewById(R.id.cname);
        subjects = findViewById(R.id.subjects);

        rootref = FirebaseDatabase.getInstance().getReference();

        cource = getIntent().getStringExtra("Course");
        courseref = rootref.child("Cources").child(cource);

        cname.setText(cource);

        subjects.setLayoutManager(new GridLayoutManager(this,2));

        loadSubjects();
        subjects.setAdapter(new myadaptor(cources));
    }

    public void loadSubjects(){
//       TODO : a recycler adapter to be added

        courseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot t:dataSnapshot.getChildren()){
                    cources.add(t.getKey());
                    Log.d("My Data :",t.getKey());
                }
                subjects.setAdapter(new myadaptor(cources));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

class myadaptor extends RecyclerView.Adapter<myadaptor.myViewHolder>{

    ArrayList<String> courses;

    public myadaptor(ArrayList<String> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.each_subject,viewGroup,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i) {
            myViewHolder.subname.setText(courses.get(i));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView subname;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            subname = itemView.findViewById(R.id.subname);
        }
    }
}

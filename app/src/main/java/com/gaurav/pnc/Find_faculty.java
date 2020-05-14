package com.gaurav.pnc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gaurav.pnc.Models.User_info;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Find_faculty extends AppCompatActivity {
    private Toolbar mtoolbar;
    private RecyclerView FindFriendRecyclerList;
    private DatabaseReference UserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_faculty);
        getSupportActionBar().setTitle("Find Faculty");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendRecyclerList = findViewById(R.id.find_friend_recyclerlist);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User_info> options = new FirebaseRecyclerOptions.Builder<User_info>()
                .setQuery(UserRef, User_info.class)
                .build();

        FirebaseRecyclerAdapter<User_info, FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<User_info, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull User_info model) {
                        holder.username.setText(model.getName());
                        holder.userstatus.setText(model.getStatus());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visit_user_id = getRef(position).getKey();

                                Intent profileintent = new Intent(Find_faculty.this, ProfileActivity.class);
                                profileintent.putExtra("visit_user_id", visit_user_id);
                                startActivity(profileintent);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                        return viewHolder;
                    }
                };
        FindFriendRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        TextView username, userstatus;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_status);
        }
    }
}
package com.gaurav.pnc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc.Adapters.Course_list_adapter;
import com.gaurav.pnc.Models.Course_list_model;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.gaurav.pnc.login_activity.MyPREFERENCES;

public class Home_activity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ProgressBar pb;
    private FirebaseAuth mAuth;
    private DatabaseReference rootref;

    private View mHeader;
    private ActionBarDrawerToggle mtoggle;

    private List<Course_list_model> courselist = new ArrayList<>();
    private DatabaseReference course_list_ref;
    private Course_list_adapter adapter;
    private RecyclerView recycler;

    private String currentuserid;
    private TextView header_name, header_email;
    private String currentname, currentphone;

    private TextView hayname;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        initialise();

        mAuth = FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();

        recycler.setHasFixedSize(true);
//        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setLayoutManager(new GridLayoutManager(this,2));
        inflate_recycler_view();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home_option:
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.assignments_option:
                        return true;

                    case R.id.membership_option:
                        return true;

                    case R.id.forum_option:
                        SendUserToForumActivity();
                        return true;

                    case R.id.edit_profile_option:
                        SendUserToProfileActivity();
                        return true;

                    case R.id.nav_support:
                        return true;

                    case R.id.nav_aboutus:
                        return true;

                    case R.id.logout:
                        mAuth.signOut();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("islogin", "false");
                        editor.apply();
                        SendUserToLoginActivity();
                        finish();
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        /*FirebaseUser currentUser = mAuth.getCurrentUser();
        navigationView.getMenu().getItem(0).setChecked(true);
        drawerLayout.closeDrawers();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            verifyuserexistance();
        }*/
        String islogin = sharedPreferences.getString("islogin", "false");
        if (islogin.equalsIgnoreCase("true")) {
            verifyuserexistance();
        }
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mtoggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.logout_option:
                mAuth.signOut();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("islogin", "false");
                editor.apply();
                SendUserToLoginActivity();
                finish();
                return true;
            case R.id.profile_option:
                SendUserToProfileActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logout, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void initialise() {
        hayname = findViewById((R.id.hayname));
        recycler = findViewById(R.id.course_list_recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.getMenu().getItem(0).setChecked(true);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        pb = findViewById(R.id.ProgressBB);
        pb.setVisibility(View.VISIBLE);

        mHeader = navigationView.getHeaderView(0);
        header_name = mHeader.findViewById(R.id.header_user_name);
        header_email = mHeader.findViewById(R.id.header_user_email);

        mtoggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void verifyuserexistance() {
        currentuserid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        rootref.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("name").exists()) {
                    updateuserinfo();

                } else {
                    SendUserToProfileActivity();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToProfileActivity() {
        startActivity(new Intent(Home_activity.this, Self_Profile_Activity.class));
    }

    private void SendUserToLoginActivity() {
        startActivity(new Intent(Home_activity.this, login_activity.class));
        finish();
    }

    private void SendUserToForumActivity() {
        startActivity(new Intent(Home_activity.this, Forum_activity.class));
    }

    private void updateuserinfo() {
        String savecurrenttime, savecurrentdate;
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd, yyyy");
        savecurrentdate = currentdate.format(calender.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("hh:mm a");
        savecurrenttime = currenttime.format(calender.getTime());
        HashMap<String, Object> onlineStatemap = new HashMap<>();
        onlineStatemap.put("time", savecurrenttime);
        onlineStatemap.put("date", savecurrentdate);
        if (mAuth.getCurrentUser() != null) {
            currentuserid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            rootref.child("Users").child(currentuserid)
                    .updateChildren(onlineStatemap);
            rootref.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    currentname = dataSnapshot.child("name").getValue().toString();
                    currentphone = dataSnapshot.child("email").getValue().toString();
                    header_name.setText(currentname);
                    header_email.setText(currentphone);
                    hayname.setText("Hey! "+currentname.split(" ")[0]);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void inflate_recycler_view() {
        course_list_ref = FirebaseDatabase.getInstance().getReference("Cources");
        course_list_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courselist = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String name = snap.getKey();
                    Course_list_model crs = new Course_list_model();
                    crs.setCourse(name);
                    courselist.add(crs);
                }
                pb.setVisibility(View.INVISIBLE);
                TextView txt = findViewById(R.id.my_courses_home_activity);
                txt.setVisibility(View.VISIBLE);
                adapter = new Course_list_adapter(Home_activity.this, courselist);
                recycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

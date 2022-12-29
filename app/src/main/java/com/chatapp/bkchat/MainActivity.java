package com.chatapp.bkchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAccessorAdapter tabAccessorAdapter;
    FirebaseAuth auth;
    DatabaseReference rootRef;
    private String currentUserId;
    private long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        khoiTao();
        listener();
    }

    @SuppressLint("NonConstantResourceId")
    private void listener() {
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.logOut: {
                    updateUserState("offline");
                    auth.signOut();
                    sendUserToLogin();
                    return true;
                }
                case R.id.settings: {
                    sendUserToActivity(SettingActivity.class);
                    return true;
                }
                case R.id.FindFriends: {
                    sendUserToActivity(FindFriendsActivity.class);
                    return true;
                }
                case R.id.qrCode: {
                    sendUserToActivity(ScanQrActivity.class);
                    return true;
                }
                default:
                    return false;
            }
        });
    }

    private void khoiTao() {
        auth = FirebaseAuth.getInstance();
        //   currentUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolBar);
        toolbar.inflateMenu(R.menu.option_menu);


        tabLayout = findViewById(R.id.main_tab_layout);
        tabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager(), 0);

        viewPager = findViewById(R.id.main_tabs_pager);
        viewPager.setAdapter(tabAccessorAdapter);

        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_question_answer_white_18);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_people_white_18);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_group_add_white_18);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            sendUserToLogin();
        } else {
            updateUserState("online");
            verifyUser();
        }
    }


    @Override
    protected void onDestroy() {
        updateUserState("offline");
        super.onDestroy();
    }


    private void verifyUser() {

        rootRef.child("Users").child(currentUserId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.child("username").exists())) {
//                    Toast.makeText(MainActivity.this, "Welcome !", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void sendUserToLogin() {
        Intent login = new Intent(MainActivity.this, SignInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    private void sendUserToActivity(Class<?> toActivity) {
        Intent activity = new Intent(MainActivity.this, toActivity);
        startActivity(activity);
    }


    private void updateUserState(String state) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            String saveCurrentTime, saveCurrentDate;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-YYYY");
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
            saveCurrentDate = currentDate.format(calendar.getTime());
            saveCurrentTime = currentTime.format(calendar.getTime());

            HashMap<String, Object> onlineStateMap = new HashMap<>();
            onlineStateMap.put("time", saveCurrentTime);
            onlineStateMap.put("date", saveCurrentDate);
            onlineStateMap.put("state", state);


            rootRef.child("Users").child(currentUserId).child("userState")
                    .updateChildren(onlineStateMap);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

//    @Override
//    protected void onUserLeaveHint() {
//        updateUserState("offline");
//        super.onUserLeaveHint();
//    }


}
package com.chatapp.bkchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;

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

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAccessorAdapter tabAccessorAdapter;

    FirebaseUser currentUser;
    FirebaseAuth auth;
    DatabaseReference rootRef;

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
                    auth.signOut();
                    sendUserToLogin();
                    return true;
                }
                case R.id.settings:{
                    sendUserToSetting();
                    return true ;
                }
                case R.id.FindFriends:{
                    sendUserToFindFriends();
                    return true;

                }
                default:
                    return false;
            }
        });
    }

    private void khoiTao() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        rootRef= FirebaseDatabase.getInstance().getReference();

        toolbar = findViewById(R.id.toolBar);
        toolbar.inflateMenu(R.menu.option_menu);

        tabLayout = findViewById(R.id.main_tab_layout);
        tabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager(), 0);

        viewPager = findViewById(R.id.main_tabs_pager);
        viewPager.setAdapter(tabAccessorAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null) {
            sendUserToLogin();
        }
        else{
            //verifyUser();
        }
    }

//    private void verifyUser() {
//        String uid=auth.getCurrentUser().getUid();
//        rootRef.child(uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if()
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void sendUserToLogin() {
        Intent login = new Intent(MainActivity.this, SignInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }
    private void sendUserToSetting() {
        Intent set = new Intent(MainActivity.this, SettingActivity.class);
        //set.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(set);
        //finish();
    }
    private void sendUserToFindFriends() {
        Intent find = new Intent(MainActivity.this, FindFriendsActivity.class);
       // find.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(find);

    }

}
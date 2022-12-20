package com.chatapp.bkchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAccessorAdapter tabAccessorAdapter;

    FirebaseUser currentUser;
    FirebaseAuth auth;

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
        if (currentUser == null) {
            sendUserToLogin();
        }
    }

    private void sendUserToLogin() {
        Intent login = new Intent(MainActivity.this, SignInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }
    private void sendUserToSetting() {
        Intent set = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(set);
    }
    private void sendUserToFindFriends() {
        Intent find = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(find);
    }

}
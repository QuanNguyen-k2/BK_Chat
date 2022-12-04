package com.chatapp.bkchat;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    TabAccessorAdapter tabAccessorAdapter;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolBar);
        //setSupportActionBar(toolbar);

        viewPager=findViewById(R.id.main_tabs_pager);
        tabLayout=findViewById(R.id.main_tab_layout);
        tabAccessorAdapter= new TabAccessorAdapter(getSupportFragmentManager(),0);

        viewPager.setAdapter(tabAccessorAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null){
            SendUserToLogin();
        }
    }

    private void SendUserToLogin() {
        Intent login= new Intent(MainActivity.this,SignInActivity.class);
        startActivity(login);
    }
}
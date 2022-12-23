package com.chatapp.bkchat;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FindFriendsActivity extends AppCompatActivity {

    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private DatabaseReference usersRef;
    private Toolbar mToolbar;
    private EditText editText;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mToolbar = (Toolbar) findViewById(R.id.find_friends_layout);

        editText = findViewById(R.id.search_infor);
        editText.clearFocus();


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.find_friends_recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchFriend("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            searchFriend(editText.getText().toString());
            editText.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchFriend(String text) {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Contacts> listContact = new HashMap<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Contacts contacts = data.getValue(Contacts.class);
                    if (contacts.getUsername().toLowerCase().contains(text.toLowerCase()) && !data.getKey().toString().equals(uid)) {
                        listContact.put(data.getKey(), contacts);
                    }
                }
                userAdapter = new UserAdapter(listContact, FindFriendsActivity.this);
                recyclerView.setAdapter(userAdapter);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

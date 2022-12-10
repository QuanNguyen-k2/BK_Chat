package com.chatapp.bkchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference UsersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendsRecyclerList =(RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar=(Toolbar) findViewById(R.id.find_friends_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }


    @Override
    protected void onStart()
    {

        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(UsersRef,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Contacts model) {

                        holder.userName.setText(model.getUsername());
                        holder.userStatus.setText(model.getDescription());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.image_user).into(holder.profileImage);
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()) .inflate(R.layout.users_display_layout,parent,false);
                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };

        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();


    }
    public static  class FindFriendViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName, userStatus;
        ImageView profileImage;
        public FindFriendViewHolder(@NonNull View itemView)
        {


            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);

        }
    }
}


package com.chatapp.bkchat;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

//import com.chatapp.bkchat.ChatActivity;
import com.chatapp.bkchat.Contacts;
import com.chatapp.bkchat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;




/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View PrivateChatView;
    private RecyclerView chatsList;

    private DatabaseReference ChatListRequestsRef, UsersRef, ContactsRef, ChatsRef;
    private FirebaseAuth myAuth;
    private String currentUserID;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatView = inflater.inflate(R.layout.fragment_mes, container, false);

        myAuth = FirebaseAuth.getInstance();

        currentUserID = myAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatsList = (RecyclerView) PrivateChatView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return PrivateChatView;
    }

    @Override
    public void onStart() {
        super.onStart();

   //     FirebaseUser currentUser1 = myAuth.getCurrentUser();

//        if(currentUser1 != null) {
//            currentUserID = currentUser1.getUid();
//            ChatListRequestsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
//            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatsRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts contacts) {
                        final String list_user_id_chats = getRef(position).getKey();
                        final String[] retImage = {"default_image"};
                      //  UsersRef.child(list_user_id_chats)
                     //   final String[] imageUserChat = {"defauly"};

                        UsersRef.child(list_user_id_chats).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("image")) {
                                          retImage[0] = dataSnapshot.child("image").getValue().toString();

                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }


                                    final String retName = dataSnapshot.child("username").getValue().toString();
                                    final String retStatus = dataSnapshot.child("description").getValue().toString();

                                    holder.userName.setText(retName);
                                // set trang thai online/offline
                                    if(dataSnapshot.child("userState").hasChild("state")){
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if(state.equals("online"))
                                        {
                                            holder.userStatus.setText("Online");
                                        }
                                        else if(state.equals("offline"))
                                        {
                                            holder.userStatus.setText("Last seen: "+date+" "+time);
                                        }
                                    }
                                    else{
                                        holder.userStatus.setText("Offline");
                                    }
//
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", list_user_id_chats);
                                            chatIntent.putExtra("visit_user_name", retName);
                                            chatIntent.putExtra("visit_image", retImage[0]);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };
        chatsList.setAdapter(adapter);
        adapter.startListening();
            }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        RoundedImageView profileImage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}

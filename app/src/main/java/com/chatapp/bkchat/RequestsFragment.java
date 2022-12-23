package com.chatapp.bkchat;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;


public class RequestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private View RequestsFragmentView;
    private RecyclerView myRequestsList;

    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef;
    private FirebaseAuth myAuth;
    private String currentUserID;

    public RequestsFragment() {
        // Required empty public constructor
    }

    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);


        myAuth = FirebaseAuth.getInstance();
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        currentUserID = myAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        myRequestsList = (RecyclerView) RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequestsRef.child(currentUserID), Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {
                        final String uid = getRef(position).getKey();
                        holder.itemView.setOnClickListener(v -> {
                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                            profileIntent.putExtra("visit_user_id", uid);
                            getContext().startActivity(profileIntent);
                        });

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")) {
                                        UsersRef.child(uid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }
                                                final String requestUserName = dataSnapshot.child("username").getValue().toString();
                                                holder.userName.setText(requestUserName);
                                                holder.userStatus.setText("Want to add friend with you!");
                                                handleAcceptRequest(holder, uid);
                                                handleDeleteRequest(holder, uid);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else if (type.equals("sent")) {

                                        holder.AcceptButton.setText(R.string.cancel_request);
                                        holder.AcceptButton.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                                        holder.CancelButton.setVisibility(View.GONE);
                                        UsersRef.child(uid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                final String requestUserName = dataSnapshot.child("username").getValue().toString();
                                                if (dataSnapshot.hasChild("image")) {
                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }
                                                handleHolderSendRequest(holder, requestUserName, uid);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new RequestsViewHolder(view);
                    }
                };
        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void handleHolderSendRequest(RequestsViewHolder holder, String requestUserName, String uid) {
        holder.userName.setText(requestUserName);
        holder.userStatus.setText("You have send request to " + requestUserName);
        holder.AcceptButton.setOnClickListener(v -> {
            {
                ChatRequestsRef.child(currentUserID).child(uid).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ChatRequestsRef.child(uid).child(currentUserID).removeValue().addOnCompleteListener(task13 -> {
                            if (task13.isSuccessful()) {
                                Toast.makeText(getContext(), "Cancelled request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

    }

    private void handleAcceptRequest(RequestsViewHolder holder, String uid) {
        holder.AcceptButton.setOnClickListener(v -> {
            ContactsRef.child(currentUserID).child(uid).child("Contacts").setValue("Saved").addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ContactsRef.child(uid).child(currentUserID).child("Contacts").setValue("Saved").addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            ChatRequestsRef.child(currentUserID).child(uid).removeValue().addOnCompleteListener(task11 -> {
                                if (task11.isSuccessful()) {
                                    ChatRequestsRef.child(uid).child(currentUserID).removeValue().addOnCompleteListener(task111 -> {
                                        if (task111.isSuccessful()) {
                                            Toast.makeText(getContext(), "New Contacts Saved", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        });
    }

    private void handleDeleteRequest(RequestsViewHolder holder, String uid) {
        holder.CancelButton.setOnClickListener(v -> {
            ChatRequestsRef.child(currentUserID).child(uid).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ChatRequestsRef.child(uid).child(currentUserID).removeValue().addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            Toast.makeText(getContext(), "Contacts is Denied", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        RoundedImageView profileImage;
        private LinearLayout acceptLayout;
        Button AcceptButton, CancelButton;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
            acceptLayout = itemView.findViewById(R.id.hide_layout_accept);
            acceptLayout.setVisibility(View.VISIBLE);
        }
    }
}

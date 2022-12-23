package com.chatapp.bkchat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID, senderUserID, Current_State;
    private com.makeramen.roundedimageview.RoundedImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton, DeclineMessageRequestButton;
    private ImageView imageCover;

    private DatabaseReference UserRef, ChatRequestRef, ContactsRef, NotificationRef;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar mToolbar;

        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();


        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName = findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = findViewById(R.id.decline_message_request_button);
        Current_State = "new";
        imageCover = findViewById(R.id.image_cover);
        mToolbar = findViewById(R.id.toolbar_setting);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        RetrieveUserInfo();
    }


    private void RetrieveUserInfo() {
        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("username").getValue().toString();
                    String userStatus = snapshot.child("description").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    if (snapshot.hasChild("image")) {
                        String retrieveImage = snapshot.child("image").getValue().toString().trim();
                        Picasso.get().load(retrieveImage).into(userProfileImage);
                    }
                    if (snapshot.hasChild("coverImage")) {
                        String retrieveImage = snapshot.child("coverImage").getValue().toString().trim();
                        Picasso.get().load(retrieveImage).into(imageCover);
                    }
                    ManageChatRequests();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ManageChatRequests() {
        ChatRequestRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(receiverUserID)) {
                    String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        Current_State = "request_sent";
                        SendMessageRequestButton.setText("Cancel Request");
                    } else if (request_type.equals("received")) {
                        Current_State = "request_received";
                        SendMessageRequestButton.setText(R.string.accept);

                        DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                        DeclineMessageRequestButton.setEnabled(true);

                        DeclineMessageRequestButton.setOnClickListener(v -> CancelChatRequest());
                    }
                } else {
                    ContactsRef.child(senderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(receiverUserID)) {
                                Current_State = "friends";
                                SendMessageRequestButton.setText("Remove Contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (!senderUserID.equals(receiverUserID)) {
            SendMessageRequestButton.setOnClickListener(v -> {
                SendMessageRequestButton.setEnabled(false);
                if (Current_State.equals("new")) {
                    SendChatRequest();
                }
                if (Current_State.equals("request_sent")) {
                    CancelChatRequest();
                }
                if (Current_State.equals("request_received")) {
                    AcceptChatRequest();
                }
                if (Current_State.equals("friends")) {
                    RemoveSpecificContact();
                }
            });
        } else {
            SendMessageRequestButton.setVisibility(View.GONE);
        }
    }

    private void RemoveSpecificContact() {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ContactsRef.child(receiverUserID).child(senderUserID)
                                .removeValue()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        SendMessageRequestButton.setEnabled(true);
                                        Current_State = "new";
                                        SendMessageRequestButton.setText("Add Friend");
                                        DeclineMessageRequestButton.setVisibility(View.GONE);
                                        DeclineMessageRequestButton.setEnabled(false);
                                    }

                                });
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ContactsRef.child(receiverUserID).child(senderUserID)
                                .child("Contacts").setValue("Saved")
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task1) {
                                                        if (task1.isSuccessful()) {
                                                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(task11 -> {
                                                                        SendMessageRequestButton.setEnabled(true);
                                                                        Current_State = "friends";
                                                                        SendMessageRequestButton.setText("Remove Contact");
                                                                        DeclineMessageRequestButton.setVisibility(View.GONE);
                                                                        DeclineMessageRequestButton.setEnabled(false);
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }


    private void CancelChatRequest() {
        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ChatRequestRef.child(receiverUserID).child(senderUserID)
                                .removeValue()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        SendMessageRequestButton.setEnabled(true);
                                        Current_State = "new";
                                        SendMessageRequestButton.setText("Add Friend");

                                        DeclineMessageRequestButton.setVisibility(View.GONE);
                                        DeclineMessageRequestButton.setEnabled(false);
                                    }

                                });
                    }
                });
    }

    private void SendChatRequest() {
        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(task -> ChatRequestRef.child(receiverUserID).child(senderUserID)
                        .child("request_type").setValue("received")
                        .addOnCompleteListener(task1 -> {

                            HashMap<String, String> chatNotificationMap = new HashMap<>();
                            chatNotificationMap.put("from", senderUserID);
                            chatNotificationMap.put("type", "request");
                            NotificationRef.child(receiverUserID).push()
                                    .setValue(chatNotificationMap)
                                    .addOnCompleteListener(task11 -> {
                                        if (task11.isSuccessful()) {
                                            SendMessageRequestButton.setEnabled(true);
                                            Current_State = "request_sent";
                                            SendMessageRequestButton.setText("Cancel Request");
                                        }
                                    });

                        }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
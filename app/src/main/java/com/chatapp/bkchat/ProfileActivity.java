package com.chatapp.bkchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity
{

    private  String receiverUserID,sendertUserID, Current_State;


    private com.makeramen.roundedimageview.RoundedImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton;

    private DatabaseReference UserRef, ChatRequestRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");


        receiverUserID= getIntent().getExtras().get("visit_user_id").toString();
        sendertUserID = mAuth.getCurrentUser().getUid();


        userProfileImage =(com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);
        Current_State = "new";


        RetrieveUserInfo();


    }



    private void RetrieveUserInfo()
    {
        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if((snapshot.exists()) && (snapshot.hasChild("image")))
                {
                    String userImage = snapshot.child("image").getValue().toString();
                    String userName = snapshot.child("username").getValue().toString();
                    String userstatus = snapshot.child("description").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);

                    ManageChatRequests();


                }
                else
                {

                    String userName = snapshot.child("username").getValue().toString();
                    String userstatus = snapshot.child("description").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);
                    ManageChatRequests();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }



    private void ManageChatRequests()
    {
        ChatRequestRef.child(sendertUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.hasChild(receiverUserID))
                        {
                            String request_type =  snapshot.child(receiverUserID).child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {
                                Current_State="request_sent";
                                SendMessageRequestButton.setText("Cancel Chat Request");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if(!sendertUserID.equals(receiverUserID))
        {
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessageRequestButton.setEnabled(false);

                    if(Current_State.equals("new"))
                    {
                        SendChatRequest();
                    }
                }
            });
        }
        else
        {
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void SendChatRequest()
    {
        ChatRequestRef.child(sendertUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        ChatRequestRef.child(receiverUserID).child(sendertUserID)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        SendMessageRequestButton.setEnabled(true);
                                        Current_State= "request_sent";
                                        SendMessageRequestButton.setText("Cancel Chat Request");
                                    }
                                });

                    }
                });
    }
}
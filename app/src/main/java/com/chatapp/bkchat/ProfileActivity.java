package com.chatapp.bkchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity
{

    private  String receiverUserID;


    private com.makeramen.roundedimageview.RoundedImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton;

    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");


        receiverUserID= getIntent().getExtras().get("visit_user_id").toString();


        userProfileImage =(com.makeramen.roundedimageview.RoundedImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView) findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView) findViewById(R.id.visit_profile_status);
        SendMessageRequestButton = (Button) findViewById(R.id.send_message_request_button);


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

                }
                else
                {

                    String userName = snapshot.child("username").getValue().toString();
                    String userstatus = snapshot.child("description").getValue().toString();
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userstatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}
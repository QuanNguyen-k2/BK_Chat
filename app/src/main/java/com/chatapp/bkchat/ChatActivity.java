package com.chatapp.bkchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity
{
    private  String messageReceiverID, messageReceiverName,messageReceiverImage,messengerSenderId;
    private TextView userName, userLastSeen;
    private RoundedImageView userImage;
    private Toolbar ChatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton;
    private EditText MessageInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth= FirebaseAuth.getInstance();
        messengerSenderId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();



        IntializeControllers();

        userName.setText(messageReceiverName);
        // userLastSeen.setText(messagernReceiverId);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);



        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessenger();
            }
        });





    }

    private void IntializeControllers()
    {

        ChatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userImage = (RoundedImageView) findViewById(R.id.custom_profile_IMAGE);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);


        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);





    }
    //gởi tin nhắn kiểu text
    private void SendMessenger(){
        String messengerText = MessageInputText.getText().toString();

        if(TextUtils.isEmpty(messengerText)){
            Toast.makeText(this, "Text your message..!", Toast.LENGTH_SHORT).show();
        }
        else {
            String messengerSenderRef = "Messages/" + messengerSenderId +"/" + messageReceiverID;
            String messengerReceiverRef = "Messages/" + messageReceiverID +"/" + messengerSenderId;

            DatabaseReference userMessengerKeyRef =RootRef.child("Messages")
                    .child(messengerSenderId).child(messengerSenderId).push();

            String messagePushId = userMessengerKeyRef.getKey();

            Map messengerTextBody = new HashMap();
            messengerTextBody.put("message", messengerText);
            messengerTextBody.put("type", "text");
            messengerTextBody.put("from", messengerSenderId);
            messengerTextBody.put("to", messageReceiverID);
            messengerTextBody.put("messageID", messagePushId);
            messengerTextBody.put("name", "text");

            Map messengerBodyDetails = new HashMap();
            messengerBodyDetails.put(messengerSenderRef + "/" + messagePushId, messengerTextBody);
            messengerBodyDetails.put(messengerReceiverRef + "/" + messagePushId, messengerTextBody);

            RootRef.updateChildren(messengerBodyDetails).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Your message has been sent!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });

        }
    }



}
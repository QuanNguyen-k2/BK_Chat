package com.chatapp.bkchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity
{
    private  String messageReceiverID, messageReceiverName,messageReceiverImage,messengerSenderId;
    private TextView userName, userLastSeen;
    private RoundedImageView userImage;
    private Toolbar ChatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton,SendFileButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker ="",myUrl="";
    private Uri fileUri;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;

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



        DisplayLastSeen();

        SendFileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CharSequence options[]= new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "Ms Word Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the file");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which==0)
                        {

                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Image !"), 439);
                        }
                        if(which==1)
                        {
                            checker = "pdf";


                        }
                        if(which==2)
                        {
                            checker = "docx";

                        }

                    }
                });

                builder.show();
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
        SendFileButton = (ImageButton) findViewById(R.id.send_files_btn);

        MessageInputText = (EditText) findViewById(R.id.input_message);


        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);

        loadingBar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentDate = currentDate.format(calendar.getTime());
        saveCurrentTime = currentTime.format(calendar.getTime());


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode ==439 && resultCode==RESULT_OK && data !=null && data.getData() !=null)
        {

            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileUri = data.getData();

            if(!checker.equals("image"))
            {

            }
            else if(checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                String messengerSenderRef = "Messages/" + messengerSenderId +"/" + messageReceiverID;
                String messengerReceiverRef = "Messages/" + messageReceiverID +"/" + messengerSenderId;

                DatabaseReference userMessengerKeyRef =RootRef.child("Messages")
                        .child(messengerSenderId).child(messengerSenderId).push();

                String messagePushId = userMessengerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushId + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw  task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>()
                {

                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {

                        if(task.isSuccessful())
                        {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Map messengerTextBody = new HashMap();
                            messengerTextBody.put("message", myUrl);
                            messengerTextBody.put("type", checker);
                            messengerTextBody.put("from", messengerSenderId);
                            messengerTextBody.put("to", messageReceiverID);
                            messengerTextBody.put("messageID", messagePushId);
                            messengerTextBody.put("name", fileUri.getLastPathSegment());
                            messengerTextBody.put("date",saveCurrentDate);
                            messengerTextBody.put("time", saveCurrentTime);

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
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Your message has been sent!", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                    MessageInputText.setText("");
                                }
                            });

                        }
                    }
                });
            }
            else
            {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected, Error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //hàm set online offline lastseen
    private void DisplayLastSeen(){
        RootRef.child("Users").child(messengerSenderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("userState").hasChild("state")){
                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                    String date = dataSnapshot.child("userState").child("date").getValue().toString();
                    String time = dataSnapshot.child("userState").child("time").getValue().toString();

                    if(state.equals("online"))
                    {
                        userLastSeen.setText("Online");
                    }
                    else if(state.equals("offline"))
                    {
                        userLastSeen.setText("Last seen: "+date+" "+time);
                    }
                }
                else{
                    userLastSeen.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messengerSenderId).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        Messages messages = snapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messagesAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
            messengerTextBody.put("date",saveCurrentDate);
            messengerTextBody.put("time", saveCurrentTime);

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


}
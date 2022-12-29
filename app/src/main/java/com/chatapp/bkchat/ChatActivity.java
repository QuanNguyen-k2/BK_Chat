package com.chatapp.bkchat;

import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messengerSenderId;
    private TextView userName, userLastSeen;
    private RoundedImageView userImage;
    private Toolbar ChatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton, SendFileButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl = "";
    private Uri fileUri;
    //private StorageTask uploadTask;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        messengerSenderId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();

        InitializeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);


        SendMessageButton.setOnClickListener(view -> SendMessenger());

        DisplayLastSeen();

        SendFileButton.setOnClickListener(v -> {
            CharSequence[] options = new CharSequence[]
                    {
                            "Images",
                            "PDF Files",
                            "Ms Word Files"
                    };
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setTitle("Select the file");
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    checker = "image";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image !"), 439);
                }
                if (which == 1) {
                    checker = "pdf";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent.createChooser(intent, "Select PDF File!"), 439);
                }
                if (which == 2) {
                    checker = "doc";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/msword");
                    startActivityForResult(intent.createChooser(intent, "Select Word File!"), 439);

                }

            });

            builder.show();
        });


    }

    private void InitializeControllers() {

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
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-YYYY");
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentDate = currentDate.format(calendar.getTime());
        saveCurrentTime = currentTime.format(calendar.getTime());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 439 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileUri = data.getData();

            if (!checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                String messengerSenderRef = "Messages/" + messengerSenderId + "/" + messageReceiverID;
                String messengerReceiverRef = "Messages/" + messageReceiverID + "/" + messengerSenderId;

                DatabaseReference userMessengerKeyRef = RootRef.child("Messages")
                        .child(messengerSenderId).child(messengerSenderId).push();

                String messagePushId = userMessengerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushId + "." + checker);
                filePath.putFile(fileUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                            Map messengerTextBody = new HashMap();
                            myUrl = uri.toString().trim();
                            messengerTextBody.put("message", myUrl);
                            messengerTextBody.put("type", checker);
                            messengerTextBody.put("from", messengerSenderId);
                            messengerTextBody.put("to", messageReceiverID);
                            messengerTextBody.put("messageID", messagePushId);
                            messengerTextBody.put("name", fileUri.getLastPathSegment());
                            messengerTextBody.put("date", saveCurrentDate);
                            messengerTextBody.put("time", saveCurrentTime);

                            Map messengerBodyDetails = new HashMap();
                            messengerBodyDetails.put(messengerSenderRef + "/" + messagePushId, messengerTextBody);
                            messengerBodyDetails.put(messengerReceiverRef + "/" + messagePushId, messengerTextBody);

                            RootRef.updateChildren(messengerBodyDetails);
                            loadingBar.dismiss();
                        });

                    }

                }).addOnFailureListener(e -> {
                    loadingBar.dismiss();
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnProgressListener(snapshot -> {
                    double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    loadingBar.setMessage((int) p + " % Uploading...");
                });

            } else if (checker.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                String messengerSenderRef = "Messages/" + messengerSenderId + "/" + messageReceiverID;
                String messengerReceiverRef = "Messages/" + messageReceiverID + "/" + messengerSenderId;

                DatabaseReference userMessengerKeyRef = RootRef.child("Messages")
                        .child(messengerSenderId).child(messengerSenderId).push();

                String messagePushId = userMessengerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushId + "." + "jpg");

                filePath.putFile(fileUri).addOnCompleteListener(task -> {
//                uploadTask = filePath.putFile(fileUri);
//                uploadTask.continueWithTask(task -> {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//                    return filePath.getDownloadUrl();
//                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnSuccessListener(uri -> {
//                        Uri downloadUrl = task.getResult();
                            myUrl = uri.toString().trim();

                            Map messengerTextBody = new HashMap();
                            messengerTextBody.put("message", myUrl);
                            messengerTextBody.put("type", checker);
                            messengerTextBody.put("from", messengerSenderId);
                            messengerTextBody.put("to", messageReceiverID);
                            messengerTextBody.put("messageID", messagePushId);
                            messengerTextBody.put("name", fileUri.getLastPathSegment());
                            messengerTextBody.put("date", saveCurrentDate);
                            messengerTextBody.put("time", saveCurrentTime);

                            Map messengerBodyDetails = new HashMap();
                            messengerBodyDetails.put(messengerSenderRef + "/" + messagePushId, messengerTextBody);
                            messengerBodyDetails.put(messengerReceiverRef + "/" + messagePushId, messengerTextBody);

                            RootRef.updateChildren(messengerBodyDetails).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Toast.makeText(ChatActivity.this, "Your message has been sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                                MessageInputText.setText("");
                            });

                        });
                    }
                });
            } else {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected, Error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //hàm set online offline lastseen
    private void DisplayLastSeen() {
        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("userState").hasChild("state")) {
                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                    String date = dataSnapshot.child("userState").child("date").getValue().toString();
                    String time = dataSnapshot.child("userState").child("time").getValue().toString();

                    if (state.equals("online")) {
                        userLastSeen.setText("Online");
                    } else if (state.equals("offline")) {
                        userLastSeen.setText("Last seen: " + date + " " + time);
                    }
                } else {
                    userLastSeen.setText("Offline");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(messengerSenderId).child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Messages messages = data.getValue(Messages.class);
                    messagesList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //gởi tin nhắn kiểu text
    private void SendMessenger() {
        String messengerText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messengerText)) {
            Toast.makeText(this, "Text your message..!", Toast.LENGTH_SHORT).show();
        } else {
            String messengerSenderRef = "Messages/" + messengerSenderId + "/" + messageReceiverID;
            String messengerReceiverRef = "Messages/" + messageReceiverID + "/" + messengerSenderId;

            DatabaseReference userMessengerKeyRef = RootRef.child("Messages")
                    .child(messengerSenderId).child(messengerSenderId).push();

            String messagePushId = userMessengerKeyRef.getKey();

            Map messengerTextBody = new HashMap();
            messengerTextBody.put("message", messengerText);
            messengerTextBody.put("type", "text");
            messengerTextBody.put("from", messengerSenderId);
            messengerTextBody.put("to", messageReceiverID);
            messengerTextBody.put("messageID", messagePushId);
            messengerTextBody.put("name", "text");
            messengerTextBody.put("date", saveCurrentDate);
            messengerTextBody.put("time", saveCurrentTime);

            Map messengerBodyDetails = new HashMap();
            messengerBodyDetails.put(messengerSenderRef + "/" + messagePushId, messengerTextBody);
            messengerBodyDetails.put(messengerReceiverRef + "/" + messagePushId, messengerTextBody);

            RootRef.updateChildren(messengerBodyDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "Your message has been sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                MessageInputText.setText("");
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
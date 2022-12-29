package com.chatapp.bkchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth myAuth;
    private DatabaseReference userReference;

    public MessagesAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        public TextView receiverMessengerText, senderMessengerText;
        public RoundedImageView receiverProfileImg;
        public ImageView receiverMessengerImage, senderMessengerImage;
        public ImageView receiverMessengerFile, senderMessengerFile;


        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessengerText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            senderMessengerText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverProfileImg = (RoundedImageView) itemView.findViewById(R.id.message_profile_image);
            receiverMessengerImage = (ImageView) itemView.findViewById(R.id.message_receiver_image_view);
            senderMessengerImage = (ImageView) itemView.findViewById(R.id.message_sender_image_view);
            receiverMessengerFile = (ImageView) itemView.findViewById(R.id.message_receiver_file_view);
            senderMessengerFile = (ImageView) itemView.findViewById(R.id.message_sender_file_view);
        }
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messenger_layout, viewGroup, false);

        myAuth = FirebaseAuth.getInstance();

        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder messagesViewHolder, @SuppressLint("RecyclerView") final int position) {
        String messageSenderId = myAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")) {
                    String receiverImg = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImg).placeholder(R.drawable.image_user).into(messagesViewHolder.receiverProfileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messagesViewHolder.receiverMessengerText.setVisibility(View.GONE);
        messagesViewHolder.receiverProfileImg.setVisibility(View.GONE);
        messagesViewHolder.senderMessengerText.setVisibility(View.GONE);
        messagesViewHolder.senderMessengerImage.setVisibility(View.GONE);
        messagesViewHolder.receiverMessengerImage.setVisibility(View.GONE);
        messagesViewHolder.senderMessengerFile.setVisibility(View.GONE);
        messagesViewHolder.receiverMessengerFile.setVisibility(View.GONE);


        switch (fromMessageType) {
            case "text":

                //nguoi nhan ben trai
                if (fromUserId.equals(messageSenderId)) {
                    messagesViewHolder.senderMessengerText.setVisibility(View.VISIBLE);
                    messagesViewHolder.senderMessengerText.setBackgroundResource(R.drawable.sender_messenger_layout);
                    messagesViewHolder.senderMessengerText.setTextColor(Color.BLACK);
                    messagesViewHolder.senderMessengerText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                }
                //nguoi goi ben phai
                else {
                    messagesViewHolder.receiverProfileImg.setVisibility(View.VISIBLE);
                    messagesViewHolder.receiverMessengerText.setVisibility(View.VISIBLE);
                    messagesViewHolder.receiverMessengerText.setBackgroundResource(R.drawable.receiver_messenger_layout);
                    messagesViewHolder.receiverMessengerText.setTextColor(Color.BLACK);
                    messagesViewHolder.receiverMessengerText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                }
                break;
            case "image":
                if (fromUserId.equals(messageSenderId)) {
                    messagesViewHolder.senderMessengerImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messagesViewHolder.senderMessengerImage);
                    messagesViewHolder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messagesViewHolder.itemView.getContext().startActivity(intent);
                    });
                } else {
                    messagesViewHolder.receiverProfileImg.setVisibility(View.VISIBLE);
                    messagesViewHolder.receiverMessengerImage.setVisibility(View.VISIBLE);

                    Picasso.get().load(messages.getMessage()).into(messagesViewHolder.receiverMessengerImage);
                    messagesViewHolder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        /// loi cho nay
                        messagesViewHolder.itemView.getContext().startActivity(intent);
                    });
                }
                break;
            //nếu là file pdf, docx
            case "pdf":
                if (fromUserId.equals(messageSenderId)) {
                    messagesViewHolder.senderMessengerFile.setVisibility(View.VISIBLE);
                    messagesViewHolder.senderMessengerFile.setBackgroundResource(R.drawable.filepdf);
                } else {
                    messagesViewHolder.receiverProfileImg.setVisibility(View.VISIBLE);
                    messagesViewHolder.receiverMessengerFile.setVisibility(View.VISIBLE);

                    messagesViewHolder.receiverMessengerFile.setBackgroundResource(R.drawable.filepdf);

                }
                break;
            case "doc":
                if (fromUserId.equals(messageSenderId)) {
                    messagesViewHolder.senderMessengerFile.setVisibility(View.VISIBLE);
                    messagesViewHolder.senderMessengerFile.setBackgroundResource(R.drawable.filedoc);
                    messagesViewHolder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        /// loi cho nay
                        messagesViewHolder.itemView.getContext().startActivity(intent);
                    });
                } else {
                    messagesViewHolder.receiverProfileImg.setVisibility(View.VISIBLE);
                    messagesViewHolder.receiverMessengerFile.setVisibility(View.VISIBLE);

                    messagesViewHolder.receiverMessengerFile.setBackgroundResource(R.drawable.filedoc);

                    messagesViewHolder.itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messagesViewHolder.itemView.getContext().startActivity(intent);
                    });
                }
                break;
        }
        if (fromUserId.equals(messageSenderId)) {
            messagesViewHolder.itemView.setOnLongClickListener(v -> {
                switch (userMessagesList.get(position).getType()) {
                    case "pdf":
                    case "doc": {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download",
                                        "Cancel",
                                        "Delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messagesViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                deleteSentMessage(position, messagesViewHolder);
                            } else if (which == 1) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                messagesViewHolder.itemView.getContext().startActivity(intent);
                            } else if (which == 3) {
                                deleteMessageForEveryOne(position, messagesViewHolder);
                            }
                        });
                        builder.show();
                        break;
                    }
                    case "text": {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel",
                                        "Delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messagesViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                deleteSentMessage(position, messagesViewHolder);
                            } else if (which == 2) {
                                deleteMessageForEveryOne(position, messagesViewHolder);
                            }
                        });
                        builder.show();
                        break;
                    }
                    case "image": {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "View this image",
                                        "Cancel",
                                        "Delete for everyone"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messagesViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                deleteSentMessage(position, messagesViewHolder);
                            } else if (which == 1) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                messagesViewHolder.itemView.getContext().startActivity(intent);
                            } else if (which == 3) {
                                deleteMessageForEveryOne(position, messagesViewHolder);
                            }
                        });
                        builder.show();
                        break;
                    }
                }
                return false;
            });

        } else {
            messagesViewHolder.itemView.setOnLongClickListener( v -> {
                switch (userMessagesList.get(position).getType()) {
                    case "pdf":
                    case "doc": {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Download",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messagesViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                deleteReceiveMessage(position, messagesViewHolder);

                            } else if (which == 1) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                messagesViewHolder.itemView.getContext().startActivity(intent);
                            }

                        });
                        builder.show();
                        break;
                    }
                    case "text": {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messagesViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                deleteReceiveMessage(position, messagesViewHolder);
                            }
                        });
                        builder.show();
                        break;
                    }
                    case "image": {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete for me",
                                        "View this image",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messagesViewHolder.itemView.getContext());
                        builder.setTitle("Delete message?");
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                deleteReceiveMessage(position, messagesViewHolder);
                            } else if (which == 1) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                messagesViewHolder.itemView.getContext().startActivity(intent);
                            }
                        });
                        builder.show();
                        break;
                    }
                }
                return false;
            });

        }

    }

    @Override
    public int getItemCount() {

        return userMessagesList.size();
    }

    public void deleteSentMessage(final int position, final MessagesViewHolder holder) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            userMessagesList.remove(position);
//                            notifyDataSetChanged();
                        Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void deleteReceiveMessage(final int position, final MessagesViewHolder holder) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            userMessagesList.remove(position);
//                            notifyDataSetChanged();
                        Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }

                });
    }


    public void deleteMessageForEveryOne(final int position, final MessagesViewHolder holder) {
        String to = userMessagesList.get(position).getTo();
        String from = userMessagesList.get(position).getFrom();
        String mid = userMessagesList.get(position).getMessageID();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(to)
                .child(from)
                .child(mid)
                .removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rootRef.child("Messages")
                                .child(from)
                                .child(to)
                                .child(mid)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                                userMessagesList.remove(position);
//                                                notifyDataSetChanged();
                                            Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

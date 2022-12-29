package com.chatapp.bkchat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    private Map<String, Contacts> listContacts;
    private Context context;
    private List<String> listMyFriends;

    public UserAdapter(Map<String, Contacts> listContacts, Context context) {
        this.listContacts = listContacts;
        this.context = context;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if (listContacts != null) {
            String uid = (String) listContacts.keySet().toArray()[position];
            Contacts contact = listContacts.get(uid);
            holder.userName.setText(contact.getUsername());
            holder.userStatus.setText(contact.getDescription());
            Picasso.get().load(contact.getImage()).placeholder(R.drawable.image_user).into(holder.profileImage);

            holder.itemView.setOnClickListener(v -> {
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.putExtra("visit_user_id", uid);
                context.startActivity(profileIntent);
            });
            return;
        }

        if (listMyFriends != null) {
            String uid = listMyFriends.get(position);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Contacts contact = snapshot.getValue(Contacts.class);
                    holder.userName.setText(contact.getUsername());
                    holder.userStatus.setText(contact.getDescription());
                    Picasso.get().load(contact.getImage()).placeholder(R.drawable.image_user).into(holder.profileImage);

                    holder.itemView.setOnClickListener(v -> {
                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", uid);
                        context.startActivity(profileIntent);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (listContacts != null) {
            return listContacts.size();
        }
        if (listMyFriends != null) {
            return listMyFriends.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        RoundedImageView profileImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}

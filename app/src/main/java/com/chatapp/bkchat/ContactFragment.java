package com.chatapp.bkchat;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.makeramen.roundedimageview.RoundedImageView;



/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactsList;

    private DatabaseReference ContactsReference, UserReference;
    private FirebaseAuth myAuth;
    private String currentUserId;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contact, container, false);

        myContactsList = (RecyclerView) contactsView.findViewById(R.id.contacts_recyclerview);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        myAuth = FirebaseAuth.getInstance();


        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser1 = myAuth.getCurrentUser();
        if(currentUser1 != null) {
            currentUserId = myAuth.getCurrentUser().getUid();
            UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
            ContactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(ContactsReference, Contacts.class).build();

            FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int i, @NonNull Contacts contacts) {
                    final String userId = getRef(i).getKey();

                    UserReference.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                if(dataSnapshot.child("userState").hasChild("state")){
                                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                    String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                    String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                    if(state.equals("online"))
                                    {
                                        holder.onlineIcon.setVisibility(View.VISIBLE);
                                    }
                                    else if(state.equals("offline"))
                                    {
                                        holder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }
                                }
                                else{
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }

                                if(dataSnapshot.hasChild("image")){
                                    String profileImage = dataSnapshot.child("image").getValue().toString();
                                    String profileName = dataSnapshot.child("username").getValue().toString();
                                    String profileStatus = dataSnapshot.child("description").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.userImage);

                                }
                                else {
                                    String profileName = dataSnapshot.child("username").getValue().toString();
                                    String profileStatus = dataSnapshot.child("description").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
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
                public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                    ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                    return viewHolder;
                }
            };
            myContactsList.setAdapter(adapter);
            adapter.startListening();
        }}

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        RoundedImageView userImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            userImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = (ImageView) itemView.findViewById(R.id.user_online_status);
        }
    }
}







//package com.chatapp.bkchat;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ContactFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class ContactFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private UserAdapter userAdapter;
//    private RecyclerView recyclerView;
//    private DatabaseReference usersRef, contactsRef;
//    private View contactsView;
//
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public ContactFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ContactFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ContactFragment newInstance(String param1, String param2) {
//        ContactFragment fragment = new ContactFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        contactsView = inflater.inflate(R.layout.fragment_contact, container, false);
//        recyclerView = contactsView.findViewById(R.id.contacts_recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//
//        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
//        getAllContacts();
//        return contactsView;
//    }
//    private void getAllContacts() {
//        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
//        contactsRef.child(uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<String> dataContact = new ArrayList<>();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    dataContact.add(data.getKey().toString());
//                }
//                userAdapter= new UserAdapter(dataContact,getContext());
//                recyclerView.setAdapter(userAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//}
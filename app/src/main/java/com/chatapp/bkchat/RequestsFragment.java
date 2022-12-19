 package com.chatapp.bkchat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.makeramen.roundedimageview.RoundedImageView;





 public class RequestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private  View RequestsFragmentView;
    private RecyclerView myRequestsList;

    private DatabaseReference ChatRequestsRef, UsersRef,ContactsRef;
    private FirebaseAuth myAuth;
    private String currentUserID;

    public RequestsFragment() {
        // Required empty public constructor
    }
/**
 * A simple {@link Fragment} subclass.
 */
//public class RequestsFragment extends Fragment {
//
//    private View RequestsFragmentView;
//    private RecyclerView myRequestsList;
//
//    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef;
//    private FirebaseAuth myAuth;
//    private String currentUserID;
//
//    public RequestsFragment() {
//        // Required empty public constructor
//    }
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
       // receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
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
                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();

                                    if(type.equals("received")){
                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("image")){
                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }
                                                final String requestUserName = dataSnapshot.child("username").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("description").getValue().toString();

                                                holder.userName.setText(requestUserName);
                                                holder.userStatus.setText("Want add friends with you!");

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Accept",
                                                                "Cancel"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle(requestUserName +"Chat Request");
                                                        builder.setItems(options, new DialogInterface.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i)
                                                            {
                                                                if(i == 0)
                                                                {
                                                                    ContactsRef.child(currentUserID).child(list_user_id).child("Contacts")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>()
                                                                            {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful()){
                                                                                        ContactsRef.child(list_user_id).child(currentUserID).child("Contacts")
                                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if(task.isSuccessful())
                                                                                                        {
                                                                                                            ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                                                                    .removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                                    {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                                {
                                                                                                                    if(task.isSuccessful())
                                                                                                                    {
                                                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                                                .removeValue()
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                                                {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if(task.isSuccessful()){
                                                                                                                                    Toast.makeText(getContext(), "New Contacts Saved", Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }

                                                                if(i == 1){
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                            {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if(task.isSuccessful()){
                                                                                ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                        {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            Toast.makeText(getContext(), "Contacts is Denied", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
                                            }


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if(type.equals("sent")){
                                        Button btn_request_send = holder.itemView.findViewById(R.id.request_accept_btn);
                                        btn_request_send.setText("sent");

                                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("image")){
                                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }
                                                final String requestUserName = dataSnapshot.child("username").getValue().toString();
                                                final String requestUserStatus = dataSnapshot.child("description").getValue().toString();

                                                holder.userName.setText(requestUserName);
                                                holder.userStatus.setText("You have send request to "+requestUserName);

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Cancel"
                                                        };
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                        builder.setTitle("Sent");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                if(i == 0){
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                ChatRequestsRef.child(list_user_id).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            Toast.makeText(getContext(), "Cancelled request", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });
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
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };
        myRequestsList.setAdapter(adapter);
        adapter.startListening();
        // myRequestsList.setAdapter(adapter);
        // adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        RoundedImageView profileImage;
        Button AcceptButton, CancelButton;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}







//package com.chatapp.bkchat;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.makeramen.roundedimageview.RoundedImageView;
//import com.squareup.picasso.Picasso;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link RequestsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class RequestsFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//
//    private  View RequestsFragmentView;
//    private RecyclerView myRequestsList;
//
//    private DatabaseReference ChatRequestsRef, UsersRef;
//    private FirebaseAuth mAuth;
//    private String currentUserID;
//
//    public RequestsFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RequestsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static RequestsFragment newInstance(String param1, String param2) {
//        RequestsFragment fragment = new RequestsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
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
//        // Inflate the layout for this fragment
//        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);
//
//        mAuth = FirebaseAuth.getInstance();
//        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
//        currentUserID = mAuth.getCurrentUser().getUid();
//        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        myRequestsList = (RecyclerView) RequestsFragmentView.findViewById(R.id.chat_requests_list);
//        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        return  RequestsFragmentView;
//    }
//
//
//    @Override
//    public void onStart() {
//
//        super.onStart();
//        FirebaseRecyclerOptions<Contacts> options=
//                new FirebaseRecyclerOptions.Builder<Contacts>()
//                        .setQuery(ChatRequestsRef.child(currentUserID), Contacts.class)
//                        .build();
//        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Contacts model)
//                    {
//                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
//                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);
//
//                        final  String list_user_id =  getRef(position).getKey();
//
//                        DatabaseReference getTypeRef =  getRef(position).child("Request_type").getRef();
//                        getTypeRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot)
//                            {
//                                if(snapshot.exists())
//                                {
//                                    String type = snapshot.getValue().toString();
//
//                                    if(type.equals("received"))
//                                    {
//                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot)
//                                            {
//                                                if(snapshot.hasChild("image"))
//                                                {
//                                                    final  String requestUserName = snapshot.child("username").getValue().toString();
//                                                    final  String requestUserStatus = snapshot.child("description").getValue().toString();
//                                                    final  String requestProfileImage = snapshot.child("image").getValue().toString();
//
//                                                    holder.userName.setText(requestUserName);
//                                                    holder.userStatus.setText(requestUserStatus);
//                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
//                                                }
//                                                else
//                                                {
//
//                                                    final  String requestUserName = snapshot.child("username").getValue().toString();
//                                                    final  String requestUserStatus = snapshot.child("description").getValue().toString();
//
//                                                    holder.userName.setText(requestUserName);
//                                                    holder.userStatus.setText(requestUserStatus);
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error)
//                                            {
//
//                                            }
//                                        });
//
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
//                        RequestsViewHolder holder = new RequestsViewHolder(view);
//                        return holder;
//                    }
//                };
//
//        myRequestsList.setAdapter(adapter);
//        adapter.startListening();
//
//
//    }
//
//    public static  class RequestsViewHolder extends RecyclerView.ViewHolder
//    {
//
//        TextView userName, userStatus;
//        RoundedImageView profileImage;
//        Button AcceptButton, CancelButton;
//
//
//        public RequestsViewHolder(@NonNull View itemView)
//        {
//            super(itemView);
//
//
//            userName = itemView.findViewById(R.id.user_profile_name);
//            userStatus = itemView.findViewById(R.id.user_status);
//            profileImage = itemView.findViewById(R.id.users_profile_image);
//            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
//            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
//        }
//    }
//}
////package com.chatapp.bkchat;
////
////import android.os.Bundle;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////
////import androidx.annotation.NonNull;
////import androidx.fragment.app.Fragment;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.database.DataSnapshot;
////import com.google.firebase.database.DatabaseError;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////import com.google.firebase.database.ValueEventListener;
////
////import java.util.ArrayList;
////import java.util.List;
////
/////**
//// * A simple {@link Fragment} subclass.
//// * Use the {@link ContactFragment#newInstance} factory method to
//// * create an instance of this fragment.
//// */
////public class RequestsFragment extends Fragment {
////
////    // TODO: Rename parameter arguments, choose names that match
////
////    private static final String ARG_PARAM1 = "param1";
////    private static final String ARG_PARAM2 = "param2";
////
////    private UserAdapter userAdapter;
////    private RecyclerView recyclerView;
////    private DatabaseReference usersRef, requestsRef;
////    private View requestsView;
////
////
////    // TODO: Rename and change types of parameters
////    private String mParam1;
////    private String mParam2;
////
////    public RequestsFragment() {
////        // Required empty public constructor
////    }
////
////    /**
////     * Use this factory method to create a new instance of
////     * this fragment using the provided parameters.
////     *
////     * @param param1 Parameter 1.
////     * @param param2 Parameter 2.
////     * @return A new instance of fragment ContactFragment.
////     */
////    // TODO: Rename and change types and number of parameters
////    public static ContactFragment newInstance(String param1, String param2) {
////        ContactFragment fragment = new ContactFragment();
////        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
////        fragment.setArguments(args);
////        return fragment;
////    }
////
////
////    @Override
////    public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
////        }
////    }
////
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container,
////                             Bundle savedInstanceState) {
////
////        requestsView = inflater.inflate(R.layout.fragment_requests, container, false);
////        recyclerView = requestsView.findViewById(R.id.chat_requests_list);
////        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
////
////        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
////        requestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
////        getAllRequests();
////        return requestsView;
////    }
////
////    private void getAllRequests() {
////        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
////        requestsRef.child(uid).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                List<String> dataContact = new ArrayList<>();
////                for (DataSnapshot data : snapshot.getChildren()) {
////                    dataContact.add(data.getKey().toString());
////                }
////                userAdapter = new UserAdapter(dataContact, getContext());
////                recyclerView.setAdapter(userAdapter);
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {
////
////            }
////        });
////    }
////}
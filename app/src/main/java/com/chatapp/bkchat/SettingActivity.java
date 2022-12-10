package com.chatapp.bkchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class SettingActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;

    Button update;
    EditText name;
    EditText description;
    FirebaseAuth auth;
    DatabaseReference rootRef;
    StorageReference storageRef;
    RoundedImageView avatar;
    String uid;
    Uri selectedImageUri;
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        khoiTao();
        listener();
        retrieveUser();

    }

    private void listener() {
        update.setOnClickListener(v -> updateData());
        avatar.setOnClickListener(v -> selectImage());
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    private void updateData() {
        if (isValidate()) {
            String username = name.getText().toString();
            String newDescription = description.getText().toString();

            rootRef.child("Users").child(uid).child("username").setValue(username);
            rootRef.child("Users").child(uid).child("description").setValue(newDescription);
            Toast.makeText(SettingActivity.this, "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    StorageReference filePath = storageRef.child(uid + ".jpg");
                    filePath.putFile(selectedImageUri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(uri -> rootRef.child("Users").child(uid).child("image").setValue(uri.toString().trim()));
                            Toast.makeText(SettingActivity.this, "Ảnh Đại Diện Đã Thay Đổi", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private boolean isValidate() {
        if (name.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void retrieveUser() {
        rootRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String retrieveName = snapshot.child("username").getValue().toString();
                    String retrieveDescription = snapshot.child("description").getValue().toString();
                    name.setText(retrieveName);
                    description.setText(retrieveDescription);
                    if (snapshot.hasChild("image")) {
                        String retrieveImage = snapshot.child("image").getValue().toString().trim();
                        Picasso.get().load(retrieveImage).into(avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void khoiTao() {
        update = findViewById(R.id.buttonUpdate);
        name = findViewById(R.id.set_name);
        description = findViewById(R.id.set_description);

        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference().child("ImageProfile");
        avatar = findViewById(R.id.yourAvatar);

        mToolbar = findViewById(R.id.toolbar_setting);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Setting");

    }

}
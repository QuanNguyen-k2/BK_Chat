package com.chatapp.bkchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;


public class SignInActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    Button signInButton;
    EditText userEmail,userPassword;
    TextView signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        khoiTao();
        listener();
    }

    private void listener() {
        signUp.setOnClickListener(v -> sendUserToSignUp());

    }

    private void khoiTao() {
        signInButton=findViewById(R.id.buttonSignIn);
        userEmail=findViewById(R.id.inputEmail);
        userPassword=findViewById(R.id.inputPassword);
        signUp=findViewById(R.id.textSignUp);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            sendUserToMainAc();
        }
    }

    private void sendUserToMainAc() {
        Intent main= new Intent(SignInActivity.this,MainActivity.class);
        startActivity(main);
    }
    private void sendUserToSignUp() {
        Intent register= new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(register);
    }
}
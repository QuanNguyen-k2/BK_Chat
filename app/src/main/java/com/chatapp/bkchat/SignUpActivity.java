package com.chatapp.bkchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    Button signUpButton;
    EditText userEmail,userPassword,userName;
    TextView signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        khoiTao();
        listener();
    }

    private void listener() {
        signIn.setOnClickListener(v -> sendUserToSignIn());
    }

    private void khoiTao() {
        signUpButton=findViewById(R.id.buttonSignUp);
        userEmail=findViewById(R.id.inputEmail);
        userPassword=findViewById(R.id.inputPassword);
        userName=findViewById(R.id.inputName);
        signIn=findViewById(R.id.textSignIn);

    }

    private void sendUserToSignIn() {
        Intent login= new Intent(SignUpActivity.this,SignInActivity.class);
        startActivity(login);
    }
}
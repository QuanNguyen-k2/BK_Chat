package com.chatapp.bkchat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignInActivity extends AppCompatActivity {

    Button signInButton;
    EditText userEmail, userPassword;
    TextView signUp,forgotPassword;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        khoiTao();
        listener();
    }

    private void listener() {
        signUp.setOnClickListener(v -> sendUserToSignUp());
        signInButton.setOnClickListener(v -> signIn());
        forgotPassword.setOnClickListener(v->sendUserToForgotPassword());

    }

    private void signIn() {
        if (isValidateSignIn()) {
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainAc();
                        Toast.makeText(SignInActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    } else {
                        String mes = task.getException().toString();
                        Toast.makeText(SignInActivity.this, "Error " + mes, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidateSignIn() {
        if (userEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void khoiTao() {
        signInButton = findViewById(R.id.buttonSignIn);
        userEmail = findViewById(R.id.inputEmail);
        userPassword = findViewById(R.id.inputPassword);
        signUp = findViewById(R.id.textSignUp);
        forgotPassword=findViewById(R.id.textForgotPassWord);

        auth = FirebaseAuth.getInstance();

    }

    private void sendUserToMainAc() {
        Intent main = new Intent(SignInActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    private void sendUserToSignUp() {
        Intent register = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(register);
    }
    private void sendUserToForgotPassword(){
        Intent forgot = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(forgot);
    }
}
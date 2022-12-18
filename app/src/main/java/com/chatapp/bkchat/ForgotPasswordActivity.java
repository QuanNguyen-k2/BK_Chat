package com.chatapp.bkchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button forgotPassword;
    private TextView notificationForgotPass,signIn;
    private EditText emailForgotPass;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        khoiTao();
        listener();
    }

    private void listener() {
        signIn.setOnClickListener(v->sendToUserSignIn());
        forgotPassword.setOnClickListener(v->forgotPassword());
        emailForgotPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    forgotPassword.setEnabled(true);
                    notificationForgotPass.setVisibility(View.GONE);
                    notificationForgotPass.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void forgotPassword() {
        if(!emailForgotPass.getText().toString().trim().isEmpty()){
            String email=emailForgotPass.getText().toString();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        notificationForgotPass.setVisibility(View.GONE);
                        notificationForgotPass.setVisibility(View.VISIBLE);
                        notificationForgotPass.setText(getString(R.string.please_click_the_link_in_your_email_to_create_new_password));
                        forgotPassword.setEnabled(false);
                    }
                    else{
                        notificationForgotPass.setVisibility(View.GONE);
                        notificationForgotPass.setVisibility(View.VISIBLE);
                        notificationForgotPass.setText(getString(R.string.email_incorrect));
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Enter your email !", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToUserSignIn() {
        onBackPressed();
    }

    private void khoiTao() {
        forgotPassword=findViewById(R.id.buttonForgotPassword);
        notificationForgotPass=findViewById(R.id.notificationPass);
        signIn=findViewById(R.id.textSignIn);
        emailForgotPass=findViewById(R.id.inputEmail);
        auth=FirebaseAuth.getInstance();
    }

}
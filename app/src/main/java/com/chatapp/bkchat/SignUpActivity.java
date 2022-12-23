package com.chatapp.bkchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.chatapp.bkchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    Button signUpButton;
    EditText userEmail,userPassword,userPasswordConfirm,userName;
    TextView signIn;
    FirebaseAuth auth;
    DatabaseReference rootRef;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        khoiTao();
        listener();
    }

    private void listener() {
        signIn.setOnClickListener(v -> sendUserToSignIn());
        signUpButton.setOnClickListener(v->{
            if(isValidate()){
                createNewAccount();
            }
        });
    }

    private void khoiTao() {
        signUpButton=findViewById(R.id.buttonSignUp);
        userEmail=findViewById(R.id.inputEmail);
        userPassword=findViewById(R.id.inputPassword);
        userPasswordConfirm=findViewById(R.id.inputConfirmPassword);
        userName=findViewById(R.id.inputName);
        signIn=findViewById(R.id.textSignIn);

        progressDialog= new ProgressDialog(this);

    }

    private void createNewAccount() {
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();
        String username=userName.getText().toString();


        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            if(task.isSuccessful()){
                String userID =auth.getCurrentUser().getUid();
                User user= new User(username,email,"Một Ngày Tuyệt Vời");
                rootRef.child("Users").child(userID).setValue(user);
                sendUserToMainAc();
            }
            else{
                String message=task.getException().toString();
                showToast("Error "+message);
            }
            progressDialog.dismiss();
        });

    }


    private void sendUserToSignIn() {
        Intent login= new Intent(SignUpActivity.this,SignInActivity.class);
        startActivity(login);
    }
    private void sendUserToMainAc() {
        Intent main= new Intent(SignUpActivity.this,MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    private void showToast(String mes){
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidate(){
        if(userName.getText().toString().trim().isEmpty()){
            showToast("Enter your name");
            return false;
        }
        else if(userEmail.getText().toString().trim().isEmpty()){
            showToast("Enter your email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail.getText().toString()).matches()){
            showToast("Invalid your email");
            return false;
        }
        else if(userPassword.getText().toString().trim().isEmpty()){
            showToast("Enter your password");
            return false;
        }
        else if(userPassword.getText().toString().trim().length()<8){
            showToast("Passwords Must Be At Least 8 Characters Long");
            return false;
        }
        else if(!userPasswordConfirm.getText().toString().equals(userPassword.getText().toString())){
            showToast("Password & confirm password must be same");
            return false;
        }
        else {
            return true;
        }
    }
}
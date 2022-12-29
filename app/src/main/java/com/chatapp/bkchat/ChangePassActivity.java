package com.chatapp.bkchat;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassActivity extends AppCompatActivity {

    Button changeBtn;
    EditText name;
    EditText oldPass;
    EditText newPass;
    EditText confirmNewPass;
    TextView textWarning;
    String username, uid;
    ImageView visibleOldPass;
    ImageView visibleNewPass;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_pass);
        changeBtn = findViewById(R.id.buttonChange);
        name = findViewById(R.id.inputName);
        oldPass = findViewById(R.id.inputOldPassword);
        newPass = findViewById(R.id.inputNewPassword);
        confirmNewPass = findViewById(R.id.inputConfirmPassword);
        textWarning = findViewById(R.id.text_warning);
        changeBtn.setOnClickListener(v -> changeData());
        username = getIntent().getExtras().get("name").toString();
        uid = getIntent().getExtras().get("uid").toString();
        name.setText(username);
        mToolbar = findViewById(R.id.toolbar_setting);
        visibleNewPass = findViewById(R.id.visibleNewPass);
        visibleNewPass.setOnClickListener(v -> visibleNewPass());
        visibleOldPass = findViewById(R.id.visibleOldPass);
        visibleOldPass.setOnClickListener(v -> visibleOldPass());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
    }

    private void visibleNewPass() {
        if (newPass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            newPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            confirmNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        newPass.setSelection(newPass.length());
        confirmNewPass.setSelection(confirmNewPass.length());
    }

    private void visibleOldPass() {

        if (oldPass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            oldPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        oldPass.setSelection(oldPass.length());

    }

    private void changeData() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.changePassword_layout).getWindowToken(), 0);

        if (isValidateChangeName() && !username.equals(name.getText().toString())) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass.getText().toString());

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    textWarning.setVisibility(View.GONE);
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.child("Users").child(uid).child("username").setValue(name.getText().toString())
                            .addOnCompleteListener(task1 -> Toast.makeText(ChangePassActivity.this, "Changed Name Succeed", Toast.LENGTH_SHORT).show()
                            );
                } else {
                    textWarning.setText("Incorrect password");
                }
            })
            ;
        }

        if (!newPass.getText().toString().trim().isEmpty() && isValidateChangePass()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass.getText().toString());

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass.getText().toString()).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(ChangePassActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                    textWarning.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(ChangePassActivity.this, "Save Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            textWarning.setText("Incorrect password");
                        }
                    });
        }
    }

    private boolean isValidateChangeName() {
        if (name.getText().toString().trim().isEmpty()) {
            textWarning.setText("Input your name");
            textWarning.setVisibility(View.VISIBLE);
            return false;
        } else if (oldPass.getText().toString().trim().isEmpty()) {
            textWarning.setText("Input your password");
            textWarning.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private boolean isValidateChangePass() {
        if (newPass.getText().toString().trim().length() < 8 && !newPass.getText().toString().trim().isEmpty()) {
            textWarning.setText("Passwords Must Be At Least 8 Characters Long");
            textWarning.setVisibility(View.VISIBLE);
            return false;
        } else if (!confirmNewPass.getText().toString().equals(newPass.getText().toString())) {
            textWarning.setText("Incorrect confirm password");
            textWarning.setVisibility(View.VISIBLE);
            return false;
        } else if (oldPass.getText().toString().trim().isEmpty()) {
            textWarning.setText("Input your password");
            textWarning.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

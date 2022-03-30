package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        EditText getEmail = findViewById(R.id.forg_pass_email);
        getEmail.setText(this.getIntent().getExtras().getString("currentEmail"));
    }

    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.forgot_pass_view), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public void retrievePass(View v) {
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                findViewById(R.id.forgot_pass_view).getWindowToken(), 0);

        EditText getEmail = findViewById(R.id.forg_pass_email);
        String email = getEmail.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            notifyUser("Email Sent. Check Your Inbox and Spam Folder.");
                        } else {
                            Log.d(TAG, "failure");
                        }
                    }
                }).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                String errorCode =
                        ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                    notifyUser("Invalid Email Format");
                } else {
                    notifyUser(errorCode);
                }
            } else if (e instanceof FirebaseAuthInvalidUserException) {
                String errorCode =
                        ((FirebaseAuthInvalidUserException) e).getErrorCode();
                if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                    notifyUser("There Is No User Associated With This Email");
                }else if(errorCode.equals("ERROR_USER_DISABLED")){
                    notifyUser("The Account Associated With This Email Was Disabled");
                }
            } else {
                notifyUser(e.getLocalizedMessage());
            }
        });
    }
}
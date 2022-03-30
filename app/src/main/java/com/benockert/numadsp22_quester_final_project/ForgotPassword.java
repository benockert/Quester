package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference dr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        EditText getEmail = findViewById(R.id.forg_pass_email);
        if (getEmail != null && this.getIntent().getExtras() != null) {
            getEmail.setText(this.getIntent().getExtras().getString("currentEmail"));
        }
    }

    /**
     * notifies the user of any changes/validation issues
     *
     * @param message String represents the message to be displayed
     */
    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.forgot_pass_view), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    /**
     * returns user to the view they we previously on
     *
     * @param v View current user view
     */
    //TODO: decide if necessary
    public void back(View v) {
        finish();
    }

    /**
     * sends an email to the given email parameter with a way for the user to reset their password
     *
     * @param email String email of a User
     */
    private void resetWithEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notifyUser("Email Sent. Check Your Inbox and Spam Folder.");
                    } else {
                        Log.d(TAG, "failure");
                    }
                }).addOnFailureListener(e -> {
            //checks the exception type and error code
            //displays appropriate message based on error code
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
                } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                    notifyUser("The Account Associated With This Email Was Disabled");
                }
            } else {
                notifyUser(e.getLocalizedMessage());
            }
        });
    }

    /**
     * ensures that the given username exists, calls reset email using the email associated
     * with the username in the database
     *
     * @param username String username to sign in with
     */
    private void resetWithUsername(String username) {
        dr.child("users").child(username).child("email").get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().getValue() != null) {
                        String email = task.getResult().getValue().toString();
                        resetWithEmail(email);
                    } else {
                        notifyUser("Username Provided Is Not Associated With An Account");
                    }
                });
    }

    /**
     * sends an email to the inputted email or email associated with the given username
     *
     * @param v View the current view
     */
    public void resetPassword(View v) {
        //hides the keyboard so users can see error messages that may pop-up
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                findViewById(R.id.forgot_pass_view).getWindowToken(), 0);

        EditText getEmail = findViewById(R.id.forg_pass_email);
        String email = getEmail.getText().toString();


        //ensures that the email/username field is not empty
        if ("".equals(email)) {
            notifyUser("Email/Username Field Must Not Be Empty");
        } else {
            if (!email.contains("@")) {
                //helper method to keep this method short
                resetWithUsername(email);
            } else {
                //helper method to keep this method short
                resetWithEmail(email);
            }
        }
    }
}
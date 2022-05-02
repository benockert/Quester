package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;


public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;

    DatabaseReference dr;
    EditText getEmail;
    EditText getUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getEmail = findViewById(R.id.reg_email);
        getUsername = findViewById(R.id.reg_username);
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        //auto-fills in information gotten from the log in page
        if (this.getIntent().getExtras().get("email") != null) {
            String info = this.getIntent().getExtras().get("email").toString();
            if (info.contains("@")) {
                getEmail.setText(info);
            } else {
                getUsername.setText(info);
            }
        }
    }

    /**
     * Brings user back to login page if they do not wish to register/already have an account
     *
     * @param v View represents the current view
     */
    public void login(View v) {
        finish();
    }

    /**
     * brings users to a new activity where they can reset their password
     *
     * @param v View represents the current view
     */
    public void forgot_password(View v) {
        EditText getEmail = findViewById(R.id.reg_email);
        EditText getUsername = findViewById(R.id.reg_username);

        Intent i = new Intent(this, ForgotPassword.class);
        //passes along inputted email/username information if the user moves to forgot password
        //so they do not need to re-type
        if (getEmail.getText() != null) {
            String currentEmail = getEmail.getText().toString();
            i.putExtra("email", currentEmail);
        } else if(getUsername.getText() != null){
            String currentUsername = getUsername.getText().toString();
            i.putExtra("username", currentUsername);
        }
        startActivity(i);
    }

    /**
     * notifies the user of any changes/validation issues
     *
     * @param message String represents the message to be displayed
     */
    private void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.reg_view), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    /**
     * attempts to register a user based on the given username, input from the email and password field
     *
     * @param username String represents the username the user registering wants to have
     */
    private void attemptRegistration(String username) {
        EditText getPass = findViewById(R.id.reg_password);
        String email = getEmail.getText().toString().trim();
        String password = getPass.getText().toString();

        //ensures the password field is not empty
        if ("".equals(password)) {
            notifyUser("Password Field Cannot Be Empty");
        } else if ("".equals(email)) {
            //ensures the email field is not empty
            notifyUser("Email Field Cannot Be Empty");
        } else {
            //attempts to register a user in the database if the given username is valid and if the
            //username and password field are not empty
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();

                            //ensures the user is not null
                            assert user != null;
                            //adds the registered user to the realtime database
                            dr.child("users").child(username).child("email").setValue(user.getEmail());
                            String rand = UUID.randomUUID().toString();
                            String id = "u"+ rand.substring(rand.length() - 5);
                            dr.child("users").child(username).child("id").setValue(id);

                            //storing username in user database
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates);

                            //navigating the user to our apps main page
                            //passing through username - not necessary as we just need to get the
                            // current user instance to grab the display name that was set above
                            //TODO
                            Intent i = new Intent(this, MainActivity.class);
                            i.putExtra("username", username);
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(e -> {
                        //checks the exception type and error code
                        //displays appropriate message based on error code
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            switch (errorCode) {
                                case "ERROR_INVALID_EMAIL":
                                    notifyUser("Invalid Email Format");
                                    break;
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    notifyUser("Email Already In Use");
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    notifyUser("Invalid Password. Password Must Be At Least 6 Characters");
                                    break;
                                default:
                                    Log.d(TAG, errorCode);
                                    break;
                            }
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            String errorCode =
                                    ((FirebaseAuthUserCollisionException) e).getErrorCode();
                            if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                notifyUser("Email Already In Use. Login or Try Again");
                            } else {
                                Log.d(TAG, errorCode);
                            }
                        } else {
                            notifyUser(e.getMessage());
                        }
                    });
        }
    }

    /**
     * method called when Register Button on Register Page is clicked
     *
     * @param v View represents the current view screen
     */
    public void register(View v) {
        //hides the keyboard so users can see error messages that may pop-up
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                findViewById(R.id.reg_view).getWindowToken(), 0);

        EditText getUsrName = findViewById(R.id.reg_username);
        String username = getUsrName.getText().toString().trim();

        //ensures the username field is not empty
        if ("".equals(username)) {
            notifyUser("Username Field Must Not Be Empty");
        } else {
            //ensures that the username given does not already belong to another user
            dr.child("users").child(username).get().addOnCompleteListener(task -> {
                if (task.getResult().getValue() == null) {
                    //attempting to register the user as the username is not in use
                    attemptRegistration(username);
                } else {
                    notifyUser("Username Already in Use");
                }
            });
        }
    }
}
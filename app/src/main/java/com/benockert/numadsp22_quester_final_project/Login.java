package com.benockert.numadsp22_quester_final_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    DatabaseReference dr;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * notifies the user of any changes/validation issues
     *
     * @param message String represents the message to be displayed
     */
    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.login_view), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    /**
     * Overrides the default onStart method to keep users signed in if applicable
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        //ensures user says signed in unless they log out
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("username", currentUser.getDisplayName());
            startActivity(i);
        }
    }

    /**
     * brings users to a new activity where they can reset their password
     *
     * @param v View represents the current view
     */
    public void forgot_password(View v) {
        EditText getEmail = findViewById(R.id.log_email);
        Intent i = new Intent(this, Register.class);
        //passes in prior inputted information from email field if the field is not empty
        if (getEmail.getText() != null) {
            String currentEmail = getEmail.getText().toString();
            i.putExtra("email", currentEmail);
        }
        startActivity(i);
    }

    /**
     * Brings user registration Page to sign up if they don't have an account
     *
     * @param v View represents the current view
     */
    public void register(View v) {
        EditText getEmail = findViewById(R.id.log_email);
        Intent i = new Intent(this, Register.class);
        //passes in prior inputted information from email field if the field is not empty
        if (getEmail.getText() != null) {
            String currentEmail = getEmail.getText().toString();
            i.putExtra("email", currentEmail);
        }
        startActivity(i);
    }

    /**
     * helper method to sign users in with an email and password
     *
     * @param email    String representing the current user email to sign in with
     * @param password String represents the password associated with the given email account
     */
    public void LoginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success
                FirebaseUser user = mAuth.getCurrentUser();
                Intent i = new Intent(this, MainActivity.class);
                //ensures the user is not null
                if (user != null) {
                    //navigating to main app activity page
                    i.putExtra("username", user.getDisplayName());
                    startActivity(i);
                }
            }
        }).addOnFailureListener(e -> {
            //checks the exception type and error code
            //displays appropriate message based on error code
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                String errorCode =
                        ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                switch (errorCode) {
                    case "ERROR_INVALID_EMAIL":
                        notifyUser("Invalid Email Format");
                        break;
                    case "ERROR_WRONG_PASSWORD":
                        notifyUser("Invalid Password Or User Does Not Have A Password");
                        break;
                    default:
                        notifyUser(e.getLocalizedMessage());
                        break;
                }
            } else if (e instanceof FirebaseAuthInvalidUserException) {
                String errorCode =
                        ((FirebaseAuthInvalidUserException) e).getErrorCode();
                switch (errorCode) {
                    case "ERROR_USER_NOT_FOUND":
                        notifyUser("No matching account found");
                        break;
                    case "ERROR_USER_DISABLED":
                        notifyUser("User account has been disabled");
                        break;
                    default:
                        notifyUser(e.getLocalizedMessage());
                        break;
                }
            }
        });
    }

    /**
     * helper method to sign users in with their username and password
     *
     * @param username String representing the user account to login to
     * @param password String representing the supposed password to the
     *                 account using the given username
     */
    public void LoginWithUsername(String username, String password) {
        //attempts to get the email associated with the given username
        dr.child("users").child(username).child("email").get().addOnCompleteListener(task -> {
            //if the value returned is null then the given username is not associated with any user
            if (task.getResult().getValue() == null) {
                notifyUser("Username Does Not Exist");
            } else {
                //retrieves the email associated with the account
                email = task.getResult().getValue().toString();
                //signs user in with the associated email and password
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(t -> {
                    if (t.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent i = new Intent(this, MainActivity.class);
                        //ensures user is not null
                        if (user != null) {
                            //navigating to main app activity page
                            i.putExtra("currentUser", user.getDisplayName());
                            startActivity(i);
                        }
                    }
                }).addOnFailureListener(e -> {
                    //checks the exception type and error code
                    //displays appropriate message based on error code
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        String errorCode =
                                ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                        if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                            notifyUser("Invalid Password Or User Does Not Have A Password");
                        } else {
                            notifyUser(e.getLocalizedMessage());
                        }
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        String errorCode =
                                ((FirebaseAuthInvalidUserException) e).getErrorCode();
                        if (errorCode.equals("ERROR_USER_DISABLED")) {
                            notifyUser("User account has been disabled");
                        } else {
                            notifyUser(e.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }

    /**
     * method called to let people login to our app
     *
     * @param v View represents the current view screen
     */
    public void login(View v) {
        //hides the keyboard so users can see error messages that may pop-up
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                findViewById(R.id.login_view).getWindowToken(), 0);

        mAuth = FirebaseAuth.getInstance();
        EditText getEmail = findViewById(R.id.log_email);
        EditText getPass = findViewById(R.id.log_password);
        email = getEmail.getText().toString().trim();
        String password = getPass.getText().toString();

        //ensures the password and email/username fields are not empty
        if (password.equals("")) {
            notifyUser("Password Field Must Not Be Empty");
        } else if (email.equals("")) {
            notifyUser("Email/Username Field Must Not Be Empty");
        } else {
            //if the email field does not contain an @ then it is assumed the user
            // inputted their username
            if (!email.contains("@")) {
                String username = email;
                //helper method to keep this method short
                LoginWithUsername(username, password);
            } else {
                //helper method to keep this method short
                LoginWithEmail(email, password);
            }
        }
    }
}
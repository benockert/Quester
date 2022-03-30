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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.login_view), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //ensures user says signed in unless they log out
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("currentUser", currentUser.getDisplayName());
            startActivity(i);
        }
    }

    public void forgot_password(View v){
        EditText getEmail = findViewById(R.id.log_email);
        String currentEmail = getEmail.getText().toString();
        Intent i = new Intent(this, ForgotPassword.class);
        i.putExtra("currentEmail", currentEmail);
        startActivity(i);
    }

    public void register(View v) {
        EditText getEmail = findViewById(R.id.log_email);
        String currentEmail = getEmail.getText().toString();
        Intent i = new Intent(this, Register.class);
        i.putExtra("currentEmail", currentEmail);
        startActivity(i);
    }

    public void login(View v) {
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                findViewById(R.id.login_view).getWindowToken(), 0);

        mAuth = FirebaseAuth.getInstance();
        EditText getEmail = findViewById(R.id.log_email);
        EditText getPass = findViewById(R.id.log_password);
        String email = getEmail.getText().toString().trim();
        String password = getPass.getText().toString();

        if (password.equals("")) {
            Snackbar.make(v, "Password Field Must Not Be Empty", BaseTransientBottomBar.LENGTH_LONG).show();
        } else if (email.equals("")) {
            Snackbar.make(v, "Email Field Must Not Be Empty", BaseTransientBottomBar.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent i = new Intent(this, MainActivity.class);
                    if (user != null) {
                        i.putExtra("currentUser", user.getDisplayName());
                        startActivity(i);
                    }
                }
            }).addOnFailureListener(e -> {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    String errorCode =
                            ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                    if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                        notifyUser("Invalid Email Format");
                    } else if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
                        notifyUser("Invalid Password Or User Does Not Have A Password");
                    } else {
                        notifyUser(e.getLocalizedMessage());
                    }
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    String errorCode =
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();
                    if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                        notifyUser("No matching account found");
                    } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                        notifyUser("User account has been disabled");
                    } else {
                        notifyUser(e.getLocalizedMessage());
                    }
                }
            });
        }
    }
}
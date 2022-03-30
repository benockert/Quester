package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;

    DatabaseReference dr;
    EditText getEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getEmail = findViewById(R.id.reg_email);

        dr = FirebaseDatabase.getInstance().getReference();
        getEmail.setText(this.getIntent().getExtras().get("currentEmail").toString());
    }

    public void login(View v) {
        finish();
    }

    public void forgot_password(View v){
        EditText getEmail = findViewById(R.id.log_email);
        String currentEmail = getEmail.getText().toString();
        Intent i = new Intent(this, ForgotPassword.class);
        i.putExtra("currentEmail", currentEmail);
        startActivity(i);
    }

    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.reg_view), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public void attemptRegistration(String username) {
        EditText getPass = findViewById(R.id.reg_password);
        String email = getEmail.getText().toString().trim();
        String password = getPass.getText().toString();

        if ("".equals(password)) {
            notifyUser("Password Field Cannot Be Empty");
        } else if ("".equals(email)) {
            notifyUser("Email Field Cannot Be Empty");
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            dr.child("users").child(username).child("email").setValue(user.getEmail());

                            //can store username here
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            user.updateProfile(profileUpdates);
                            Intent i = new Intent(this, MainActivity.class);
                            i.putExtra("userName", username);
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            if (errorCode.equals("ERROR_INVALID_EMAIL")) {
                                notifyUser("Invalid Email Format");
                            } else if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                notifyUser("Email Already In Use");
                            } else if (errorCode.equals("ERROR_WEAK_PASSWORD")) {
                                notifyUser("Invalid Password. Password Must Be At Least 6 Characters");
                            } else {
                                Log.d(TAG,errorCode);
                            }
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            String errorCode =
                                    ((FirebaseAuthUserCollisionException) e).getErrorCode();
                            if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                notifyUser("Email Already In Use. Login or Try Again");
                            } else{
                                Log.d(TAG,errorCode);
                            }
                        } else {
                            notifyUser(e.getMessage());
                        }
                    });
        }
    }

    public void register(View v) {
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                findViewById(R.id.reg_view).getWindowToken(), 0);

        mAuth = FirebaseAuth.getInstance();
        EditText getUsrName = findViewById(R.id.reg_username);
        String username = getUsrName.getText().toString().trim();

        if ("".equals(username)) {
            String message = "Username Field Must Not Be Empty";
            Snackbar.make(v, message, BaseTransientBottomBar.LENGTH_LONG).show();
        } else {
            dr.child("users").child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Log.i("task", task.getResult().toString());

                    if (task.getResult().getValue() == null) {
                        attemptRegistration(username);
                    } else {
                        Snackbar.make(v,
                                "Username Already in Use", BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
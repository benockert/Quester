package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.CharBuffer;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dr;

    TextView currentEmail;
    TextView currentUsername;

    EditText editEmail;
    EditText editUsername;
    EditText editPassword;

    FirebaseUser user;
    String email;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //init data from db
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        this.currentEmail = (TextView) findViewById(R.id.current_email);
        this.currentUsername = (TextView) findViewById(R.id.current_username);

        this.editEmail = (EditText) findViewById(R.id.edit_email);
        this.editUsername = (EditText) findViewById(R.id.edit_username);
        this.editPassword = (EditText) findViewById(R.id.edit_password);

        populateData();
    }

    private void populateData() {
        this.user = mAuth.getCurrentUser();
        this.email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        this.username = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();

        this.currentEmail.setText(String.format(getString(R.string.current_email), this.email));
        this.currentUsername.setText(String.format(getString(R.string.current_username), this.currentUsername));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_email:
                if (this.editEmail.getText() != null || !this.editEmail.getText().toString().equals("")) {
                    if (this.editEmail.getText().toString().contains("@")) {
                        this.updateEmail(this.editEmail.getText().toString(), view);
                    } else {
                        notifyUser("InvalidEmail");
                    }
                } else {
                    notifyUser("Invalid Email Entered");
                }
                break;
            case R.id.save_username:
                if (this.editUsername.getText() != null || !this.editUsername.getText().toString().equals("")) {
                    this.updateUsername(this.editUsername.getText().toString());
                } else {
                    notifyUser("Invalid Username Entered");
                }
            case R.id.save_password:
                if (this.editPassword.getText() != null || !this.editPassword.getText().toString().equals("")) {
                    this.updatePassword(this.editPassword.getText().toString(), view);
                } else {
                    notifyUser("Invalid Password Entered");
                }
                break;
        }
    }

    private void updateEmail(String newEmail, View view) {
        this.user.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                this.email = newEmail;
                populateData();
                Log.d("MY_PROFILE_ACTIVITY", "Email updated");
            } else {
                notifyUser("Email Update Failed");
                Log.d("MY_PROFILE_ACTIVITY", "Error email not updated");
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
                    case "ERROR_EMAIL_ALREADY_IN_USE":
                        notifyUser("Email Already In Use");
                        break;
                    default:
                        Log.d(TAG, errorCode);
                        break;
                }
            } else if (e instanceof FirebaseAuthUserCollisionException) {
                String errorCode =
                        ((FirebaseAuthUserCollisionException) e).getErrorCode();
                if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                    notifyUser("Email Already In Use");
                } else {
                    Log.d(TAG, errorCode);
                }
            } else {
                notifyUser(e.getMessage());
            }
        });
    }

    private void updateUsername(String newUsername) {
        //todo update quests in db to have new username and update users child
    }

    //todo split into helpers
    private void updatePassword(String newPassword, View view) {
        this.user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //todo do we still need to populate? just have to clear text box
                populateData();
                Log.d("MY_PROFILE_ACTIVITY", "Password updated");
            } else {
                notifyUser("Password Update Failed");
                Log.d("MY_PROFILE_ACTIVITY", "Error password not updated");
            }
        }).addOnFailureListener(e -> {
            //checks the exception type and error code
            //displays appropriate message based on error code
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                switch (errorCode) {
                    case "ERROR_WEAK_PASSWORD":
                        notifyUser("Invalid Password. Password Must Be At Least 6 Characters");
                        break;
                    default:
                        Log.d(TAG, errorCode);
                        break;
                }
            } else if (e instanceof FirebaseAuthUserCollisionException) {
                String errorCode = ((FirebaseAuthUserCollisionException) e).getErrorCode();
                Log.d(TAG, errorCode);
            } else if (e instanceof FirebaseAuthRecentLoginRequiredException){
                //todo prompt for username/email and password
                AuthCredential credential = EmailAuthProvider
                        .getCredential("adrgasg", "adfgasd");

                // Prompt the user to re-provide their sign-in credentials
                this.user.reauthenticate(credential).addOnCompleteListener(task ->{
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                            if (task.isSuccessful()) {
                                populateData();
                                Log.d("MY_PROFILE_ACTIVITY", "Password updated");
                            } else {
                                notifyUser("Password Update Failed");
                                Log.d("MY_PROFILE_ACTIVITY", "Error password not updated");
                            }
                        });
                    } else {
                        notifyUser("Authentication Failed");
                        Log.d(TAG, "Error auth failed");
                    }
                }).addOnFailureListener(error -> {
                    if (error instanceof FirebaseAuthInvalidUserException) {
                        Log.d(TAG, "Invalid User");
                    } else if (error instanceof FirebaseAuthInvalidCredentialsException) {
                        //todo prompt for credentials again
                    }
                });
            } else {
                notifyUser(e.getMessage());
            }
        });
    }

    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.user_profile), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public void logOut(View v){
        FirebaseAuth.getInstance().signOut();
        finish();
    }
}
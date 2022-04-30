package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewAllRecaps;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
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
    TextView currentPassword;

    EditText editEmail;
    EditText editUsername;
    EditText editPassword;

    FirebaseUser user;
    String email;
    String username;
    String password;

    boolean passwordVisable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        BottomNavigationView bNavView = findViewById(R.id.bottomNavigationView);
        bNavView.setSelectedItemId(R.id.nav_profile);

        Context context = this;
        bNavView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.nav_home) {
                i = new Intent(context, MyQuestsActivity.class);
                startActivity(i);
            } else if (item.getItemId() == R.drawable.ic_recaps) {
                    i = new Intent(context, ViewAllRecaps.class);
                    startActivity(i);
            } else if (item.getItemId() == R.drawable.ic_curr_activity) {
//                    i = new Intent(context, MainActivity.class);
//                    startActivity(i);
            }
            return false;
        });
        bNavView.setOnItemReselectedListener(item -> {
            Snackbar.make(this.getCurrentFocus(), "Already At Location", BaseTransientBottomBar.LENGTH_LONG).show();
        });

        //init data from db
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        this.currentEmail = (TextView) findViewById(R.id.current_email);
        this.currentUsername = (TextView) findViewById(R.id.current_username);
        this.currentPassword = (TextView) findViewById(R.id.current_password);

        this.editEmail = (EditText) findViewById(R.id.edit_email);
        this.editUsername = (EditText) findViewById(R.id.edit_username);
        this.editPassword = (EditText) findViewById(R.id.edit_password);

        this.passwordVisable = false;

        populateData();
    }

    private void populateData() {
        this.user = mAuth.getCurrentUser();
        this.email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        this.username = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        //this.password = Objects.requireNonNull(mAuth.getCurrentUser())

        this.currentEmail.setText(String.format(getString(R.string.current_email), this.email));
        this.currentUsername.setText(String.format(getString(R.string.current_username), this.currentUsername));
        if (this.passwordVisable) {
            this.currentPassword.setText(String.format(getString(R.string.current_password), this.password));
        } else {
            String passwordAlias = CharBuffer.allocate(this.password.length()).toString().replace( '\0', '*' );;
            this.currentPassword.setText(String.format(getString(R.string.current_password), passwordAlias));
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_hide_password:
                this.passwordVisable = !this.passwordVisable;
                Button btn = (Button) findViewById(R.id.view_hide_password);
                if (this.passwordVisable) {
                    btn.setText(getString(R.string.hide_password));
                } else {
                    btn.setText(getString(R.string.view_password));
                }
                break;
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
        //update quests in db to have new username
    }

    private void updatePassword(String newPassword, View view) {
        this.user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                this.password = newPassword;
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
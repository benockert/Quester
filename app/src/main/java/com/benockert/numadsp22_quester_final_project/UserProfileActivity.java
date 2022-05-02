package com.benockert.numadsp22_quester_final_project;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benockert.numadsp22_quester_final_project.PhotoRecap.ViewAllRecaps;
import com.benockert.numadsp22_quester_final_project.createQuest.CreateQuestActivity;
import com.benockert.numadsp22_quester_final_project.myQuests.MyQuestsActivity;
import com.benockert.numadsp22_quester_final_project.types.Quest;
import com.benockert.numadsp22_quester_final_project.types.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

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

        BottomNavigationView bNavView = findViewById(R.id.bottomNavigationView);
        bNavView.setBackground(null);
        bNavView.setSelectedItemId(R.id.nav_profile);
        Context context = this.getBaseContext();
        bNavView.setOnItemSelectedListener(item -> {
            Intent i;
            if (item.getItemId() == R.id.nav_recap) {
                i = new Intent(context, ViewAllRecaps.class);
                startActivity(i);
            } else if (item.getItemId() == R.id.nav_home) {
                i = new Intent(context, MyQuestsActivity.class);
                startActivity(i);
            }else if (item.getItemId() == R.id.nav_createQuest) {
                i = new Intent(context, CreateQuestActivity.class);
                startActivity(i);
            }
            return false;
        });
        bNavView.setOnItemReselectedListener(item -> {
            Snackbar.make(bNavView.getRootView(), "Already At Location", BaseTransientBottomBar.LENGTH_LONG).show();
        });

        //init data from db
        mAuth = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        this.currentEmail = (TextView) findViewById(R.id.current_email);
        this.currentUsername = (TextView) findViewById(R.id.current_username);

        this.editEmail = (EditText) findViewById(R.id.edit_email);
        this.editEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    saveEmailTriggered(getCurrentFocus());
                }
                return false;
            }
        });

        this.editUsername = (EditText) findViewById(R.id.edit_username);
        this.editUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    saveUsernameTriggered();
                }
                return false;
            }
        });

        this.editPassword = (EditText) findViewById(R.id.edit_password);
        this.editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    savePasswordTriggered(getCurrentFocus());
                }
                return false;
            }
        });

        this.user = mAuth.getCurrentUser();
        this.email = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());
        this.username = Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName());

        populateData();
    }

    private void populateData() {
        this.currentEmail.setText(String.format(getString(R.string.current_email), this.email));
        this.currentUsername.setText(String.format(getString(R.string.current_username), this.username));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_email:
                saveEmailTriggered(view);
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("MY_PROFILE_ACTIVITY", "Keyboard not open");
                }
                break;
            case R.id.save_username:
                saveUsernameTriggered();
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("MY_PROFILE_ACTIVITY", "Keyboard not open");
                }
                break;
            case R.id.save_password:
                savePasswordTriggered(view);
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.d("MY_PROFILE_ACTIVITY", "Keyboard not open");
                }
                break;
        }
    }

    private void saveEmailTriggered(View view) {
        if (this.editEmail.getText() != null
                || !this.editEmail.getText().toString().equals("")
                || !this.editEmail.getText().toString().equals(this.email)) {
            if (this.editEmail.getText().toString().contains("@")) {
                updateEmail(this.editEmail.getText().toString(), view);
                this.editEmail.setText("");
            } else {
                notifyUser("Invalid Email");
            }
        } else {
            notifyUser("Invalid Email Entered");
        }
    }

    private void updateEmail(String newEmail, View view) {
        this.user.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                this.email = newEmail;
                dr.child("users").child(this.username).child("email").setValue(newEmail);
                populateData();
                notifyUser("Email Has Been Updated");
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
            } else if (e instanceof FirebaseAuthRecentLoginRequiredException) {
                inputCredentials(this.getCurrentFocus(), newEmail, "email");

                // Prompt the user to re-provide their sign-in credentials
            } else {
                notifyUser(e.getMessage());
            }
        });
    }

    private void saveUsernameTriggered() {
        if ((this.editUsername.getText() != null
                || !this.editUsername.getText().toString().equals(""))
                && !this.editUsername.getText().toString().equals(this.username)) {
            this.updateUsername(this.editUsername.getText().toString());
            this.editUsername.setText("");
        } else {
            notifyUser("Invalid Username Entered");
        }
    }

    private void updateUsername(String newUsername) {
        String oldUsername = this.username;
        //update users
        dr.child("users").child(oldUsername).get().addOnCompleteListener(updateUsersTask -> {
            if (updateUsersTask.getResult().getValue() != null ) {
                String result = convertStingToJson(String.valueOf(updateUsersTask.getResult().getValue())).replaceAll(" ", "_");
                try {
                    JSONObject jsonResult = new JSONObject(result);
                    List<UserProfile.UserRecap> userRecaps = UserProfile.getRecapsFromJson(jsonResult.toString());

                    dr.child("users").child(newUsername).child("email").setValue(jsonResult.getString("email"));

                    for (UserProfile.UserRecap recap : userRecaps) {
                        dr.child("users").child(newUsername).child("recaps").child(recap.getName()).child("dateGenerated").setValue(recap.getDateGenerated());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    notifyUser("Username Failed To Updated");
                    return;
                }
            } else {
                notifyUser("Current Username not Found");
                return;
            }
        });

        //updateQuests
        dr.child("quests").get().addOnCompleteListener(updateQuestsTask -> {
            if (updateQuestsTask.getResult().getValue() != null) {
                String result = convertStingToJson(String.valueOf(updateQuestsTask.getResult().getValue())).replaceAll(" ", "_");
                Log.d("json", result);
                try {
                    List<Quest> userQuests = new ArrayList<>();
                    JSONObject jsonResults = new JSONObject(result);
                    Iterator<String> questsIterator = jsonResults.keys();

                    while(questsIterator.hasNext()){
                        String name = questsIterator.next();
                        Quest quest = Quest.getQuestFromJSON(name, jsonResults.getString(name));
                        System.out.println(quest.getUsers());
                        if (quest.getUsers().contains(oldUsername)) {
                            userQuests.add(quest);
                        }
                    }

                    System.out.println(userQuests);

                    for (Quest q : userQuests) {
                        System.out.println(newUsername);
                        System.out.println(oldUsername);
                        dr.child("quests").child(q.joinCode).child("users").child(newUsername).setValue(true);
                        dr.child("quests").child(q.joinCode).child("users").child(oldUsername).removeValue();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    notifyUser("Username Failed To Updated");
                    return;
                }
            }
        });

        dr.child("users").child(oldUsername).removeValue();

        //update this username
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();
        this.user.updateProfile(profileUpdates);
        this.username = newUsername;
        populateData();
        notifyUser("Username Has Been Updated");
    }

    /**
     * converts the String to a parsable json string
     *
     * @param s String of information gotten from the get request
     * @return String of json info to parse through
     */
    private String convertStingToJson(String s) {
        Scanner sc = new Scanner(s).useDelimiter("\\A");
        return sc.hasNext() ? sc.next().replace(", ", ",\n") : "";
    }

    private void savePasswordTriggered(View view) {
        if (this.editPassword.getText() != null || !this.editPassword.getText().toString().equals("")) {
            this.updatePassword(this.editPassword.getText().toString(), view);
            this.editPassword.setText("");
        } else {
            notifyUser("Invalid Password Entered");
        }
    }

    private void updatePassword(String newPassword, View view) {
        this.user.updatePassword(newPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                populateData();
                notifyUser("Password Has Been Updated");
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
                inputCredentials(this.getCurrentFocus(), newPassword, "password");
            } else {
                notifyUser(e.getMessage());
            }
        });
    }

    private void inputCredentials(View view, String newValue, String function) {

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Enter Credentials");

        LinearLayout layout= new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputEmail = new EditText(this);
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEmail.setHint("Email");

        final EditText inputPassword = new EditText(this);
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPassword.setHint("Password");
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(inputEmail);
        layout.addView(inputPassword);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(inputEmail.getText().toString(), inputPassword.getText().toString());
                reauthenticate(credential, newValue, view, "email");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();
    }

    private void reauthenticate(AuthCredential credential, String newValue, View view, String function) {
        this.user.reauthenticate(credential).addOnCompleteListener(task ->{
            if (task.isSuccessful()) {
                if (function.equals("email")) {
                    this.updateEmail(newValue, view);
                } else if (function.equals("password")) {
                    this.updatePassword(newValue, view);
                } else {
                    Log.d("MY_PROFILE", "Authenticating for a function that does not exist");
                }
            } else {
                notifyUser("Authentication Failed");
                Log.d(TAG, "Error auth failed");
            }
        }).addOnFailureListener(error -> {
            if (error instanceof FirebaseAuthInvalidUserException) {
                Log.d(TAG, "Invalid User");
            } else if (error instanceof FirebaseAuthInvalidCredentialsException) {
                inputCredentials(this.getCurrentFocus(), newValue, function);
            }
        });
    }

    public void notifyUser(String message) {
        Snackbar.make(findViewById(R.id.user_profile), message, BaseTransientBottomBar.LENGTH_LONG).show();
    }

    public void logOut(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
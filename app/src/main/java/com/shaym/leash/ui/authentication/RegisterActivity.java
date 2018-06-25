package com.shaym.leash.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shaym.leash.R;
import com.shaym.leash.ui.home.HomeActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button mSignUpButton;
    private EditText mSignUpEmailField;
    private EditText mSignUpPassField;
    private EditText mSignUpNameField;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mSignUpButton = findViewById(R.id.signupbtn);
        mSignUpButton.setOnClickListener(this);
        mSignUpEmailField = findViewById(R.id.signupemail);
        mSignUpPassField = findViewById(R.id.signuppass);
        mSignUpNameField = findViewById(R.id.signupname);
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signupbtn:
                Log.d(TAG, "onClick: LoginBtn");

                String email = mSignUpEmailField.getText().toString();
                String pass = mSignUpPassField.getText().toString();
                if (email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Email/Password is missing.",
                            Toast.LENGTH_SHORT).show();
                }
                else {

                    if (pass.length() < 6){
                        Toast.makeText(RegisterActivity.this, "Password should be at least 6 characters",
                                Toast.LENGTH_SHORT).show();

                    }
                    else {
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (!mSignUpNameField.getText().toString().isEmpty()) {
                                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(mSignUpNameField.getText().toString()).build();
                                                user.updateProfile(profileUpdate);
                                            }
                                            updateUI(user);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            updateUI(null);
                                        }

                                        // ...
                                    }
                                });
                    }
                    break;
                }
        }
    }

    private void  updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        }

    }
}

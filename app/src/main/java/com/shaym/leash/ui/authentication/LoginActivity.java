package com.shaym.leash.ui.authentication;


import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.R;
import com.shaym.leash.ui.home.HomeActivity;


import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EMAIL = "email";
    private static final String TAG = "LoginActivity";
    RelativeLayout rellay1, rellay2;
    private FirebaseAuth mAuth;
    private Button mLoginButton;
    private Button mSingUpButton;
    private EditText mLoginEmailField;
    private EditText mLoginPassField;
    private CallbackManager mCallbackManager;
    private Handler mSplasHandler = new Handler();
    private LoginButton mFBLoginBtn;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);
        mLoginButton = (Button) findViewById(R.id.loginbtn);
        mSingUpButton = (Button) findViewById(R.id.signupb);
        mLoginEmailField = findViewById(R.id.loginemailfield);
        mLoginPassField = findViewById(R.id.loginpassfield);
        mFBLoginBtn = findViewById(R.id.login_button);
        mFBLoginBtn.setReadPermissions(Arrays.asList(EMAIL));

        mCallbackManager = CallbackManager.Factory.create();
        mFBLoginBtn.setReadPermissions("email", "public_profile");

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });


        mSingUpButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rellay1.setVisibility(View.VISIBLE);
                rellay2.setVisibility(View.VISIBLE);
            }
        };

        mSplasHandler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }
    


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginbtn:
                Log.d(TAG, "onClick: LoginBtn");
                String pass = mLoginPassField.getText().toString();
                String email = mLoginEmailField.getText().toString();

                if (pass.isEmpty() || email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email/Password is missing.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });

                }
                break;

            case R.id.signupb:
                Log.d(TAG, "onClick: SignUpBtn");
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                break;
        }
    }
}

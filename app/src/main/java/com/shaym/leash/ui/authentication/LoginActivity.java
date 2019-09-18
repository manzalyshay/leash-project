package com.shaym.leash.ui.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shaym.leash.R;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.home.HomeActivity;

import org.json.JSONException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.shaym.leash.ui.home.HomeActivity.FROM_UID_KEY;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, FacebookCallback<LoginResult> {

    private static final String TAG = "LoginActivity";
    public static final String GENDER_KEY = "GENDER_KEY";

    RelativeLayout rellay1, rellay2;
    private FirebaseAuth mAuth;
    private EditText mLoginEmailField;
    private EditText mLoginPassField;
    private CallbackManager mCallbackManager;
    private Handler mSplasHandler = new Handler();
    private AlertDialog mForgotPassDialog;
    private EditText mResetPassEmailInput;
    private ProgressBar mLoginProgressBar;
    private String mDisplayName;

    private String mFBProfilePic = "";
    private String mFromUID= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initUI();
        initFacebookLogin();

    }

    private void initUI() {

        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);
        Button mLoginButton = findViewById(R.id.loginbtn);
        Button mSingUpButton = findViewById(R.id.signupb);
        mLoginProgressBar = findViewById(R.id.loginprogressbar);
        Button mResetPassButton = findViewById(R.id.forgotpass_btn);
        mLoginEmailField = findViewById(R.id.loginemailfield);
        mLoginPassField = findViewById(R.id.loginpassfield);


        mSingUpButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mResetPassButton.setOnClickListener(this);

        Runnable runnable = () -> {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        };

        mSplasHandler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
// Add the buttons
        mResetPassEmailInput = new EditText(LoginActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mResetPassEmailInput.setLayoutParams(lp);
        builder.setView(mResetPassEmailInput); // uncomment this line
        builder.setTitle(R.string.reset_pass_dialog_title);
        builder.setMessage(R.string.reset_pass_dialog_message);

        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            // User clicked OK button



        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> mForgotPassDialog.dismiss());

// Create the AlertDialog
        mForgotPassDialog = builder.create();

    }

    private void initFacebookLogin() {
        LoginButton mFBLoginBtn = findViewById(R.id.login_button);
        mFBLoginBtn.setPermissions(Arrays.asList("email", "user_gender"));
        mCallbackManager = CallbackManager.Factory.create();

        mFBLoginBtn.registerCallback(mCallbackManager, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(mDisplayName).build();

                        assert user != null;

                        user.updateProfile(profileUpdate);
                        updateUI(user );
                    } else {
                        mLoginProgressBar.setVisibility(View.GONE);

                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(FirebaseUser currentUser) {
        if (getIntent().getExtras() != null) {
            mFromUID = getIntent().getExtras().getString(FROM_UID_KEY);
        }

        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            Bundle b = new Bundle();
            if (!mFBProfilePic.isEmpty()) {
                FireBaseUsersHelper.getInstance().createUserInDB(mFBProfilePic, "Male", currentUser);

            }

            if (mFromUID != null && !mFromUID.isEmpty()) {
                b.putString(FROM_UID_KEY, mFromUID);
            }
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }
    }
    


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginbtn:
                mLoginProgressBar.setVisibility(View.VISIBLE);

                Log.d(TAG, "onClick: LoginBtn");
                String pass = mLoginPassField.getText().toString();
                String email = mLoginEmailField.getText().toString();

                if (pass.isEmpty() || email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email/Password is missing.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email.trim(), pass)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    mLoginProgressBar.setVisibility(View.GONE);

                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }

                                // ...
                            });

                }
                break;

            case R.id.signupb:
                Log.d(TAG, "onClick: SignUpBtn");
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                break;

            case R.id.forgotpass_btn:
                Log.d(TAG, "onClick: ForgotPass");
                mForgotPassDialog.show();
                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                mForgotPassDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    if (isEmailValid(mResetPassEmailInput.getText().toString()))
                    {
                        mAuth.sendPasswordResetEmail(mResetPassEmailInput.getText().toString())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, R.string.pass_reset_email_sent_message, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, R.string.task_failed_message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                        mForgotPassDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(this, R.string.enter_valid_email_message, Toast.LENGTH_LONG).show();

                    }
                });
                break;
        }
    }

    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {
                    // Application code
                    try {
                        Log.i("Response",response.toString());

                        String firstName = response.getJSONObject().getString("first_name");
                        String lastName = response.getJSONObject().getString("last_name");

                        String userId = loginResult.getAccessToken().getUserId();

                        mFBProfilePic = "https://graph.facebook.com/" + userId+ "/picture?type=large";
                        mDisplayName = firstName + " " + lastName;


                        handleFacebookAccessToken(loginResult.getAccessToken());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,picture,email,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "facebook:onSuccess:" + loginResult);
        mLoginProgressBar.setVisibility(View.VISIBLE);
        setFacebookData(loginResult);
    }

    @Override
    public void onCancel() {
        Log.d(TAG, "facebook:onCancel");

    }

    @Override
    public void onError(FacebookException error) {
        Log.d(TAG, "facebook:onError", error);

    }
}

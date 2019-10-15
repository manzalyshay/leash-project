package com.shaym.leash.ui.authentication;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shaym.leash.R;
import com.shaym.leash.data.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.home.HomeActivity;
import com.shaym.leash.ui.utils.UIHelper;

import static com.shaym.leash.ui.authentication.LoginActivity.isEmailValid;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText mSignUpEmailField;
    private EditText mSignUpPassField;
    private EditText mSignUpNameField;
    private ImageView mLogo;
    private Spinner mGenderSpinner;
    private String mGender;


    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button mSignUpButton = findViewById(R.id.signupbtn);
        mSignUpButton.setOnClickListener(this);
        mSignUpEmailField = findViewById(R.id.signupemail);
        mSignUpPassField = findViewById(R.id.signuppass);
        mSignUpNameField = findViewById(R.id.signupname);
        mGenderSpinner = findViewById(R.id.genderSpinner);
        mLogo = findViewById(R.id.imgView_logo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signupbtn) {
            Log.d(TAG, "RegisterActivity: LoginBtn");

            String disname = mSignUpNameField.getText().toString();
            String email = mSignUpEmailField.getText().toString();
            String pass = mSignUpPassField.getText().toString();
            mGender = mGenderSpinner.getSelectedItem().toString();

            if (email.isEmpty() || pass.isEmpty() || disname.isEmpty()) {
                Toast.makeText(RegisterActivity.this, R.string.fields_missing,
                        Toast.LENGTH_SHORT).show();
            } else {

                if (pass.length() < 6) {
                    Toast.makeText(RegisterActivity.this, R.string.password_6_chars_message,
                            Toast.LENGTH_SHORT).show();
                }

                if (!isEmailValid(email)) {
                    Toast.makeText(RegisterActivity.this, R.string.enter_valid_email_message,
                            Toast.LENGTH_SHORT).show();
                } else {
                    UIHelper.getInstance().startSpinAnimation(mLogo);
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(mSignUpNameField.getText().toString()).build();
                                    assert user != null;
                                    user.updateProfile(profileUpdate).addOnCompleteListener(task1 -> updateUI(user));


                                } else {
                                    mLogo.clearAnimation();
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            });
                }
            }
        }
    }

    private void  updateUI(FirebaseUser user) {
        Log.d(TAG, "updateUI: ");
        FireBaseUsersHelper.getInstance().createUserInDB("", mGender, user);
        if (user != null) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }

    }
}

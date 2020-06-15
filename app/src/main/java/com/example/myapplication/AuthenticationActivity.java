package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class AuthenticationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "!!!";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        setTitle("Iniciar sesión");
        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


    }
        private void createAccount(String email, String password) {
            Log.d(TAG, "createAccount:" + email);
            if (!validateForm()) {
                return;
            }



            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                openMainActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // [START_EXCLUDE]

                            // [END_EXCLUDE]
                        }
                    });
            // [END create_user_with_email]
        }

        private void signIn(String email, String password) {
            Log.d(TAG, "signIn:" + email);
            if (!validateForm()) {
                return;
            }



            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                openMainActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // [START_EXCLUDE]
                            if (!task.isSuccessful()) {
                                mStatusTextView.setText(R.string.auth_failed);
                            }

                            // [END_EXCLUDE]
                        }
                    });
            // [END sign_in_with_email]
        }

        private void signOut() {
            mAuth.signOut();
            updateUI(null);
        }


        private boolean validateForm() {
            boolean valid = true;

            String email = mEmailField.getText().toString();
            if (TextUtils.isEmpty(email)) {
                mEmailField.setError("Required.");
                valid = false;
            } else {
                mEmailField.setError(null);
            }

            String password = mPasswordField.getText().toString();
            if (TextUtils.isEmpty(password)) {
                mPasswordField.setError("Required.");
                valid = false;
            } else {
                mPasswordField.setError(null);
            }

            return valid;
        }

        private void updateUI(FirebaseUser user) {
            //hideProgressDialog();
            if (user != null) {
                mStatusTextView.setText("mail: "+ user.getEmail() + "is validated: " + user.isEmailVerified());
                mDetailTextView.setText("userID"+ user.getUid());

                //findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
                findViewById(R.id.emailSignInButton).setVisibility(View.GONE);


                //findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
                findViewById(R.id.fieldEmail).setVisibility(View.GONE);
                findViewById(R.id.fieldPassword).setVisibility(View.GONE);


            } else {
                mStatusTextView.setText(R.string.signed_out);
                mDetailTextView.setText(null);

                //findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
                findViewById(R.id.emailSignInButton).setVisibility(View.VISIBLE);


                //findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
                findViewById(R.id.fieldEmail).setVisibility(View.VISIBLE);
                findViewById(R.id.fieldPassword).setVisibility(View.VISIBLE);


            }
        }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.emailSignInButton) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        }

        private void openMainActivity (){
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
            finish();
         }
    }





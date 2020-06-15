package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Actividad en la que un usuario se puede logear.
 */
public class AuthenticationActivity extends AppCompatActivity {

    private AppAuthManager appAuthManager;
    private EditText mEmailField;
    private EditText mPasswordField;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appAuthManager = new AppAuthManager();
        setContentView(R.layout.activity_authentication);
        setTitle("Iniciar sesión");
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        findViewById(R.id.emailSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            }
        });
    }

    private void signIn(String email, String password) {
        appAuthManager.singIn(email, password, new AppAuthManager.SignInListener() {
            @Override
            public void onFinish(boolean success) {
                if (success) {
                    openMainActivity();
                } else {
                    Toast.makeText(AuthenticationActivity.this, "SIGN IN ERROR", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openMainActivity() {
        MainActivity.startActivity(this);
        finish();
    }
}





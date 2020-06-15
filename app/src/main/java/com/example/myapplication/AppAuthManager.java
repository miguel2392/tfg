package com.example.myapplication;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Esta clase implementa la lógica de autentificación de ususario que se necesita en toda la
 * aplicación.
 */
public class AppAuthManager {

    public String getUserId() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

    public void singIn(String email, String password, final SignInListener listener) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        listener.onFinish(task.isSuccessful());
                    }
                });

    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    interface SignInListener {
        void onFinish(boolean success);
    }
}

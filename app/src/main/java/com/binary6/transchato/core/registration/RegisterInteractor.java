package com.binary6.transchato.core.registration;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.binary6.transchato.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterInteractor implements RegisterContract.Interactor {
    private static final String TAG = RegisterInteractor.class.getSimpleName();

    private RegisterContract.OnRegistrationListener mOnRegistrationListener;

    public RegisterInteractor(RegisterContract.OnRegistrationListener onRegistrationListener) {
        this.mOnRegistrationListener = onRegistrationListener;
    }

    @Override
    public void performFirebaseRegistration(Activity activity, final String email, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "performFirebaseRegistration:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mOnRegistrationListener.onFailure(task.getException().getMessage());
                        } else {
                            // Add the user to users table.
                            DatabaseReference database= FirebaseDatabase.getInstance().getReference();
                            Log.e("sdfsd",task.getResult().getUser().getDisplayName()+"");
                            User user = new User(task.getResult().getUser().getUid(), email,task.getResult().getUser().getDisplayName());
                            database.child("users").push().setValue(user);

                            mOnRegistrationListener.onSuccess(task.getResult().getUser());
                        }
                    }
                });


    }
}

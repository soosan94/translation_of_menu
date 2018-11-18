package com.example.owner.real_final.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Owner on 2018-03-23.
 */

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String uid,id,password;

    EditText et_id,et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button ok = (Button) findViewById(R.id.okButton);
        Button cancel = (Button) findViewById(R.id.cancelButton);

        et_id = findViewById(R.id.idInputSignUp);
        et_password = findViewById(R.id.passwordInputSignUp);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리
                signOut();
                id = et_id.getText().toString();
                password = et_password.getText().toString();
                createAccount(id,password);


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                setResult(0, addIntent);
                finish();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void signOut() {
        mAuth.signOut();
        //updateUI(null);
    }

    private void createAccount(String email,String password){
        if (!validateForm()) {
            return;
        }
        if(!isValidEmail(id)){
            Toast.makeText(SignUpActivity.this, "이메일 양식에 맞지 않습니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = task.getResult().getUser();
                            uid = user.getUid();
                            writeNewUser(uid,id);
                            Intent addIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                            addIntent.putExtra("isSignUp","true");
                            startActivity(addIntent);
                            //updateUI(user);
                        }
                        if(!task.isSuccessful()) {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "이미 존재하는 이메일 입니다.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                Snackbar.make(getWindow().getDecorView().getRootView(), "Success SignUp!", Snackbar.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void writeNewUser(String userId, String email) {
        User user = new User( email);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("member").child(userId).setValue(user);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = et_id.getText().toString();
        if (TextUtils.isEmpty(email)) {
            et_id.setError("Required.");
            valid = false;
        } else {
            et_id.setError(null);
        }

        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Required.");
            valid = false;
        } else {
            et_password.setError(null);
        }

        return valid;
    }

    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}


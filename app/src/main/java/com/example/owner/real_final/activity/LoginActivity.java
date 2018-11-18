package com.example.owner.real_final.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.FirebaseDB;
import com.example.owner.real_final.database.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    Button signup;
    Button login;

    FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    Toolbar toolbar;
    String uid,id,password;

    EditText et_id,et_password;
    String TAG="";

    String isSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        isSignUp = (String) getIntent().getStringExtra("isSignUp") ;
        et_id = findViewById(R.id.idInput);
        et_password = findViewById(R.id.passwordInput);

        signup = (Button) findViewById(R.id.signupButton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = et_id.getText().toString().trim();
                password = et_password.getText().toString().trim();
                //createAccount(id,password);
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        login = (Button) findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 처리 + DB
                id = et_id.getText().toString().trim();
                password = et_password.getText().toString().trim();

                signIn(id,password);

                //FirebaseDB firebaseDB = new FirebaseDB();
                //firebaseDB.addTravelList();
            }
        });
        if(mAuth.getCurrentUser()!=null && isSignUp ==null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        //mAuth.addAuthStateListener(mAuthListener);
        //updateUI(mAuth.getCurrentUser());
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

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id",id);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "잘못된 email/비밀번호 입니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if(!task.isComplete()){
                            Toast.makeText(LoginActivity.this, "잘못된 email/비밀번호 입니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END sign_in_with_email]
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

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            findViewById(R.id.loginButton).setEnabled(false);
            //findViewById(R.id.passwordInput).setVisibility(View.GONE);
            //findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

            //findViewById(R.id.loginButton).setEnabled(!user.isEmailVerified());
        } else {
            findViewById(R.id.loginButton).setEnabled(true);
            //findViewById(R.id.passwordInput).setVisibility(View.VISIBLE);
            //findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }
}

package com.example.owner.real_final.database;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.owner.real_final.activity.LoginActivity;
import com.example.owner.real_final.activity.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseDB {
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String uid;
    //사용자 uid가져오기
    public FirebaseDB(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid=currentUser.getUid();
    }

    //날짜별 레스토랑 메뉴 목록 추가,삭제,업데이트

    //로그아웃
    public void signOut() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.signOut();
        //updateUI(null);
    }

}

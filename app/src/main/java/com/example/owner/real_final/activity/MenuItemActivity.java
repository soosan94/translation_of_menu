package com.example.owner.real_final.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.MenuData;
import com.example.owner.real_final.database.RestaurantData;
import com.example.owner.real_final.other.TaskforMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class MenuItemActivity  extends AppCompatActivity{

    private Toolbar toolbar;

    EditText et_item;
    EditText et_itemprice;
    EditText et_itemamount;
    TextView submit_item;
    TextView cancel_item;

    private SimpleDateFormat dateFormatter;

    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;
    RestaurantData rd;
    MenuData md;

    String travels_key, restaurants_key;
    RestaurantData restaurants;
    MenuData menus;
    String menu_name,menu_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuitem);

        toolbar = (Toolbar)findViewById(R.id.menu_toolbar);
        setSupportActionBar(toolbar);

        et_item = findViewById(R.id.et_item);
        et_itemprice = findViewById(R.id.et_itemprice);
        et_itemamount = findViewById(R.id.et_itemamount);

        submit_item = findViewById(R.id.submit_item);
        cancel_item = findViewById(R.id.cancel_item);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid=currentUser.getUid();

        final String travels = (String ) getIntent().getStringExtra("tdata");
        travels_key = travels;

        restaurants_key = (String) getIntent().getStringExtra("rdata");
        //restaurants_key = restaurants;


        menus = (MenuData) getIntent().getSerializableExtra("mdata");
        menu_name = (String) getIntent().getStringExtra("menu_name");
        menu_money = (String) getIntent().getStringExtra("menu_money");

        //dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        setToolbarTitle();

        if (menus != null) {
            et_item.setText(menus.getmName());
            et_itemprice.setText(menus.getmPrice());
            et_itemamount.setText(menus.getmAmount());

            submit_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menus.setmName(et_item.getText().toString());
                    menus.setmPrice(et_itemprice.getText().toString());
                    menus.setmAmount(et_itemamount.getText().toString());
                    updateMenuData(menus);
                    Intent submitIntent = new Intent(MenuItemActivity.this, MenuListActivity.class);
                    //submitIntent.putExtra("dayCount", diffDays);
                    startActivity(submitIntent);
                    finish();
                }
            });
            cancel_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else { //restaurants = null
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            SimpleDateFormat CurDateFormat = new SimpleDateFormat("MM / dd");

            submit_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmpty(et_item.getText().toString()) )
                        submitMenuData(md,restaurants_key);
                    else
                        Snackbar.make(findViewById(R.id.submit_item), "다시 입력해주세요!", Snackbar.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            et_item.getWindowToken(), 0);
                }
            });
            cancel_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Add Menu Item");
    }

    private void submitMenuData(MenuData menus,String restaurants_key) {
        String key = database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(restaurants_key).child("menus").push().getKey();
        menus = new MenuData(key, et_item.getText().toString(),et_itemprice.getText().toString(),
                et_itemamount.getText().toString());
        //key 인수안에 없음
        menus.setmKey(key);

        database.child("member").child(uid).child("travels").child(travels_key)
                .child("restaurants").child(restaurants_key).child("menus").child(key).setValue(menus).addOnSuccessListener(
                        this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(findViewById(R.id.submit_item), "submit", Snackbar.LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void updateMenuData(MenuData menus) {
        database.child("member").child(uid).child("travels").child(travels_key).child("restaurants")
                .child(restaurants_key).child("menus")
                .setValue(menus)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.submit_item), "update.", Snackbar.LENGTH_LONG).show();
                    }
                });
        finish();
    }

}

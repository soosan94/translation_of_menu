package com.example.owner.real_final.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.RestaurantData;
import com.example.owner.real_final.database.TravelData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/******레스토랑, 통화, 메뉴 선택하는 Activity******/
public class MenuFormActivity extends AppCompatActivity{
    private Toolbar toolbar;

    EditText et_restaurant;
    Spinner tv_rNation;
    TextView tv_today;
    TextView tv_rbudget;
    ListView menuListview;
    TextView submit_menu;
    TextView cancel_menu;

    private SimpleDateFormat dateFormatter;

    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;

    RestaurantData rd;
    static int selected;

    TravelData travels;
    String travels_key;
//    RestaurantData restaurants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuform);

        toolbar = (Toolbar)findViewById(R.id.menu_toolbar);
        setSupportActionBar(toolbar);

        et_restaurant = findViewById(R.id.et_restaurant);
        tv_rbudget = findViewById(R.id.tv_rbudget);
        tv_rNation = findViewById(R.id.tv_rNation);
        tv_rNation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        tv_today = findViewById(R.id.tv_today);
        //menuListview = findViewById(R.id.menuListview);
        submit_menu = findViewById(R.id.submit_menu);
        cancel_menu = findViewById(R.id.cancel_menu);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid=currentUser.getUid();

        travels = (TravelData) getIntent().getSerializableExtra("tdata");
        travels_key = travels.getKey();

        final RestaurantData restaurants = (RestaurantData) getIntent().getSerializableExtra("data");

        //tv_rbudget.setText(travels);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        setToolbarTitle();
        if (restaurants != null) {
            et_restaurant.setText(restaurants.getrName());
            tv_rNation.setSelection(selected);
            tv_today.setText(restaurants.getrDate());
            tv_rbudget.setText(restaurants.getrBudget());

            submit_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    restaurants.setrName(et_restaurant.getText().toString());
                    restaurants.setrDate(tv_today.getText().toString());
                    //restaurants.setrNation(tv_rNation.getSelectedItem().toString());
                    restaurants.setrBudget(tv_rbudget.getText().toString());

                    updateRestaurantData(restaurants);
                    Intent submitIntent = new Intent(MenuFormActivity.this, DayListActivity.class);
                    submitIntent.putExtra("data",travels);
                    startActivity(submitIntent);
                    //finish();
                }
            });
            cancel_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else { //restaurants = null
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat CurDateFormat = new SimpleDateFormat("MM / dd");
            tv_today.setText(CurDateFormat.format(date));
            submit_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmpty(et_restaurant.getText().toString()) && !isEmpty(tv_rNation.getItemAtPosition(selected).toString()) && !isEmpty(tv_today.getText().toString()) && !isEmpty(tv_rbudget.getText().toString()))
                        submitTravelData(rd, travels_key);
                    else
                        Snackbar.make(findViewById(R.id.submit_menu), "다시 입력해주세요!", Snackbar.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            et_restaurant.getWindowToken(), 0);
                }
            });
            cancel_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Add Restaurant");
    }

    private void submitTravelData(RestaurantData restaurants, String t_key) {
        String key = database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").push().getKey();
        restaurants = new RestaurantData(key,et_restaurant.getText().toString(),tv_today.getText().toString(),
                tv_rNation.getSelectedItem().toString(),tv_rbudget.getText().toString());
        restaurants.setrKey(key);

        database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(key).setValue(restaurants).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(findViewById(R.id.submit_menu), "submit", Snackbar.LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void updateRestaurantData(RestaurantData restaurants) {
        database.child("member").child(uid).child("travels").child(travels_key).child("restaurants")
                .child(restaurants.getrKey())
                .setValue(restaurants)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.submit_menu), "update.", Snackbar.LENGTH_LONG).show();
                    }
                });
        finish();
    }


    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }


}

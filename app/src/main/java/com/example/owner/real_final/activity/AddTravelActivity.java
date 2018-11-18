package com.example.owner.real_final.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.TravelData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTravelActivity extends AppCompatActivity {

    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;

    private TextView submitTv, cancelTv;
    private EditText tv_nation, et_budget;
    private TextView tv_min, tv_max;
    private Spinner tv_rNation2;
    final int DATE_MIN = 1, DATE_MAX = 2;
    String minDay, maxDay;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog datePickerDialog;

    TravelData tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtravel);

        tv_nation = (EditText) findViewById(R.id.tv_nation);
        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_max = (TextView) findViewById(R.id.tv_max);
        et_budget = (EditText) findViewById(R.id.et_budget);
        submitTv = (TextView) findViewById(R.id.submitTv);
        cancelTv = (TextView) findViewById(R.id.cancelTv);
        tv_rNation2 = findViewById(R.id.tv_rNation2);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        setToolbarTitle();

        final TravelData travels = (TravelData) getIntent().getSerializableExtra("data");
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        if (travels != null) {
            tv_nation.setText(travels.getNation());
            tv_min.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDateDialog(DATE_MIN);
                    tv_min.setText(travels.getMinDay());
                }
            });
            tv_min.setText(travels.getMinDay());
            tv_max.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDateDialog(DATE_MAX);
                    tv_max.setText(travels.getMaxDay());
                }
            });
            tv_max.setText(travels.getMaxDay());
            et_budget.setText(travels.getBudget());

            submitTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    travels.setNation(tv_nation.getText().toString());
                    travels.setMinDay(tv_min.getText().toString());
                    travels.setMaxDay(tv_max.getText().toString());
                    travels.setBudget(et_budget.getText().toString());
                    travels.setExpense("0");

                    updateTravelData(travels);
                    Intent submitIntent = new Intent(AddTravelActivity.this, MainActivity.class);
                    startActivity(submitIntent);
                    finish();
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else { //travels = null
            tv_min.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDateDialog(DATE_MIN);
                }
            });
            tv_max.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDateDialog(DATE_MAX);
                }
            });
            submitTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmpty(tv_nation.getText().toString()) && !isEmpty(tv_min.getText().toString()) && !isEmpty(tv_max.getText().toString()) && !isEmpty(et_budget.getText().toString()))
                        submitTravelData(tv);
                    else
                        Snackbar.make(findViewById(R.id.submitTv), "다시 입력해주세요!", Snackbar.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            tv_nation.getWindowToken(), 0);
                }
            });
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private void submitTravelData(TravelData travels) {
        String key = database.child("member").child(uid).child("travels").push().getKey();
        travels = new TravelData(key, tv_nation.getText().toString(), tv_min.getText().toString(),
                tv_max.getText().toString(), et_budget.getText().toString(),"0",tv_rNation2.getSelectedItem().toString());
        travels.setKey(key);

        database.child("member").child(uid).child("travels").child(key).setValue(travels).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(findViewById(R.id.submitTv), "submit", Snackbar.LENGTH_LONG).show();
            }
        });
        finish();
    }

    private void updateTravelData(TravelData travels) {
        database.child("member").child(uid).child("travels")
                .child(travels.getKey())
                .setValue(travels)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(findViewById(R.id.submitTv), "update.", Snackbar.LENGTH_LONG).show();
                    }
                });
        finish();
    }

    private boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Add Your Travel");
    }


    private void showDateDialog(int id) {
        Calendar newCalendar = Calendar.getInstance();
        switch (id) {
            case DATE_MIN:
                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        minDay = dateFormatter.format(newDate.getTime());
                        tv_min.setText(minDay);
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case DATE_MAX:
                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        maxDay = dateFormatter.format(newDate.getTime());
                        tv_max.setText(maxDay);
                    }
                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }
    }
}
package com.example.owner.real_final.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.FirebaseDB;
import com.example.owner.real_final.database.RestaurantData;
import com.example.owner.real_final.database.TravelData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class DayListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    ListView daylv;
    MyAdapter mMyAdapter;

    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;

    String travels_key;
    double total=0;
    TravelData travels;

    int p;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daylist);

        daylv = (ListView) findViewById(R.id.daylv);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DB
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        travels = (TravelData) getIntent().getSerializableExtra("data");


        //tv_TravelData.setText(travels.getKey());

        setToolbarTitle();

        travels_key = travels.getKey();

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMyAdapter = new MyAdapter();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    total =0;
                    String key=noteDataSnapshot.getKey();
                    RestaurantData comment = dataSnapshot.child(key).getValue(RestaurantData.class);
                    for(DataSnapshot menus : dataSnapshot.child(key).child("menus").getChildren()){
                        String menu_price = menus.child("mPrice").getValue(String.class);
                        String menu_amount = menus.child("mAmount").getValue(String.class);
                        total += Double.parseDouble(menu_price) * Double.parseDouble(menu_amount);
                    }
                    database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(key).child("rBudget").setValue(String.valueOf(total));
                    mMyAdapter.addItem(comment.getrKey(),comment.getrName(),comment.getrDate(),comment.getrNation(),String.valueOf(total));

                }
                daylv.setAdapter(mMyAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").addValueEventListener(valueEventListener);
    }


    //toolbar에 붙는 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.menu_daylist, menu);
        return true;
    }


    //toolbar에 붙는 메뉴마다 activity 붙이기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //menuForm 추가
        if (id == R.id.restaurant_add) {
            //Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            Intent addIntent = new Intent(DayListActivity.this, MenuFormActivity.class);
            addIntent.putExtra("tdata", (Serializable)travels);
            //addIntent.putExtra("rdata", (Serializable) mMyAdapter.getItem(pos));
            startActivity(addIntent);
            return true;
        }
        //menu 삭제
        /*if (id == R.id.menu_list) {
            Intent addIntent = new Intent(MenuListActivity.this, CropImageActivity.class);
            //addIntent.putExtra("tdata", travels_key);
            //addIntent.putExtra("rdata", (Serializable) mMyAdapter.getItem(pos));
            startActivity(addIntent);
            return true;


        }*/
        return true;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Restaurant List");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    public class MyAdapter extends BaseAdapter {

        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<RestaurantData> rItems = new ArrayList<>();

        @Override
        public int getCount() {
            return rItems.size();
        }

        @Override
        public RestaurantData getItem(int position) {
            return rItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_restaurant, parent, false);
            }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
            //ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
            final LinearLayout itemtravelContainer = (LinearLayout) convertView.findViewById(R.id.itemtravelContainer);
            TextView tv_Name = (TextView) convertView.findViewById(R.id.rName);
            TextView tv_Date = (TextView) convertView.findViewById(R.id.rDate);
            TextView tv_Nation = (TextView) convertView.findViewById(R.id.rNation);
            TextView tv_Budget = (TextView) convertView.findViewById(R.id.rBudget);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
            RestaurantData myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            //iv_img.setImageDrawable(myItem.getIcon());
            tv_Name.setText(myItem.getrName());
            tv_Date.setText(myItem.getrDate());
            tv_Nation.setText(myItem.getrNation());
            tv_Budget.setText(myItem.getrBudget());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
            itemtravelContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, pos + "번째 이미지 선택", Toast.LENGTH_SHORT).show();
                    Intent detailIntent = new Intent(DayListActivity.this, MenuListActivity.class);
                    detailIntent.putExtra("tdata", travels_key);
                    detailIntent.putExtra("rdata", mMyAdapter.getItem(pos).getrKey());
                    context.startActivity(detailIntent);
                }
            });

            itemtravelContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //길게 눌렀을 때 해당 여행 수정, 삭제
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_menu);
                    dialog.show();

                    Button editButton = (Button) dialog.findViewById(R.id.bt_edit_data);
                    Button delButton = (Button) dialog.findViewById(R.id.bt_delete_data);
                    /*수정*/
                    editButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Intent editIntent = new Intent(DayListActivity.this, MenuFormActivity.class);
                                    editIntent.putExtra("tdata", (Serializable) travels);
                                    editIntent.putExtra("data", (Serializable) mMyAdapter.getItem(pos));
                                    context.startActivity(editIntent);
                                }
                            }
                    );
                    /*삭제*/
                    delButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    //Toast.makeText(context, pos + "번째 이미지 선택", Toast.LENGTH_SHORT).show();
                                    database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(rItems.get(pos).getrKey()).removeValue();
                                }
                            }
                    );
                    return true;
                }
            });


            return convertView;
        }

        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem(String key,String name, String date, String nation, String budget) {

            RestaurantData rItem = new RestaurantData();

        /* MyItem에 아이템을 setting한다. */
            rItem.setrKey(key);
            rItem.setrName(name);
            rItem.setrDate(date);
            rItem.setrNation(nation);
            rItem.setrBudget(budget);

        /* mItems에 MyItem을 추가한다. */
            rItems.add(rItem);

        }
    }
}


package com.example.owner.real_final.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.MenuData;
import com.example.owner.real_final.database.TravelData;
import com.example.owner.real_final.other.TaskforMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuListActivity extends AppCompatActivity{

    private Toolbar toolbar;
    ListView menulistview;
    MyAdapter mMyAdapter;
    TextView tv_menutotal;
    static TextView tv_toKRW;

    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;
    String travels_key;
    String restaurants_key;
    static double total=0;
    double specificCurrent=0;
    static String current;

    public  static List<String> nations = new ArrayList<String>();
    public  static List<String> price = new ArrayList<String>();
    TaskforMenu task = new TaskforMenu();
    static double exchange;
    static String test = "";

    /**********restaurants_key 부분, */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);

        menulistview = (ListView) findViewById(R.id.menulistview);
        tv_menutotal = (TextView)findViewById(R.id.tv_menutotal);
        tv_toKRW = findViewById(R.id.tv_toKRW);

        toolbar = (Toolbar) findViewById(R.id.menuList_toolbar);
        setSupportActionBar(toolbar);

        //DB
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        final TravelData travels = (TravelData) getIntent().getSerializableExtra("data");

        setToolbarTitle();
        travels_key = (String) getIntent().getStringExtra("tdata");
        restaurants_key = (String) getIntent().getStringExtra("rdata");

        task.execute();

        database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(restaurants_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current = dataSnapshot.child("rNation").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

      /* int count = nations.size();
        for(int i =0; i < count; i++){
            //test += nations.get(i) +"\n";
            test += i +"  ";
            if(nations.get(i).toString().equals(current)){
                exchange = Double.parseDouble(price.get(i));
                test += "success";
                break;
            }
        }*/


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total =0;
                mMyAdapter = new MenuListActivity.MyAdapter();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String key=noteDataSnapshot.getKey();
                    MenuData comment = dataSnapshot.child(key).getValue(MenuData.class);
                    mMyAdapter.addItem(comment.getmKey(),comment.getmName(),comment.getmPrice(),comment.getmAmount());
                    String price =comment.getmPrice();
                    String amount = comment.getmAmount();
                    total += Double.parseDouble(price) * Double.parseDouble(amount);

                }

                tv_menutotal.setText(""+total);
                tv_toKRW.setText(""+(total*exchange)+"\n"+test);
                menulistview.setAdapter(mMyAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        /////여기 확인!!
        database.child("member").child(uid).child("travels").child(travels_key).child("restaurants").child(restaurants_key).child("menus").addValueEventListener(valueEventListener);
    }

    public static void calculate_exchange(){
        int count = nations.size();
        for(int i =0; i < count; i++){
            if(nations.get(i).toString().equals(current)){
                exchange = Double.parseDouble(price.get(i));
                break;
            }
        }
        tv_toKRW.setText(""+(total*exchange)+"\n"+test);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.cancel(true);
        nations.clear();
        price.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        task.cancel(true);
        nations.clear();
        price.clear();
    }

    //toolbar에 붙는 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.menu_menulist, menu);
        return true;
    }


    //toolbar에 붙는 메뉴마다 activity 붙이기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //menuForm 추가
        if (id == R.id.menu_add) { //*/activity_menuitem로 넘어가도록*/
            Intent addIntent = new Intent(MenuListActivity.this, MenuItemActivity.class);
            addIntent.putExtra("tdata", travels_key);
            addIntent.putExtra("rdata", restaurants_key);
            //addIntent.putExtra("mdata",(Serializable)mMyAdapter.getItem(pos));
            startActivity(addIntent);
            return true;
        }
        if (id == R.id.menu_list) {
                Intent addIntent = new Intent(MenuListActivity.this, CropImageActivity.class);
                addIntent.putExtra("tdata", travels_key);
                addIntent.putExtra("rdata", restaurants_key);
                startActivity(addIntent);
            return true;
        }
        return true;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Menu List");
    }

    public class MyAdapter extends BaseAdapter {

        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<MenuData> mItems = new ArrayList<>();

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public MenuData getItem(int position) {
            return mItems.get(position);
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
                convertView = inflater.inflate(R.layout.item_menulist, parent, false);
            }

            /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
            //ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
            final LinearLayout itemmenuContainer = (LinearLayout) convertView.findViewById(R.id.itemmenuContainer);
            TextView tv_menuName = (TextView) convertView.findViewById(R.id.menuName);
            TextView tv_menuPrice = (TextView) convertView.findViewById(R.id.menuPrice);
            TextView tv_menuAmount= (TextView) convertView.findViewById(R.id.menuCount);


            /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
            MenuData myItem = getItem(position);

            /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            //iv_img.setImageDrawable(myItem.getIcon());
            tv_menuName.setText(myItem.getmName());
            tv_menuPrice.setText(myItem.getmPrice());
            tv_menuAmount.setText(myItem.getmAmount());

            /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
            itemmenuContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, pos + "번째 이미지 선택", Toast.LENGTH_SHORT).show();

                }
            });

            itemmenuContainer.setOnLongClickListener(new View.OnLongClickListener() {
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
                                    /*Intent editIntent = new Intent(DayListActivity.this, AddTravelActivity.class);
                                    editIntent.putExtra("data", (Serializable) mMyAdapter.getItem(pos));
                                    context.startActivity(editIntent);*/
                                }
                            }
                    );
                    /*삭제*/
                    delButton.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    database.child("member").child(uid).child("travels").child(travels_key).child("restaurants")
                                            .child(restaurants_key).child("menus").child(mItems.get(pos).getmKey()).removeValue();
                                }
                            }
                    );
                    return true;
                }
            });


            return convertView;
        }

        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem(String key,String name, String price, String amount) {

            MenuData mItem = new MenuData();

            /* MyItem에 아이템을 setting한다. */
            mItem.setmKey(key);
            mItem.setmName(name);
            mItem.setmPrice(price);
            mItem.setmAmount(amount);


            /* mItems에 MyItem을 추가한다. */
            mItems.add(mItem);

        }
    }

}

package com.example.owner.real_final.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.owner.real_final.R;
import com.example.owner.real_final.database.FirebaseDB;
import com.example.owner.real_final.database.TravelData;
import com.example.owner.real_final.fragment.HomeFragment;
import com.example.owner.real_final.fragment.MoviesFragment;
import com.example.owner.real_final.fragment.NotificationsFragment;
import com.example.owner.real_final.fragment.PhotosFragment;
import com.example.owner.real_final.fragment.SettingsFragment;
import com.example.owner.real_final.other.CircleTransform;
import com.example.owner.real_final.other.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    private static ListView mListView;
    MyAdapter mMyAdapter;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "R.drawable.ic_launcher_background ";//https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    //DB variable
    private DatabaseReference database;
    FirebaseAuth mAuth;
    String uid;

    String id;

    static final String[] LIST_MENU = {"LIST1", "LIST2", "LIST3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = new Intent(this.getIntent());
        id = intent.getStringExtra("id");

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        mListView = (ListView) findViewById(R.id.lv_Travel);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        //DB
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        mMyAdapter = new MyAdapter();

        /* 리스트뷰에 어댑터 등록 */
        mListView.setAdapter(mMyAdapter);
        registerForContextMenu(mListView);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMyAdapter = new MyAdapter();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String key=noteDataSnapshot.getKey();
                    TravelData comment = dataSnapshot.child(key).getValue(TravelData.class);
                    String expense = "0";
                    for(DataSnapshot restaurant : noteDataSnapshot.child("restaurants").getChildren()){
                         expense = "" + (Double.parseDouble(expense) + Double.parseDouble(restaurant.child("rBudget").getValue().toString()));
                    }
                    database.child("member").child(uid).child("travels").child(key).child("expense").setValue(expense);
                    mMyAdapter.addItem(comment.getKey(),comment.getNation(),comment.getMinDay(),comment.getMaxDay(),comment.getBudget() ,expense,comment.getUnit());

                }
                mListView.setAdapter(mMyAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("member").child(uid).child("travels").addValueEventListener(valueEventListener);
    }

    /***
     * Load navigation menu header information like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name(id), website
        txtName.setText(id);
        txtWebsite.setText("id");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                PhotosFragment photosFragment = new PhotosFragment();
                return photosFragment;
            case 2:
                // movies fragment
                MoviesFragment moviesFragment = new MoviesFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            FirebaseDB firebaseDB = new FirebaseDB();
            firebaseDB.signOut();
            Intent addIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(addIntent);
            return true;
        }

        if (id == R.id.action_addItem) {
            Intent addIntent = new Intent(MainActivity.this, AddTravelActivity.class);
            startActivity(addIntent);
        }



        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    public class MyAdapter extends BaseAdapter {

        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<TravelData> tItems = new ArrayList<>();

        @Override
        public int getCount() {
            return tItems.size();
        }

        @Override
        public TravelData getItem(int position) {
            return tItems.get(position);
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
                convertView = inflater.inflate(R.layout.item_travel, parent, false);
            }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
            //ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
            final LinearLayout itemtravelContainer = (LinearLayout) convertView.findViewById(R.id.itemtravelContainer);
            TextView tv_Nation = (TextView) convertView.findViewById(R.id.tv_Nation);
            TextView tv_Minday = (TextView) convertView.findViewById(R.id.tv_Minday);
            TextView tv_Maxday = (TextView) convertView.findViewById(R.id.tv_Maxday);
            TextView tv_Budget = (TextView) convertView.findViewById(R.id.tv_Budget);
            TextView tv_Expense = (TextView) convertView.findViewById(R.id.tv_Expense);
            TextView tv_Unit = convertView.findViewById(R.id.tv_Unit);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
            TravelData myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            //iv_img.setImageDrawable(myItem.getIcon());
            tv_Nation.setText(myItem.getNation());
            tv_Minday.setText(myItem.getMinDay());
            tv_Maxday.setText(myItem.getMaxDay());
            tv_Budget.setText(myItem.getBudget());
            tv_Expense.setText(myItem.getExpense());
            tv_Unit.setText(myItem.getUnit());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
            itemtravelContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, pos + "번째 이미지 선택", Toast.LENGTH_SHORT).show();
                    Intent detailIntent = new Intent(MainActivity.this, DayListActivity.class);
                    detailIntent.putExtra("data", (Serializable) mMyAdapter.getItem(pos));
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
                                    Intent editIntent = new Intent(MainActivity.this, AddTravelActivity.class);
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
                                    database.child("member").child(uid).child("travels").child(tItems.get(pos).getKey()).removeValue();
                                }
                            }
                    );
                    return true;
                }
            });


            return convertView;
        }

        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem(String key,String nation, String minday, String maxday, String budget, String expense, String unit) {

            TravelData tItem = new TravelData();

        /* MyItem에 아이템을 setting한다. */
            tItem.setKey(key);
            tItem.setNation(nation);
            tItem.setMinDay(minday);
            tItem.setMaxDay(maxday);
            tItem.setBudget(budget);
            tItem.setExpense(expense);
            tItem.setUnit(unit);

        /* mItems에 MyItem을 추가한다. */
            tItems.add(tItem);

        }
    }

}
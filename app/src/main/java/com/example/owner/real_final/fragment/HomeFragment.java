package com.example.owner.real_final.fragment;


import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.owner.real_final.R;
import com.example.owner.real_final.activity.AddTravelActivity;
import com.example.owner.real_final.activity.MainActivity;
import com.example.owner.real_final.database.TravelData;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private OnFragmentInteractionListener mListener;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
/*

        View view = inflater.inflate(R.layout.fragment_home,container,false);
        //ArrayList<String> mList;
        //mList = new ArrayList<String>();


        ArrayList<TravelData> travelData = new ArrayList<TravelData>();
        travelData.add(new TravelData("스페인","2018-03-16","2018-03-20","1000000"));
        MyAdapter mMyAdapter = new MyAdapter(getActivity(),travelData);
        //mMyAdapter.addItem("스페인","2018-03-16","2018-03-20","1000000");
        listView = (ListView)view.findViewById(R.id.lv_Travel);
        //ArrayAdapter mAdapter = new ArrayAdapter(getActivity(),R.layout.item_test,mList);
        listView.setAdapter(mMyAdapter);
        //mList.add("test1");
        //mList.add("test2");

        //mMyAdapter.notifyDataSetChanged();
        // Inflate the layout for this fragment*/
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    ////////////////////////////////////////////////////

    public static class MyAdapter extends BaseAdapter {

        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<TravelData> tItems = new ArrayList<>();

        LayoutInflater inflater;



        public MyAdapter(Context context, ArrayList<TravelData> tItems) {
            //  mContext = context;
            this.tItems = tItems;

            inflater = LayoutInflater.from(context);

        }


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
        public View getView(int position, View convertView, ViewGroup parent) {

            Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item_travel, parent, false);
            }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
            //ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
            TextView tv_Nation = (TextView) convertView.findViewById(R.id.tv_Nation) ;
            TextView tv_Minday = (TextView) convertView.findViewById(R.id.tv_Minday) ;
            TextView tv_Maxday = (TextView) convertView.findViewById(R.id.tv_Maxday);
            TextView tv_Budget = (TextView) convertView.findViewById(R.id.tv_Budget);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
            TravelData myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            //iv_img.setImageDrawable(myItem.getIcon());
            tv_Nation.setText(myItem.getNation());
            tv_Minday.setText(myItem.getMinDay());
            tv_Maxday.setText(myItem.getMaxDay());
            tv_Budget.setText(myItem.getBudget());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


            return convertView;
        }

        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem( String nation, String minday, String maxday, String budget) {

            TravelData tItem = new TravelData();

        /* MyItem에 아이템을 setting한다. */
            tItem.setNation(nation);
            tItem.setMinDay(minday);
            tItem.setMaxDay(maxday);
            tItem.setBudget(budget);

        /* mItems에 MyItem을 추가한다. */
            tItems.add(tItem);

        }
    }
}
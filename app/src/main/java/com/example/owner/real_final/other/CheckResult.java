package com.example.owner.real_final.other;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.owner.real_final.R;
import com.example.owner.real_final.activity.MenuFormActivity;
import com.example.owner.real_final.activity.MenuItemActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by vns on 2018-03-29.
 */

public class CheckResult extends AppCompatActivity {
    TextView result, translation, parsing;
    String name, money;
    int count = 0;
    String []temp;
    String console;
    String menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translation_result);

        result = (TextView) findViewById(R.id.result);
        translation = (TextView) findViewById(R.id.translation);
        parsing = (TextView) findViewById(R.id.parsing);
        NaverTranslateTask asyncTask = new NaverTranslateTask();

        Intent intent = getIntent();
        String[] ocr = intent.getStringArrayExtra("streams");
        for(int i=0; i< ocr.length; ++i){
            //result.append(ocr[i] + ","+ ocr[i].length() +"\n//\n");
            if(ocr[i].length() != 0){
                count++;
                /*String []temp = ocr[i].split("\n");
                for(int j=0; j<temp.length; ++j)
                    parsing.append(j + temp[j] + "\n\n");*/
            }
           // asyncTask.execute(ocr[i]);
        }

        name = "";
        int name_count = 0;
        money = "";
        int money_count = 0;

        for(int i=0; i< ocr.length; ++i){
            //result.append(ocr[i] + ","+ ocr[i].length() +"\n//\n");
            if(ocr[i].length() != 0){
                count++;
                temp = ocr[i].split("\n");
                for(int j=0; j<temp.length; ++j) {
                    //parsing.append(j + temp[j] + "\n\n");
                }
                name = temp[0];
                money = temp[temp.length-1];
            }
        }

        menu = name + "\n" + money;
        result.setText(menu);
        //String txt = intent.getStringExtra("ocr");
        //result.setText(txt);
        //NaverTranslateTask asyncTask = new NaverTranslateTask();
        asyncTask.execute(name.toLowerCase());
        parsing.setText(name.toLowerCase());

        /*********/


        /*********/


        ArrayList<String> menu_name = new ArrayList<String>();
        menu_name.add(name);
        ArrayList<String> menu_money = new ArrayList<String>();
        menu_money.add(money);
    }


    public void back(View view){ //완료버튼

        finish();
    }

    //ASYNCTASK
    public class NaverTranslateTask extends AsyncTask<String, Void, String> {

        public String resultText;
        //Naver
        String clientId = "6c2Li8nKhiwzmhCtLL5T";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "dujkbcOZ34";//애플리케이션 클라이언트 시크릿값";
        //언어선택도 나중에 사용자가 선택할 수 있게 옵션 처리해 주면 된다.
        String sourceLang = "en";
        String targetLang = "ko";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {
//네이버제공 예제 복사해 넣자.
//Log.d("AsyncTask:", "1.Background");

            String sourceText = strings[0];

            try {
//String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
// post request
                String postParams = "source=" + sourceLang + "&target=" + targetLang + "&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else { // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
//System.out.println(response.toString());
                return response.toString();

            } catch (Exception e) {
//System.out.println(e);
                Log.d("error", e.getMessage());
                return null;
            }
        }

        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//최종 결과 처리부
//Log.d("background result", s.toString()); //네이버에 보내주는 응답결과가 JSON 데이터이다.

//JSON데이터를 자바객체로 변환해야 한다.
//Gson을 사용할 것이다.

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString())
//원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
//안드로이드 객체에 담기
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
//Log.d("result", items.getTranslatedText());
//번역결과를 텍스트뷰에 넣는다.
            //translation.append(items.getTranslatedText() + "\n");
            translation.setText(items.getTranslatedText());
        }


        //자바용 그릇
        public class TranslatedItem {
            String translatedText;

            public String getTranslatedText() {
                return translatedText;
            }
        }

    }
}

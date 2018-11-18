package com.example.owner.real_final.other;

import android.os.AsyncTask;

import com.example.owner.real_final.activity.MenuListActivity;
import com.example.owner.real_final.fragment.MoviesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vns on 2018-05-11.
 */

public class TaskforMenu extends AsyncTask<Void,Void,Void> {
    String data ="";
    String dataParsed = "";
    String singleParsed ="";
    String cur_unit="";
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=mtTuCdFG8glBKUBNgkQ7Qq55aO6UCzAw&searchdate=20180313&data=AP01");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray JA = new JSONArray(data);

            for(int i =0 ;i <JA.length(); i++){
                JSONObject JO = (JSONObject) JA.get(i);
                singleParsed =  JO.optString("cur_nm")+"("+JO.optString("cur_unit")+"/KRW)";
                cur_unit = JO.optString("cur_unit");
                MenuListActivity.nations.add(cur_unit);
                MenuListActivity.price.add(JO.optString("kftc_bkpr").replace(",",""));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MenuListActivity.calculate_exchange();

    }

}

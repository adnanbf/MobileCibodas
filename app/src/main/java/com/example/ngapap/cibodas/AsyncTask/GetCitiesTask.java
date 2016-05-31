package com.example.ngapap.cibodas.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.City;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by user on 30/05/2016.
 */
public class GetCitiesTask extends AsyncTask<Void,Void,String> {
    Context context;
    private ArrayList<City> mListCity;
//    private ArrayList<City> listCity;
    private ProgressDialog progressDialog;
    private int province_id;
    private NetworkUtils networkUtils;

    public GetCitiesTask(Context context, int id, ArrayList<City> listCity) {
        this.context = context;
        province_id = id;
        this.mListCity = listCity;
        networkUtils = new NetworkUtils(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Populating...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
//            mList = new ArrayList<>();
        String returnValue = "";
        String url = "http://192.168.137.1/C-Bodas/public/api/v1/customers/getCities?province_id="+province_id;
        if (networkUtils.isConnectedToServer(url)) {
            JSONParser jsonParser = new JSONParser();
            String request = jsonParser.getJSON(url);
            try {
                JSONArray response = new JSONArray(request);
                for (int i = 0; i < response.length(); i++) {
                    City mCity = new City();
                    mCity = mCity.toCity(response,i);
                    mListCity.add(mCity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                returnValue = "DBproblem";
            }

        } else {
            returnValue = "notConnected";
        }
        return returnValue;
    }

    @Override
    protected void onPostExecute(String s) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.dismiss();
        switch (s) {
            case "notConnected":
                alert.setTitle(context.getString(R.string.con_problem));
                alert.setMessage(context.getString(R.string.serv_problem));
                alert.setPositiveButton("OK", null);
                alert.show();
                break;
            case "DBproblem":
                alert.setTitle(context.getString(R.string.db_problem));
                alert.setMessage(context.getString(R.string.db_connection));
                alert.setPositiveButton("OK", null);
                alert.show();
                break;
            default:
                Log.d("GetProvincesTask", s);
        }
        super.onPostExecute(s);
    }
}

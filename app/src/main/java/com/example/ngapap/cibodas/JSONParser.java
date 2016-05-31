package com.example.ngapap.cibodas;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 25/03/2016.
 */
public class JSONParser {
    HttpURLConnection httpURLConnection;
    private static final String TAG = JSONParser.class.getSimpleName();

    public static String postImage(String serverUrl,String dataToSend){
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //set timeout of 30 seconds
            con.setConnectTimeout(1000 * 30);
            con.setReadTimeout(1000 * 30);
            //method
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));

            //make request
            writer.write(dataToSend);
            writer.flush();
            writer.close();
            os.close();

            //get the response
            int responseCode = con.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                //read the response
                StringBuilder sb = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String line;

                //loop through the response from the server
                while ((line = reader.readLine()) != null){
                    sb.append(line).append("\n");
                }

                //return the response
                return sb.toString();
            }else{
                Log.e(TAG,"ERROR - Invalid response code from server "+ responseCode);
                return "DBproblem";
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR " + e);
            return "DBproblem";
        }
    }


    public String getJSON(String myURL){
        StringBuilder result = new StringBuilder();
        try{
            URL url= new URL(myURL);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            InputStream inputStream= new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            String value="DBproblem";
            e.printStackTrace();
            return value;
        }finally {
            httpURLConnection.disconnect();
        }

    }

    public String postJSON(String myURL, JSONObject request){
        StringBuilder result = new StringBuilder();
        try{
            URL url = new URL(myURL);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(request.toString());
            writer.flush();
            writer.close();

            InputStream input = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            String value="DBproblem";
            e.printStackTrace();
            return value;
        }finally {
            httpURLConnection.disconnect();
        }

    }

    public String checkDelivCost(String params){
        StringBuilder result = new StringBuilder();
        String myURL = "http://api.rajaongkir.com/starter/cost";
//        String myURL = "http://api.ongkir.info/cost/find";
//        String param = "API-Key=ff1cf34185eb10b93332dd3e668e542a&from=BandungBArat&to=Jakarta&weight=1500&courier=jne&format=json";
        try{
            URL url = new URL(myURL);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("key", "cf2bcde1967e60ec66bd849c68a19e2a");
//            httpURLConnection.setRequestProperty("application/x-www-form-urlencoded", "origin=501&destination=114&weight=1700&courier=jne");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream());
            writer.write(params);
//            writer.write(param);
            writer.flush();
            writer.close();

            InputStream input = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            String value="DBproblem";
            e.printStackTrace();
            return value;
        }finally {
            httpURLConnection.disconnect();
        }
    }
}

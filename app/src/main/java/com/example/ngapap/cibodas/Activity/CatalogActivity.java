package com.example.ngapap.cibodas.Activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.ngapap.cibodas.Fragment.CatalogFragment;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by user on 25/03/2016.
 */
public class CatalogActivity extends MenuActivity {
    private ArrayList<Product> listProduct;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // get the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        session = new SessionManager(getApplicationContext());
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
        handleIntent(getIntent());
        networkUtils = new NetworkUtils(getApplicationContext());
        Bundle catalog = getIntent().getExtras();
        String jsonString = catalog.getString("catalog");
        ArrayList<Product> list = new ArrayList<>();
        try {
            JSONArray jsonProduct = new JSONArray(jsonString);
            Product product = new Product();
            for (int i = 0; i < jsonProduct.length(); i++) {
                product = product.toProduct(jsonProduct, i);
                list.add(product);
                Log.d("shitstain-" + i, list.get(i).getProduct_name());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setListProduct(list);


//        Bundle bundle = new Bundle();
//        bundle.putSerializable("catalog", list);
        CatalogFragment catalogFragment = new CatalogFragment();
//        catalogFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame, catalogFragment);
        fragmentTransaction.commit();
    }

    private class FindProductAsynctask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog progressDialog;

        FindProductAsynctask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_create_con));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String name = params[0].toString();
            String returnValue = "";
            String myURL = getString(R.string.base_url) + "products/find?find=" + name;
            JSONParser jsonParser = new JSONParser();
            try {
                if (networkUtils.isConnectedToServer(myURL)) {
                    String result = jsonParser.getJSON(myURL);
                    if (!result.equals("DBproblem")) {
                        ArrayList<Product> listProduct = new ArrayList<>();
                        JSONArray jsonProduct = new JSONArray(result);
                        Product product = new Product();
                        if (jsonProduct.length() > 0) {
                            for (int i = 0; i < jsonProduct.length(); i++) {
                                product = product.toProduct(jsonProduct, i);
                                listProduct.add(product);
                                Log.d("shitstain-" + i, listProduct.get(i).getProduct_name());
                            }
                            returnValue = "Found";
                            setListProduct(listProduct);
                        } else {
                            returnValue = "notFound";
                        }
                    }
                } else {
                    returnValue = "notConnected";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (result) {
                case "Found":
                    searchView.clearFocus();
                    CatalogFragment catalogFragment = new CatalogFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, catalogFragment);
                    fragmentTransaction.commit();
                    break;
                case "notFound":
                    alert.setTitle("Product Not Found");
                    alert.setMessage("Wrong Keyword");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "notConnected":
                    searchView.clearFocus();
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    Log.d("From FindProduct", result);
                    searchView.clearFocus();
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("shitstain-", query);
            FindProductAsynctask task = new FindProductAsynctask(CatalogActivity.this);
            task.execute(query);
            // Do work using string
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public ArrayList<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
//         Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setFocusable(false);
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v.findFocus());
                }
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(false);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                //do your action here.
                onBackPressed();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


}

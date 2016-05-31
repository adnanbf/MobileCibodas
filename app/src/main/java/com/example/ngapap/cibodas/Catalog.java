package com.example.ngapap.cibodas;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.Adapter.CatalogContentAdapter;
import com.example.ngapap.cibodas.Fragment.CatalogFragment;
import com.example.ngapap.cibodas.Fragment.DetailProductFragment;
import com.example.ngapap.cibodas.Model.Product;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 26/05/2016.
 */
public class Catalog extends NavActivity {
    private ArrayList<Product> listProduct;
    private String catalogType;
    boolean isLoaded;
    private int selectedSort=-1;
    FloatingActionButton _fab;

    protected void handleIntent(Intent intent, Context context, SearchView searchView, ListView listView) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            FindProductAsyncTask task = new FindProductAsyncTask(context, searchView, listView);
            task.execute(query, customer.getApi_token());
            // Do work using string
        }
    }

    protected void loadProduct(Context context, String url, BaseAdapter adapter, ListView listView, FloatingActionButton fab){
        _fab = fab;
        FirstLoadAsynctask task = new FirstLoadAsynctask(context, url, adapter, listView);
        task.execute();
    }

    protected void sortProduct(final Context context, final BaseAdapter adapter){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        final CharSequence[] items = {" Rating"," Harga Tinggi ke Rendah"," Harga Rendah ke Tinggi "};
        builder.setTitle("Sort by").setCancelable(false)
                .setSingleChoiceItems(items, selectedSort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedSort=which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPost= ((android.app.AlertDialog)dialog).getListView().getCheckedItemPosition();
                Log.d("From Dialog", String.valueOf(selectedPost));
                SortAsyncTask task = new SortAsyncTask(context, adapter);
                task.execute(String.valueOf(selectedPost));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    private class FirstLoadAsynctask extends AsyncTask<Void,Void,String> {
        private Context context;
        private String url;
        private ProgressDialog progressDialog;
        private BaseAdapter adapter;
        private ListView _listView;

        FirstLoadAsynctask(Context context, String url, BaseAdapter adapter, ListView listView) {
            this.context = context;
            this.url = url;
            this.adapter = adapter;
            this._listView = listView;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(context.getString(R.string.dialog_create_con));
            progressDialog.show();
            progressDialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            NetworkUtils networkUtils = new NetworkUtils(context);
            if (networkUtils.isConnectedToServer(url)) {
                JSONParser jsonParser = new JSONParser();
                result = jsonParser.getJSON(url);
                try {
                    JSONArray jsonProduct = new JSONArray(result);
                    Product product = new Product();
                    for (int i = 0; i < jsonProduct.length(); i++) {
                        product = product.toProduct(jsonProduct, i);
                        getListProduct().add(product);
                        Log.d("shitstain-" + i, getListProduct().get(i).getProduct_name());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                result = "notConnected";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (result) {
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
                    isLoaded = true;
                    adapter = new CatalogContentAdapter(context, getListProduct());
                    _listView.setAdapter(adapter);
                    _listView.setOnScrollListener(new EndlessScrollListener() {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {
                            LoadMoreAsynctask loadMoreCatalog = new LoadMoreAsynctask(context, adapter);
                            loadMoreCatalog.execute(customer.getApi_token(), String.valueOf(totalItemsCount), getCatalogType());
                            return isLoaded;
                        }
                    });
                    _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Bundle bundle = new Bundle();
                            Product product = getListProduct().get(position);
                            bundle.putSerializable("product", product);
                            DetailProductFragment detailProductFragment = new DetailProductFragment();
                            detailProductFragment.setArguments(bundle);
                            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                                    R.animator.slide_in_left, R.animator.slide_out_left);
                            fragmentTransaction.replace(R.id.frame, detailProductFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            _listView.setVisibility(View.GONE);
                            _fab.setVisibility(View.GONE);
                            disableNavIcon();
                        }
                    });
                    _fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sortProduct(context,adapter);
                        }
                    });
                    Log.d("ProductFirstLoad", result);
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private class FindProductAsyncTask extends AsyncTask<String,String ,String>{
        private Context context;
        private ProgressDialog progressDialog;
        private SearchView searchView;
        private ListView listView;
        ArrayList<Product> listProduct = new ArrayList<>();

        FindProductAsyncTask(Context context, SearchView searchView, ListView listView) {
            this.context = context;
            this.searchView = searchView;
            this.listView = listView;
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
            String name = params[0];
            String api_token = params[1];
            String returnValue = "";
            String myURL = getString(R.string.base_url) + "products/find?api_token=" + api_token + "&find=" + name;
            JSONParser jsonParser = new JSONParser();
            try {
                if (networkUtils.isConnectedToServer(myURL)) {
                    String result = jsonParser.getJSON(myURL);
                    if (!result.equals("DBproblem")) {
                        JSONArray jsonProduct = new JSONArray(result);
                        Product product = new Product();
                        if (jsonProduct.length() > 0) {
                            for (int i = 0; i < jsonProduct.length(); i++) {
                                product = product.toProduct(jsonProduct, i);
                                listProduct.add(product);
                            }
                            Log.d("findProduct", String.valueOf(listProduct.size()));
                            returnValue = "Found";
//                            setListProduct(listProduct);
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
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("listProduct", listProduct);
                    searchView.clearFocus();
                    CatalogFragment catalogFragment = new CatalogFragment();
                    catalogFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                            R.animator.slide_in_left, R.animator.slide_out_left);
                    fragmentTransaction.replace(R.id.frame, catalogFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    disableNavIcon();
                    listView.setVisibility(View.GONE);
                    _fab.setVisibility(View.GONE);
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

    private class LoadMoreAsynctask extends AsyncTask<String, String, String>{
        private Context context;
        private BaseAdapter adapter;

        LoadMoreAsynctask(Context context, BaseAdapter adapter) {
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        protected String doInBackground(String... params) {
            String api_token = params[0];
            String offset= params[1];
            String catalogType=params[2];
            String myURL = getString(R.string.base_url) + "products/catalog?api_token="+api_token+
                    "&catalog="+catalogType+"&offset="+offset;
            String result;
            if (networkUtils.isConnectedToServer(myURL)) {
                JSONParser jsonParser = new JSONParser();
                result = jsonParser.getJSON(myURL);
                if(result.length()>0){
                    try {
                        JSONArray jsonProduct = new JSONArray(result);
                        Product product = new Product();
                        for (int i = 0; i < jsonProduct.length(); i++) {
                            product = product.toProduct(jsonProduct, i);
                            getListProduct().add(product);
                            Log.d("shitstain-" + i, getListProduct().get(i).getProduct_name());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    isLoaded=false;
                }

            } else {
                result = "notConnected";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (result) {
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "DBproblem":
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    adapter.notifyDataSetChanged();
            }
            super.onPostExecute(result);
        }
    }

    private class SortAsyncTask extends AsyncTask<String,String,String>{
        private Context context;
        private ProgressDialog progressDialog;
        private BaseAdapter adapter;

        SortAsyncTask(Context context, BaseAdapter adapter){
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            switch (params[0]){
                case "0":
                    Collections.sort(getListProduct(), Product.RatingComparator);
                    break;
                case "1":
                    Collections.sort(getListProduct(), Product.DescPriceComparator);
                    break;
                case "2":
                    Collections.sort(getListProduct(), Product.AscPriceComparator);
                    break;
                default:
                    Log.d("From Sort ", params[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    public ArrayList<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }


    public String getCatalogType() {
        return catalogType;
    }

    public void setCatalogType(String catalogType) {
        this.catalogType = catalogType;
    }


}

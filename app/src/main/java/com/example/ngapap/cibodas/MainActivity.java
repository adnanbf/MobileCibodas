package com.example.ngapap.cibodas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.Adapter.GridContentAdapter;
import com.example.ngapap.cibodas.Fragment.DetailProductFragment;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Product;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends NavActivity {
//    SessionManager session;
//    MenuActivity resideMenu;
    long lastPress;
    @Bind(R.id.gridview)
    GridView _gridView;
//    @Bind(R.id.agri_logo)
//    ImageView _agriLogo;
//    @Bind(R.id.tourism_logo)
//    ImageView _tourismLogo;
//    @Bind(R.id.text_email) TextView _textEmail;
//    @Bind(R.id.text_name) TextView _textName;
    Customer customer ;
    private ArrayList<Product> listProduct;
    GridContentAdapter adapter;
    boolean isLoaded=true;
    protected SessionManager session;
    protected NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = (LinearLayout) findViewById(R.id.content_frame);
        inflater.inflate(R.layout.activity_main, container);
        ButterKnife.bind(this);
        listProduct = new ArrayList<>();
        session = new SessionManager(getApplicationContext());
        networkUtils = new NetworkUtils(getApplicationContext());
        if (session.checkLogin())
            finish();
        HashMap<String, String> user = session.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        customer = new Customer();
        try {
            JSONArray jsonArray = new JSONArray(data);
//            Customer customer = new Customer();
            customer = customer.toCustomer(jsonArray);
            UpdateUserAsyncTask task = new UpdateUserAsyncTask(MainActivity.this);
            task.execute(customer.getId());
//            Log.d("MainBought", String.valueOf(customer.getListProduct().get(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url= getString(R.string.base_url) + "products/catalog?api_token="+customer.getApi_token()+
                "&catalog=all&offset=0";
        ProductFirstLoadTask productFirstLoad = new ProductFirstLoadTask(MainActivity.this,url);
        productFirstLoad.execute();
//        Log.d("mainActivity", listProduct.get(0).getProduct_name());

//        _agriLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RequestCatalogAsynctask task = new RequestCatalogAsynctask(MainActivity.this);
//                task.execute("pertanian", customer.getApi_token());
//            }
//        });
//        _tourismLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RequestCatalogAsynctask task = new RequestCatalogAsynctask(MainActivity.this);
//                task.execute("pariwisata", customer.getApi_token());
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.main_menu);
        onNavigationItemSelected(0);

    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                _gridView.setVisibility(View.VISIBLE);
                enableNavIcon();
            }
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 5000) {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
                lastPress = currentTime;
            }else{
                super.onBackPressed();
            }
        }
    }

    public ArrayList<Product> getListProduct() {
        return listProduct;
    }

    public void setListProduct(ArrayList<Product> listProduct) {
        this.listProduct = listProduct;
    }

    private class ProductFirstLoadTask extends AsyncTask<Void,Void,String>{
        private Context context;
        private String url;
        private ProgressDialog progressDialog;

        public ProductFirstLoadTask (Context context, String url) {
            this.context = context;
            this.url = url;
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
            String result="";
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
            switch (result.toUpperCase()) {
                case "NOTCONNECTED":
                    alert.setTitle(context.getString(R.string.con_problem));
                    alert.setMessage(context.getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "DBPROBLEM":
                    alert.setTitle(context.getString(R.string.db_problem));
                    alert.setMessage(context.getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
//                    isLoaded=true;
                    adapter = new GridContentAdapter(MainActivity.this, getListProduct());
                    _gridView.setAdapter(adapter);
                    _gridView.setOnScrollListener(new EndlessScrollListener(4, 1) {
                        @Override
                        public boolean onLoadMore(int page, int totalItemsCount) {
                            if (page < 4) {
                                LoadMoreProduct loadMoreProduct = new LoadMoreProduct(MainActivity.this);
                                loadMoreProduct.execute(customer.getApi_token(), String.valueOf(totalItemsCount));
                            } else {
                                isLoaded = false;
                            }

                            return isLoaded;
                        }
                    });
                    _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            disableNavIcon();
                            _gridView.setVisibility(View.GONE);
                        }
                    });
                    Log.d("ProductFirstLoad", result);
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private class UpdateUserAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private Context context;

        UpdateUserAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_message_auth));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String returnValue = "";
            String id = params[0];
            JSONObject object = new JSONObject();
            String myURL = getString(R.string.base_url) + "customers/maintainLogin";
//            Log.d("From updateUser ",myURL);
            Customer customer = new Customer();
            try {
                if (networkUtils.isConnectedToServer(myURL)) {
                    JSONParser jsonParser = new JSONParser();
                    object.put("id_customer", id);
                    String jsonString = jsonParser.postJSON(myURL, object);
                    JSONArray jsonLogin = new JSONArray(jsonString);
                    if (jsonLogin.length() == 1) {
                        customer = customer.toCustomer(jsonLogin);
                        session.createLoginSession(customer.getEmail(), customer.createJSONArray().toString());
                        returnValue = "valid";
                    } else
                        returnValue = "invalid";
                } else
                    returnValue = "notConnected";

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.dismiss();
            switch (s) {
                case "valid":
                    Log.d("From Update User", s);
                    break;
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    alert.show();
                    break;
                default:
                    Log.d("From Update User", s);
            }
            super.onPostExecute(s);
        }
    }

    private class LoadMoreProduct extends AsyncTask<String, String, String> {
        private Context context;


        //        ArrayList<Product> listCatalog= new ArrayList<Product>();
//        HttpURLConnection httpURLConnection;
        LoadMoreProduct(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String api_token = params[0];
            String offset= params[1];
            String myURL = getString(R.string.base_url) + "products/catalog?api_token="+api_token+
                    "&catalog=all&offset="+offset;
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

}

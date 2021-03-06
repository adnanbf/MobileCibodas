package com.example.ngapap.cibodas.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ngapap.cibodas.Adapter.CatalogContentAdapter;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 25/03/2016.
 */
public class CatalogFragment extends android.app.Fragment  {
    private View rootView;
    private ListView _listCatalog;
    private ArrayList<Product> listCatalog;
    private int selectedSort = -1;
    private FloatingActionButton _fab;
    CatalogContentAdapter adapter;
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.activity_catalog, null);

        listCatalog = (ArrayList<Product>) this.getArguments().getSerializable("listProduct");

//        listCatalog=((Catalog) getActivity()).getListProduct();
//        getArguments().remove("catalog");
//        Log.d("From Fragment", listCatalog.get(0).getProduct_name());
        _listCatalog= (ListView) rootView.findViewById(R.id.list);
        _fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        adapter=new CatalogContentAdapter(getActivity(),listCatalog);
        _listCatalog.setAdapter(adapter);
//        ((MenuActivity) getActivity()).setListViewHeightBasedOnChildren(_listCatalog);
//        _listCatalog.setAdapter(new CatalogContentAdapter(getActivity(), listCatalog));

        _listCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                Product product = listCatalog.get(position);
                bundle.putSerializable("product", product);
                DetailProductFragment detailProductFragment = new DetailProductFragment();
                detailProductFragment.setArguments(bundle);
                android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                        R.animator.slide_in_left, R.animator.slide_out_left);
                fragmentTransaction.replace(R.id.frame, detailProductFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });




        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSort();
            }
        });
        return rootView;
    }

    private void dialogSort(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] items = {" Rating"," Harga Tinggi ke Rendah"," Harga Rendah ke Tinggi "};
        builder.setTitle("Sort by").setCancelable(false)
                .setSingleChoiceItems(items, selectedSort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedSort=which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPost= ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                Log.d("From Dialog", String.valueOf(selectedPost));
                SortAsyncTask task = new SortAsyncTask(getActivity());
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

    private class SortAsyncTask extends AsyncTask<String,String,String>{
        private Context context;
        private ProgressDialog progressDialog;

        SortAsyncTask(Context context){
            this.context = context;
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
                    Collections.sort(listCatalog, Product.RatingComparator);
                    break;
                case "1":
                    Collections.sort(listCatalog, Product.DescPriceComparator);
                    break;
                case "2":
                    Collections.sort(listCatalog, Product.AscPriceComparator);
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

}

package com.example.ngapap.cibodas.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 17/05/2016.
 */
public class PaymentConfirmFragment extends Fragment {
    private static final int RESULT_SELECT_IMAGE = 1;
    public String timestamp;
    public ImageView imageView;
    public Button selectImage, uploadImage;
    private Reservation reservation;
    SessionManager sessionManager;
    private Customer customer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment, null);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        selectImage = (Button) rootView.findViewById(R.id.selectImage);
        uploadImage = (Button) rootView.findViewById(R.id.uploadImage);
        reservation = (Reservation) this.getArguments().getSerializable("reservation");
        sessionManager = new SessionManager(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        customer = new Customer();
        try {
            JSONArray jsonArray = new JSONArray(data);
            customer = customer.toCustomer(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //when selectImage button is pressed
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the function to select image from album
                selectImage();
            }
        });

        //when uploadImage button is pressed
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image in bitmap format
                if (imageView.getDrawable() != null) {
                    dialogUpload();
                } else {
                    Toast.makeText(getActivity(), "Please Select Your Payment Proof!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return rootView;
    }

    private void dialogUpload() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("Upload Bukti Pembayaran");
        // Setting Dialog Message
        alertDialog.setMessage("Apakah Anda yakin ?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                //execute the async task and upload the image to server
                new Upload(image, "IMG_" + timestamp + reservation.getId_reservation(), customer.getApi_token()).execute();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Konfirmasi Pembayaran ");
    }

    private void selectImage() {
        //open album to select image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);

//        imageView.setAdjustViewBounds(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SELECT_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            //set the selected image to image variable
            Uri image = data.getData();
            imageView.setImageURI(image);
            imageView.setBackground(null);
            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

            Toast.makeText(getActivity(), timestamp, Toast.LENGTH_SHORT).show();
        }
    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class Upload extends AsyncTask<Void, Void, String> {
        private Bitmap image;
        private String name;
        private String api_token;
        private ProgressDialog progressDialog;

        public Upload(Bitmap image, String name, String api_token) {
            this.image = image;
            this.name = name;
            this.api_token = api_token;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String SERVER = "http://192.168.137.1/C-Bodas/public/api/v1/reservation/paymentConfirmation?api_token=" + api_token;
            String returnValue = "";
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            //generate hashMap to store encodedImage and the name
            HashMap<String, String> detail = new HashMap<>();
            detail.put("name", name);
            detail.put("image", encodeImage);
            detail.put("id_reservation", reservation.getId_reservation());
            try {
                //convert this HashMap to encodedUrl to send to php file
                NetworkUtils networkUtils = new NetworkUtils(getActivity());
                if (networkUtils.isConnectedToServer(SERVER)) {
                    String dataToSend = hashMapToUrl(detail);
                    //make a Http request and send data to saveImage.php file
                    JSONParser jsonParser = new JSONParser();
                    Log.d("PaymentConfirm Req", dataToSend);
                    String response = jsonParser.postImage(SERVER, dataToSend);
                    if (!response.equals("DBproblem")) {
                        returnValue = "connected";
                    }
                    Log.d("PaymentConfirm Resp", response);
                } else {
                    returnValue = "notConnected";
                }
                return returnValue;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Payment Fragment ", "ERROR  " + e);
                return returnValue;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            progressDialog.dismiss();
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dark_Dialog);
            Log.d("Payment PostExecute", s);
            switch (s) {
                case "connected":
                    alert.setTitle("Upload Success");
                    alert.setMessage("Pembayaran Akan Segera Diperiksa. Terima Kasih");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
            Toast.makeText(getActivity(), "Image Uploaded", Toast.LENGTH_SHORT).show();
        }
    }


}

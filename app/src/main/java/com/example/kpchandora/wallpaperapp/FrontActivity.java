package com.example.kpchandora.wallpaperapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FrontActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyImageAdapter adapter;
    private List<ImagePOJO> imagePOJOList;

    private RequestQueue newRqst;
    private static final int PERMISSION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.front_activity_progressBar);
        imagePOJOList = new ArrayList<>();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        getCDataFromApi();

        if (!checkPermission()){
            askPermission();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(FrontActivity.this, 2));
    }

    private boolean checkPermission(){

        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void getCDataFromApi() {

        newRqst = Volley.newRequestQueue(FrontActivity.this);

        String api_url = "https://mynewproject-3dd78.firebaseio.com/.json";

        Log.i("DATA", "getDataFromApi: ");
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, api_url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray array = response.getJSONArray("category");
                    Log.i("CURRENT", "Array: " + array);
                    int noOfImages = response.getInt("count");

                    int num = 0;

                    for (int i = 0; i < array.length(); i++) {
                        if (!array.getString(i).equals("null")) {
                            JSONObject currentObj = array.getJSONObject(i);
                            JSONArray jsonArray = currentObj.getJSONArray("name");
                            String title = currentObj.getString("title");
                            String[] names = new String[jsonArray.length()];

                            num++;

                            int k = 0;

                            for (int j = 0; j < jsonArray.length(); j++) {
                                if (!jsonArray.getString(j).equals("null")) {
                                    Log.i("LENGTH", "String: " + jsonArray.getString(j));
                                    names[j] = jsonArray.getString(j);
                                    k++;
                                }
                            }

                            String[] categoryItems = new String[k];
                            k = 0;
                            for (int n = 0; n < names.length; n++) {
                                if (names[n] != null) {
                                    categoryItems[k] = names[n];
                                    k++;
                                }
                            }

                            Log.i("LENGTH", "Categories: " + categoryItems.length);

                            String url = currentObj.getString("url");
                            imagePOJOList.add(new ImagePOJO(url, title, categoryItems, noOfImages));
                        }
                    }
                    Log.i("NUMCount", "Count: "+num);
                    adapter = new MyImageAdapter(FrontActivity.this, imagePOJOList);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Log.i("NEW", "error: " + e);
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        newRqst.add(request);
    }

}

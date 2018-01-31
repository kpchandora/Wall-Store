package developer.code.kpchandora.wallstore;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

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

public class FrontActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MyImageAdapter adapter;
    private List<ImagePOJO> imagePOJOList;

    private NotificationManager manager;
    private NotificationCompat.Builder builder;

    public static final String CHANNEL_ID = "notification_channel";

    private RequestQueue newRqst;
    private static final int PERMISSION_REQUEST_CODE = 3;
    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.front_activity_progressBar);
        imagePOJOList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        getCDataFromApi();

        if (!checkPermission()) {
            askPermission();
        }


        NavigationView navigationView = findViewById(R.id.navigation_view);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(FrontActivity.this, 2));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initNotification() {

        builder = new NotificationCompat.Builder(FrontActivity.this, CHANNEL_ID);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

    }

    private boolean checkPermission() {

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
                    Log.i("NUMCount", "Count: " + num);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}

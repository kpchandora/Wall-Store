package developer.code.kpchandora.wallstore;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import developer.code.kpchandora.wallstore.Database.CategoryContract;
import developer.code.kpchandora.wallstore.Database.DbHelper;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;

public class FrontActivity extends RootAnimActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "FrontActivity";
    private static final int PERMISSION_REQUEST_CODE = 3;

    protected static int pageCount = 0;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private DrawerLayout drawerLayout;


    private RequestQueue newRqst;
    private ArrayList<String> titlesList;
    private FloatingActionButton fab;
    private FrontAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.front_activity_progressBar);
        fab = findViewById(R.id.floatingActionButton);
        titlesList = new ArrayList<>();

        pageCount = 1;

        //Checks if the user has opened the app for first time
        checkIfFirstTime();

        relativeLayout = findViewById(R.id.empty_categoryLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FrontActivity.this, CategoriesActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("ArrayList", titlesList);
                i.putExtra("TitleBundle", b);
                startActivity(i);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(FrontActivity.this, "Add Category", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //One signal API for notifications
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        if (!checkPermission()) {
            askPermission();
        }

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(FrontActivity.this, 2));

        registerForContextMenu(recyclerView);
    }

    private void checkIfFirstTime() {

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();

            //Intro instructions for New Users

            new GuideView.Builder(this)
                    .setTitle("Add categories")
                    .setTargetView(fab)
                    .build().show();

        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_delete, menu);
        Log.i(TAG, "onCreateContextMenu: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.noti_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        startActivity(new Intent(FrontActivity.this, NotificationActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((FrontAdapter) recyclerView.getAdapter()).getCategoryPosition();
        ((FrontAdapter) recyclerView.getAdapter()).removeCategory(position);
        deleteCategory(position);
        Log.i(TAG, "onContextItemSelected: " + position);
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(int position) {

        DbHelper helper = new DbHelper(FrontActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        long numRow = db.delete(CategoryContract.FRONT_TABLE, CategoryContract.FRONT_ID + "=?",
                new String[]{titlesList.get(position)});
        titlesList.remove(position);
        Log.i(TAG, "deleteCategory: " + numRow);

        if (titlesList.size() < 1) {
            relativeLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        new FrontDataClass().execute();
        Log.i(TAG, "onStart: ");
        if (titlesList.size() < 1) {
            relativeLayout.setVisibility(View.VISIBLE);
        }
        getCDataFromApi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    private class FrontDataClass extends AsyncTask<Void, Void, ArrayList<CategoryModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.setEnabled(false);
        }

        @Override
        protected ArrayList<CategoryModel> doInBackground(Void... voids) {
            return fetchDataFromTable();
        }

        @Override
        protected void onPostExecute(ArrayList<CategoryModel> categoryModels) {
            fab.setEnabled(true);

            titlesList.clear();

            if (categoryModels != null) {
                for (int i = 0; i < categoryModels.size(); i++) {
                    titlesList.add(categoryModels.get(i).getImageTitle());
                }
            }

            if (categoryModels != null && categoryModels.size() > 0) {
                relativeLayout.setVisibility(View.GONE);
            } else {
                relativeLayout.setVisibility(View.VISIBLE);
            }

            adapter = new FrontAdapter(FrontActivity.this, categoryModels);
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    private ArrayList<CategoryModel> fetchDataFromTable() {

        ArrayList<CategoryModel> categoryModels = new ArrayList<>();

        DbHelper dbHelper = new DbHelper(FrontActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CategoryContract.FRONT_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        int titleIndex = cursor.getColumnIndex(CategoryContract.FRONT_TITLE);
        int urlIndex = cursor.getColumnIndex(CategoryContract.FRONT_IMAGE_URL);

        while (cursor.moveToNext()) {

            String title = cursor.getString(titleIndex);
            String url = cursor.getString(urlIndex);

            Log.i(TAG, "fetchDataFromTable: Title" + title + "\nUrl" + url);
            CategoryModel model = new CategoryModel(url, title);
            categoryModels.add(model);

        }

        cursor.close();
        return categoryModels;

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

                    pageCount = response.getInt("pageCount");
                    Log.i(TAG, "onResponse: PageCount " + pageCount);


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

        switch (item.getItemId()) {
            case R.id.downloads_menu:
                startActivity(new Intent(FrontActivity.this, DownloadActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

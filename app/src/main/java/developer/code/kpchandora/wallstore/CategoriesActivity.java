package developer.code.kpchandora.wallstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import developer.code.kpchandora.wallstore.Database.CategoryContract;
import developer.code.kpchandora.wallstore.Database.DbHelper;

import static developer.code.kpchandora.wallstore.Database.CategoryContract.IMAGE_URL_COLUMN;
import static developer.code.kpchandora.wallstore.Database.CategoryContract.NOTIFICATION_CONTENT_COLUMN;
import static developer.code.kpchandora.wallstore.Database.CategoryContract.NOTIFICATION_EXP_TIME;
import static developer.code.kpchandora.wallstore.Database.CategoryContract.NOTIFICATION_TIME_STAMP;
import static developer.code.kpchandora.wallstore.Database.CategoryContract.NOTIFICATION_TITLE_COLUMN;
import static developer.code.kpchandora.wallstore.Database.CategoryContract.NOTIFICATION_TOTAL_TIME;
import static developer.code.kpchandora.wallstore.Database.CategoryContract.TABLE_NOTIFICATION;

public class CategoriesActivity extends RootAnimActivity {

    private static final String TAG = "CategoryActivity";

    private RecyclerView categoryRecyclerView;
    private RequestQueue requestQueue;
    private CategoriesAdapter adapter;
    private ArrayList<String> titleArrayList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        progressBar = findViewById(R.id.category_activity_progressBar);

        setTitle("Categories");

        deleteTable();

        Bundle b = getIntent().getBundleExtra("TitleBundle");
        titleArrayList = new ArrayList<>();
        if (b != null) {
            titleArrayList = b.getStringArrayList("ArrayList");
        }

//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayout.HORIZONTAL);

//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        categoryRecyclerView = findViewById(R.id.category_recyclerView);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(CategoriesActivity.this, 3));
        categoryRecyclerView.setHasFixedSize(true);
//        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryRecyclerView.addItemDecoration(new SpacesItem(8));
        requestQueue = Volley.newRequestQueue(CategoriesActivity.this);

        getDataFromApi();


    }

    private void deleteTable() {
        DbHelper helper = new DbHelper(CategoriesActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        String DELETE_TABLE = "DELETE FROM " + CategoryContract.CATEGORY_TABLE + " WHERE " +
                CategoryContract.CATEGORY_ID + " != \"-1\";";
        db.execSQL(DELETE_TABLE);
        Log.i(TAG, "deleteTable: ");

    }


    private ArrayList<CategoryModel> fetchCategories() {

        ArrayList<CategoryModel> categoryModels = new ArrayList<>();

        DbHelper dbHelper = new DbHelper(CategoriesActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                CategoryContract.CATEGORY_TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );

        int idIndex = cursor.getColumnIndex(CategoryContract.CATEGORY_ID);
        int titleIndex = cursor.getColumnIndex(CategoryContract.CATEGORY_TITLE);
        int flagIndex = cursor.getColumnIndex(CategoryContract.CATEGORY_FLAG);
        int urlIndex = cursor.getColumnIndex(CategoryContract.CATEGORY_IMAGE_URL);

        while (cursor.moveToNext()) {

            String id = cursor.getString(idIndex);
            String title = cursor.getString(titleIndex);
            int flag = cursor.getInt(flagIndex);
            String url = cursor.getString(urlIndex);

            CategoryModel model = new CategoryModel(id, url, title);
            model.setFlag(flag);
            categoryModels.add(model);

        }

        cursor.close();
        return categoryModels;
    }

    private void getDataFromApi() {

        String api_url = "https://mynewproject-3dd78.firebaseio.com/.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, api_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray array = response.getJSONArray("categories");

                            for (int i = 0; i < array.length(); i++) {

                                if (!array.getString(i).equals("null")) {
                                    JSONObject currentObj = array.getJSONObject(i);
                                    String title = currentObj.getString("title");
                                    String url = currentObj.getString("url");

                                    insertData(title, url);
                                } else {
                                    Log.i(TAG, "onResponse: " + array.getString(i) + " at pos " + i);
                                }


                            }

                            updateFlagOfTitles(titleArrayList);
                            progressBar.setVisibility(View.GONE);
                            adapter = new CategoriesAdapter(CategoriesActivity.this, fetchCategories());
                            categoryRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Categories", "onErrorResponse: " + error);
            }
        });

        requestQueue.add(request);

    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "Long press to delete a category", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    private void updateFlagOfTitles(ArrayList<String> titlesList) {

        DbHelper dbHelper = new DbHelper(CategoriesActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < titlesList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(CategoryContract.CATEGORY_FLAG, 1);
            long numRows = db.update(CategoryContract.CATEGORY_TABLE, values, CategoryContract.CATEGORY_ID + "=?",
                    new String[]{titlesList.get(i)});
            Log.i(TAG, "updateFlagOfTitles: " + numRows);
        }

    }

    private void insertData(String title, String url) {

        DbHelper helper = new DbHelper(CategoriesActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoryContract.CATEGORY_ID, title);
        values.put(CategoryContract.CATEGORY_TITLE, title);
        values.put(CategoryContract.CATEGORY_IMAGE_URL, url);

        long rowId = db.insertWithOnConflict(CategoryContract.CATEGORY_TABLE,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);

        db.close();
        Log.i("Category", "insertData: " + rowId);
    }

//    private void put(){
//
//
//        long currentTime = System.currentTimeMillis();
//        long expTime = 2 * 60000;
//
//
//        DbHelper helper = new DbHelper(this);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(NOTIFICATION_TITLE_COLUMN, "");
//        values.put(NOTIFICATION_CONTENT_COLUMN, "");
//        values.put(NOTIFICATION_TIME_STAMP, currentTime);
//        values.put(NOTIFICATION_EXP_TIME, expTime);
//        values.put(NOTIFICATION_TOTAL_TIME, currentTime + expTime);
//        values.put(IMAGE_URL_COLUMN, "https://cdn.pixabay.com/photo/2018/01/11/19/02/architecture-3076685_960_720.jpg");
//
//        long rowId = db.insertWithOnConflict(TABLE_NOTIFICATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//    }

}

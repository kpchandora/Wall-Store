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
import android.widget.LinearLayout;

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

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = "CategoryActivity";

    private RecyclerView categoryRecyclerView;
    private RequestQueue requestQueue;
    private CategoriesAdapter adapter;
    private ArrayList<String> titleArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

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

    private class CategoryFetchData extends AsyncTask<Void, Void, ArrayList<CategoryModel>> {


        @Override
        protected ArrayList<CategoryModel> doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<CategoryModel> categoryModels) {

        }
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

                                JSONObject currentObj = array.getJSONObject(i);
                                String title = currentObj.getString("title");
                                String url = currentObj.getString("url");

                                insertData(title, url);

                            }

                            updateFlagOfTitles(titleArrayList);

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

        Log.i("Category", "insertData: " + rowId);
    }

}
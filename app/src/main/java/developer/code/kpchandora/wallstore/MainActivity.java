package developer.code.kpchandora.wallstore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageAdapter imageAdapter;
    private List<ImagePOJO> pojoList;

    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public static String toolBarTitle = "";
    private int count = 0;
    private int flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pojoList = new ArrayList<>();
        recyclerView = findViewById(R.id.image_recyclerView);
        progressBar = findViewById(R.id.main_activity_progressBar);
        requestQueue = Volley.newRequestQueue(MainActivity.this);


        Bundle bundle = getIntent().getExtras();

        String[] categoryArray = null;

        if (bundle != null) {
            categoryArray = bundle.getStringArray("Category");
            toolBarTitle = bundle.getString("Title");
            count = bundle.getInt("NoOfImages");
        }

        setTitle(toolBarTitle);

        addWallpapers(categoryArray);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
    }


    private void addWallpapers(final String[] title) {

        flag = 0;

        for (int k = 0; k < title.length; k++) {

            String pixaBayApi = "https://pixabay.com/api/?key=6318588-51c982aa8d01fb8ba1030b311&q=" + title[k] + "&order=popular&response_group=high_resolution&per_page=" + count + "&image_type=photo&editors_choice=true&safesearch=true";

            final String url = "https://api.pexels.com/v1/search?query=" + title[k] + "&per_page=40&page=1";

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

//                    try {
//                        JSONArray urlArray = response.getJSONArray("hits");
//
//                        for (int i = 0; i < urlArray.length(); i++) {
//                            JSONObject currentUrl = urlArray.getJSONObject(i);
//                            String imageUrl = currentUrl.getString("fullHDURL");
//                            ImagePOJO pojo = new ImagePOJO(imageUrl);
//                            pojoList.add(pojo);
//                            Log.i(TAG, "URL: " + imageUrl);
//                        }
//                        if (flag == title.length - 1) {
//                            imageAdapter = new ImageAdapter(MainActivity.this, pojoList);
//                            recyclerView.setAdapter(imageAdapter);
//                            progressBar.setVisibility(View.GONE);
//                        }
//                        flag++;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    try {

                        Log.i("Resp", "onResponse: " + response);
                        JSONArray array = response.getJSONArray("photos");

                        for (int i = 0; i < array.length(); i++) {

                            JSONObject src = array.getJSONObject(i);
                            JSONObject currentObj = src.getJSONObject("src");
                            String imageUrl = currentObj.getString("large2x");
                            Log.i("URL", "" + imageUrl);
                            ImagePOJO pojo = new ImagePOJO(imageUrl);
                            pojoList.add(pojo);
                        }
                        if (flag == title.length - 1) {
                            imageAdapter = new ImageAdapter(MainActivity.this, pojoList);
                            recyclerView.setAdapter(imageAdapter);
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.i(TAG, "Flag" + flag);
                        flag++;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG, "onErrorResponse: ");
                            flag++;
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", getResources().getString(R.string.api_key));
                    return params;
                }
            };

            requestQueue.add(jsonRequest);

        }
    }

}

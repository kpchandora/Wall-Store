package developer.code.kpchandora.wallstore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import developer.code.kpchandora.wallstore.Database.CategoryContract;
import developer.code.kpchandora.wallstore.Database.DbHelper;

public class DownloadActivity extends RootAnimActivity {

    private static final String TAG = "DownloadActivity";

    private RecyclerView recyclerView;
    private View view;
    private ArrayList<DownloadModel> uriList;
    private ArrayList<Integer>integerArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        setTitle("Downloads");

        recyclerView = findViewById(R.id.download_listView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        view = findViewById(R.id.empty_downloads);

        uriList = new ArrayList<>();
        integerArrayList = new ArrayList<>();

        registerForContextMenu(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new LoadImages().execute();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_delete, menu);
        Log.i(TAG, "onCreateContextMenu: Size "+uriList.size());

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = ((DownloadAdapter) recyclerView.getAdapter()).getPositionOfImage();
        ((DownloadAdapter) recyclerView.getAdapter()).deleteImage(position);
        deleteCategory( position);
        Log.i(TAG, "onContextItemSelected: "+uriList.size());
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(int position) {
        Log.i(TAG, "deleteCategory: Size "+uriList.size());
        DbHelper helper = new DbHelper(DownloadActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        Log.i(TAG, "deleteCategory:Pos "+position);


        long numRow = db.delete(CategoryContract.DOWNLOAD_TABLE, CategoryContract.DOWNLOAD_ID + "=?",
                new String[]{"" + integerArrayList.get(position)});

        Log.i(TAG, "deleteCategory: " + numRow);

        if (uriList.size() < 1) {
            view.setVisibility(View.VISIBLE);
        }

    }

    private class LoadImages extends AsyncTask<Void, Void, ArrayList<DownloadModel>> {

        @Override
        protected ArrayList<DownloadModel> doInBackground(Void... voids) {
            return fetchWallpapers();
        }

        @Override
        protected void onPostExecute(ArrayList<DownloadModel> arrayList) {

            integerArrayList.clear();

            if (arrayList != null && arrayList.size() > 0) {
                view.setVisibility(View.GONE);
                for (int i = 0; i < arrayList.size(); i++){
                    integerArrayList.add(arrayList.get(i).getId());
                }
            }
            uriList.clear();

            uriList = arrayList;
            Log.i(TAG, "onPostExecute: I"+integerArrayList.size());
            Log.i(TAG, "onPostExecute: "+uriList.size());
            DownloadAdapter adapter = new DownloadAdapter(DownloadActivity.this, arrayList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    }

    private ArrayList<DownloadModel> fetchWallpapers() {

        DbHelper helper = new DbHelper(DownloadActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();

        ArrayList<DownloadModel> uriList = new ArrayList<>();

        String orderBy = CategoryContract.DOWNLOAD_ID + " DESC";

        Cursor cursor = db.query(
                CategoryContract.DOWNLOAD_TABLE,
                null,
                null,
                null,
                null,
                null,
                orderBy
        );

        int uriIndex = cursor.getColumnIndex(CategoryContract.DOWNLOAD_URI);
        int idIndex = cursor.getColumnIndex(CategoryContract.DOWNLOAD_ID);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String uri = cursor.getString(uriIndex);
            uriList.add(new DownloadModel(id, uri));
        }
        cursor.close();
        db.close();
        return uriList;
    }

}

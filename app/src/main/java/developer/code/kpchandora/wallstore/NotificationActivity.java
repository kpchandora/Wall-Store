package developer.code.kpchandora.wallstore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import developer.code.kpchandora.wallstore.Database.DbHelper;

import static developer.code.kpchandora.wallstore.Database.CategoryContract.*;


public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";

    private RecyclerView recyclerView;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setTitle("Notifications");

        view = findViewById(R.id.empty_notification);

        recyclerView = findViewById(R.id.notificationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

//        deleteAfterCertainTime();

        new FetchNotification().execute();

    }

    private class FetchNotification extends AsyncTask<Void, Void, ArrayList<NotificationModel>> {


        @Override
        protected ArrayList<NotificationModel> doInBackground(Void... voids) {
            return fetchData();
        }

        @Override
        protected void onPostExecute(ArrayList<NotificationModel> notificationModels) {

            if (notificationModels != null && notificationModels.size() > 0) {
                view.setVisibility(View.GONE);
            }

            NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, notificationModels);
            recyclerView.setAdapter(adapter);
            Log.i(TAG, "onPostExecute: ");
        }
    }

    private ArrayList<NotificationModel> fetchData() {

        ArrayList<NotificationModel> models = new ArrayList<>();

        DbHelper helper = new DbHelper(NotificationActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String orderBy = NOTIFICATION_ID_COLUMN + " DESC";

        Cursor cursor = db.query(
                TABLE_NOTIFICATION,
                null,
                null,
                null,
                null,
                null,
                orderBy
        );

        int idIndex = cursor.getColumnIndex(NOTIFICATION_ID_COLUMN);
        int titleIndex = cursor.getColumnIndex(NOTIFICATION_TITLE_COLUMN);
        int contentIndex = cursor.getColumnIndex(NOTIFICATION_CONTENT_COLUMN);
        int imageUrlIndex = cursor.getColumnIndex(IMAGE_URL_COLUMN);
        int currentTimeIndex = cursor.getColumnIndex(NOTIFICATION_TIME_STAMP);
        int expTimeIndex = cursor.getColumnIndex(NOTIFICATION_EXP_TIME);
        int totalTimeIndex = cursor.getColumnIndex(NOTIFICATION_TOTAL_TIME);
        int flagIndex = cursor.getColumnIndex(NOTIFICATION_FLAG);

        while (cursor.moveToNext()) {

            int id = cursor.getInt(idIndex);
            String title = cursor.getString(titleIndex);
            String content = cursor.getString(contentIndex);
            String url = cursor.getString(imageUrlIndex);
            long timeStamp = cursor.getLong(currentTimeIndex);
            int flag = cursor.getInt(flagIndex);

            Log.i(TAG, "fetchData: " + title + " url" + url);
            NotificationModel model = new NotificationModel(id, title, content, url, timeStamp);
            model.setFlag(flag);
            models.add(model);

        }
        cursor.close();
        Log.i(TAG, "fetchData: " + models.size());
        return models;
    }

    private void deleteAfterCertainTime() {

        DbHelper databaseHelper = new DbHelper(NotificationActivity.this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String selection = NOTIFICATION_TOTAL_TIME + "<?";
        String[] selectionArgs = {"" + System.currentTimeMillis()};

        long deletedID = db.delete(
                TABLE_NOTIFICATION,
                selection,
                selectionArgs
        );
        Log.i(TAG, "deleteAfterCertainTime: " + deletedID);

    }

}

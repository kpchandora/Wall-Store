package developer.code.kpchandora.wallstore;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

import developer.code.kpchandora.wallstore.Database.DbHelper;


import static developer.code.kpchandora.wallstore.Database.CategoryContract.*;

/**
 * Created by kpchandora on 30/1/18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyMHolder> {

    private ArrayList<NotificationModel> modelArrayList;
    private Context context;
    private static final String TAG = "Adapter";

    public NotificationAdapter(Context context, ArrayList<NotificationModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
        Log.i(TAG, "NotificationAdapter: ");
    }

    @Override
    public MyMHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_layout, null);
        Log.i(TAG, "onCreateViewHolder: ");
        return new MyMHolder(view, modelArrayList, context);
    }

    @Override
    public void onBindViewHolder(final MyMHolder holder, int position) {

        NotificationModel model = modelArrayList.get(position);
        holder.notificationTitle.setText(model.getTitle());
        holder.notificationContent.setText(model.getContent());

        final String imageUrl = model.getImageUrl();

        long currentTime = System.currentTimeMillis();
        long notificationTime = model.getTimeStamp();

        long timeSpent = currentTime - notificationTime;

        String timeToShow = "";

        final int id = model.getId();
        final int flag = model.getFlag();

        if (timeSpent < 60000) {
            timeSpent = timeSpent / 1000;
            timeToShow = timeSpent + "s";
        } else if (timeSpent < 60 * 60000) {
            timeSpent = timeSpent / (60000);
            timeToShow = timeSpent + "m";
        } else if (timeSpent < 60 * 60 * 60000) {
            timeSpent = timeSpent / (60 * 60000);
            timeToShow = timeSpent + "h";
        } else {
            timeSpent = timeSpent / (60 * 60 * 60000);
            timeToShow = timeSpent + "d";
        }

        if (flag == 1) {
            holder.layout.setBackgroundColor(Color.WHITE);
        }

        holder.timeSpent.setText(timeToShow);

        if (imageUrl != null && !imageUrl.equals("")) {


            Picasso.with(context)
                    .load(imageUrl)
                    .fit()
                    .into(holder.notificationImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });
//            Toast.makeText(context, ""+imageUrl, Toast.LENGTH_SHORT).show();
        }


        Log.i("Adapter", "onBindViewHolder: ");
    }

    private void updateFlag(int id, int flag) {

        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_FLAG, flag);

        long rowsAffected = db.update(TABLE_NOTIFICATION, values,
                NOTIFICATION_ID_COLUMN + "=?", new String[]{"" + id});
        Log.i("NotificationAdapter", "updateFlag: " + rowsAffected);

    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class MyMHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context ctx;
        TextView notificationTitle, notificationContent;
        ImageView notificationImage;
        TextView timeSpent;
        LinearLayout layout;
        ProgressBar progressBar;
        private ArrayList<NotificationModel> arrayList;

        public MyMHolder(View itemView, ArrayList<NotificationModel> notificationModels, Context ctx) {
            super(itemView);
            this.ctx = ctx;
            progressBar = itemView.findViewById(R.id.noti_progressBar);
            this.arrayList = notificationModels;
            notificationContent = itemView.findViewById(R.id.noti_content);
            notificationTitle = itemView.findViewById(R.id.noti_title);
            notificationImage = itemView.findViewById(R.id.noti_image);
            timeSpent = itemView.findViewById(R.id.time_spent);
            layout = itemView.findViewById(R.id.notification_linearLayout);//#dfd9d9
            notificationImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            NotificationModel model = arrayList.get(position);
            String url = model.getImageUrl();

            Intent intent = new Intent(context, ImageOpenActivity.class);
            intent.putExtra("NotiUrl", url);
            intent.putExtra("TAG", 1);
            context.startActivity(intent);
        }
    }
}

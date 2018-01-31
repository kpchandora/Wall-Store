package developer.code.kpchandora.wallstore;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class ImageOpenActivity extends AppCompatActivity {

    private static String url = "";
    private int width, height;

    private ImageView imageView;
    private ProgressBar progressBar;

    private String new_url = "";

    private int notificationId;
    private Context context;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_open);

        notificationId = new Random().nextInt(50);

        String title = "";

        context = ImageOpenActivity.this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("URL");
            title = bundle.getString("Title");
            if (url.contains("dpr=2")) {
//                url = url.replace("dpr=2", "dpr=3");
            }
            if (url.contains("&auto=compress")) {
                url = url.replace("&auto=compress", "&fit=crop");
            }

//            String regex = "w=\\d\\d\\d\\d?&h=\\d\\d\\d\\d?";
//            new_url = url.replaceAll(regex, "w=1200&h=1200");

        }
        imageView = findViewById(R.id.open_imageView);
        progressBar = findViewById(R.id.open_activity_progressBar);

        setTitle(title);

        Picasso.with(this)
                .load(url)
                .fit()

                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        findViewById(R.id.wallpaper_set_button).setEnabled(true);
                        findViewById(R.id.download_button).setEnabled(true);
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.GONE);
                    }
                });


//        Glide.with(this)
//                .load(url)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .centerCrop()
//                .into(imageView);


    }

    public void downloadClick(View view) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            permissionDialog();

        } else {
            new DownloadImage().execute();
        }

    }

    private void sendNotification(String uri, Bitmap bitmap, int flag) {

        String CHANNEL_ID = "notification_channel";

        builder = new NotificationCompat.Builder(ImageOpenActivity.this, CHANNEL_ID)
                .setSound(null);


         manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(uri), "image/*");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                ImageOpenActivity.this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT

        );

        if (flag == 0) {
            manager.cancel(notificationId);
            return;
        }

        if (uri.equals("")) {

            builder.setSmallIcon(R.drawable.downloading)
                    .setContentTitle("Downloading...")
                    .setProgress(0, 0, true);

        } else {

            builder.setSmallIcon(R.drawable.downloaded)
                    .setLargeIcon(bitmap)
                    .setContentTitle("Download Successful")
                    .setContentText("Tap to open")
                    .setProgress(0, 0, false);
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap));
            builder.setContentIntent(pendingIntent);
        }


        NotificationChannel channel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        if (manager != null) {
            builder.setAutoCancel(true);
            manager.notify(notificationId, builder.build());
        }

    }



    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        AlertDialog alertDialog;
        ImageOpenActivity activity;


        @Override
        protected void onPreExecute() {
//            alertDialog.show();
//            alertDialog.setMessage("Downloading...");
            sendNotification("", null, 1);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;

            try {
                bitmap = Glide.with(ImageOpenActivity.this)
                        .load(url)
                        .asBitmap()
                        .into(-1, -1)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, "");
//                alertDialog.dismiss();
                sendNotification(uri, bitmap, 1);
                Toast.makeText(context, "Downloaded Successfully", Toast.LENGTH_LONG).show();
            } else {
//                alertDialog.dismiss();
                sendNotification("", null, 0);
                Toast.makeText(context, "Downloading Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setWallpaper(View view) {
        new SetWallpaper(ImageOpenActivity.this).execute();
    }

    private class SetWallpaper extends AsyncTask<Void, Void, Bitmap> {

        AlertDialog alertDialog;
        Context context;
        WallpaperManager wallpaperManager;
        BitmapDrawable bitmapDrawable;

        SetWallpaper(Context context) {
            this.context = context;
            alertDialog = new SpotsDialog(context);
            wallpaperManager = WallpaperManager.getInstance(context);
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
            alertDialog.setMessage("Setting Wallpaper...");
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {

            bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            getScreenWidthHeight();
            Bitmap bitmapB = Bitmap.createScaledBitmap(bitmap, width, height, false);

            return bitmapB;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            try {
                wallpaperManager.setBitmap(bitmap);
                wallpaperManager.suggestDesiredDimensions(width, height);
                alertDialog.dismiss();
                Toast.makeText(ImageOpenActivity.this, "Wallpaper set succcessfully", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                alertDialog.dismiss();
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }
    }


    public void getScreenWidthHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        Log.i("DM", "getScreenWidthHeight: " + width + "\n" + height);
    }

    private void permissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageOpenActivity.this);
        builder.setTitle("Permission");
        builder.setMessage("This app needs permission")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openSettings();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}

package com.example.kpchandora.wallpaperapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class ImageOpenActivity extends AppCompatActivity {

    private String url = "";
    private int width, height;

    private DisplayMetrics dm;
    private WallpaperManager wallpaperManager;
    private Bitmap bitmap1, bitmap2;
    private BitmapDrawable bitmapDrawable;

    private ImageView imageView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_open);

        String title = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("URL");
            title = bundle.getString("Title");
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

            AlertDialog alertDialog = new SpotsDialog(ImageOpenActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Downloading...");

            String fileName = UUID.randomUUID().toString() + ".jpg";
            Picasso.with(getBaseContext())
                    .load(url)
                    .into(new SaveImageHelper(getBaseContext(),
                            alertDialog,
                            getApplicationContext().getContentResolver(),
                            fileName,
                            "Image Description"));
        }
    }

    public void setWallpaper(View view) {

        final AlertDialog alertDialog = new SpotsDialog(ImageOpenActivity.this);
        alertDialog.show();
        alertDialog.setMessage("Setting Wallpaper...");

        Glide.with(ImageOpenActivity.this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {


                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                wallpaperManager = WallpaperManager.getInstance(ImageOpenActivity.this);
                                bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                                bitmap1 = bitmapDrawable.getBitmap();
                                GetScreenWidthHeight();
                                bitmap2 = Bitmap.createScaledBitmap(bitmap1, width, height, false);
                                wallpaperManager = WallpaperManager.getInstance(ImageOpenActivity.this);
                                try {
                                    wallpaperManager.setBitmap(bitmap2);
                                    wallpaperManager.suggestDesiredDimensions(width, height);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                        Toast.makeText(ImageOpenActivity.this, "Wallpaper set succcessfully", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

    }

    public void GetScreenWidthHeight() {
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
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

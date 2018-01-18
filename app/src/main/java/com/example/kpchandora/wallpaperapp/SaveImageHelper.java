package com.example.kpchandora.wallpaperapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

/**
 * Created by kpchandora on 16/1/18.
 */

public class SaveImageHelper implements Target {

    private Context context;
    private WeakReference<AlertDialog> alertDialogWeakReference;
    private WeakReference<ContentResolver> resolverWeakReference;
    private String name;
    private String desc;

    public SaveImageHelper(Context context, AlertDialog alertDialog, ContentResolver resolver, String name, String desc) {
        this.context = context;
        this.alertDialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
        this.resolverWeakReference = new WeakReference<ContentResolver>(resolver);
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        ContentResolver resolver = resolverWeakReference.get();
        AlertDialog alertDialog = alertDialogWeakReference.get();

        if (resolver != null) {
            MediaStore.Images.Media.insertImage(resolver, bitmap, name, desc);
        }
        alertDialog.dismiss();
        Toast.makeText(context, "Downloaded Successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}

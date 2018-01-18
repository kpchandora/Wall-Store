package com.example.kpchandora.wallpaperapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kpchandora on 15/1/18.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyImageHolder> {

    private List<ImagePOJO> imagePOJOList;
    private Context context;

    public ImageAdapter(Context context, List<ImagePOJO> imagePOJOList) {
        this.context = context;
        this.imagePOJOList = imagePOJOList;
        Log.i("Adapter", "ImageAdapter: ");

    }

    @Override
    public MyImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
        return new MyImageHolder(view, imagePOJOList, context);
    }

    @Override
    public void onBindViewHolder(final MyImageHolder holder, int position) {

        ImagePOJO pojo = imagePOJOList.get(position);
        String url = pojo.getImageUrl();

        Picasso.with(context)
                .load(url)
                .fit()
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });

//        Glide.with(context)
//                .load(url)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        holder.progressBar.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(holder.imageView);


        Log.i("OnBind", "onBindViewHolder: ");

    }

    @Override
    public int getItemCount() {
        return imagePOJOList.size();
    }

    public class MyImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        private List<ImagePOJO> pojoList;
        private Context ctx;
        private ProgressBar progressBar;

        public MyImageHolder(View itemView, List<ImagePOJO> pojoList, Context context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            progressBar = itemView.findViewById(R.id.progressBar);
            this.ctx = context;
            this.pojoList = pojoList;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            ImagePOJO pojo = this.pojoList.get(position);
            String url = pojo.getImageUrl();
            Intent intent = new Intent(context, ImageOpenActivity.class);
            intent.putExtra("URL", url);
            intent.putExtra("Title", MainActivity.toolBarTitle);
            ctx.startActivity(intent);
        }
    }
}

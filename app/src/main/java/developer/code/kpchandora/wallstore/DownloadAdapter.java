package developer.code.kpchandora.wallstore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kpchandora on 3/2/18.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadHolder> {

    private static final String TAG = "DownloadAdapter";

    private Context context;
    private ArrayList<DownloadModel> arrayList;

    private int positionOfImage;
    private int idImage;

    public DownloadAdapter(Context context, ArrayList<DownloadModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public DownloadHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, null);
        return new DownloadHolder(context, view, arrayList);
    }

    @Override
    public void onBindViewHolder(final DownloadHolder holder, final int position) {

        String uri = arrayList.get(position).getUri();

        final int id = arrayList.get(position).getId();

        Picasso.with(context)
                .load(uri)
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

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                positionOfImage = position;
                idImage = id;
                return false;
            }
        });

    }

    public void deleteImage(int position) {

        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();

    }

    public int getPositionOfImage() {
        Log.i(TAG, "getPositionOfImage: " + positionOfImage);
        return positionOfImage;
    }

    public int getIdImage() {
        return idImage;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class DownloadHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ProgressBar progressBar;
        private ArrayList<DownloadModel> uriList;
        private Context context;

        public DownloadHolder(Context context, View itemView, ArrayList<DownloadModel> uriList) {
            super(itemView);
            this.context = context;
            this.uriList = uriList;
            imageView = itemView.findViewById(R.id.image_view);
            progressBar = itemView.findViewById(R.id.progressBar);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(uriList.get(position).getUri()), "image/*");
            context.startActivity(intent);
        }
    }
}

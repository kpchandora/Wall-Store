package developer.code.kpchandora.wallstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kpchandora on 16/1/18.
 */

public class MyImageAdapter extends RecyclerView.Adapter<MyImageAdapter.MyViewHolder> {

    private List<CategoryModel> imagePOJOList;
    private Context context;

    public MyImageAdapter(Context context, List<CategoryModel> imagePOJOList) {

        this.context = context;
        this.imagePOJOList = imagePOJOList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.front_item_layout, parent, false);

        return new MyViewHolder(view, imagePOJOList, context);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        CategoryModel pojo = imagePOJOList.get(position);
        String url = pojo.getImageUrl();
        String title = pojo.getImageTitle();
        holder.titleTextView.setText(title);

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

    }

    @Override
    public int getItemCount() {
        return imagePOJOList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        Context ctx;
        List<CategoryModel> pojos;
        TextView titleTextView;
        ProgressBar progressBar;

        public MyViewHolder(View itemView, List<CategoryModel> pojos, Context context) {
            super(itemView);
            this.ctx = context;
            this.pojos = pojos;
            progressBar = itemView.findViewById(R.id.front_progressBar);
            titleTextView = itemView.findViewById(R.id.text_view);
            imageView = itemView.findViewById(R.id.image_id);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();

            CategoryModel pojo = this.pojos.get(position);
            String name = pojo.getImageTitle();

            Intent i = new Intent(ctx, MainActivity.class);
            i.putExtra("Title", name);
            Bundle b = new Bundle();
            ctx.startActivity(i);

        }
    }
}

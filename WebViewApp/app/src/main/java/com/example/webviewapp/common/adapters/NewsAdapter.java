package com.example.webviewapp.common.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.webviewapp.R;
import com.example.webviewapp.data.NewsItem;
import com.example.webviewapp.ui.activity.MainActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private static final String TAG = "NewsAdapter";

    private final List<NewsItem> news;
    private final Context context;
    private final LayoutInflater inflater;
    private final Integer layoutResId;

    public NewsAdapter(List<NewsItem> news, Context context, Integer layoutResId) {
        this.news = news;
        this.context = context;
        this.layoutResId = layoutResId;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(layoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsItem item = news.get(position);
        holder.title.setText(item.getTitle());
        holder.authorName.setText(item.getAuthorName());
        if (item.getThumbnailPics().size() == 1) {
            RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) holder.newsImage1.getLayoutParams();
            imgParams.addRule(RelativeLayout.RIGHT_OF, R.id.title);
            imgParams.addRule(RelativeLayout.BELOW, RelativeLayout.ALIGN_PARENT_TOP);
            RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) holder.title.getLayoutParams();
            titleParams.width = 600;
            Glide.with(context).load(item.getThumbnailPics().get(0)).into(holder.newsImage1);
        }
        if (item.getThumbnailPics().size() == 2) {
            Glide.with(context).load(item.getThumbnailPics().get(0)).into(holder.newsImage1);
            Glide.with(context).load(item.getThumbnailPics().get(1)).into(holder.newsImage2);
        }
        if (item.getThumbnailPics().size() >= 3) {
            Glide.with(context).load(item.getThumbnailPics().get(0)).into(holder.newsImage1);
            Glide.with(context).load(item.getThumbnailPics().get(1)).into(holder.newsImage2);
            Glide.with(context).load(item.getThumbnailPics().get(2)).into(holder.newsImage3);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("url", item.getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView newsImage1;
        public ImageView newsImage2;
        public ImageView newsImage3;
        public TextView authorName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            newsImage1 = itemView.findViewById(R.id.news_image1);
            newsImage2 = itemView.findViewById(R.id.news_image2);
            newsImage3 = itemView.findViewById(R.id.news_image3);
            authorName = itemView.findViewById(R.id.author_name);
        }
    }
}

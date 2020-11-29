package com.singularitycoder.newstime.sources.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ListItemSourcesBinding;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.home.model.NewsItem;

import java.util.Collections;
import java.util.List;

public final class SourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private List<NewsItem.NewsArticle> newsList = Collections.emptyList();

    @Nullable
    private NewsViewListener newsViewListener;

    @NonNull
    private final Context context;

    public SourcesAdapter(@NonNull final List<NewsItem.NewsArticle> newsList, @NonNull final Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sources, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NewsItem.NewsArticle newsArticle = newsList.get(position);
        if (null != holder && holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
//            newsViewHolder.binding.tvAuthor.setText("Author: " + newsArticle.getAuthor());
//            newsViewHolder.binding.tvTitle.setText(newsArticle.getTitle());
//            newsViewHolder.binding.tvDescription.setText(newsArticle.getDescription());
//            newsViewHolder.binding.tvPublishedAt.setText("Published at: " + newsArticle.getPublishedAt());
//            newsViewHolder.binding.tvSource.setText("Source: " + newsArticle.getSource().getName());
//            appUtils.glideImage(context, newsArticle.getUrlToImage(), newsViewHolder.binding.ivHeaderImage);
            setAnimation(newsViewHolder);
        }
    }

    private void setAnimation(NewsViewHolder newsViewHolder) {
//        newsViewHolder.binding.ivHeaderImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
//        newsViewHolder.binding.cardDetails.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));
    }

    public interface NewsViewListener {
        void onNewsItemClicked(int position);
    }

    public final void setNewsViewListener(NewsViewListener newsViewListener) {
        this.newsViewListener = newsViewListener;
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    final class NewsViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemSourcesBinding binding;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemSourcesBinding.bind(itemView);
            itemView.setOnClickListener(v -> newsViewListener.onNewsItemClicked(getAdapterPosition()));
        }
    }
}
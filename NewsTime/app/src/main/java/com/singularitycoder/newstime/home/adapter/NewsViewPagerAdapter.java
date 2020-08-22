package com.singularitycoder.newstime.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ListItemNewsSwipeBinding;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.home.model.NewsItem;

import java.util.Collections;
import java.util.List;

public final class NewsViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Nullable
    private List<NewsItem.NewsArticle> newsList = Collections.emptyList();

    @Nullable
    private Context context;

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    public NewsViewPagerAdapter(@Nullable List<NewsItem.NewsArticle> newsList, @Nullable Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news_swipe, parent, false);
        return new NewsViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NewsItem.NewsArticle newsArticle = newsList.get(position);
        if (holder instanceof NewsViewPagerViewHolder) {
            final NewsViewPagerViewHolder newsViewPagerViewHolder = (NewsViewPagerViewHolder) holder;
            newsViewPagerViewHolder.binding.tvAuthor.setText(newsArticle.getAuthor());
            newsViewPagerViewHolder.binding.tvTitle.setText(newsArticle.getTitle());
            newsViewPagerViewHolder.binding.tvDescription.setText(newsArticle.getDescription());
            newsViewPagerViewHolder.binding.tvPublishedAt.setText(appUtils.formatDate(newsArticle.getPublishedAt()));
            newsViewPagerViewHolder.binding.tvSource.setText(newsArticle.getSource().getName());
            appUtils.glideImage(context, newsArticle.getUrlToImage(), newsViewPagerViewHolder.binding.ivHeaderImage);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public final class NewsViewPagerViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemNewsSwipeBinding binding;

        public NewsViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemNewsSwipeBinding.bind(itemView);
        }
    }
}

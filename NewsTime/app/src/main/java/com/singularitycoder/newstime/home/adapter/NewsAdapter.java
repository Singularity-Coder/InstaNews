package com.singularitycoder.newstime.home.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ListItemNewsAllDetailsBinding;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.home.model.NewsItem;

import java.util.Collections;
import java.util.List;

public final class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LAYOUT_STANDARD = 0;
    private static final int LAYOUT_ALL_DETAILS = 1;
    private static final int LAYOUT_COMPACT = 2;
    private static final int LAYOUT_FANCY = 3;
    private static final int LAYOUT_ONLY_TEXT = 4;
    private static final int LAYOUT_ONLY_IMAGE = 5;
    private static final int LAYOUT_ONLY_HEADLINES = 6;

    @NonNull
    private final String TAG = "NewsAdapter";

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private AppSharedPreference appSharedPreference;

    @NonNull
    private List<NewsItem.NewsArticle> newsList = Collections.emptyList();

    @Nullable
    private Context context;

    @Nullable
    private NewsViewListener newsViewListener;

    public NewsAdapter(List<NewsItem.NewsArticle> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
        appSharedPreference = AppSharedPreference.getInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == LAYOUT_STANDARD) {
            v = layoutInflater.inflate(R.layout.list_item_news_standard, parent, false);
            return new NewsViewHolder(v);
        }

        if (viewType == LAYOUT_ALL_DETAILS) {
            v = layoutInflater.inflate(R.layout.list_item_news_all_details, parent, false);
            return new NewsViewHolder(v);
        }

        if (viewType == LAYOUT_COMPACT) {
            v = layoutInflater.inflate(R.layout.list_item_news_compact, parent, false);
            return new NewsViewHolder(v);
        }

        if (viewType == LAYOUT_FANCY) {
            v = layoutInflater.inflate(R.layout.list_item_news_fancy, parent, false);
            return new NewsViewHolder(v);
        }

        if (viewType == LAYOUT_ONLY_TEXT) {
            v = layoutInflater.inflate(R.layout.list_item_news_only_text, parent, false);
            return new NewsViewHolder(v);
        }

        if (viewType == LAYOUT_ONLY_IMAGE) {
            v = layoutInflater.inflate(R.layout.list_item_news_only_image, parent, false);
            return new NewsViewHolder(v);
        }

        if (viewType == LAYOUT_ONLY_HEADLINES) {
            v = layoutInflater.inflate(R.layout.list_item_news_only_headline, parent, false);
            return new NewsViewHolder(v);
        }

        v = layoutInflater.inflate(R.layout.list_item_news_all_details, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NewsItem.NewsArticle newsArticle = newsList.get(position);
        if (holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.binding.tvAuthor.setText("Author: " + newsArticle.getAuthor());
            newsViewHolder.binding.tvTitle.setText(newsArticle.getTitle());
            newsViewHolder.binding.tvDescription.setText(newsArticle.getDescription());
            newsViewHolder.binding.tvPublishedAt.setText("Published at: " + appUtils.formatDate(newsArticle.getPublishedAt()));
            newsViewHolder.binding.tvSource.setText("Source: " + newsArticle.getSource().getName());
            appUtils.glideImage(context, newsArticle.getUrlToImage(), newsViewHolder.binding.ivHeaderImage);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Log.d(TAG, "getItemViewType: sp: " + appSharedPreference.getNewsLayout());

        if (null == appSharedPreference) return LAYOUT_ALL_DETAILS;

        if (("").equals(appSharedPreference.getNewsLayout()) || null == appSharedPreference.getNewsLayout()) {
            return LAYOUT_ALL_DETAILS;
        }

        if (("Standard").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_STANDARD;
        }

        if (("All Details").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_ALL_DETAILS;
        }

        if (("Compact").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_COMPACT;
        }

        if (("Fancy").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_FANCY;
        }

        if (("Only Text").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_ONLY_TEXT;
        }

        if (("Only Image").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_ONLY_IMAGE;
        }

        if (("Only Headlines").equals(appSharedPreference.getNewsLayout())) {
            return LAYOUT_ONLY_HEADLINES;
        }

        return LAYOUT_ALL_DETAILS;
    }

    public interface NewsViewListener {
        void onNewsItemClicked(int position);
    }

    public final void setNewsViewListener(NewsViewListener newsViewListener) {
        this.newsViewListener = newsViewListener;
    }

    final class NewsViewHolder extends RecyclerView.ViewHolder {
        @Nullable
        private ListItemNewsAllDetailsBinding binding;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemNewsAllDetailsBinding.bind(itemView);
            itemView.setOnClickListener(view -> newsViewListener.onNewsItemClicked(getAdapterPosition()));
        }
    }
}
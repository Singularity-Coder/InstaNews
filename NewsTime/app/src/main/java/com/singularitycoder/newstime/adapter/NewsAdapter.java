package com.singularitycoder.newstime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding3.view.RxView;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ItemNewsBinding;
import com.singularitycoder.newstime.helper.HelperGeneral;
import com.singularitycoder.newstime.model.NewsItem;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public final class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private final String TAG = "NewsAdapter";

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @NonNull
    private List<NewsItem.NewsArticle> newsList = Collections.emptyList();

    @Nullable
    private Context context;

    @Nullable
    private NewsViewListener newsViewListener;

    public NewsAdapter(List<NewsItem.NewsArticle> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NewsItem.NewsArticle newsArticle = newsList.get(position);
        if (null != holder) {
            NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.binding.tvAuthor.setText("Author: " + newsArticle.getAuthor());
            newsViewHolder.binding.tvTitle.setText(newsArticle.getTitle());
            newsViewHolder.binding.tvDescription.setText(newsArticle.getDescription());
            newsViewHolder.binding.tvPublishedAt.setText("Published at: " + newsArticle.getPublishedAt());
            newsViewHolder.binding.tvSource.setText("Source: " + newsArticle.getSource().getName());
            helperObject.glideImage(context, newsArticle.getUrlToImage(), newsViewHolder.binding.ivHeaderImage);
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

    public interface NewsViewListener {
        void onNewsItemClicked(int position);
    }

    public final void setNewsViewListener(NewsViewListener newsViewListener) {
        this.newsViewListener = newsViewListener;
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ItemNewsBinding binding;

        @NonNull
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsBinding.bind(itemView);

            compositeDisposable.add(
                    RxView.clicks(itemView)
                            .map(o -> itemView)
                            .subscribe(
                                    button -> newsViewListener.onNewsItemClicked(getAdapterPosition()),
                                    throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            )
            );
        }
    }
}
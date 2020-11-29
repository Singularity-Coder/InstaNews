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
import com.singularitycoder.newstime.sources.model.SourceItem;

import java.util.Collections;
import java.util.List;

public final class SourcesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private List<SourceItem.SourceNews> sourceList = Collections.emptyList();

    @Nullable
    private SourceViewListener sourceViewListener;

    @Nullable
    private SourceViewLinkListener sourceViewLinkListener;

    @NonNull
    private final Context context;

    public SourcesAdapter(@NonNull final List<SourceItem.SourceNews> sourceList, @NonNull final Context context) {
        this.sourceList = sourceList;
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
        final SourceItem.SourceNews sourceNews = sourceList.get(position);
        if (null != holder && holder instanceof NewsViewHolder) {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.binding.tvSourceCategory.setText(sourceNews.getCategory());
            newsViewHolder.binding.tvSourceName.setText(sourceNews.getName());
            newsViewHolder.binding.tvSourceDescription.setText(sourceNews.getDescription());
            setAnimation(newsViewHolder);
        }
    }

    private void setAnimation(NewsViewHolder newsViewHolder) {
        newsViewHolder.binding.ivHeaderImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        newsViewHolder.binding.cardSourceRoot.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale));
    }

    public interface SourceViewListener {
        void onSourceItemClicked(int position);
    }

    public interface SourceViewLinkListener {
        void onSourceItemLinkClicked(int position);
    }

    public final void setSourceViewListener(@NonNull final SourceViewListener sourceViewListener) {
        this.sourceViewListener = sourceViewListener;
    }

    public final void setSourceViewLinkListener(@NonNull final SourceViewLinkListener sourceViewLinkListener) {
        this.sourceViewLinkListener = sourceViewLinkListener;
    }

    @Override
    public int getItemCount() {
        return sourceList.size();
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
            itemView.setOnClickListener(v -> sourceViewListener.onSourceItemClicked(getAdapterPosition()));
            binding.ivOpenLink.setOnClickListener(v -> sourceViewLinkListener.onSourceItemLinkClicked(getAdapterPosition()));
        }
    }
}
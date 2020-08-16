package com.singularitycoder.newstime.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ListItemMoreBinding;
import com.singularitycoder.newstime.databinding.ListItemMoreHeaderBinding;

import java.util.Collections;
import java.util.List;

public final class MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MORE_HEADER = 0;
    private static final int MORE_ITEM = 1;

    @NonNull
    private static final String TAG = "MoreAdapter";

    @NonNull
    private List<MoreItem> moreList = Collections.EMPTY_LIST;

    @Nullable
    private Context context;

    @Nullable
    private ItemClickListener itemClickListener;

    public MoreAdapter(List<MoreItem> moreList, Context context) {
        this.moreList = moreList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case MORE_HEADER:
                v = layoutInflater.inflate(R.layout.list_item_more_header, parent, false);
                return new MoreHeaderViewHolder(v);
            default:
                v = layoutInflater.inflate(R.layout.list_item_more, parent, false);
                return new MoreViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MoreItem moreItem = moreList.get(position);
        if (holder instanceof MoreViewHolder) {
            final MoreViewHolder moreViewHolder = (MoreViewHolder) holder;
            moreViewHolder.binding.tvMoreSubtitle.setText(moreItem.getSubtitle());
            moreViewHolder.binding.tvMoreTitle.setText(moreItem.getTitle());
            moreViewHolder.binding.tvMoreTitle.setTextColor(context.getResources().getColor(moreItem.getTitleColor()));
            moreViewHolder.binding.ivMore.setImageResource(moreItem.getIcon());
            moreViewHolder.binding.ivMore.setColorFilter(context.getResources().getColor(moreItem.getIconColor()));
        } else if (holder instanceof MoreHeaderViewHolder) {
            final MoreHeaderViewHolder moreHeaderViewHolder = (MoreHeaderViewHolder) holder;
            moreHeaderViewHolder.binding.tvVersion.setText(moreItem.getVersion());
        }
    }

    @Override
    public int getItemCount() {
        return moreList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? MORE_HEADER : MORE_ITEM;
    }

    public interface ItemClickListener {
        void onItemClick(final int position, @Nullable final TextView tvMoreSubtitle);
    }

    public final void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    final class MoreHeaderViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemMoreHeaderBinding binding;

        public MoreHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemMoreHeaderBinding.bind(itemView);
        }
    }

    final class MoreViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemMoreBinding binding;

        public MoreViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemMoreBinding.bind(itemView);
            itemView.setOnClickListener(view -> {
                try {
                    itemClickListener.onItemClick(getAdapterPosition(), binding.tvMoreSubtitle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

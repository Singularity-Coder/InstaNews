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
import com.singularitycoder.newstime.helper.AppSharedPreference;

import java.util.Collections;
import java.util.List;

public final class MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MORE_HEADER = 0;
    private static final int MORE_ITEM = 1;

    @Nullable
    private AppSharedPreference appSharedPreference;

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
        appSharedPreference = AppSharedPreference.getInstance(context);
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
            if (1 == position) setDefaultCountry(moreViewHolder);
            if (2 == position) setDefaultNewsLayout(moreViewHolder);
            if (3 == position) setDefaultAppLanguage(moreViewHolder);
            if (4 == position) setDefaultAppTheme(moreViewHolder);
        }

        if (holder instanceof MoreHeaderViewHolder) {
            final MoreHeaderViewHolder moreHeaderViewHolder = (MoreHeaderViewHolder) holder;
            moreHeaderViewHolder.binding.tvVersion.setText(moreItem.getVersion());
        }
    }

    private void setDefaultCountry(@NonNull final MoreViewHolder moreViewHolder) {
        if (null != appSharedPreference.getCountry() && !("").equals(appSharedPreference.getCountry())) {
            String[] countriesArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
            String[] countriesArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
            for (int i = 0; i < countriesArray.length; i++) {
                if ((countriesArray[i]).equals(appSharedPreference.getCountry())) {
                    moreViewHolder.binding.tvMoreSubtitle.setText("Country: " + countriesArrayAlias[i]);
                }
            }
        } else {
            moreViewHolder.binding.tvMoreSubtitle.setText("Country: India");
        }
    }

    private void setDefaultNewsLayout(@NonNull final MoreViewHolder moreViewHolder) {
        if (null != appSharedPreference.getNewsLayout() && !("").equals(appSharedPreference.getNewsLayout())) {
            moreViewHolder.binding.tvMoreSubtitle.setText("Layout: " + appSharedPreference.getNewsLayout());
        } else {
            moreViewHolder.binding.tvMoreSubtitle.setText("Layout: Standard");
        }
    }

    private void setDefaultAppLanguage(@NonNull final MoreViewHolder moreViewHolder) {
        if (null != appSharedPreference.getAppLanguage() && !("").equals(appSharedPreference.getAppLanguage())) {
            moreViewHolder.binding.tvMoreSubtitle.setText("App Language: " + appSharedPreference.getAppLanguage());
        } else {
            moreViewHolder.binding.tvMoreSubtitle.setText("App Language: English");
        }
    }

    private void setDefaultAppTheme(@NonNull final MoreViewHolder moreViewHolder) {
        if (null != appSharedPreference.getAppTheme() && !("").equals(appSharedPreference.getAppTheme())) {
            moreViewHolder.binding.tvMoreSubtitle.setText("App Theme: " + appSharedPreference.getAppTheme());
        } else {
            moreViewHolder.binding.tvMoreSubtitle.setText("App Theme: Light Mode");
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

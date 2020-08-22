package com.singularitycoder.newstime.intro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentIntroBinding;

public final class ViewPager2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int[] layouts;

    @Nullable
    private Context context;

    public ViewPager2Adapter(int[] layouts, Context context) {
        this.layouts = layouts;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewPager2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewPager2ViewHolder viewPager2ViewHolder = (ViewPager2ViewHolder) holder;

        if (0 == position) {
            viewPager2ViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_insta_news);
            viewPager2ViewHolder.binding.tvTitle.setText(context.getResources().getString(R.string.app_name));
            viewPager2ViewHolder.binding.tvSubtitle.setText("Get Updated Instantly!");
        }

        if (1 == position) {
            viewPager2ViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
            viewPager2ViewHolder.binding.tvTitle.setText("Weather");
            viewPager2ViewHolder.binding.tvSubtitle.setText("Get Weather News Instantly!");
        }

        if (2 == position) {
            viewPager2ViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_baseline_favorite_24);
            viewPager2ViewHolder.binding.tvTitle.setText("Favorites");
            viewPager2ViewHolder.binding.tvSubtitle.setText("Save your favorite News Instantly!");
        }

        if (3 == position) {
            viewPager2ViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_baseline_tune_24);
            viewPager2ViewHolder.binding.tvTitle.setText("Customise");
            viewPager2ViewHolder.binding.tvSubtitle.setText("Control what you see Instantly!");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return layouts[position];
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    public final class ViewPager2ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private FragmentIntroBinding binding;

        public ViewPager2ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentIntroBinding.bind(itemView);
        }
    }
}

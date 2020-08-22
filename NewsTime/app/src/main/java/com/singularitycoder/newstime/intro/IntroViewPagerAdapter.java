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

public final class IntroViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int[] layouts;

    @Nullable
    private Context context;

    public IntroViewPagerAdapter(int[] layouts, Context context) {
        this.layouts = layouts;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new IntroViewPagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IntroViewPagerViewHolder introViewPagerViewHolder = (IntroViewPagerViewHolder) holder;

        if (0 == position) {
            introViewPagerViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_insta_news);
            introViewPagerViewHolder.binding.tvTitle.setText(context.getResources().getString(R.string.app_name));
            introViewPagerViewHolder.binding.tvSubtitle.setText("Get Updated Instantly!");
        }

        if (1 == position) {
            introViewPagerViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
            introViewPagerViewHolder.binding.tvTitle.setText("Weather");
            introViewPagerViewHolder.binding.tvSubtitle.setText("Get Weather News Instantly!");
        }

        if (2 == position) {
            introViewPagerViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_baseline_favorite_24);
            introViewPagerViewHolder.binding.tvTitle.setText("Favorites");
            introViewPagerViewHolder.binding.tvSubtitle.setText("Save your favorite News Instantly!");
        }

        if (3 == position) {
            introViewPagerViewHolder.binding.ivHeaderImage.setImageResource(R.drawable.ic_baseline_tune_24);
            introViewPagerViewHolder.binding.tvTitle.setText("Customise");
            introViewPagerViewHolder.binding.tvSubtitle.setText("Control what you see Instantly!");
        }
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return layouts[position];
    }

    public final class IntroViewPagerViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private FragmentIntroBinding binding;

        public IntroViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FragmentIntroBinding.bind(itemView);
        }
    }
}

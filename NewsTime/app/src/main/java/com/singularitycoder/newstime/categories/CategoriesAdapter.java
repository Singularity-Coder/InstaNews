package com.singularitycoder.newstime.categories;

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
import com.singularitycoder.newstime.databinding.ListItemCategoriesBinding;
import com.singularitycoder.newstime.helper.AppUtils;

import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public final class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    private AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private List<CategoriesItem> categoriesList = Collections.EMPTY_LIST;

    @Nullable
    private Context context;

    @Nullable
    private CategoryClickListener categoryClickListener;

    public CategoriesAdapter(List<CategoriesItem> categoriesList, Context context) {
        this.categoriesList = categoriesList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.list_item_categories, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoriesItem categoriesItem = categoriesList.get(position);
        if (holder instanceof CategoriesViewHolder && null != holder) {
            CategoriesViewHolder categoriesViewHolder = (CategoriesViewHolder) holder;

            appUtils.glideImage(context, categoriesItem.getCategoryImage(), categoriesViewHolder.binding.ivCategories);
            categoriesViewHolder.binding.tvCategoriesTitle.setText(categoriesItem.getCategoryName());
        }
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface CategoryClickListener {
        void onCategoryItemClick(int position);
    }

    public final void setCategoryClickListener(CategoryClickListener categoryClickListener) {
        this.categoryClickListener = categoryClickListener;
    }

    public final class CategoriesViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        private ListItemCategoriesBinding binding;

        @NonNull
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();

        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemCategoriesBinding.bind(itemView);

            compositeDisposable.add(
                    RxView.clicks(itemView)
                            .map(o -> itemView)
                            .subscribe(
                                    button -> categoryClickListener.onCategoryItemClick(getAdapterPosition()),
                                    throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            )
            );
        }
    }
}

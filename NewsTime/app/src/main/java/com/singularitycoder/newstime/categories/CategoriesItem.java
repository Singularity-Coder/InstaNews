package com.singularitycoder.newstime.categories;

public final class CategoriesItem {

    private String categoryImage;
    private String categoryName;

    public CategoriesItem(String categoryImage, String categoryName) {
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

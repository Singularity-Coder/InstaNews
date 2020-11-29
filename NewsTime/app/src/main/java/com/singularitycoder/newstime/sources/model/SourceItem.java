package com.singularitycoder.newstime.sources.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public final class SourceItem {

    public final class SourceResponse {
        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("sources")
        @Expose
        private List<SourceNews> sources = new ArrayList<>();

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<SourceNews> getSources() {
            return sources;
        }

        public void setSources(List<SourceNews> sources) {
            this.sources = sources;
        }
    }

    public final class SourceNews {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("url")
        @Expose
        private String url;

        @SerializedName("category")
        @Expose
        private String category;

        @SerializedName("language")
        @Expose
        private String language;

        @SerializedName("country")
        @Expose
        private String country;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}

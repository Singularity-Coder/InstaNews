package com.singularitycoder.newstime.home.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public final class NewsItem {

    public static final class NewsResponse {

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("totalResults")
        @Expose
        private Integer totalResults;

        @SerializedName("articles")
        @Expose
        private List<NewsArticle> articles = Collections.EMPTY_LIST;

        public List<NewsArticle> getArticles() {
            return articles;
        }
    }

    @Entity(tableName = "table_article")
    public static final class NewsArticle {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "RoomId")
        private int roomId;

        //    @ColumnInfo(name = "source")
        @Ignore
        @SerializedName("source")
        @Expose
        private NewsSource source;

        @ColumnInfo(name = "author")
        @SerializedName("author")
        @Expose
        private String author;

        @ColumnInfo(name = "title")
        @SerializedName("title")
        @Expose
        private String title;

        @ColumnInfo(name = "description")
        @SerializedName("description")
        @Expose
        private String description;

        @ColumnInfo(name = "content")
        @SerializedName("content")
        @Expose
        private String content;

        @ColumnInfo(name = "urlToImage")
        @SerializedName("urlToImage")
        @Expose
        private String urlToImage;

        @ColumnInfo(name = "publishedAt")
        @SerializedName("publishedAt")
        @Expose
        private String publishedAt;

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public NewsSource getSource() {
            return source;
        }

        public void setSource(NewsSource source) {
            this.source = source;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public void setUrlToImage(String urlToImage) {
            this.urlToImage = urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }
    }

    public static final class NewsSource {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("name")
        @Expose
        private String name;

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
    }
}

package com.singularitycoder.newstime.home.roomdao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.singularitycoder.newstime.home.model.NewsItem;

import java.util.List;

@Dao
public interface NewsDaoRoom {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNews(NewsItem.NewsArticle newsArticle);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateNews(NewsItem.NewsArticle newsArticle);

    @Delete
    void deleteNews(NewsItem.NewsArticle newsArticle);

    @Query("SELECT * FROM table_article WHERE RoomId=:id")
    NewsItem.NewsArticle getNews(int id);

    @Query("SELECT * FROM table_article ORDER BY RoomId ASC")
    LiveData<List<NewsItem.NewsArticle>> getAllNews();

    @Query("DELETE FROM table_article")
    void deleteAllNews();
}
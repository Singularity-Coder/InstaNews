package com.singularitycoder.newstime.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.singularitycoder.newstime.model.NewsArticle;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNews(NewsArticle newsArticle);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateNews(NewsArticle newsArticle);

    @Delete
    void deleteNews(NewsArticle newsArticle);

    @Query("SELECT * FROM article_table WHERE RoomId=:id")
    NewsArticle getNews(int id);

    @Query("SELECT * FROM article_table ORDER BY RoomId ASC")
    LiveData<List<NewsArticle>> getAllNews();

    @Query("DELETE FROM article_table")
    void deleteAllNews();
}
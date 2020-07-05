package com.singularitycoder.newstime.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.singularitycoder.newstime.model.NewsItem;
import com.singularitycoder.newstime.roomdao.NewsDaoRoom;

@Database(entities = {NewsItem.NewsArticle.class}, version = 1, exportSchema = false)
public abstract class NewsTimeRoomDatabase extends RoomDatabase {

    @Nullable
    private static NewsTimeRoomDatabase instance;

    @Nullable
    public abstract NewsDaoRoom newsDao();

    @NonNull
    public static synchronized NewsTimeRoomDatabase getInstance(Context context) {
        if (null == instance) {
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), NewsTimeRoomDatabase.class, "newstime_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
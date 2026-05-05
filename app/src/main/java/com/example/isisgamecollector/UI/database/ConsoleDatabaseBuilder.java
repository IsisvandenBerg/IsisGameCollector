package com.example.isisgamecollector.UI.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.isisgamecollector.UI.dao.GameDAO;
import com.example.isisgamecollector.UI.dao.ConsoleDAO;
import com.example.isisgamecollector.UI.dao.UserDAO;
import com.example.isisgamecollector.UI.entities.Game;
import com.example.isisgamecollector.UI.entities.Console;
import com.example.isisgamecollector.UI.entities.User;

@Database(entities = {Console.class, Game.class, User.class}, version = 12, exportSchema = false)
public abstract class ConsoleDatabaseBuilder extends RoomDatabase {
    public abstract ConsoleDAO consoleDAO();
    public abstract GameDAO gameDAO();
    public abstract UserDAO userDAO();

    private static volatile ConsoleDatabaseBuilder INSTANCE;

    static ConsoleDatabaseBuilder getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (ConsoleDatabaseBuilder.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),ConsoleDatabaseBuilder.class,"MyConsoleDatabase.db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

package com.example.isisgamecollector.UI.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.isisgamecollector.UI.entities.Game;

import java.util.List;

@Dao
public interface GameDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Game game);

    @Update
    void update(Game game);

    @Delete
    void delete(Game game);

    @Query("SELECT * FROM GAMES ORDER BY gameID ASC")
    List<Game> getAllGames();
    @Query("SELECT * FROM GAMES WHERE consoleID=:prod ORDER BY gameID ASC ")
    List<Game> getAssociatedGames(int prod);

    @Query("DELETE FROM GAMES")
    void deleteAllGames();
}

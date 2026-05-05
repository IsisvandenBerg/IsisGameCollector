package com.example.isisgamecollector.UI.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class Game {
    @PrimaryKey(autoGenerate = true)
    private int gameID;
    private String gameName;
    private String gameReleaseDate;
    private int consoleID;

    public Game(int gameID, String gameName, String gameReleaseDate, int consoleID) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.gameReleaseDate = gameReleaseDate;
        this.consoleID = consoleID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameReleaseDate() {
        return gameReleaseDate;
    }

    public void setGameReleaseDate(String gameReleaseDate) {
        this.gameReleaseDate = gameReleaseDate;
    }

    public int getConsoleID() {
        return consoleID;
    }

    public void setConsoleID(int consoleID) {
        this.consoleID = consoleID;
    }

    @Override
    public String toString() {
        if (gameReleaseDate == null || gameReleaseDate.isEmpty()) {
            return gameName;
        }
        return gameName + " (" + gameReleaseDate + ")";
    }
}

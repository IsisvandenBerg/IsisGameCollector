package com.example.isisgamecollector.UI.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "consoles")
public class Console {
    @PrimaryKey(autoGenerate = true)
    private int consoleID;
    private String consoleName;
    private String consoleBrand;
    private String consoleReleaseDate;
    private String acquisitionDate;

    public Console(int consoleID, String consoleName, String consoleBrand, String consoleReleaseDate, String acquisitionDate) {
        this.consoleID = consoleID;
        this.consoleName = consoleName;
        this.consoleBrand = consoleBrand;
        this.consoleReleaseDate = consoleReleaseDate;
        this.acquisitionDate = acquisitionDate;
    }

    public int getConsoleID() {
        return consoleID;
    }

    public void setConsoleID(int consoleID) {
        this.consoleID = consoleID;
    }

    public String getConsoleName() {
        return consoleName;
    }

    public void setConsoleName(String consoleName) {
        this.consoleName = consoleName;
    }

    public void setConsoleBrand(String consoleBrand) {
        this.consoleBrand = consoleBrand;
    }

    public String getConsoleBrand() {
        return consoleBrand;
    }

    public void setConsoleReleaseDate(String consoleReleaseDate) {
        this.consoleReleaseDate = consoleReleaseDate;
    }
    public String getConsoleReleaseDate(){
        return consoleReleaseDate;
    }

    public String getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(String acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }
}

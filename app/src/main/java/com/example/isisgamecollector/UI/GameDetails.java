package com.example.isisgamecollector.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.database.Repository;
import com.example.isisgamecollector.UI.entities.Game;
import com.example.isisgamecollector.UI.entities.Console;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GameDetails extends AppCompatActivity {
    EditText editName;
    EditText editDate;
    EditText editAcquisitionDate;
    int gameID;
    String name;
    String date;
    String acquisitionDate;
    int consoleID;
    String consoleRelease;
    Repository repository;
    DatePickerDialog.OnDateSetListener dateListener;
    DatePickerDialog.OnDateSetListener acquisitionDateListener;
    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendarAcquisition = Calendar.getInstance();
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_details);

        repository = new Repository(getApplication());
        editName = findViewById(R.id.gameNameText);
        editDate = findViewById(R.id.gameReleaseDateText);
        editAcquisitionDate = findViewById(R.id.gameAcquisitionDateText);
        Button btnSave = findViewById(R.id.btnSaveGame);

        gameID = getIntent().getIntExtra("id", -1);

        name = getIntent().getStringExtra("name");
        date = getIntent().getStringExtra("date");
        acquisitionDate = getIntent().getStringExtra("acquisitionDate");
        consoleID = getIntent().getIntExtra("consoleID", -1);
        consoleRelease = getIntent().getStringExtra("consoleRelease");

        editName.setText(name);
        editDate.setText(date);
        editAcquisitionDate.setText(acquisitionDate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (consoleRelease == null) {
            Console console = repository.getConsoleByID(consoleID);
            if (console != null) {
                consoleRelease = console.getConsoleReleaseDate();
            }
        }

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(GameDetails.this, dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        acquisitionDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarAcquisition.set(Calendar.YEAR, year);
                myCalendarAcquisition.set(Calendar.MONTH, monthOfYear);
                myCalendarAcquisition.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelAcquisition();
            }
        };

        editAcquisitionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(GameDetails.this, acquisitionDateListener, myCalendarAcquisition
                        .get(Calendar.YEAR), myCalendarAcquisition.get(Calendar.MONTH),
                        myCalendarAcquisition.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGame();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveGame() {
        String gameTitle = editName.getText().toString().trim();
        String gameDateStr = editDate.getText().toString().trim();
        String acqDateStr = editAcquisitionDate.getText().toString().trim();

        if (gameTitle.isEmpty()) {
            editName.setError("Game title is required");
            return;
        }

        if (gameDateStr.isEmpty()) {
            editDate.setError("Release date is required");
            return;
        }

        if (acqDateStr.isEmpty()) {
            editAcquisitionDate.setError("Acquisition date is required");
            return;
        }

        Date gameD, acqD;
        try {
            gameD = sdf.parse(gameDateStr);
            acqD = sdf.parse(acqDateStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Please enter dates in MM/dd/yy format", Toast.LENGTH_LONG).show();
            return;
        }

        Date today = new Date();
        if (acqD.after(today)) {
            Toast.makeText(this, "Acquisition date cannot be in the future", Toast.LENGTH_LONG).show();
            return;
        }

        if (acqD.before(gameD)) {
            Toast.makeText(this, "Acquisition date cannot be before release date", Toast.LENGTH_LONG).show();
            return;
        }

        if (consoleRelease != null) {
            try {
                Date startD = sdf.parse(consoleRelease);
                if (gameD.before(startD)) {
                    Toast.makeText(this, "Game release date must be after console release (" + consoleRelease + ")", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (ParseException e) {
            }
        }

        Game game;
        if (gameID == -1) {
            if (repository.getAllGames().size() == 0) gameID = 1;
            else
                gameID = repository.getAllGames().get(repository.getAllGames().size() - 1).getGameID() + 1;
            game = new Game(gameID, editName.getText().toString(), editDate.getText().toString(), editAcquisitionDate.getText().toString(), consoleID);
            repository.insert(game);
        } else {
            game = new Game(gameID, editName.getText().toString(), editDate.getText().toString(), editAcquisitionDate.getText().toString(), consoleID);
            repository.update(game);
        }
        this.finish();
    }

    private void updateLabel() {
        editDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelAcquisition() {
        editAcquisitionDate.setText(sdf.format(myCalendarAcquisition.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.gamedelete) {
            for (Game game : repository.getAllGames()) {
                if (game.getGameID() == gameID) {
                    repository.delete(game);
                    Toast.makeText(this, "Game deleted successfully", Toast.LENGTH_LONG).show();
                    this.finish();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

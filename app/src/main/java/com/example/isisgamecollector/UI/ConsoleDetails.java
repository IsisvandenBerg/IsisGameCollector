package com.example.isisgamecollector.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConsoleDetails extends AppCompatActivity {
    String name;
    String brand;
    String releaseDate;
    String acquisitionDate;
    int consoleID;
    EditText editName;
    EditText editAcquisitionDate;
    EditText editBrand;
    EditText editReleaseDate;
    Spinner gameSpinner;
    Repository repository;
    DatePickerDialog.OnDateSetListener releaseDateListener;
    DatePickerDialog.OnDateSetListener acquisitionDateListener;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarAcquisition = Calendar.getInstance();
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_console_details);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);

        editName = findViewById(R.id.titletext);
        editAcquisitionDate = findViewById(R.id.acquisitiondatetext);
        editBrand = findViewById(R.id.hoteltext);
        editReleaseDate = findViewById(R.id.startdatetext);

        consoleID = getIntent().getIntExtra("id",-1);
        repository = new Repository(getApplication());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = getIntent().getStringExtra("consoleName");
        brand = getIntent().getStringExtra("consoleBrand");
        releaseDate = getIntent().getStringExtra("consoleReleaseDate");
        acquisitionDate = getIntent().getStringExtra("acquisitionDate");

        if (editName != null) editName.setText(name);
        if (editAcquisitionDate != null) editAcquisitionDate.setText(acquisitionDate);
        if (editBrand != null) editBrand.setText(brand);
        if (editReleaseDate != null) editReleaseDate.setText(releaseDate);

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
                new DatePickerDialog(ConsoleDetails.this, acquisitionDateListener, myCalendarAcquisition
                        .get(Calendar.YEAR), myCalendarAcquisition.get(Calendar.MONTH),
                        myCalendarAcquisition.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        releaseDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelRelease();
            }
        };

        editReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ConsoleDetails.this, releaseDateListener, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsoleDetails.this, GameDetails.class);
                intent.putExtra("consoleID", consoleID);
                intent.putExtra("consoleRelease", editReleaseDate.getText().toString());
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        gameSpinner = findViewById(R.id.gameSpinner);
        repository = new Repository(getApplication());
        updateGameSpinner();
    }

    private void updateGameSpinner() {
        List<Game> filteredGames = new ArrayList<>();
        filteredGames.add(new Game(-1, "Select Game", "", -1));

        List<Game> allGames = repository.getAllGames();
        if (allGames != null) {
            for (Game game : allGames) {
                if (game.getConsoleID() == consoleID) filteredGames.add(game);
            }
        }

        ArrayAdapter<Game> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filteredGames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSpinner.setAdapter(adapter);

        gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Game selectedGame = (Game) parent.getItemAtPosition(position);
                if (selectedGame != null && selectedGame.getGameID() != -1) {
                    Intent intent = new Intent(ConsoleDetails.this, GameDetails.class);
                    intent.putExtra("id", selectedGame.getGameID());
                    intent.putExtra("name", selectedGame.getGameName());
                    intent.putExtra("date", selectedGame.getGameReleaseDate());
                    intent.putExtra("consoleID", selectedGame.getConsoleID());
                    intent.putExtra("consoleRelease", editReleaseDate.getText().toString());
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateLabelRelease() {
        editReleaseDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    private void updateLabelAcquisition() {
        editAcquisitionDate.setText(sdf.format(myCalendarAcquisition.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_consoledetails, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.consolesave) {
            String releaseStr = editReleaseDate.getText().toString();
            String acquisitionStr = editAcquisitionDate.getText().toString();
            Date releaseD, acquisitionD;
            try {
                releaseD = sdf.parse(releaseStr);
                acquisitionD = sdf.parse(acquisitionStr);
            } catch (ParseException e) {
                Toast.makeText(this, "Please enter dates in MM/dd/yy format", Toast.LENGTH_LONG).show();
                return true;
            }

            if (acquisitionD.before(releaseD)) {
                Toast.makeText(this, "Acquisition date cannot be before release date", Toast.LENGTH_LONG).show();
                return true;
            }

            Console console;
            if (consoleID == -1) {
                if (repository.getmAllConsoles().size() == 0) consoleID = 1;
                else
                    consoleID = repository.getmAllConsoles().get(repository.getmAllConsoles().size() - 1).getConsoleID() + 1;
                console = new Console(consoleID, editName.getText().toString(), editBrand.getText().toString(), editReleaseDate.getText().toString(), editAcquisitionDate.getText().toString());
                repository.insert(console);
            } else {
                console = new Console(consoleID, editName.getText().toString(), editBrand.getText().toString(), editReleaseDate.getText().toString(), editAcquisitionDate.getText().toString());
                repository.update(console);
            }
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.consoledelete) {
            for (Console con : repository.getmAllConsoles()) {
                if (con.getConsoleID() == consoleID) {
                    List<Game> games = repository.getAllGames();
                    boolean hasGames = false;
                    for (Game game : games) {
                        if (game.getConsoleID() == consoleID) {
                            hasGames = true;
                            break;
                        }
                    }
                    if (hasGames) {
                        Toast.makeText(this, "Cannot delete console with associated games", Toast.LENGTH_LONG).show();
                    } else {
                        repository.delete(con);
                        Toast.makeText(this, "Console deleted", Toast.LENGTH_LONG).show();
                        this.finish();
                    }
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.notify) {
            String startStr = editReleaseDate.getText().toString();
            Date startD;
            try {
                startD = sdf.parse(startStr);
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_LONG).show();
                return true;
            }

            // Start Date Alert
            Long triggerStart = startD.getTime();
            Intent intentStart = new Intent(ConsoleDetails.this, MyReceiver.class);
            intentStart.putExtra("key", editName.getText().toString() + " was released on this day!");
            PendingIntent senderStart = PendingIntent.getBroadcast(ConsoleDetails.this, consoleID * 100, intentStart, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManagerStart = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManagerStart.set(AlarmManager.RTC_WAKEUP, triggerStart, senderStart);

            Toast.makeText(this, "Alert set for release date", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGameSpinner();
    }
}


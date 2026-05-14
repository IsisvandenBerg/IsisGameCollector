package com.example.isisgamecollector.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
    Repository repository;
    DatePickerDialog.OnDateSetListener releaseDateListener;
    DatePickerDialog.OnDateSetListener acquisitionDateListener;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarAcquisition = Calendar.getInstance();
    String myFormat = "MM/DD/YY";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_console_details);

        editName = findViewById(R.id.titletext);
        editAcquisitionDate = findViewById(R.id.acquisitiondatetext);
        editBrand = findViewById(R.id.hoteltext);
        editReleaseDate = findViewById(R.id.startdatetext);
        Button saveButton = findViewById(R.id.btnSaveConsole);

        saveButton.setOnClickListener(v -> saveConsole());

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

        acquisitionDateListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarAcquisition.set(Calendar.YEAR, year);
            myCalendarAcquisition.set(Calendar.MONTH, monthOfYear);
            myCalendarAcquisition.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelAcquisition();
        };

        editAcquisitionDate.setOnClickListener(v -> new DatePickerDialog(ConsoleDetails.this, acquisitionDateListener, myCalendarAcquisition
                .get(Calendar.YEAR), myCalendarAcquisition.get(Calendar.MONTH),
                myCalendarAcquisition.get(Calendar.DAY_OF_MONTH)).show());

        releaseDateListener = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarStart.set(Calendar.YEAR, year);
            myCalendarStart.set(Calendar.MONTH, monthOfYear);
            myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabelRelease();
        };

        editReleaseDate.setOnClickListener(v -> new DatePickerDialog(ConsoleDetails.this, releaseDateListener, myCalendarStart
                .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                myCalendarStart.get(Calendar.DAY_OF_MONTH)).show());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveConsole() {
        String consoleName = editName.getText().toString().trim();
        String consoleBrand = editBrand.getText().toString().trim();
        String releaseStr = editReleaseDate.getText().toString().trim();
        String acquisitionStr = editAcquisitionDate.getText().toString().trim();

        if (consoleName.isEmpty()) {
            editName.setError("Console title is required");
            return;
        }

        if (consoleBrand.isEmpty()) {
            editBrand.setError("Brand is required");
            return;
        }

        if (releaseStr.isEmpty()) {
            editReleaseDate.setError("Release date is required");
            return;
        }

        if (acquisitionStr.isEmpty()) {
            editAcquisitionDate.setError("Acquisition date is required");
            return;
        }

        Date releaseD, acquisitionD;
        try {
            releaseD = sdf.parse(releaseStr);
            acquisitionD = sdf.parse(acquisitionStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Please enter dates in MM/dd/yy format", Toast.LENGTH_LONG).show();
            return;
        }

        Date today = new Date();
        if (acquisitionD.after(today)) {
            Toast.makeText(this, "Acquisition date cannot be in the future", Toast.LENGTH_LONG).show();
            return;
        }

        if (acquisitionD.before(releaseD)) {
            Toast.makeText(this, "Acquisition date cannot be before release date", Toast.LENGTH_LONG).show();
            return;
        }

        Console console;
        if (consoleID == -1) {
            if (repository.getmAllConsoles().isEmpty()) consoleID = 1;
            else
                consoleID = repository.getmAllConsoles().get(repository.getmAllConsoles().size() - 1).getConsoleID() + 1;
            console = new Console(consoleID, editName.getText().toString(), editBrand.getText().toString(), editReleaseDate.getText().toString(), editAcquisitionDate.getText().toString());
            repository.insert(console);
        } else {
            console = new Console(consoleID, editName.getText().toString(), editBrand.getText().toString(), editReleaseDate.getText().toString(), editAcquisitionDate.getText().toString());
            repository.update(console);
        }
        this.finish();
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
        if (item.getItemId() == R.id.consoledelete) {
            confirmDeleteConsole();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteConsole() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Console")
                .setMessage("Are you sure you want to delete this console? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
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
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

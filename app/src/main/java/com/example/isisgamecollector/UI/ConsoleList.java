package com.example.isisgamecollector.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.database.Repository;
import com.example.isisgamecollector.UI.entities.Game;
import com.example.isisgamecollector.UI.entities.Console;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ConsoleList extends AppCompatActivity {
    private Repository repository;
    private ConsoleAdapter consoleAdapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_console_list);

        emptyStateText = findViewById(R.id.empty_state_text);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsoleList.this, ConsoleDetails.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        repository = new Repository(getApplication());
        consoleAdapter = new ConsoleAdapter(this);
        recyclerView.setAdapter(consoleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    private void updateList() {
        List<Console> allConsoles = repository.getmAllConsoles();
        consoleAdapter.setConsoles(allConsoles);

        if (allConsoles == null || allConsoles.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_console_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_all) {
            shareAllConsolesAndGames();
            return true;
        }
        if (item.getItemId() == R.id.logout) {
            Intent intent = new Intent(ConsoleList.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareAllConsolesAndGames() {
        List<Console> allConsoles = repository.getmAllConsoles();
        List<Game> allGames = repository.getAllGames();

        if (allConsoles == null || allConsoles.isEmpty()) {
            return;
        }

        StringBuilder sharedString = new StringBuilder();
        sharedString.append("My Console Collection:\n\n");

        for (Console console : allConsoles) {
            sharedString.append("Console: ").append(console.getConsoleName()).append("\n");
            sharedString.append("Brand: ").append(console.getConsoleBrand()).append("\n");
            sharedString.append("Release Date: ").append(console.getConsoleReleaseDate()).append("\n");
            sharedString.append("Acquisition Date: ").append(console.getAcquisitionDate()).append("\n");

            sharedString.append("Games:\n");
            boolean hasGames = false;
            for (Game game : allGames) {
                if (game.getConsoleID() == console.getConsoleID()) {
                    sharedString.append(" - ").append(game.getGameName())
                            .append(" (Rel: ").append(game.getGameReleaseDate())
                            .append(", Acq: ").append(game.getAcquisitionDate()).append(")\n");
                    hasGames = true;
                }
            }
            if (!hasGames) {
                sharedString.append(" - No games added\n");
            }
            sharedString.append("\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedString.toString());
        sendIntent.putExtra(Intent.EXTRA_TITLE, "My Collection");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}

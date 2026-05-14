package com.example.isisgamecollector.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConsoleList extends AppCompatActivity {
    private Repository repository;
    private ConsoleAdapter consoleAdapter;
    private RecyclerView recyclerView;
    private TextView emptyStateText;
    private TextView statsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_console_list);

        emptyStateText = findViewById(R.id.empty_state_text);
        statsContent = findViewById(R.id.stats_content);

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
        List<Game> allGames = repository.getAllGames();
        
        consoleAdapter.setData(allConsoles, allGames);

        updateStats(allConsoles, allGames);

        if (allConsoles == null || allConsoles.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateStats(List<Console> consoles, List<Game> games) {
        int consoleCount = (consoles != null) ? consoles.size() : 0;
        int gameCount = (games != null) ? games.size() : 0;
        
        String statsText = String.format(Locale.getDefault(), 
            "Consoles: %d | Games: %d", 
            consoleCount, gameCount);
        
        if (statsContent != null) {
            statsContent.setText(statsText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_console_list, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint("Search consoles and games...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (consoleAdapter != null) {
                            consoleAdapter.getFilter().filter(newText);
                        }
                        return false;
                    }
                });
            }
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_all) {
            generateCollectionReport();
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

    private void generateCollectionReport() {
        List<Console> allConsoles = repository.getmAllConsoles();
        List<Game> allGames = repository.getAllGames();

        if (allConsoles == null || allConsoles.isEmpty()) {
            return;
        }

        String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        
        StringBuilder report = new StringBuilder();
        report.append("ISIS GAME COLLECTOR - FULL COLLECTION REPORT\n");
        report.append("Generated on: ").append(timeStamp).append("\n");
        report.append("==========================================\n\n");

        // Column headers for Console
        report.append(String.format("%-20s | %-15s | %-12s | %-12s\n", "CONSOLE NAME", "BRAND", "REL. DATE", "ACQ. DATE"));
        report.append("--------------------------------------------------------------------------\n");

        for (Console console : allConsoles) {
            report.append(String.format("%-20s | %-15s | %-12s | %-12s\n", 
                truncate(console.getConsoleName(), 20), 
                truncate(console.getConsoleBrand(), 15), 
                console.getConsoleReleaseDate(), 
                console.getAcquisitionDate()));

            // Sub-rows for Games
            boolean firstGame = true;
            for (Game game : allGames) {
                if (game.getConsoleID() == console.getConsoleID()) {
                    if (firstGame) {
                        report.append("   > GAMES:\n");
                        report.append(String.format("     %-25s | %-12s | %-12s\n", "GAME TITLE", "REL. DATE", "ACQ. DATE"));
                        firstGame = false;
                    }
                    report.append(String.format("     %-25s | %-12s | %-12s\n", 
                        truncate(game.getGameName(), 25), 
                        game.getGameReleaseDate(), 
                        game.getAcquisitionDate()));
                }
            }
            if (firstGame) {
                report.append("   (No games assigned)\n");
            }
            report.append("--------------------------------------------------------------------------\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, report.toString());
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Game Collector Collection Report");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "Export Report");
        startActivity(shareIntent);
    }

    private String truncate(String text, int length) {
        if (text == null) return "N/A";
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}

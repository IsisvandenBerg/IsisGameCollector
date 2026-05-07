package com.example.isisgamecollector.UI;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.database.Repository;
import com.example.isisgamecollector.UI.entities.Console;
import com.example.isisgamecollector.UI.entities.Game;

import java.util.ArrayList;
import java.util.List;

public class ConsoleAdapter extends RecyclerView.Adapter<ConsoleAdapter.ConsoleViewHolder> {
    private List<Console> mConsoles;
    private final Context context;
    private final LayoutInflater mInflater;

    public ConsoleAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class ConsoleViewHolder extends RecyclerView.ViewHolder {
        private final TextView consoleItemView;
        private final ImageView addGameIcon;
        private final ImageView expandIcon;
        private final Spinner gameSpinner;

        public ConsoleViewHolder(@NonNull View itemView) {
            super(itemView);
            consoleItemView = itemView.findViewById(R.id.textView2);
            addGameIcon = itemView.findViewById(R.id.add_game_icon);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            gameSpinner = itemView.findViewById(R.id.game_list_spinner);

            consoleItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final Console current = mConsoles.get(position);
                        Intent intent = new Intent(context, ConsoleDetails.class);
                        intent.putExtra("id", current.getConsoleID());
                        intent.putExtra("consoleName", current.getConsoleName());
                        intent.putExtra("consoleBrand", current.getConsoleBrand());
                        intent.putExtra("consoleReleaseDate", current.getConsoleReleaseDate());
                        intent.putExtra("acquisitionDate", current.getAcquisitionDate());
                        context.startActivity(intent);
                    }
                }
            });

            addGameIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final Console current = mConsoles.get(position);
                        Intent intent = new Intent(context, GameDetails.class);
                        intent.putExtra("consoleID", current.getConsoleID());
                        intent.putExtra("consoleRelease", current.getConsoleReleaseDate());
                        context.startActivity(intent);
                    }
                }
            });

            expandIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameSpinner.performClick();
                }
            });

            gameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) { // Skip header
                        List<Game> gamesForThisConsole = (List<Game>) gameSpinner.getTag();
                        if (gamesForThisConsole != null && position <= gamesForThisConsole.size()) {
                            Game selectedGame = gamesForThisConsole.get(position - 1);
                            Intent intent = new Intent(context, GameDetails.class);
                            intent.putExtra("id", selectedGame.getGameID());
                            intent.putExtra("name", selectedGame.getGameName());
                            intent.putExtra("date", selectedGame.getGameReleaseDate());
                            intent.putExtra("acquisitionDate", selectedGame.getAcquisitionDate());
                            intent.putExtra("consoleID", selectedGame.getConsoleID());
                            // We need console release for date validation in GameDetails
                            int consolePos = getAdapterPosition();
                            if (consolePos != RecyclerView.NO_POSITION) {
                                intent.putExtra("consoleRelease", mConsoles.get(consolePos).getConsoleReleaseDate());
                            }
                            context.startActivity(intent);
                        }
                        gameSpinner.setSelection(0); // Reset for next click
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    @NonNull
    @Override
    public ConsoleAdapter.ConsoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.console_list_item, parent, false);
        return new ConsoleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsoleAdapter.ConsoleViewHolder holder, int position) {
        if (mConsoles != null) {
            Console current = mConsoles.get(position);
            holder.consoleItemView.setText(current.getConsoleName());

            // Fetch games for this console
            Repository repo = new Repository((Application) context.getApplicationContext());
            List<Game> allGames = repo.getAllGames();
            List<Game> filteredGames = new ArrayList<>();
            
            if (allGames != null) {
                for (Game game : allGames) {
                    if (game.getConsoleID() == current.getConsoleID()) {
                        filteredGames.add(game);
                    }
                }
            }

            if (!filteredGames.isEmpty()) {
                holder.expandIcon.setVisibility(View.VISIBLE);
                
                List<String> displayStrings = new ArrayList<>();
                displayStrings.add("Games for " + current.getConsoleName() + ":");
                for (Game g : filteredGames) {
                    displayStrings.add(g.getGameName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, displayStrings);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.gameSpinner.setAdapter(adapter);
                holder.gameSpinner.setTag(filteredGames);
            } else {
                holder.expandIcon.setVisibility(View.GONE);
            }
        } else {
            holder.consoleItemView.setText("No console name");
            holder.expandIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mConsoles != null ? mConsoles.size() : 0;
    }

    public void setConsoles(List<Console> consoles) {
        mConsoles = consoles;
        notifyDataSetChanged();
    }
}

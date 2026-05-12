package com.example.isisgamecollector.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.entities.Console;
import com.example.isisgamecollector.UI.entities.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsoleAdapter extends RecyclerView.Adapter<ConsoleAdapter.ConsoleViewHolder> implements Filterable {
    private List<Console> mConsoles = new ArrayList<>();
    private List<Console> mConsolesFull = new ArrayList<>();
    private Map<Integer, List<Game>> mGamesMap = new HashMap<>();
    private final Context context;
    private final LayoutInflater mInflater;
    private final Set<Integer> expandedConsoleIds = new HashSet<>();

    public ConsoleAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public class ConsoleViewHolder extends RecyclerView.ViewHolder {
        private final TextView consoleItemView;
        private final ImageView addGameIcon;
        private final ImageView expandIcon;
        private final LinearLayout gameListContainer;

        public ConsoleViewHolder(@NonNull View itemView) {
            super(itemView);
            consoleItemView = itemView.findViewById(R.id.textView2);
            addGameIcon = itemView.findViewById(R.id.add_game_icon);
            expandIcon = itemView.findViewById(R.id.expand_icon);
            gameListContainer = itemView.findViewById(R.id.game_list_container);

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
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        int consoleId = mConsoles.get(position).getConsoleID();
                        if (expandedConsoleIds.contains(consoleId)) {
                            expandedConsoleIds.remove(consoleId);
                        } else {
                            expandedConsoleIds.add(consoleId);
                        }
                        notifyItemChanged(position);
                    }
                }
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
        if (mConsoles != null && position < mConsoles.size()) {
            Console current = mConsoles.get(position);
            holder.consoleItemView.setText(current.getConsoleName());

            List<Game> filteredGames = mGamesMap.get(current.getConsoleID());

            if (filteredGames != null && !filteredGames.isEmpty()) {
                holder.expandIcon.setVisibility(View.VISIBLE);
                boolean isExpanded = expandedConsoleIds.contains(current.getConsoleID());
                
                if (isExpanded) {
                    holder.expandIcon.setImageResource(android.R.drawable.arrow_up_float);
                    holder.gameListContainer.setVisibility(View.VISIBLE);
                    holder.gameListContainer.removeAllViews();
                    
                    for (Game game : filteredGames) {
                        TextView gameView = new TextView(context);
                        String bulletText = "• " + game.getGameName();
                        gameView.setText(bulletText);
                        gameView.setPadding(32, 12, 16, 12); // Slightly more padding
                        gameView.setTextSize(18);
                        gameView.setTextColor(context.getColor(R.color.purple_primary));
                        gameView.setBackgroundResource(R.drawable.game_item_selector);
                        gameView.setClickable(true);
                        gameView.setFocusable(true);
                        gameView.setOnClickListener(v -> {
                            Intent intent = new Intent(context, GameDetails.class);
                            intent.putExtra("id", game.getGameID());
                            intent.putExtra("name", game.getGameName());
                            intent.putExtra("date", game.getGameReleaseDate());
                            intent.putExtra("acquisitionDate", game.getAcquisitionDate());
                            intent.putExtra("consoleID", game.getConsoleID());
                            intent.putExtra("consoleRelease", current.getConsoleReleaseDate());
                            context.startActivity(intent);
                        });
                        holder.gameListContainer.addView(gameView);
                    }
                } else {
                    holder.expandIcon.setImageResource(android.R.drawable.arrow_down_float);
                    holder.gameListContainer.setVisibility(View.GONE);
                }
            } else {
                holder.expandIcon.setVisibility(View.GONE);
                holder.gameListContainer.setVisibility(View.GONE);
            }
        } else {
            holder.consoleItemView.setText("No console name");
            holder.expandIcon.setVisibility(View.GONE);
            holder.gameListContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mConsoles != null ? mConsoles.size() : 0;
    }

    public void setData(List<Console> consoles, List<Game> games) {
        if (consoles != null) {
            mConsoles = new ArrayList<>(consoles);
            mConsolesFull = new ArrayList<>(consoles);
        } else {
            mConsoles = new ArrayList<>();
            mConsolesFull = new ArrayList<>();
        }

        mGamesMap.clear();
        if (games != null) {
            for (Game game : games) {
                int consoleId = game.getConsoleID();
                if (!mGamesMap.containsKey(consoleId)) {
                    mGamesMap.put(consoleId, new ArrayList<>());
                }
                mGamesMap.get(consoleId).add(game);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return consoleFilter;
    }

    private final Filter consoleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Console> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mConsolesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Console item : mConsolesFull) {
                    boolean match = false;
                    
                    // Check console features
                    if (item.getConsoleName().toLowerCase().contains(filterPattern) ||
                        item.getConsoleBrand().toLowerCase().contains(filterPattern) ||
                        item.getConsoleReleaseDate().toLowerCase().contains(filterPattern) ||
                        item.getAcquisitionDate().toLowerCase().contains(filterPattern)) {
                        match = true;
                    }
                    
                    // Check associated games' features
                    if (!match) {
                        List<Game> associatedGames = mGamesMap.get(item.getConsoleID());
                        if (associatedGames != null) {
                            for (Game game : associatedGames) {
                                if (game.getGameName().toLowerCase().contains(filterPattern) ||
                                    game.getGameReleaseDate().toLowerCase().contains(filterPattern) ||
                                    game.getAcquisitionDate().toLowerCase().contains(filterPattern)) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (match) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mConsoles.clear();
            if (results.values != null) {
                mConsoles.addAll((List) results.values);
            }
            notifyDataSetChanged();
        }
    };
}

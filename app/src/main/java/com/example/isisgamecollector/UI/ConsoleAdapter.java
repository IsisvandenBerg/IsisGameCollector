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

public class ConsoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int TYPE_CONSOLE = 0;
    private static final int TYPE_GAME = 1;

    private List<Object> mDisplayList = new ArrayList<>();
    private List<Console> mConsolesFull = new ArrayList<>();
    private List<Game> mAllGamesFull = new ArrayList<>();
    private Map<Integer, List<Game>> mGamesMap = new HashMap<>();
    
    private final Context context;
    private final LayoutInflater mInflater;
    private final Set<Integer> expandedConsoleIds = new HashSet<>();
    private boolean isSearching = false;

    public ConsoleAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDisplayList.get(position) instanceof Console) {
            return TYPE_CONSOLE;
        } else {
            return TYPE_GAME;
        }
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

            consoleItemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mDisplayList.get(position) instanceof Console) {
                    final Console current = (Console) mDisplayList.get(position);
                    Intent intent = new Intent(context, ConsoleDetails.class);
                    intent.putExtra("id", current.getConsoleID());
                    intent.putExtra("consoleName", current.getConsoleName());
                    intent.putExtra("consoleBrand", current.getConsoleBrand());
                    intent.putExtra("consoleReleaseDate", current.getConsoleReleaseDate());
                    intent.putExtra("acquisitionDate", current.getAcquisitionDate());
                    context.startActivity(intent);
                }
            });

            addGameIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mDisplayList.get(position) instanceof Console) {
                    final Console current = (Console) mDisplayList.get(position);
                    Intent intent = new Intent(context, GameDetails.class);
                    intent.putExtra("consoleID", current.getConsoleID());
                    intent.putExtra("consoleRelease", current.getConsoleReleaseDate());
                    context.startActivity(intent);
                }
            });

            expandIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mDisplayList.get(position) instanceof Console) {
                    int consoleId = ((Console) mDisplayList.get(position)).getConsoleID();
                    if (expandedConsoleIds.contains(consoleId)) {
                        expandedConsoleIds.remove(consoleId);
                    } else {
                        expandedConsoleIds.add(consoleId);
                    }
                    notifyItemChanged(position);
                }
            });
        }
    }

    public class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView gameTitleView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameTitleView = itemView.findViewById(R.id.game_title_standalone);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mDisplayList.get(position) instanceof Game) {
                    final Game game = (Game) mDisplayList.get(position);
                    Intent intent = new Intent(context, GameDetails.class);
                    intent.putExtra("id", game.getGameID());
                    intent.putExtra("name", game.getGameName());
                    intent.putExtra("date", game.getGameReleaseDate());
                    intent.putExtra("acquisitionDate", game.getAcquisitionDate());
                    intent.putExtra("consoleID", game.getConsoleID());
                    
                    // Find associated console release date
                    for (Console console : mConsolesFull) {
                        if (console.getConsoleID() == game.getConsoleID()) {
                            intent.putExtra("consoleRelease", console.getConsoleReleaseDate());
                            break;
                        }
                    }
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONSOLE) {
            View itemView = mInflater.inflate(R.layout.console_list_item, parent, false);
            return new ConsoleViewHolder(itemView);
        } else {
            View itemView = mInflater.inflate(R.layout.game_standalone_item, parent, false);
            return new GameViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = mDisplayList.get(position);

        if (holder instanceof ConsoleViewHolder) {
            ConsoleViewHolder consoleHolder = (ConsoleViewHolder) holder;
            Console current = (Console) item;
            consoleHolder.consoleItemView.setText(current.getConsoleName());

            if (isSearching) {
                // Hide expansion logic in search mode
                consoleHolder.expandIcon.setVisibility(View.GONE);
                consoleHolder.gameListContainer.setVisibility(View.GONE);
            } else {
                List<Game> filteredGames = mGamesMap.get(current.getConsoleID());
                if (filteredGames != null && !filteredGames.isEmpty()) {
                    consoleHolder.expandIcon.setVisibility(View.VISIBLE);
                    boolean isExpanded = expandedConsoleIds.contains(current.getConsoleID());
                    
                    if (isExpanded) {
                        consoleHolder.expandIcon.setImageResource(android.R.drawable.arrow_up_float);
                        consoleHolder.gameListContainer.setVisibility(View.VISIBLE);
                        consoleHolder.gameListContainer.removeAllViews();
                        
                        for (Game game : filteredGames) {
                            TextView gameView = new TextView(context);
                            String bulletText = "• " + game.getGameName();
                            gameView.setText(bulletText);
                            gameView.setPadding(32, 12, 16, 12);
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
                            consoleHolder.gameListContainer.addView(gameView);
                        }
                    } else {
                        consoleHolder.expandIcon.setImageResource(android.R.drawable.arrow_down_float);
                        consoleHolder.gameListContainer.setVisibility(View.GONE);
                    }
                } else {
                    consoleHolder.expandIcon.setVisibility(View.GONE);
                    consoleHolder.gameListContainer.setVisibility(View.GONE);
                }
            }
        } else if (holder instanceof GameViewHolder) {
            GameViewHolder gameHolder = (GameViewHolder) holder;
            Game current = (Game) item;
            gameHolder.gameTitleView.setText(current.getGameName());
        }
    }

    @Override
    public int getItemCount() {
        return mDisplayList.size();
    }

    public void setData(List<Console> consoles, List<Game> games) {
        mConsolesFull = new ArrayList<>(consoles);
        mAllGamesFull = new ArrayList<>(games);
        
        mDisplayList.clear();
        mDisplayList.addAll(mConsolesFull);

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
        isSearching = false;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return consoleFilter;
    }

    private final Filter consoleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Object> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mConsolesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                // Find matching consoles
                for (Console item : mConsolesFull) {
                    if (item.getConsoleName().toLowerCase().contains(filterPattern) ||
                        item.getConsoleBrand().toLowerCase().contains(filterPattern) ||
                        item.getConsoleReleaseDate().toLowerCase().contains(filterPattern) ||
                        item.getAcquisitionDate().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                
                // Find matching games independently
                for (Game game : mAllGamesFull) {
                    if (game.getGameName().toLowerCase().contains(filterPattern) ||
                        game.getGameReleaseDate().toLowerCase().contains(filterPattern) ||
                        game.getAcquisitionDate().toLowerCase().contains(filterPattern)) {
                        filteredList.add(game);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDisplayList.clear();
            if (results.values != null) {
                mDisplayList.addAll((List) results.values);
            }
            isSearching = (constraint != null && constraint.length() > 0);
            notifyDataSetChanged();
        }
    };
}

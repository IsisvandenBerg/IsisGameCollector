package com.example.isisgamecollector.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.isisgamecollector.R;
import com.example.isisgamecollector.UI.entities.Game;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    class GameViewHolder extends RecyclerView.ViewHolder{
        private final TextView gameItemView;
        private final TextView gameItemView2;
        private GameViewHolder(View itemView){
            super(itemView);
            gameItemView=itemView.findViewById(R.id.textView3);
            gameItemView2=itemView.findViewById(R.id.textView4);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    final Game current=mGames.get(position);
                    Intent intent=new Intent(context, GameDetails.class);
                    intent.putExtra("id", current.getGameID());
                    intent.putExtra("name", current.getGameName());
                    intent.putExtra("date", current.getGameReleaseDate());
                    intent.putExtra("acquisitionDate", current.getAcquisitionDate());
                    intent.putExtra("consoleID",current.getConsoleID());
                    context.startActivity(intent);
                }
            });
        }
    }
    private List<Game> mGames;
    private final Context context;
    private final LayoutInflater mInflater;

    public GameAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }
    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.game_list_item,parent,false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        if(mGames!=null && position < mGames.size()){
            Game current=mGames.get(position);
            String name=current.getGameName();
            String date= current.getGameReleaseDate();
            if (holder.gameItemView != null) holder.gameItemView.setText(name);
            if (holder.gameItemView2 != null) holder.gameItemView2.setText(date);
        }
        else{
            if (holder.gameItemView != null) holder.gameItemView.setText("No game name");
            if (holder.gameItemView2 != null) holder.gameItemView2.setText("No date");
        }
    }

    public void setGames(List<Game> games){
        mGames=games;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (mGames!=null) return mGames.size();
        else return 0;
    }
}